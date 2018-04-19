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
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * This an implementation of a lock-free ordered linked list data structure. The
 * implementation is according to the paper <a
 * href="http://www.research.ibm.com/people/m/michael/spaa-2002.pdf"> High
 * Performance Dynamic Lock-Free Hash Tables and List-Based Sets</a> by Maged
 * M. Michael, 2002. To gain a complete understanding of this data structure,
 * please first read this paper, available at:
 * http://www.research.ibm.com/people/m/michael/spaa-2002.pdf
 * <p>
 * An unbounded thread-safe linked list which its element is ordered.
 * 
 * A <tt>LockFreeOrderedList</tt> is an appropriate choice when many threads
 * will share access to a common collection. This list does not permit
 * <tt>null</tt> elements. All elements in the list is ordered according to
 * compare()
 * <p>
 * This is a lock-free implementation intended for highly scalable add, remove
 * and contains which is thread safe. All mothed related to index is not thread
 * safe. Add() will add the element to the head of the list which is different
 * with the normal list.
 * 
 * @author Xiao Jun Dai
 * 
 * @param <E>
 *            the type of elements held in this collection
 * 
 */
public class LockFreeOrderedList<E> extends LockFreeList<E> {
	/**
	 * Creates a <tt>LockfreeList</tt> that is initially empty.
	 */
	public LockFreeOrderedList() {
		super();
	}

	/**
	 * Creates a <tt>LockfreeList</tt> initially containing the elements of
	 * the given collection, added in traversal order of the collection's
	 * iterator.
	 * 
	 * @param c
	 *            the collection of elements to initially contain
	 */
	public LockFreeOrderedList(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	private boolean add(E e, AtomicMarkableReference<Entry<E>> start) {
		// Create a new node
		Entry<E> node = new Entry<E>(e);
		ListStateHolder<E> holder = new ListStateHolder<E>();

		while (true) {
			// find the right position to insert
			findByObject(e, start, holder);

			Entry<E> cur = holder.cur;
			// make new node's next pointer point to next node
			node.next = new AtomicMarkableReference<Entry<E>>(cur, false);

			// change the next pointer of previous node to new node
			if (holder.prev.compareAndSet(cur, node, false, false)) {
				return true;
			}
		}
	}

	/**
	 * Adds the specified element to this list.
	 * 
	 * <p>
	 * Thread Safe
	 * 
	 * @param e
	 *            the element to add.
	 * @return <tt>true</tt> (as per the general contract of
	 *         <tt>Collection.add</tt>).
	 * 
	 */
	public boolean add(E e) {
		if (null == e)
			throw new NullPointerException();
		return add(e, head);
	}

	/**
	 * Find object o start from start position and record the previous, current,
	 * next pointer and index in the list state holder.
	 * 
	 * <p>
	 * Thread Safe.
	 * 
	 * @param o
	 *            element whose presence in this list is to be tested.
	 * @param start
	 *            start position to find.
	 * @param holder
	 *            information of position found.
	 * 
	 * @return state holder of list
	 */
	@SuppressWarnings("unchecked")
	private ListStateHolder<E> findByObject(Object o,
			AtomicMarkableReference<Entry<E>> start, ListStateHolder<E> holder) {

		/*
		 * local variable for cache the position of pointers of previous node,
		 * current node and next node
		 */
		AtomicMarkableReference<Entry<E>> prev;
		Entry<E> cur = null;
		Entry<E> nextEntry = null;

		/*
		 * Purpose of this loop is retry infinitly if CAS operation fails. It
		 * will terminate when reach the end of list or find the expected
		 * element. continue statement will skip two while loop and is suitable
		 * to use a label try_again
		 */
		try_again: while (true) {
			/* start from the head of list */
			prev = start;
			cur = prev.getReference();

			/*
			 * Purpose of this loop is find the expected element in the list. It
			 * will terminate when reach the end of list or find the greater or
			 * expected element. this is the only difference from LockFreeList.
			 */
			while (true) {
				/* the list and empty or reach the end of list */
				if (null == cur) {
					holder.prev = prev;
					holder.cur = cur;
					holder.next = nextEntry;
					holder.found = false;
					return holder;
				}

				/*
				 * nextRef is cached in a local variable in order to reduce the
				 * times of read a object field cur.next from twice to once.
				 */
				AtomicMarkableReference<Entry<E>> nextEntryRef = cur.next;
				nextEntry = nextEntryRef.getReference();
				Comparable cKey = (Comparable) cur.element;
				int cr = cKey.compareTo(o);

				/*
				 * If the node is marked, it means logically removed. Find
				 * routine will help to remove it from the list physically
				 */
				if (nextEntryRef.isMarked()) {
					/*
					 * In lock-free algorithm, threads will help to finish each
					 * others' work. find() will help to finish remove()'s work
					 * if thread executing remove() is dead or stuck.
					 */
					if (!prev.compareAndSet(cur, nextEntry, false, false)) {
						/*
						 * If CAS fails, it means other thread succeeds and
						 * physical revomal is completed. Now the value of prev
						 * is not correct and need to retry from the head. No
						 * ABA problem here.
						 */
						continue try_again;
					}
				} else {
					/*
					 * stop before the greater element. because the list is
					 * ordered.
					 */
					if (cr > 0) {
						/*
						 * stop before the greater element.because the list is
						 * ordered. store the previous, current and next pointer
						 * to stateholder which is used by remove and insert
						 */
						holder.found = false;
						holder.prev = prev;
						holder.cur = cur;
						holder.next = nextEntry;
						return holder;
					} else if (cr == 0 && (cKey == o || cKey.equals(o))) {
						/*
						 * found the expected elements. store the previous,
						 * current and next pointer to stateholder which is used
						 * by remove and insert
						 */
						holder.found = true;
						holder.prev = prev;
						holder.cur = cur;
						holder.next = nextEntry;
						return holder;
					} else {
						/* make prev pointer move one node forward */
						prev = nextEntryRef;
					}
				}

				// move forward
				cur = nextEntry;
			}
		}
	}

}
