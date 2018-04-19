package org.amino.ds.lockfree;

import java.util.AbstractQueue;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * http://www.cs.chalmers.se/~dcs/ConcurrentDataStructures/phd_chap7.pdf
 * 
 * Hakan Sundell, Philippas Tsigas Department of Computing Science Chalmers
 * Univ. of Technol. and Gteborg Univ. 412 96 Goteborg, Sweden E-mail: {phs,
 * tsigas}@cs.chalmers.se
 * */
public class HakanDeque<V> extends AbstractQueue<V> implements Deque<V> {
	static class Node<V> {
		V value;
		AtomicMarkableReference<Node<V>> prev;
		AtomicMarkableReference<Node<V>> next;

		public Node(V val) {
			value = val;
			prev = new AtomicMarkableReference<Node<V>>(null, false);
			next = new AtomicMarkableReference<Node<V>>(null, false);
		}

		void setPrev(Node<V> prevNode, boolean flag) {
			prev.set(prevNode, flag);
		}

		void setNext(Node<V> nextNode, boolean flag) {
			next.set(nextNode, flag);
		}
	}

	static class BoolArrayThreadLocal extends ThreadLocal<boolean[]> {
		@Override
		protected boolean[] initialValue() {
			return new boolean[1];
		}
	}

	final Node<V> head, tail;
	final BoolArrayThreadLocal flag1;

	public HakanDeque() {
		head = new Node<V>(null);
		tail = new Node<V>(null);
		head.setNext(tail, false);
		tail.setPrev(head, false);
		flag1 = new BoolArrayThreadLocal();
	}

	public void addFirst(V value) {
		Node<V> node = new Node<V>(value);
		Node<V> prev = head;
		Node<V> next = prev.next.getReference();
		while (true) {
			Node<V> res = prev.next.getReference();
			if (res != next) {
				next = res;
				continue;
			}
			node.setPrev(prev, false);
			node.setNext(next, false);

			if (prev.next.compareAndSet(next, node, false, false)) {
				break;
			}
		}
		pushCommon(node, next);
	}

	public void addLast(V value) {
		Node<V> node = new Node<V>(value);
		Node<V> next = tail;
		Node<V> prev = next.prev.getReference();
		boolean[] markHolder = flag1.get();
		while (true) {
			Node<V> res = prev.next.get(markHolder);
			if (res != next || markHolder[0] != false) {
				prev = helpInsert(prev, next);
				continue;
			}
			node.setPrev(prev, false);
			node.setNext(next, false);
			if (prev.next.compareAndSet(next, node, false, false)) {
				break;
			}
		}
		pushCommon(node, next);
	}

	BoolArrayThreadLocal hiflag1 = new BoolArrayThreadLocal();
	BoolArrayThreadLocal hiflag2 = new BoolArrayThreadLocal();

	/**
	 * This function will automatically update node.prev to prev.
	 * 
	 * @param prev
	 * @param node
	 * @return
	 */
	private Node<V> helpInsert(Node<V> prev, Node<V> node) {
		boolean lastlinkflag = true;
		boolean[] link1flag = hiflag1.get();
		boolean[] prev2flag = hiflag2.get();
		while (true) {
			Node<V> prev2 = prev.next.get(prev2flag);

			// find the 1st prev which hasn't been deleted yet
			if (prev2flag[0]) {
				if (lastlinkflag == false) {
					helpDelete(prev);
					lastlinkflag = true;
				}

				prev = prev.prev.getReference();
				continue;
			}
			Node<V> link1 = node.prev.get(link1flag);
			if (node.next.isMarked()) {
				/*
				 * node is deleted already. It's not meaningful to update its
				 * prev pointer
				 */
				break;
			}
			if (prev2 != node) {
				//TODO:annotate these codes
//				if (prev2 == tail) {
//					prev = prev2;
//					break;
//				}
				prev = prev2;
				lastlinkflag = false;
				continue;
			}
			if (node.prev.compareAndSet(link1, prev, link1flag[0], false)) {
				if (prev.prev.isMarked()) 
					continue;
				break;
			}
		}
//		if (i > 10)
//			System.out.println("Count: " + i);
		return prev;
	}

	/**
	 * Dump the whole deque for debug purpose
	 */
	private void dumpDeque() {
		System.err.println("Head: " + head);
		Node<V> a = head.next.getReference();
		while (a != tail) {
			System.err.println(a);
			a = a.next.getReference();
		}
	}

	BoolArrayThreadLocal pcflag1 = new BoolArrayThreadLocal();

	/**
	 * Try to set prev pointer of next to make it points to node
	 * 
	 * @param node
	 * @param next
	 */
	private void pushCommon(Node<V> node, Node<V> next) {
		boolean[] link1flag = pcflag1.get();
		boolean[] markHolder1 = link1flag;
		while (true) {
			Node<V> link1 = next.prev.get(link1flag);
			if (link1flag[0])
				break;
			Node<V> res1 = node.next.get(markHolder1);
			if (res1 != next || markHolder1[0]) {
				break;
			}
			if (next.prev.compareAndSet(link1, node, false, false)) {
				if (node.prev.isMarked()) {
					helpInsert(node, next);
				}

				break;
			}
		}
	}

	public V pollFirst() {
		V value;
		Node<V> prev = head;
		boolean[] link1flag = flag1.get();
		Node<V> node;
		while (true) {
			node = prev.next.getReference();
			if (node == tail) {
				return null;
			}
			Node<V> link1 = node.next.get(link1flag);
			if (link1flag[0]) {
				helpDelete(node);
				continue;
			}
			if (node.next.compareAndSet(link1, link1, link1flag[0], true)) {
				helpDelete(node);
				Node<V> next = node.next.getReference();
				helpInsert(prev, next);
				value = node.value;
				break;
			}
		}
		removeCrossReference(node);
		return value;
	}

	public V pollLast() {
		V value;
		Node<V> next = tail;
		Node<V> node = next.prev.getReference();
		boolean[] markHolder = flag1.get();
		while (true) {
			Node<V> res = node.next.get(markHolder);
			if (res != next || markHolder[0]) {
				node = helpInsert(node, next);
				continue;
			}
			if (node == head) {
				return null;
			}

			if (node.next.compareAndSet(next, next, false, true)) {
				helpDelete(node);
				Node<V> prev = node.prev.getReference();
				helpInsert(prev, next);
				value = node.value;
				break;
			}
		}
		removeCrossReference(node);
		return value;
	}

	private void removeCrossReference(Node<V> node) {
		while (true) {
			Node<V> prev = node.prev.getReference();
			if (prev.next.isMarked()) {
				Node<V> prev2 = prev.prev.getReference();
				node.setPrev(prev2, true);
				continue;
			}
			Node<V> next = node.next.getReference();
			if (next.next.isMarked()) {
				Node<V> next2 = next.next.getReference();
				node.setNext(next2, true);
				continue;
			}
			break;
		}
	}

	BoolArrayThreadLocal hdflag1 = new BoolArrayThreadLocal();

	/**
	 * Find the unmarked prev and next neighbor of node. And set prev.next point
	 * to next.
	 * 
	 * @param deling
	 */
	private void helpDelete(Node<V> deling) {
		boolean[] link1flag = hdflag1.get();

		// Set deletion mark of prev pointer
		while (true) {
			Node<V> link1 = deling.prev.get(link1flag);
			if (link1flag[0]
					|| deling.prev.compareAndSet(link1, link1, false, true))
				break;
		}

		// Now begin to connect prev to next
		boolean lastlinkflag = true;
		Node<V> prev = deling.prev.getReference();
		Node<V> next = deling.next.getReference();
		boolean[] prev2flag = link1flag;
		while (true) {
			if (prev == next)
				break;
			
			// Recursive to find the 1st next which hasn't been deleted
			if (next.next.isMarked()) {
				next = next.next.getReference();
				continue;
			}

			// Iterate to find the 1st prev which hasn't been deleted
			Node<V> prev2 = prev.next.get(prev2flag);
			if (prev2flag[0]) {
				if (lastlinkflag == false) {//TODO:has problem,should stop after recursive helpDelete return
					/*
					 * We only call helpDelete() again when we found the
					 * unmarked prev
					 */
					helpDelete(prev);
					//TODO:annotate these code
					//lastlinkflag = true;
					return;
				}
				prev = prev.prev.getReference();
				continue;
			}
			/*
			 * Found the unmarked prev which is closest to deleting node. If its
			 * next neighbor is not to-be-deleted "node", we "continue" so
			 * helpDelete() will be called recursively.
			 */
			if (prev2 != deling) {
				lastlinkflag = false;
				prev = prev2;
				continue;
			}
			if (prev.next.compareAndSet(deling, next, false, false))
				break;
		}
	}

	public V removeFirst() {
		V result = pollFirst();
		if (result == null)
			throw new NoSuchElementException();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public V removeLast() {
		V result = pollLast();
		if (result == null)
			throw new NoSuchElementException();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeFirstOccurrence(Object o) {
		//throw new UnsupportedOperationException();
		if(o == null)
			throw new NullPointerException();
		
		boolean[] markHolder = flag1.get();
		Node<V> prev = head;
		Node<V> next = prev.next.getReference();
		while(true){
			if(next == tail)
				return false;
			
			Node<V> next2 = next.next.get(markHolder);
			if(markHolder[0] || !next.value.equals(o)){
				next = next2;
				continue;
			}
			
			if(next.next.compareAndSet(next2, next2, false, true)){
				helpDelete(next);
				removeCrossReference(next);
				return true;
			}
		}
	}

	/**
	 * @return element popped
	 */
	public V pop() {
		return removeFirst();
	}

	/**
	 * @param e
	 *            element pushed
	 */
	public void push(V e) {
		addFirst(e);
	}

	/**
	 * Inserts element e at the tail of this deque. Preferable to
	 * <code>addLast(E)</code>.
	 * 
	 * @param e
	 *            element to insert
	 * @return true if the element was successfully added, false otherwise.
	 */
	public boolean offer(V e) {
		return offerLast(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean offerFirst(V e) {
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
	public boolean offerLast(V e) {
		try {
			addLast(e);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	/**
	 * Returns and removes first (head) element of this deque, or null if this
	 * deque is empty.
	 * 
	 * @return head of this deque, or null if empty.
	 */
	public V poll() {
		return pollFirst();
	}

	@Override
	public Iterator<V> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		int _size = 0;
		Node<V> p = head;
		while (true) {
			p = p.next.getReference();
			if (p == tail)
				break;
			_size++;
		}
		return _size;
	}

	@Override
	public Iterator<V> descendingIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public V getFirst() {
		final V res = peekFirst();
		if (res == null)
			throw new NoSuchElementException();
		return res;
	}

	@Override
	public V getLast() {
		final V res = peekLast();
		if (res == null)
			throw new NoSuchElementException();
		return res;
	}

	@Override
	public V peekFirst() {
		Node<V> p = head;
		while (true) {
			p = p.next.getReference();
			if (p == tail)
				break;
			if (p.next.isMarked())
				continue;
			return p.value;
		}
		return null;
	}

	@Override
	public V peekLast() {
		Node<V> p = tail;
		while (true) {
			Node<V> prev = p.prev.getReference();
			while (true) {
				final Node<V> prevnext = prev.next.getReference();
				if (prevnext == p || prevnext == tail)
					break;
				prev = prevnext;
			}
			p = prev;
			if (p.next.isMarked())
				continue;
			return p.value;
		}
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		//throw new UnsupportedOperationException();
		if(o == null)
			throw new NullPointerException();
		
		boolean[] markHolder = flag1.get();
		Node<V> next = tail;
		Node<V> prev = next.prev.getReference();
		while(true){
			Node<V>	prevnext = prev.next.get(markHolder);
			if(prevnext != next || markHolder[0] != false){
				prev = helpInsert(prev, next);
				continue;
			}
			
			if(prev == head)
				return false;
			
			if(markHolder[0] || !prev.value.equals(o)){
				next = prev;
				prev = prev.prev.getReference();
				continue;
			}
			
			if(prev.next.compareAndSet(next, next, false, true)){
				helpDelete(prev);
				removeCrossReference(prev);
				return true;
			}
		}
	}

	@Override
	public V peek() {
		return peekFirst();
	}
}
