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
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * This is an implementation of a lock-free linked list data structure. The
 * implementation is according to the paper <a
 * href="http://www.research.ibm.com/people/m/michael/spaa-2002.pdf"> High
 * Performance Dynamic Lock-Free Hash Tables and List-Based Sets</a> by Maged M.
 * Michael, 2002. To gain a complete understanding of this data structure,
 * please first read this paper, available at:
 * http://www.research.ibm.com/people/m/michael/spaa-2002.pdf
 * <p>
 * This lock-free linked list is an unbounded thread-safe linked list. A
 * <tt>LockFreeList</tt> is an appropriate choice when many threads will share
 * access to a common collection. This list does not permit <tt>null</tt>
 * elements.
 * <p>
 * This is a lock-free implementation intended for highly scalable add, remove
 * and contains which is thread safe. All method related to index is not
 * implemented. Add() will add the element to the head of the list which is
 * different with the normal list.
 * 
 * @author Xiao Jun Dai
 * 
 * @param <E>
 *            the type of elements held in this collection
 * 
 */
public class LockFreeList<E> implements List<E> {

	/**
	 * internal node definition. A Entry stands for one node in the list. This
	 * class is used to initialize a new node and inserted into the list.
	 * 
	 * It is protected because LockFreeList and LockFreeOrderedList could access
	 * field of Entry directly instead of use an access method. It could reduce
	 * the overhead of function call since access function is called frequently.
	 * These fields are used internally only
	 * 
	 * @param <E>
	 *            type of element on node
	 */
	protected static class Entry<E> {
		/**
		 * element in the entry.
		 */
		E element;
		/**
		 * point to next entry in the list. According to the algorithm, next
		 * pointer of node should be compare and swap in order to make status of
		 * list consistent.
		 */
		AtomicMarkableReference<Entry<E>> next;

		/**
		 * @param element
		 *            default value of element
		 */
		public Entry(E e) {
			element = e;
			next = null;
		}

		public Entry(E e, AtomicMarkableReference<Entry<E>> n) {
			element = e;
			next = n;
		}
	}

	/**
	 * hold state between two function calls. It take place of thread local
	 * variable because the performance of ThreadLocal is not good.
	 * 
	 * ListStateHolder is also used by LockFreeOrderedList so it is protected.
	 * 
	 * @param <E>
	 *            type of element on node
	 */
	protected static class ListStateHolder<E> {
		/*
		 * all variables in this class could be accessed by outer class for
		 * performance issue. It could reduce the overhead of get/set method.
		 */

		/**
		 * Found the node with the expected element.
		 */
		boolean found;
		/**
		 * Pointer to the previous node.
		 */
		AtomicMarkableReference<Entry<E>> prev;
		/**
		 * Pointer to the current node.
		 */
		Entry<E> cur;
		/**
		 * Pointer to the next node.
		 */
		Entry<E> next;

		public ListStateHolder() {
			found = false;
			prev = null;
			cur = null;
			next = null;
		}

		public void casPrev() {
			prev.compareAndSet(cur, next, false, false);
		}

		public boolean markRemoved() {
			return cur.next.compareAndSet(next, next, false, true);
		}
	}

	/**
	 * iterator definition of list. Iterator definition of list. This iterator
	 * is not thread safe. The action of method of iterator is not specified if
	 * other. It is only used for debug. iterator use LockFreeList.head to
	 * initialize which is not static. Could not declare to static.
	 * 
	 */
	private class ListItr implements Iterator<E> {
		/**
		 * Pointer to the previous node.
		 */
		AtomicMarkableReference<Entry<E>> prev;
		/**
		 * Pointer to the current node.
		 */
		Entry<E> cur;
		/**
		 * Pointer to the next node.
		 */
		Entry<E> next;

		public ListItr() {
			prev = head;
			cur = head.getReference();
			next = null;
		}

		/**
		 * move iterator one node forward and return the value of current node.
		 * Iterator points to the dummy node of list. The actual first node is
		 * the next node of dummy node.
		 * 
		 * @return the value of current node
		 */
		private E advance() {
			/* at the end of list */
			if (null == cur) {
				return null;
			}

			try_again: while (true) {
//				System.out.println("cur: " + cur);
				E curItem = cur.element;
				next = cur.next.getReference();
				
				if(next == null){
					cur = null;
					return curItem;
				}

				/*
				 * If the node is marked, it means logically removed. Find
				 * routine will help to remove it from the list physically
				 */
				if (cur.next.isMarked()) {
					/*
					 * Every thread tries to help deletion of certain elements.
					 * If CAS fails, it means other thread succeeds and physical
					 * deletion is completed. Now the value of prev is not
					 * correct and need to retry from the head. No ABA problem
					 * here. in garbage-collected language such Java, ABA does
					 * not occur.
					 */
					prev.compareAndSet(cur, next, false, false);

					cur = next;
					continue try_again;
				} else {
					prev = cur.next;
					cur = next;
				}
				return curItem;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasNext() {
			return null != cur;
		}

		/**
		 * {@inheritDoc}
		 */
		public E next() {
			E result = advance();

			if (null == result)
				throw new NoSuchElementException();
			else {
				return result;
			}
		}

		/**
		 * Iterator is just for debugging. It is a read-only iterator.
		 * 
		 * {@inheritDoc}
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Pointer to header node, initialized to a dummy node. The first actual
	 * node is at head.getNext().
	 */
	protected AtomicMarkableReference<Entry<E>> head;

	/**
	 * Constructs an empty list.
	 */
	public LockFreeList() {
		head = new AtomicMarkableReference<Entry<E>>(null, false);
	}

	/**
	 * Adds the specified element to the head of this list.
	 * 
	 * NOTICE: add the element to the head of the list which is different with
	 * the normal list.
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
		/* Input verification */
		if (null == e)
			throw new NullPointerException();

		/* Create a new node and put e on it */
		final Entry<E> newNode = new Entry<E>(e,
				new AtomicMarkableReference<Entry<E>>(null, false));

		/*
		 * loop will terminate when CAS success, otherwise retry infinitely.
		 */
		while (true) {
			/* Find right position */
			Entry<E> cur = head.getReference();

			/*
			 * Set node.next = current node. AtomicMarkableReference is need
			 * here. Because deleting a node is made by two steps. First step,
			 * mark the node is logically deleted (use a mark in marked
			 * references). Second step, remove the node from the list by change
			 * the next pointer of previous node. Otherwise, it would be wrong
			 * if two threads remove the same node in the list.
			 */
			newNode.next.set(cur, false);

			/*
			 * Add adding node to the specified position. CAS must be used to
			 * avoid race condition. No ABA problem here. in garbage-collected
			 * language such Java, ABA does not occur.
			 */
			if (head.compareAndSet(cur, newNode, false, false)) {
				return true;
			}
			/* Retry if CAS is not success */
		}
	}

	/**
	 * Removes all of the elements from this list (optional operation). This
	 * list will be empty after this call returns (unless it throws an
	 * exception).
	 * 
	 * <p>
	 * Thread Safe
	 */
	public void clear() {
		head.set(null, false);
	}

	/**
	 * Returns true if this list contains the specified element. More formally,
	 * returns true if and only if this list contains at least one element e
	 * such that o.equals(e).
	 * 
	 * <p>
	 * Thread Safe
	 * 
	 * @param o
	 *            element whose presence in this list is to be tested.
	 * @return true if this list contains the specified element.
	 */
	public boolean contains(Object o) {
		/* Input verification */
		if (null == o)
			throw new NullPointerException();

		ListStateHolder<E> holder = new ListStateHolder<E>();
		return findByObject(o, head, holder).found;
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
		 * Purpose of this loop is retry infinite if CAS operation fails. It
		 * will terminate when reach the end of list or find the expected
		 * element. continue statement will skip two while loop and is suitable
		 * to use a label try_again
		 */
		try_again: while (true) {
			/*
			 * Search starts from the head of list. so prev pointer the set to
			 * list head which is a dummy node. cur pointer is set to the first
			 * actual node in the list. Maybe it is null.
			 */
			prev = start;
			cur = prev.getReference();

			/*
			 * Purpose of this loop is find the expected element in the list. It
			 * will terminate when reach the end of list or find the expected
			 * element.
			 */
			while (true) {
				/*
				 * the list and empty or reach the end of list. store the
				 * previous, current and next pointer to stateholder which is
				 * used by remove and insert
				 */
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
				Object cKey = cur.element;

				/*
				 * If the node is marked, it means logically removed. Find
				 * routine will help to remove it from the list physically
				 */
				if (nextEntryRef.isMarked()) {
					/*
					 * In lock-free algorithm, threads will help to finish each
					 * others' work. find() will help to finish remove()'s work
					 * if thread executing remove() is dead or stuck. This CAS
					 * remove the node from the list. by set the next pointer of
					 * previous node to next node.
					 */
					if (!prev.compareAndSet(cur, nextEntry, false, false)) {
						/*
						 * If CAS fails, it means other thread succeeds and
						 * physical removal is completed. Now the value of prev
						 * is not correct and need to retry from the head. No
						 * ABA problem here. in garbage-collected language such
						 * Java, ABA does not occur.
						 */
						continue try_again;
					}
				} else {
					/* stop when found the same object */
					if (cKey != o && !cKey.equals(o)) {
						/* make prev pointer move one node forward */
						prev = nextEntryRef;
					} else {
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
					}
				}

				/* move forward */
				cur = nextEntry;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Thread Safe
	 */
	public boolean isEmpty() {
		return null == head.getReference();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Thread Safe
	 */
	public boolean remove(Object o) {
		/* Input verification */
		if (null == o)
			throw new NullPointerException();

		return remove(o, head);
	}

	/**
	 * Remove element. The search starts from parameter <code>start</code>
	 * 
	 * <p>
	 * Thread Safe
	 * 
	 * @param o
	 *            object
	 * @param start
	 *            start node
	 * @return true if remove successful, otherwise false
	 */
	private boolean remove(Object o, AtomicMarkableReference<Entry<E>> start) {
		/*
		 * hold state between two function calls. It take place of thread local
		 * variable because the performance of thread local variable is not
		 * good. It will use ThreadLocal when it has better performance.
		 */
		ListStateHolder<E> holder = new ListStateHolder<E>();
		/*
		 * The purpose of loop is find the expected element first and remove it.
		 * If CAS in the loop fails, retry again. If Loop will terminate when
		 * didn't find the expected element (return false) or remove the
		 * expected element successfully (return true).
		 */
		while (true) {
			/*
			 * Find the position of object o and keep the value of current and
			 * previous pointers and other status in holder
			 */
			findByObject(o, start, holder);
			/*
			 * holder.found record whether the expected element is found in
			 * previous find(). If not found, remove fails, return false.
			 * otherwise, remove the found object.
			 */
			if (!holder.found) {
				return false;
			}

			/*
			 * Remove a node from the list is divided into two steps: logical
			 * removal and physical removal. Logical removal means compare and
			 * swap the mark field in the node.next which is
			 * AtomicMarkableReference. It shows that some thread is about to
			 * remove the node from the list soon but haven't start. insert()
			 * and remove() will bypass this node in order to not get a node
			 * will be removed soon. Physical removal means actually remove the
			 * node from the list by point the next pointer of previous node to
			 * next node by compare and swap. It is the same as normal remove of
			 * list. Set the flag to indicate logical deletion. CAS must be used
			 * to avoid race condition, which might cause deletion of irrelevant
			 * nodes. Thread will retry if CAS fails. No ABA problem here. in
			 * garbage-collected language such Java, ABA does not occur.
			 */

			if (!holder.markRemoved()) {
				continue;
			}
			/*
			 * This CAS remove the node from the list. by set the next pointer
			 * of previous node to next node. No ABA problem here. in
			 * garbage-collected language such Java, ABA does not occur. Thread
			 * will not retry if CAS fails because other threads will remove
			 * this node later. Nothing needs to be done here because a
			 * subsequent "find" operation will try to physically delete the
			 * entry from the list.
			 */
			/* TODO can find() be removed? because other threads will do the job */
			holder.casPrev();
			return true;
		}
	}

	/*
	 * Following operations are not thread safe. They are used when debugging.
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Not Thread Safe
	 */
	public Iterator<E> iterator() {
		return new ListItr();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Not Thread Safe
	 */
	public int size() {
		int i = 0;
		for (Iterator<E> iter = iterator(); iter.hasNext(); iter.next()) {
			++i;
		}
		return i;
	}

	/*
	 * Unsupported Operations. This is a lock-free implementation intended for
	 * highly scalable add(), remove() and contains() which is thread safe. All
	 * operations related to index cannot be thread-safe. They are not supported
	 * in this class.
	 */

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E get(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
}
