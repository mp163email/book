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

/**
 * This Deque implementation is based on the algorithm defined in the follwoing
 * paper: CAS-Based Lock-Free Algorithm for Shared Deques By Maged M. Michael
 * 
 * <p>
 * As the terminology between below description differs from the Deque interface
 * defined in Java 6, please translate left = last and right = first.
 * 
 * Right => First, Left => Last
 * 
 * <p>
 * The deque is a double ended queue. Insertion and deletion can happen at both
 * ends. The basic deque is maintained as a doubly linked list. An anchor data
 * structure which comprise of 2 pointers (left and right) is used to keep track
 * of the leftmost and rightmost element of the deque. A deque status flag is
 * maintained to keep track of the state of the deque.
 * 
 * <p>
 * The deque is said to be in a stable state when it is 1) empty, 2) contains a
 * single item and both the anchor pointers point to the item and 3) for all
 * nodes x in the deque, x.right.left = x unless x is the rightmost node and
 * x.left.right = x unless x is the leftmost node.
 * 
 * <p>
 * There are 4 unstable states for the deque. A deque can be unstable only if it
 * has 2 or more elements. Also, only one end of the deque can have problems at
 * any given time and the status has to show that it is unstable with the flag
 * having a value of RPUSH or LPUSH depending on the problem side (right or
 * left). Unstable states: 1) x.left.right != x, where x is the rightmost node.
 * Deque status flag = RPUSH 2) x.right.left != x, where x is the leftmost node.
 * Deque status flag = LPUSH It is acceptable for a deque to have no problems
 * but the status flag may show that it is in an unstable state. So, the deque
 * may have a status of RPUSH or LPUSH but still be coherent (the other two
 * unstable states)
 * 
 * <p>
 * Insertion or deletion from either end can only be done on a stable deque. If
 * the deque is in an unstable state it has to be stabilized first before the
 * operation can proceed. After a right insertion the deque status is changed to
 * an RPUSH atomically with the insertion. Similarly after a left insertion the
 * deque status is changed to LPUSH atomically with the insertion. To put the
 * deque back to a stable state the pointers have to be made coherent.
 * 
 * <p>
 * <b>Thread Safety:</b> <br>
 * Follow methods are NOT thread-safe:
 * <ol>
 * <li>contains(Object)</li>
 * <li>iterator()</li>
 * <li>descendingIterator()</li>
 * </ol>
 * 
 * @author raja, ganzhi
 * 
 * @param <E>
 *            type of element in the deque
 */

public class LockFreeDeque<E> extends AbstractQueue<E> implements Deque<E> {
	/**
	 * Change to true if want to use BACKOFF.
	 */
	private static final boolean BACKOFF = false;

	/**
	 * The number of sleeping time in the unit of millisecond.
	 */
	private static final int BACKOFFTIME = 20;

	/**
	 * The different states of the Deque.
	 */

	private static final int STABLE = 0, RPUSH = 1, LPUSH = 2;

	/**
	 * Shared variable which holds the pointer to the leftmost and rightmost
	 * node of the deque.
	 */

	/**
	 * This is the anchor of the deque, which contains right header, left
	 * header, status, and size information of the deque. This variable MUST be
	 * modified in an atomic way, so we used an AtomicReference here.
	 */
	private AtomicReference<AnchorType<E>> anchor = new AtomicReference<AnchorType<E>>(
			new AnchorType<E>());

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
	 * Removes all elements, clears the deque.
	 */
	public void clear() {
		AnchorType<E> old_anchor, new_anchor;

		/*
		 * This loop won't stop until the deque is set to empty successfully.
		 */
		while (true) {
			old_anchor = anchor.get();

			// return if already empty
			if (old_anchor.right == null)
				return;

			// Create a new anchor with empty left/right pointers
			new_anchor = new AnchorType<E>(null, null, STABLE, 0);

			/*
			 * anchor is an AtomicReference variable. Modification to anchor
			 * variable should be an atomic operation. If CAS fails, thread will
			 * retry until succeed.
			 */
			if (anchor.compareAndSet(old_anchor, new_anchor))
				return;

			/*
			 * if BACKOFF=true and if the CAS fails then give some other thread
			 * a chance
			 */
			if (BACKOFF) {
				try {
					Thread.sleep(BACKOFFTIME);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Contains function is not thread safe. It can only be used when no other
	 * threads are modifying this deque.
	 * 
	 * @param o
	 *            is the Object we are looking for.
	 * @throws UnsupportedOperationException
	 */
	public boolean contains(Object o) {
		E element;
		Iterator<E> itr = new DeqIterator();

		/**
		 * Iterate through the list to see if the object is present or not
		 */

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
		return anchor.get().numElements;
	}

	/**
	 * This is the method that does a right push into the Deque. It takes the
	 * data as input, creates a deque node with the data and then pushes it onto
	 * the deque from right.
	 * 
	 * @param d
	 *            element being added
	 */
	public void addFirst(E d) {
		DequeNode<E> newtop;
		AnchorType<E> old_anchor;
		/* Create a new anchor object to replace the old one */
		AnchorType<E> update_anchor = new AnchorType<E>();

		/* Create new deque node and put the data into it */
		newtop = new DequeNode<E>(d);

		/* Main insertion loop. This loop will finish until insertion succeeds */
		while (true) {
			// make a copy of the anchor variable
			old_anchor = anchor.get();

			if (old_anchor.right == null) // deque is empty
			{
				/*
				 * Create a the new anchor with both pointer pointing to the new
				 * node
				 */
				update_anchor.setup(newtop, newtop, old_anchor.status, 1);
				/*
				 * Change the old anchor variable to the new one atomically
				 */
				if (anchor.compareAndSet(old_anchor, update_anchor))
					return;

				/*
				 * If CAS fails and BACKOFF is set, give some other thread a
				 * chance
				 */
				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (old_anchor.status == STABLE) { // if anchor copy is
				// stable,
				/*
				 * insert data. make the new nodes left-pointer point to the
				 * deque's rightmost node
				 */
				newtop.setLeft(old_anchor.right);

				/*
				 * create a new anchor with the right pointer pointing to the
				 * new node and the left pointer pointing to the old anchor left
				 */
				update_anchor.setup(newtop, old_anchor.left, RPUSH,
						old_anchor.numElements + 1);
				/*
				 * Change the old anchor to the new anchor atomically
				 */
				if (anchor.compareAndSet(old_anchor, update_anchor)) {
					// At this point the deque is in an unstable state. call
					// stabilize
					stabilizeRight(update_anchor);
					return;

				}

				/*
				 * If CAS fails and BACKOFF is set, give some other thread a
				 * chance
				 */

				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				/* deque is not in a stable state */
				stabilize(old_anchor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public E peekFirst() {
		AnchorType<E> old_anchor = anchor.get();
		if (old_anchor.right == null)
			return null;
		return old_anchor.right.data;
	}

	/**
	 * This is the method to pop the Right node from the Deque.
	 * 
	 * @return head of this deque, or null if empty.
	 */
	public E pollFirst() {
		AnchorType<E> old_anchor;
		AnchorType<E> new_anchor = new AnchorType<E>();

		/*
		 * Main loop to remove the rightmost node from the deque. It will retry
		 * until succeeds.
		 */
		while (true) {
			// Get a current copy of the anchor variable
			old_anchor = anchor.get();

			// Deque is empty; return null
			if (old_anchor.right == null)
				return null;

			// Deque has just one node.
			if (old_anchor.right == old_anchor.left) {
				// Create a the new anchor, with the left and right pointer both
				// null; empty deque
				new_anchor.setup(null, null, old_anchor.status, 0);
				/*
				 * Change old anchor to new anchor atomically. There is NO ABA
				 * problem since Java GC.
				 */
				if (anchor.compareAndSet(old_anchor, new_anchor))
					break;
				/*
				 * If CAS fails and BACKOFF is set, give some other thread a
				 * chance
				 */
				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (old_anchor.status == STABLE) {
				/*
				 * Get the node left to the rightmost node
				 */
				DequeNode<E> prevNode = old_anchor.right.left.get();

				/*
				 * Setup new anchor with the right pointer pointing to the prev
				 * node and the left pointer pointing to the old anchor left
				 */
				new_anchor.setup(prevNode, old_anchor.left, old_anchor.status,
						old_anchor.numElements - 1);
				/*
				 * Change the old anchor to the new anchor atomically. There is
				 * NO ABA problem since Java GC.
				 */
				if (anchor.compareAndSet(old_anchor, new_anchor)) {
					// set the right pointer of the rightmost node to null thus
					// prevent leakage
					prevNode.right.compareAndSet(old_anchor.right, null);
					break;
				}
				/*
				 * If CAS fails and BACKOFF is set, thread yield and give other
				 * threads more chance to succeed
				 */
				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else
				// Now deque is in unstable state; stabilize it at first.
				stabilize(old_anchor);
		}// End Main Loop

		// return the data just removed
		return old_anchor.right.data;
	}

	/**
	 * This is the method that does a left push into the Deque. It takes the
	 * data as input, creates a deque node with the data and then pushes it onto
	 * the deque from left.
	 * 
	 * @param d
	 *            element to add
	 */
	public void addLast(E d) {
		DequeNode<E> newtop;
		AnchorType<E> old_anchor;
		/* Create a new anchor to replace the old one */
		AnchorType<E> update_anchor = new AnchorType<E>(); // new anchor node

		// create a new deque node with the data
		newtop = new DequeNode<E>(d);

		/* Main loop to insert data, which will exit when operation succeeds. */
		while (true) {
			old_anchor = anchor.get();

			if (old_anchor.left == null) // deque is empty
			{
				/*
				 * create new anchor with both pointers pointing to the new
				 * node.
				 */
				update_anchor.setup(newtop, newtop, old_anchor.status, 1);

				/*
				 * Change the anchor from old to new atomically. There is NO ABA
				 * problem since Java GC.
				 */
				if (anchor.compareAndSet(old_anchor, update_anchor))
					return;

				/*
				 * If CAS fails and BACKOFF is set, yield thus give other
				 * threads more chance to succeed
				 */
				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (old_anchor.status == STABLE) {
				/*
				 * make the new nodes right pointer point to the deque's
				 * leftmost node
				 */
				newtop.setRight(old_anchor.left);

				/*
				 * create a new anchor with the right pointer pointing to the
				 * rightmost node of the deque and the left pointer pointing to
				 * the new node
				 */
				update_anchor.setup(old_anchor.right, newtop, LPUSH,
						old_anchor.numElements + 1);

				/*
				 * change the old anchor to the new anchor atomically. There is
				 * NO ABA problem since Java GC.
				 */
				if (anchor.compareAndSet(old_anchor, update_anchor)) {
					/*
					 * Deque is in unstable state after the insert; stabilize
					 * the it at first
					 */
					this.stabilizeLeft(update_anchor);
					return;

				}

				/*
				 * If CAS fails and BACKOFF is set, give some other thread a
				 * chance.
				 */
				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				// deque is not in a stable state
				this.stabilize(old_anchor);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public E peekLast() {
		AnchorType<E> old_anchor;

		old_anchor = anchor.get();
		if (old_anchor.left == null)
			return null;
		return old_anchor.left.data;
	}

	/**
	 * {@inheritDoc}
	 */
	public E pollLast() {
		AnchorType<E> oldAnchor;
		AnchorType<E> newAnchor = new AnchorType<E>();

		/*
		 * Main loop to remove the leftmost node, which won't end until
		 * succeeds.
		 */
		while (true) {
			// get a copy of the anchor node
			oldAnchor = anchor.get();

			// deque is empty, return null
			if (oldAnchor.right == null)
				return null;

			// deque has just one node, empty it!
			if (oldAnchor.right == oldAnchor.left) {
				/*
				 * Setup a new anchor with both left and right pointer set to
				 * null
				 */
				newAnchor.setup(null, null, oldAnchor.status, 0);

				/*
				 * Replace anchor from old to new atomically. There is NO ABA
				 * problem since Java GC.
				 */
				if (anchor.compareAndSet(oldAnchor, newAnchor))
					break;

				/*
				 * If CAS fails and BACKOFF is set, yield to give other threads
				 * more chance to succeed
				 */
				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (oldAnchor.status == STABLE) {
				// Deque is stable so we can try to remove element from it
				DequeNode<E> prev = oldAnchor.left.right.get();

				/*
				 * create a new anchor with the right pointer pointing to the
				 * old anchor right and the left pointer pointing to prev node
				 */
				newAnchor.setup(oldAnchor.right, prev, oldAnchor.status,
						oldAnchor.numElements - 1);
				/*
				 * Change anchor from old anchor to new atomically. There is NO
				 * ABA problem since Java GC.
				 */
				if (anchor.compareAndSet(oldAnchor, newAnchor)) {
					/**
					 * set the left pointer of the leftmost node to null;
					 * prevent leakage. CAS may fail if other threads are
					 * pushing, popping, or stabilizing this deque.
					 */
					prev.left.compareAndSet(oldAnchor.left, null);
					break;
				}

				if (BACKOFF) {
					try {
						Thread.sleep(BACKOFFTIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else
				// Now deque is not stable, stablize it at first
				stabilize(oldAnchor);
		}

		return oldAnchor.left.data;

	}

	/**
	 * The base routine to stabilize the deque. It calls the StabilizeRight or
	 * StabilizeLeft based on the status flag
	 */
	private void stabilize(AnchorType<E> a) {
		if (a.status == RPUSH)
			stabilizeRight(a);
		else
			stabilizeLeft(a);
	}

	/**
	 * Stabilize the deque after a right push.
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
			/*
			 * Modification to <code>left</code> pointer should be atomic
			 * operation to avoid race condition. It doesn't matter which thread
			 * stablized the deque. If CAS fails, thread will directly return
			 * without doing anything.
			 */
			if (!prev.right.compareAndSet(prevnext, a.right))
				return;
		}

		a.stableStatus(RPUSH);
	}

	/**
	 * Stabilize the deque after a left push.
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
			/*
			 * Modification to <code>left</code> pointer should be atomic
			 * operation. It doesn't matter which thread stablized the deque. If
			 * CAS fails, thread will return without doing anything.
			 */
			if (!prev.left.compareAndSet(prevnext, a.left))
				return;
		}

		a.stableStatus(LPUSH);
	}

	/**
	 * @return element popped
	 */
	public E pop() {
		return removeFirst();
	}

	/**
	 * @param e
	 *            element pushed
	 */
	public void push(E e) {
		addFirst(e);
	}

	/**
	 * Iterator definition of deque. This iterator is NOT thread-safe
	 * 
	 */
	private class DeqIterator implements Iterator<E> {

		private DequeNode<E> cursor = anchor.get().left;

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
}
