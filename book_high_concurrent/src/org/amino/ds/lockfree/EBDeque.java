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

import java.util.AbstractQueue;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import org.amino.utility.AdaptEliminationArray;
import org.amino.utility.EliminationArray;
import org.amino.utility.IEliminationArray;

/**
 * This Deque implementation is based on the algorithm defined in the follwoing
 * paper: CAS-Based Lock-Free Algorithm for Shared Deques By Maged M. Michael
 * 
 * <p>
 * 
 * This deque add elimination mechanism to deal with high contention rate
 * scenario. Please read about {@link org.amino.utility.EliminationArray} to get
 * more information. If we don't consider elimination backoff, this class
 * implements the same algorithm as {@link org.amino.ds.lockfree.LockFreeDeque}
 * 
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            type of element in the deque
 * 
 */
public class EBDeque<E> extends AbstractQueue<E> implements Deque<E> {
	private static final int DEFAULT_EBARRAY_LEN = 8;

	/**
	 * Lower bound of backoff time in milliseconds. EBDeque use exponential
	 * backoff to get some adaptability.
	 */
	private static final int MIN_BACKOFF_TIME = 3;
	/**
	 * Upper bound of backoff time in milliseconds.
	 */
	private static final int MAX_BACKOFF_TIME = 128;

	/**
	 * Elimination array for head part.
	 */
	IEliminationArray eaHead;
	/**
	 * Elimination array for tail part.
	 */
	IEliminationArray eaTail;

	/**
	 * Iterator of deque.
	 * 
	 * @author Xiao Jun Dai
	 */
	private class DeqIterator implements Iterator<E> {

		private DequeNode<E> cursor = anchor.get().left;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return cursor != null;
		}

		public E next() {
			if (cursor == null)
				throw new NoSuchElementException();

			E result = cursor.data;
			cursor = cursor.right.get();
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * It's used for debugging purpose.
	 */
	public void dump() {
		// ea_head.dump();
		// ea_tail.dump();
	}

	/**
	 * default constructor.
	 */
	public EBDeque() {
		eaHead = new EliminationArray(DEFAULT_EBARRAY_LEN);
		eaTail = new EliminationArray(DEFAULT_EBARRAY_LEN);
	}

	/**
	 * @param eliminationSize
	 *            default size of elimination array
	 */
	public EBDeque(int eliminationSize) {
		if (eliminationSize > 0) {
			eaHead = new EliminationArray(eliminationSize);
			eaTail = new EliminationArray(eliminationSize);
		} else {
			eaHead = new AdaptEliminationArray(DEFAULT_EBARRAY_LEN);
			eaTail = new AdaptEliminationArray(DEFAULT_EBARRAY_LEN);
		}
	}

	/**
	 * Returns head (first element) of this deque, but does not remove it.
	 * 
	 * @return head of this deque.
	 * @throws NoSuchElementException
	 */
	public E element() {
		return getFirst();
	}

	/**
	 * Inserts element e at the tail of this deque. Preferable to
	 * <code>addLast(E)</code>.
	 * 
	 * @param e
	 *            element to insert
	 * @return true if the element was successfully added, false otherwise.
	 */
	public boolean offer(E e) {
		return offerLast(e);
	}

	/**
	 * Returns the first element of this deque, or null if deque is empty.
	 * Element is not removed.
	 * 
	 * @return head of this deque, or null if deque is empty.
	 */
	public E peek() {
		return peekFirst();
	}

	/**
	 * Returns and removes first (head) element of this deque, or null if this
	 * deque is empty.
	 * 
	 * @return head of this deque, or null if empty.
	 */
	public E poll() {
		return pollFirst();
	}

	/**
	 * Returns and removes the first element in this deque.
	 * 
	 * @return the head of this deque.
	 */
	public E remove() {
		return removeFirst();
	}

	/**
	 * Iterators not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> descendingIterator() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public E getFirst() {
		if (isEmpty())
			throw new NoSuchElementException();
		return peekFirst();
	}

	/**
	 * {@inheritDoc}
	 */
	public E getLast() {
		if (isEmpty())
			throw new NoSuchElementException();
		return peekLast();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean offerFirst(E e) {
		try {
			addFirst(e);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean offerLast(E e) {
		try {
			addLast(e);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public E removeFirst() {
		E result = pollFirst();
		if (result == null)
			throw new NoSuchElementException();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeFirstOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public E removeLast() {
		E result = pollLast();
		if (result == null)
			throw new NoSuchElementException();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeLastOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts element e at the end of the deque.
	 * 
	 * @param e
	 *            element being added to the end of the deque.
	 * 
	 * @return true if add
	 */
	public boolean add(E e) {
		addLast(e);
		return true;
	}

	/**
	 * Removes all elements, clears the deque.
	 */
	public void clear() {
		AnchorType<E> oanchor, nanchor;
		// int ostamp, nstamp;

		while (true) {
			int exp = 1;
			oanchor = anchor.get();

			if (oanchor.right == null)
				return;

			nanchor = new AnchorType<E>(null, null, STABLE, 0);
			
			/**
			 * Any modification to anchor should be an atomic operation. Thread
			 * will retry with Elimination process if CAS fails.
			 */
			if (anchor.compareAndSet(oanchor, nanchor))
				return;
			if (BACK_OFF) {
				try {
					Thread.sleep(MIN_BACKOFF_TIME * exp);
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Contains function is not thread safe.
	 * 
	 * @param o
	 *            is the Object we are looking for.
	 * @throws UnsupportedOperationException
	 */
	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Object o) {
		E element;
		Iterator<E> itr = new DeqIterator();
		while (itr.hasNext()) {
			element = itr.next();
			if (element.equals(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if the deque is empty or not.
	 * 
	 * @return true if deque is empty
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Iterators not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> iterator() {
		return new DeqIterator();
	}

	/**
	 * Remove first occurrence of specified object o in this deque.
	 * 
	 * @param o
	 *            element to be removed, if present.
	 * @return true if an element was removed.
	 */
	public boolean remove(Object o) {
		return removeFirstOccurrence(o);
	}

	/**
	 * @return size of the deque
	 */
	public int size() {
		AnchorType<E> oanchor;
		// oanchor = anchor..getReference();
		oanchor = anchor.get();
		return oanchor.getSize();
	}

	/**
	 * This object contains header pointer and footer pointer of deque. It must
	 * be replaced inside an atomic operation.
	 */
	private AtomicReference<AnchorType<E>> anchor = new AtomicReference<AnchorType<E>>(
			new AnchorType<E>());

	/**
	 * Wait for a while if CAS operation fails. (Millisecond)
	 */
	static final boolean BACK_OFF = true;

	/**
	 * This is the internal class of the Deque to keep track of two anchor
	 * pointers (left and right) and also the status of the Deque. The status
	 * can be STABLE, RPUSH and LPUSH. The anchor also keeps a count of the
	 * current number of elements in the deque.
	 */
	private static final int STABLE = 0, RPUSH = 1, LPUSH = 2;

	/**
	 * This is the method that does a right push into the Deque. It takes the
	 * data as input, creates a deque node with the data and then pushes it onto
	 * the deque from right.
	 * 
	 * @param d
	 *            element to add
	 */
	public void addFirst(E d) {
		DequeNode<E> newtop;
		AnchorType<E> oanchor;
		AnchorType<E> nanchor = new AnchorType<E>();
		// int oStamp, n1Stamp, n2Stamp;

		// Create new stack node with the data
		newtop = new DequeNode<E>(d);

		int exp = 1;
		while (true) {
			oanchor = anchor.get();

			if (oanchor.right == null) // deque is empty
			{
				nanchor.setup(newtop, newtop, oanchor.status, 1);
				// replace the anchor with this new anchor
				if (anchor.compareAndSet(oanchor, nanchor))
					return;
				if (BACK_OFF) {
					try {
						if (eaHead.tryAdd(d, MIN_BACKOFF_TIME * exp))
							return;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else if (oanchor.status == STABLE) {
				// set the newtop left pointer to the old anchor right pointer
				newtop.setLeft(oanchor.right);

				// create a new anchor with the right pointer pointing to the
				// new node
				// and the left pointer pointing to the old anchor left
				nanchor.setup(newtop, oanchor.left, RPUSH,
						oanchor.numElements + 1);
				/**
				 * Any modification to anchor should be an atomic operation.
				 * Thread will retry with Elimination process if CAS fails.
				 */
				if (anchor.compareAndSet(oanchor, nanchor)) {
					this.stabilizeRight(nanchor);
					return;
				}
				if (BACK_OFF) {
					try {
						if (eaHead.tryAdd(d, MIN_BACKOFF_TIME * exp))
							return;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else {
				// deque is not in a stable state
				this.stabilize(oanchor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public E peekFirst() {
		AnchorType<E> oanchor;

		oanchor = anchor.get();
		if (oanchor.right == null)
			return null;
		return oanchor.right.data;
	}

	/**
	 * This is the method to pop the Right node from the Deque.
	 * 
	 * @return element polled
	 */
	public E pollFirst() {
		DequeNode<E> prev;
		AnchorType<E> oanchor;
		AnchorType<E> nanchor = new AnchorType<E>();
		// int oStamp, n1Stamp, n2Stamp;

		int exp = 1;
		while (true) {
			// oanchor = anchor.getReference();
			// oStamp = anchor.getStamp();
			oanchor = anchor.get();

			// deque is empty
			if (oanchor.right == null)
				return null;

			// deque has just one node
			if (oanchor.right == oanchor.left) {
				nanchor.setup(null, null, oanchor.status, 0);
				/*
				 * n1anchor.right = null; n1anchor.left = null;
				 * n1anchor.numElements = 0; n1anchor.status = oanchor.status;
				 */
				// n1Stamp = oStamp + 1;
				if (anchor.compareAndSet(oanchor, nanchor))
					break;
				if (BACK_OFF) {
					Object res;
					try {
						res = eaHead.tryRemove(MIN_BACKOFF_TIME * exp);
						if (res != null)
							return (E) res;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else if (oanchor.status == STABLE) {
				prev = oanchor.right.left.get();

				nanchor.setup(prev, oanchor.left, oanchor.status,
						oanchor.numElements - 1);

				if (anchor.compareAndSet(oanchor, nanchor)) {
					prev.right.compareAndSet(oanchor.right, null);
					break;
				}
				if (BACK_OFF) {
					Object res;
					try {
						res = eaHead.tryRemove(MIN_BACKOFF_TIME * exp);
						if (res != null)
							return (E) res;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else {
				// deque is not stable
				stabilize(oanchor);
			}
		}

		return oanchor.right.data;
	}

	/**
	 * This is the method that does a left push into the Deque. It takes the
	 * data as input, creates a deque node with the data and then pushes it onto
	 * the deque from left.
	 * 
	 * @param d
	 *            element
	 */
	public void addLast(E d) {
		DequeNode<E> newtop;
		AnchorType<E> oanchor;
		AnchorType<E> nanchor = new AnchorType<E>();

		newtop = new DequeNode<E>(d);

		while (true) {
			oanchor = anchor.get();

			int exp = 1;
			if (oanchor.left == null) {
				nanchor.setup(newtop, newtop, oanchor.status, 1);
				/**
				 * Any modification to anchor should be an atomic operation.
				 * Thread will retry with Elimination process if CAS fails.
				 */
				if (anchor.compareAndSet(oanchor, nanchor))
					return;
				if (BACK_OFF) {
					try {
						if (eaTail.tryAdd(d, MIN_BACKOFF_TIME * exp))
							return;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else if (oanchor.status == STABLE) {
				newtop.setRight(oanchor.left);

				nanchor.setup(oanchor.right, newtop, LPUSH,
						oanchor.numElements + 1);

				/**
				 * Any modification to anchor should be an atomic operation.
				 * Thread will retry with Elimination process if CAS fails.
				 */
				if (anchor.compareAndSet(oanchor, nanchor)) {
					this.stabilizeLeft(nanchor);
					return;
				}
				if (BACK_OFF) {
					try {
						if (eaTail.tryAdd(d, MIN_BACKOFF_TIME * exp))
							return;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else {
				// deque is not in a stable state, let's stablize it
				this.stabilize(oanchor);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public E peekLast() {
		AnchorType<E> oanchor;

		oanchor = anchor.get();
		if (oanchor.left == null)
			return null;
		return oanchor.left.data;
	}

	/**
	 * {@inheritDoc}
	 */
	public E pollLast() {
		AnchorType<E> oanchor;
		AnchorType<E> nanchor = new AnchorType<E>();

		int exp = 1;
		while (true) {
			oanchor = anchor.get();

			// deque is empty, return null
			if (oanchor.right == null)
				return null;

			if (oanchor.right == oanchor.left) {
				// deque has just one node
				nanchor.setup(null, null, oanchor.status, 0);

				// try to return the only node
				if (anchor.compareAndSet(oanchor, nanchor))
					break;
				if (BACK_OFF) {
					Object res;
					try {
						res = eaTail.tryRemove(MIN_BACKOFF_TIME * exp);
						if (res != null)
							return (E) res;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else if (oanchor.status == STABLE) {
				DequeNode<E> prev = oanchor.left.right.get();
				nanchor.setup(oanchor.right, prev, oanchor.status,
						oanchor.numElements - 1);
				if (anchor.compareAndSet(oanchor, nanchor)) {
					prev.left.compareAndSet(oanchor.left, null);
					break;
				}
				if (BACK_OFF) {
					Object res;
					try {
						res = eaTail.tryRemove(MIN_BACKOFF_TIME * exp);
						if (res != null)
							return (E) res;
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
					exp = ((exp << 1) + 1) % MAX_BACKOFF_TIME;
				}
			} else
				// deque is not stable, let's stablize it
				stabilize(oanchor);
		}
		return oanchor.left.data;

	}

	/**
	 * The base routine to stabilize the deque. It calls the StabilizeRight or
	 * StabilizeLeft based on the status flag
	 * 
	 * @param a
	 */
	private void stabilize(AnchorType<E> a) {
		if (a.status == RPUSH)
			stabilizeRight(a);
		else
			// status = LPUSH
			stabilizeLeft(a);
	}

	/**
	 * Stabilize the deque after a right push.
	 * 
	 * @param a
	 *            anchor
	 */
	private void stabilizeRight(AnchorType<E> a) {
		DequeNode<E> prev, prevnext;
		prev = a.right.left.get();
		if (a.status != RPUSH)
			return;
		prevnext = prev.right.get();
		if (prevnext != a.right) {
			if (a.status != RPUSH)
				return;
			if (!prev.right.compareAndSet(prevnext, a.right))
				return;
		}

		a.stableStatus(RPUSH);
	}

	/**
	 * Stabilize the deque after a left push.
	 * 
	 * @param a
	 *            anchor
	 */
	private void stabilizeLeft(AnchorType<E> a) {
		DequeNode<E> prev, prevnext;

		prev = a.left.right.get();
		if (a.status != LPUSH)
			return;
		prevnext = prev.left.get();
		if (prevnext != a.left) {
			if (a.status != LPUSH)
				return;
			if (!prev.left.compareAndSet(prevnext, a.left))
				return;
		}

		a.stableStatus(LPUSH);
	}

	/**
	 * {@inheritDoc}
	 */
	public E pop() {
		return removeFirst();
	}

	/**
	 * {@inheritDoc}
	 */
	public void push(E e) {
		addFirst(e);		
	}
}
