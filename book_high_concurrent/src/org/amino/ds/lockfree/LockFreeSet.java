/*
 * Copyright (c) 2008 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.amino.ds.lockfree;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.amino.utility.Parallel;
import org.amino.utility.ParallelType;

/**
 * This is an implementation of lock-free hashset data structure, based on
 * algorithm described in two paper
 * <ul>
 * <li>"Split-Ordered Lists - Lock-free Resizable Hash Tables" by Ori Shalev
 * Tel Aviv University and Nir Shavit Tel-Aviv University and Sun Microsystems
 * Laboratories</li>
 * 
 * <li>"High Performance Dynamic Lock-Free Hash Tables and List-Based Set" by
 * Maged M. Michael.</li>
 * </ul>
 * 
 * The internal data structure is a single linked list, which uses the same
 * algorithm as {@link LockFreeOrderedList}. Elements are sorted by "binary
 * reversal" of hash of elements. Additionally, an array of dummy nodes is
 * stored to allow quick access to elements in the middle of elements. Elements
 * are wrapped by {@link HashLinkNode} before stored into set.
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            Type of elements stored in the set
 */
public class LockFreeSet<E> implements Set<E> {
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private static final int DEFAULT_SIZE = 512;
	private static final int MINIMAL_SIZE = 64;

	/**
	 * Create a new set with explicitly specified expected size and load factor.
	 * The number of dummy nodes has a maximum of <code>512*expectedSize</code>.
	 * This isn't a limitation of actual elements stored in set. But the average
	 * search number will increase if number of elements is bigger than
	 * <code>512*expectedSize</code>
	 * 
	 * @param expectedSize
	 *            the estimated size of set
	 * @param loadFactor
	 *            average load factor. Number of dummy nodes will expand 2X if
	 *            the actual load factor is higher than this parameter.
	 */
	public LockFreeSet(int expectedSize, float loadFactor) {
		this.loadFactor = loadFactor;

		if (expectedSize < MINIMAL_SIZE)
			expectedSize = MINIMAL_SIZE;
		segmentSize = Integer.highestOneBit(expectedSize);
		if (segmentSize < expectedSize)
			segmentSize = segmentSize << 1;
		segmentBit = Integer.numberOfTrailingZeros(segmentSize);

		initInternal();

	}

	/**
	 * 
	 * @param expectedSize
	 *            expected set size.
	 */
	public LockFreeSet(int expectedSize) {
		this(expectedSize, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Create a new LockFreeSet. The default expected size is 512. And load
	 * factor defaults to 0.75f
	 */
	public LockFreeSet() {
		this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR);
	}

	private void initInternal() {
		totalElement = new AtomicInteger(0);
		head = new AtomicMarkableReference<HashLinkNode<E>>(null, false);
		segments = new AtomicReferenceArray(N_SEGMENT);

		/*
		 * Initialize the first segment
		 */
		AtomicReferenceArray<AtomicMarkableReference<HashLinkNode<E>>> segment = new AtomicReferenceArray<AtomicMarkableReference<HashLinkNode<E>>>(
				segmentSize);
		segments.set(0, segment);

		bucketSizeLog2 = new AtomicInteger(N_INIT_BUCKET);
		int tmp = 1 << N_INIT_BUCKET;

		// Create the first dummy node and insert it into internal list, which
		// will be parent or ancestor of all other dummy nodes
		HashLinkNode dummyNode = HashLinkNode.dummyNode(0);
		insert(dummyNode);
		segment.set(0, dummyNode.next);

		// Create other dummy nodes. The initial number of dummy nodes is 1<<
		// N_INIT_BUCKET
		for (int i = 1; i < tmp; i++) {
			dummyNode = HashLinkNode.dummyNode(i);
			insert(dummyNode, segment.get(i ^ Integer.highestOneBit(i)));
			segment.set(i, dummyNode.next);

			if (segment.get(i) == null)
				throw new NullPointerException();
		}
	}

	private static final long MARK_ALL_BITS = 0xffffffffL;

	/**
	 * We store dummy nodes in this two-dimension array object. If we use
	 * one-dimension array here, we will need a large array to save dummy nodes
	 * even when elements number is less than 10.
	 */
	AtomicReferenceArray<AtomicReferenceArray<AtomicMarkableReference<HashLinkNode<E>>>> segments;

	/**
	 * Number of segments.
	 */
	static final int N_SEGMENT = 512;

	/**
	 * The initial number of dummy nodes is <code>1<< N_INIT_BUCKET</code>.
	 */
	static final int N_INIT_BUCKET = 6;

	/**
	 * Size of each segment.
	 */
	int segmentSize;

	private int segmentBit;

	/**
	 * Find the dummy node for elements which is closest to the inserting
	 * position of objects with <code>hash</code>.
	 * 
	 * @param bucket
	 *            hash value of working element
	 * 
	 * @return return the bucket when found
	 */
	@SuppressWarnings("unchecked")
	private AtomicMarkableReference<HashLinkNode<E>> bucketAt(int hash) {
		int bSize = bucketSizeLog2.get();
		// We only need several bits of the hash value.
		int bucket = hash & (0x80000000 >> bSize);
		bucket = Integer.reverse(bucket);

		// Work out the index of the segment
		int segment = bucket >> segmentBit;
		// Work out the index inside the segment
		int bkptr = bucket & (segmentSize - 1);

		// Get the segment or create a segment if it's null.
		AtomicReferenceArray<AtomicMarkableReference<HashLinkNode<E>>> seg = segments
				.get(segment);
		if (seg != null) {
			// Get the value of dummy nodes
			AtomicMarkableReference<HashLinkNode<E>> segValue = seg.get(bkptr);
			if (segValue != null) {
				return segValue;
			}
		}

		// No such dummy node yet, create one!
		int pk, ps, parent;
		parent = bucket;
		AtomicReferenceArray<AtomicMarkableReference<HashLinkNode<E>>> pseg;
		AtomicMarkableReference<HashLinkNode<E>> pSegPos = null;

		do {
			parent = getParent(parent, bSize);
			ps = parent >> segmentBit;
			pk = parent & (segmentSize - 1);

			// Get segment of parent
			pseg = segments.get(ps);
			if (pseg != null) {
				pSegPos = pseg.get(pk);
				if (pSegPos != null)
					break;
			}
		} while (true);

		// insert dummy node after ancestor dummy node
		HashLinkNode<E> dummyNode = HashLinkNode.dummyNode(bucket);
		dummyNode = insert(dummyNode, pSegPos);

		seg = segments.get(segment);
		if (seg == null) {
			seg = new AtomicReferenceArray<AtomicMarkableReference<HashLinkNode<E>>>(
					segmentSize);
			if (!segments.compareAndSet(segment, null, seg)) {
				seg = segments.get(segment);
			}
		}
		if (seg.get(bkptr) == null)
			seg.compareAndSet(bkptr, null, dummyNode.next);

		return seg.get(bkptr);

	}

	/**
	 * the current number of buckets is 2^bucketSize.
	 */
	AtomicInteger bucketSizeLog2;

	/**
	 * element number of the set.
	 */
	private AtomicInteger totalElement;

	private final float loadFactor;

	/**
	 * {@inheritDoc}
	 */
	public boolean add(E e) {
		int hash = HashLinkNode.hash(e);
		AtomicMarkableReference<HashLinkNode<E>> bucket = bucketAt(hash);

		if (addElement(e, bucket, hash)) {
			totalElement.incrementAndGet();
			int bSize = bucketSizeLog2.get();
			int cSize = 1 << bSize;

			// expand number of dummy nodes
			if (totalElement.get() > cSize * loadFactor
					&& cSize < N_SEGMENT * segmentSize)
				bucketSizeLog2.compareAndSet(bSize, bSize + 1);
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addAll(Collection<? extends E> c) {
		boolean res = false;

		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			E item = (E) iterator.next();
			if (add(item))
				res = true;
		}

		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		initInternal();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Object o) {
		int hash = HashLinkNode.hash(o);
		AtomicMarkableReference<HashLinkNode<E>> bucket = bucketAt(hash);

		return findElement((E) o, bucket, hash);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsAll(Collection<?> c) {
		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			E item = (E) iterator.next();
			if (!contains(item))
				return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return totalElement.intValue() == 0;
	}

	/**
	 * @author Zhi Gan
	 * 
	 */
	private class SetIterator implements Iterator<E> {
		private Iterator<HashLinkNode<E>> iter;
		private E current;

		public SetIterator(Iterator<HashLinkNode<E>> iter) {
			this.iter = iter;
			current = null;
		}

		public boolean hasNext() {

			while (iter.hasNext()) {
				HashLinkNode<E> next = iter.next();
				if (next.key != null) {
					current = next.key;
					return true;
				}
			}

			return false;
		}

		public E next() {
			if (current == null)
				throw new NoSuchElementException();
			return current;
		}

		public void remove() {
			iter.remove();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> iterator() {
		return new SetIterator(new CrudeItr());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		int hash = HashLinkNode.hash(o);

		AtomicMarkableReference<HashLinkNode<E>> bucket = bucketAt(hash);

		if (removeElement((E) o, bucket, hash)) {
			totalElement.decrementAndGet();
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(Collection<?> c) {
		boolean res = false;
		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			E item = (E) iterator.next();
			if (remove(item))
				res = true;
		}

		return res;
	}

	/**
	 * This method is not thread-safe. It's programmer's responsibility to
	 * ensure there is no other threads modifying the set simultaneously.
	 * 
	 * @param c
	 *            collection containing elements to be retained in this set
	 * @return true if this set changed as a result of the call
	 */
	public boolean retainAll(Collection<?> c) {
		boolean mod = false;
		for (Iterator<?> iterator = iterator(); iterator.hasNext();) {
			Object item = iterator.next();
			if (!c.contains(item) && remove(item))
				mod = true;
		}

		return mod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Parallel(ParallelType.LockFree)
	public int size() {
		return totalElement.get();
	}

	/**
	 * This method puts all elements of set into an array and returns it. This
	 * method is not thread-safe. It's programmer's responsibility to ensure
	 * there is no other threads modifying the set simultaneously. Under the
	 * hood, this method use iterator to collect elements inside the set.
	 * 
	 * @return an array containing all the elements in this set
	 */
	@Parallel(ParallelType.ThreadUnSafe)
	public Object[] toArray() {
		Object[] array = new Object[totalElement.get()];
		Iterator<E> iter = iterator();

		int index = 0;
		while (iter.hasNext()) {
			if (index >= array.length)
				break;
			array[index++] = iter.next();
		}

		return array;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] toArray(T[] a) {
		if (a.length < totalElement.get()) {
			a = (T[]) new Object[totalElement.get()];
		}

		Iterator<E> iter = iterator();
		int index = 0;
		while (iter.hasNext()) {
			if (index >= a.length)
				break;
			a[index++] = (T) iter.next();
		}

		if (a.length > index)
			a[index] = null;

		return a;
	}

	/**
	 * Find index of the parent bucket. reverse(parent)=reverse(bucket)-1 while
	 * assume only <b>bSize</b> bits.
	 * 
	 * @param bucket
	 *            to find parent of this bucket
	 * @param bSize
	 *            how many bits are used to form the bucket
	 * @return index of the parent bucket
	 */
	int getParent(int bucket, int bSize) {
		return bucket ^ Integer.highestOneBit(bucket);
	}

	/**
	 * This method computes and return the ratio between data nodes and dummy
	 * nodes.
	 * 
	 * @return dummy rate
	 */
	float dummyRate() {
		AtomicMarkableReference<HashLinkNode<E>> start = head;
		HashLinkNode<E> startRef = start.getReference();
		int dummyCount = 0;
		int realNode = 0;
		StringBuffer sb = new StringBuffer();
		while (startRef != null) {
			if (startRef.key == null) {
				dummyCount++;
				sb.append('*');
				sb.append(Integer.toBinaryString(startRef.brKey));
			} else {
				sb.append('.');
				sb.append(Integer.toBinaryString(startRef.brKey));
				realNode++;
			}
			if (startRef.next != null)
				startRef = startRef.next.getReference();
			else
				break;
		}
		float rate = realNode / (float) dummyCount;
		if (rate > 2)
			System.out.println(sb.toString());
		return rate;
	}

	/**
	 * Header pointer of backend list.
	 */
	private AtomicMarkableReference<HashLinkNode<E>> head;

	private CompositeStateHolder<?> findNode(HashLinkNode<E> o,
			AtomicMarkableReference<HashLinkNode<E>> start,
			CompositeStateHolder<E> holder) {
		try_again: while (true) {
			// initialize holder
			holder.prev = start;
			holder.cur = start.getReference();

			while (true) {
				HashLinkNode<E> cur2 = holder.cur;
				// at the end of set
				if (null == cur2) {
					holder.found = false;
					return holder;
				}

				holder.next = cur2.next.getReference();

				if (holder.prev.getReference() != cur2
						|| holder.prev.isMarked()) {
					continue try_again;
				}
				if (!cur2.next.isMarked()) {
					// modify by Jinping Chen. some key may has the same value
					int cr = cur2.compareTo(o);

					if (cr < 0)
						holder.prev = cur2.next;
					else if (cr > 0) {
						holder.found = false;
						return holder;
					} else if (cr == 0 && cur2.equals(o)) {
						holder.found = true;
						return holder;
					} else
						holder.prev = cur2.next;
				} else {
					// help remove a marked node
					if (!holder.prev.compareAndSet(cur2, holder.next, false,
							false)) {
						continue try_again;
					}
				}

				holder.cur = holder.next;
			}
		}
	}

	/**
	 * Find position by index and keep information in holder
	 * 
	 * <p>
	 * Thread Safe.
	 * 
	 * @param o
	 *            element whose presence in this set is to be tested.
	 * @param start
	 *            start position to find.
	 * @param holder
	 *            information of position found.
	 */
	private CompositeStateHolder<?> findElement(E o,
			AtomicMarkableReference<HashLinkNode<E>> start,
			CompositeStateHolder<E> holder, int hash) {
		long regKey = (hash | 1) & MARK_ALL_BITS;

		AtomicMarkableReference<HashLinkNode<E>> prev;

		HashLinkNode<E> cur;

		try_again: while (true) {
			prev = start;
			cur = prev.getReference();

			while (true) {
				if (null == cur || cur.key == null) {
					holder.found = false;
					holder.cur = cur;
					holder.prev = prev;
					return holder;
				}
				AtomicMarkableReference<HashLinkNode<E>> nextprt = cur.next;

				// if (prev.getReference() != cur || prev.isMarked()) {
				// continue try_again;
				// }
				if (!nextprt.isMarked()) {
					int cr = cur.compareToElement(regKey);

					if (cr < 0)
						prev = nextprt;
					else if (cr > 0) {
						holder.found = false;
						holder.cur = cur;
						holder.prev = prev;
						holder.next = nextprt.getReference();
						return holder;
					} else if (cr == 0 && cur.equalsElement(o)) {
						holder.found = true;
						holder.cur = cur;
						holder.prev = prev;
						holder.next = nextprt.getReference();
						return holder;
					} else
						prev = nextprt;
				} else {
					// Physically removing element which has a deletion mark
					if (!prev.compareAndSet(cur, nextprt.getReference(), false,
							false)) {
						continue try_again;
					}
				}

				cur = nextprt.getReference();
			}
		}
	}

	/**
	 * Find an element in Set.
	 * 
	 * @param o
	 *            Searching element
	 * @param start
	 *            start position
	 * @param hash
	 *            Hash value of parameter <code>o</code>
	 * @return True if found
	 */
	boolean findElement(E o, AtomicMarkableReference<HashLinkNode<E>> start,
			int hash) {
		// this key is used to compare with key of elements inside list
		long regKey = (hash | 1) & MARK_ALL_BITS;

		AtomicMarkableReference<HashLinkNode<E>> prev;

		HashLinkNode<E> cur;

		try_again: while (true) {
			prev = start;
			cur = prev.getReference();
			try {
				while (true) {
					AtomicMarkableReference<HashLinkNode<E>> nextprt = cur.next;

					int cr = cur.compareToElement(regKey);

					if (cr < 0)
						prev = nextprt;
					else if (cr > 0) {
						return false;
					} else if (cr == 0 && cur.equalsElement(o)) {
						if (nextprt.isMarked()) {
							// Found an element with deletion mark checked.
							// Let's delete it physically now.
							if (!prev.compareAndSet(cur,
									nextprt.getReference(), false, false)) {
								continue try_again;
							}
						} else
							return true;
					} else
						prev = nextprt;

					cur = nextprt.getReference();
				}
			} catch (NullPointerException e) {
				// This exception will only happen when cur equals to null
				return false;
			}
		}
	}

	/**
	 * Adds the an element to the set. This method is lock-free.
	 * 
	 * @param e
	 *            the element to add.
	 */
	void insert(HashLinkNode<E> e) {
		insert(e, head);
	}

	/**
	 * Adds an element to the set.
	 * 
	 * <p>
	 * Thread Safe
	 * 
	 * @param e
	 *            the HashLinkNode which contains an element to add.
	 * @param start
	 *            start position of searching
	 * @return reference to next node of node inserted if successful otherwise
	 *         reference to node equals to node inserted
	 */
	HashLinkNode<E> insert(HashLinkNode<E> e,
			AtomicMarkableReference<HashLinkNode<E>> start) {
		if (null == e || null == start)
			throw new NullPointerException();

		CompositeStateHolder<E> holder = new CompositeStateHolder<E>();
		while (true) {
			// Find right position
			findNode(e, start, holder);
			if (holder.found) {
				return holder.cur;
			}

			e.next.set(holder.cur, false);
			// Set prev->next = newNode
			if (holder.prev.compareAndSet(holder.cur, e, false, false)) {
				return e;
			}
		}
	}

	/**
	 * Add an element to the list which will remain sorted.
	 * 
	 * @param e
	 *            inserting element
	 * @param start
	 *            start position of searching
	 * @param hash
	 *            hash code of parameter <code>e</code>. It's here for saving
	 *            cost of hashing
	 * 
	 * @return <code>true</code> if succeed, otherwise <code>false</code>
	 */
	boolean addElement(E e, AtomicMarkableReference<HashLinkNode<E>> start,
			int hash) {
		CompositeStateHolder<E> holder = new CompositeStateHolder<E>();
		HashLinkNode<E> node = null;
		while (true) {
			// Find right position
			findElement(e, start, holder, hash);
			if (holder.found) {
				return false;
			}
			HashLinkNode<E> cur2 = holder.cur;

			if (node == null) {
				node = new HashLinkNode<E>(e, hash);
				node.next = new AtomicMarkableReference<HashLinkNode<E>>(cur2,
						false);
			} else
				node.next.set(cur2, false);
			// Set prev->next = newNode
			if (holder.prev.compareAndSet(cur2, node, false, false)) {
				return true;
			}
			// Retry if CAS is not success
		}
	}

	/**
	 * Remove element from set. The search of removing element starts from
	 * parameter <code>start</code>
	 * 
	 * @param o
	 *            Object to remove
	 * @param start
	 *            start pos
	 * @param hash
	 *            hash value of parameter <code>o</code>. It's used to save
	 *            time for regenerating hash.
	 * @return True if succeed
	 */
	boolean removeElement(E o, AtomicMarkableReference<HashLinkNode<E>> start,
			int hash) {
		CompositeStateHolder<E> holder = new CompositeStateHolder<E>();
		while (true) {
			findElement(o, start, holder, hash);

			if (!holder.found) {
				return false;
			}
			HashLinkNode<E> cur2 = holder.cur;
			HashLinkNode<E> next2 = holder.next;

			// Let's set the deletion mark of the found node. This completes the
			// removing operation logically.
			if (!cur2.next.compareAndSet(next2, next2, false, true)) {
				continue;
			}

			// Let's remove the element from the list. It doesn't matter even if
			// this operation failed, which means other threads removed or will
			// remove this node from list.
			// if (!holder.prev.compareAndSet(cur2, next2, false, false)) {
			// // retry from start
			// // findElement(o, start, holder, hash);
			//			}
			return true;
		}
	}

	/**
	 * Iterator which returns {@link HashLinkNode} instead of element itself.
	 * This class is used by the {@link SetIterator}.
	 * 
	 * @author ganzhi
	 * 
	 */
	private class CrudeItr implements Iterator<HashLinkNode<E>> {
		private final CompositeStateHolder<E> holder = new CompositeStateHolder<E>();

		CrudeItr() {
			holder.prev = head;
			holder.cur = holder.prev.getReference();
		}

		private HashLinkNode<E> advance() {
			// at the end of list
			if (null == holder.cur) {
				return null;
			}

			HashLinkNode<E> curItem = holder.cur;

			holder.next = curItem.next.getReference();

			if (!curItem.next.isMarked()) {
				holder.prev = curItem.next;
			} else {
				// help remove a marked node
				holder.prev.compareAndSet(curItem, holder.next, false, false);
			}
			holder.cur = holder.next;

			return curItem;
		}

		public boolean hasNext() {
			return null != holder.cur;
		}

		public HashLinkNode<E> next() {
			HashLinkNode<E> result = advance();

			if (null == result)
				throw new NoSuchElementException();
			else {
				return result;
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}

/**
 * This class will be used to hold state of search.
 * 
 * @author ganzhi
 * 
 * @param <E>
 *            Type of element stored in the set
 */
class CompositeStateHolder<E> {
	/**
	 * Found the node with the expected element of the iteration.
	 */
	boolean found;
	/**
	 * Pointer to the previous node.
	 */
	AtomicMarkableReference<HashLinkNode<E>> prev;
	/**
	 * Pointer to the current node.
	 */
	HashLinkNode<E> cur;
	/**
	 * Pointer to the next node.
	 */
	HashLinkNode<E> next;
}

/**
 * The node type of the list, which also stores hash of element.
 * 
 * @author ganzhi
 * 
 * @param <E>
 *            Type of element
 */
class HashLinkNode<E> implements Comparable<HashLinkNode<E>> {
	private static final long MARK_ALL_BITS = 0xffffffffL;

	/**
	 * Key in the node.
	 */
	final E key;

	/**
	 * Binary reverse key.
	 */
	int brKey;

	/**
	 * Create a new node.
	 * 
	 * @param key
	 *            stored key
	 * @param hash
	 *            hash of key
	 */
	public HashLinkNode(E key, int hash) {
		this.key = key;

		// this ensures that no key of normal nodes can be equal to key of dummy
		// nodes
		brKey = hash | 1;
	}

	private HashLinkNode() {
		key = null;
	}

	/**
	 * Create a dummy node with specified key.
	 * 
	 * @param dummykey
	 *            key for dummy node
	 * @return generated dummy node
	 */
	@SuppressWarnings("unchecked")
	public static HashLinkNode dummyNode(int dummykey) {
		HashLinkNode hn = new HashLinkNode();
		hn.next = new AtomicMarkableReference<HashLinkNode>(null, false);
		hn.brKey = Integer.reverse(dummykey);
		return hn;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(HashLinkNode<E> hn) {
		if (this == hn)
			return 0;

		long c1 = brKey & MARK_ALL_BITS;
		long c2 = hn.brKey & MARK_ALL_BITS;
		if (c1 < c2)
			return -1;
		else if (c1 > c2)
			return 1;
		else
			return 0;
	}

	/**
	 * Compare to stored value.
	 * 
	 * @param hn
	 *            value to compare
	 * @return Same as compareTo() method
	 */
	public int compareToElement(E hn) {
		if (hn == key)
			return 0;
		long c1 = brKey & MARK_ALL_BITS;
		long c2 = (hash(hn) | 1) & MARK_ALL_BITS;

		if (c1 < c2)
			return -1;
		else if (c1 > c2)
			return 1;
		else
			return 0;
	}

	/**
	 * Compare to key.
	 * 
	 * @param objKey
	 *            the key to compare
	 * @return Same result as CompareTo() method
	 */
	public int compareToElement(int objKey) {
		int c1 = brKey >>> 1;
		int c2 = objKey >>> 1;

		if (c1 < c2)
			return -1;
		else if (c1 > c2)
			return 1;
		else {
			c1 = brKey & 0x1;
			c2 = objKey & 0x1;

			if (c1 < c2)
				return -1;
			else if (c1 > c2)
				return 1;
			else
				return 0;
		}
	}

	/**
	 * Compare to key.
	 * 
	 * @param objKey
	 *            the key to compare
	 * @return Same result as CompareTo() method
	 */
	public int compareToElement(long objKey) {
		long c1 = brKey & MARK_ALL_BITS;
		if (c1 < objKey)
			return -1;
		else if (c1 > objKey)
			return 1;
		else
			return 0;
	}

	/**
	 * Hash function. We didn't do binary reversal here and directly treat the
	 * generated hash as a reversed hash.
	 * 
	 * @param obj
	 *            Object to hash
	 * @return hash of obj
	 */
	static int hash(Object obj) {
		int h = obj.hashCode();
		h ^= (h << 30) ^ (h << 28) ^ (h << 24) ^ (h << 16);
		return h;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HashLinkNode) {
			HashLinkNode hn1 = (HashLinkNode) obj;

			if (hn1.brKey != brKey)
				return false;
			else if (hn1.key == null)
				return key == null && (hn1.key == key || hn1.brKey == brKey);
			else
				return hn1.key == key || hn1.key.equals(key);
		}
		return false;
	}

	/**
	 * Judge if equal to element obj.
	 * 
	 * @param obj
	 *            object to compare
	 * @return Same as equals() method
	 */
	public boolean equalsElement(E obj) {
		return obj == key || obj.equals(key);
	}

	/**
	 * A pointer to the next node.
	 */
	AtomicMarkableReference<HashLinkNode<E>> next;

	/**
	 * @return key
	 */
	public E getKey() {
		return key;
	}
}