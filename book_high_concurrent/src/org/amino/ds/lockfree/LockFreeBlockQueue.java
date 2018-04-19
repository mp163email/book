/**
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
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * This is a blocking queue based on a lock-free FIFO queue. NULL objects are
 * not allowed in the queue.
 * 
 * The implementation is according to the paper An Optimistic Approach to
 * Lock-Free FIFO Queues by Edya Ladan-Mozes and Nir Shavit
 * 
 * @author Xiao Jun Dai
 * @author Zhi Gan
 * 
 * 
 */
public class LockFreeBlockQueue<E> extends AbstractQueue<E> implements
		BlockingQueue<E> {
	private volatile Node<E> head, tail;

	@SuppressWarnings("unchecked")
	private static final AtomicReferenceFieldUpdater<LockFreeBlockQueue, Node> tailUpdater = AtomicReferenceFieldUpdater
			.newUpdater(LockFreeBlockQueue.class, Node.class, "tail");
	@SuppressWarnings("unchecked")
	private static final AtomicReferenceFieldUpdater<LockFreeBlockQueue, Node> headUpdater = AtomicReferenceFieldUpdater
			.newUpdater(LockFreeBlockQueue.class, Node.class, "head");

	/**
	 * @param cmp
	 *            expected value
	 * @param val
	 *            new value
	 * @return true if cas is successful, otherwise false
	 */
	private boolean casTail(Node<E> cmp, Node<E> val) {
		return tailUpdater.compareAndSet(this, cmp, val);
	}

	/**
	 * @param cmp
	 *            expected value
	 * @param val
	 *            new value
	 * @return true if cas is successful, otherwise false
	 */
	private boolean casHead(Node<E> cmp, Node<E> val) {
		return headUpdater.compareAndSet(this, cmp, val);
	}

	/**
	 * Internal node definition of queue.
	 * 
	 * @param <Object>
	 *            type of element in node
	 */
	private static class Node<E> {
		E value;
		Node<E> next, prev;

		/**
		 * default contructor.
		 */
		public Node() {
			value = null;
			next = prev = null;
		}

		/**
		 * @param val
		 *            deafault value
		 */
		public Node(E val) {
			value = val;
			next = prev = null;
		}

		/**
		 * @param next
		 *            default next pointer.
		 */
		public Node(Node<E> next) {
			value = null;
			prev = null;
			this.next = next;
		}

		/**
		 * @return next node
		 */
		public Node<E> getNext() {
			return prev;
		}
	}

	public LockFreeBlockQueue(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException();
		}

		_capacity = new AtomicInteger(capacity);
		_size = new AtomicInteger(0);
		init();
	}

	private Node<E> dummy;

	private void init() {
		dummy = new Node<E>();
		head = dummy;
		tail = dummy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#isEmpty()
	 */
	public boolean isEmpty() {
		return (head.value == null) && (tail.value == null);
		// or return first() == null;
	}

	/**
	 * @param tail
	 *            tail node
	 * @param head
	 *            head node
	 */
	private void fixList(Node<E> tail, Node<E> head) {
		Node<E> curNode, curNodeNext, nextNodePrev;
		curNode = tail;
		while ((head == this.head) && (curNode != head)) {
			curNodeNext = curNode.next;
			if (null == curNodeNext)
				break;
			nextNodePrev = curNodeNext.prev;
			if (nextNodePrev != curNode) {
				curNodeNext.prev = curNode;
			}
			curNode = curNodeNext;
		}
	}

	public int size() {
		return _size.get();
	}

	public boolean offer(E e) {
		if (e == null)
			throw new NullPointerException();

		int local_capacity = _capacity.get();
		while (true) {
			int local_size = _size.get();
			if (local_size >= local_capacity)
				return false;
			if (_size.compareAndSet(local_size, local_size + 1))
				break;
		}

		Node<E> tail;
		Node<E> node = new Node<E>(e);
		while (true) {
			tail = this.tail;
			node.next = tail;
			if (casTail(tail, node)) {
				// _size.incrementAndGet();
				tail.prev = node;
				notifyGet_();
				return true;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Queue#peek()
	 */
	public E peek() {
		while (true) {
			Node<E> header = this.head;
			if (header.value != null)
				return header.value;

			Node<E> tail = this.tail;

			if (header == this.head) {
				/*
				 * In our algorithm, a dummy node is a special node with a dummy
				 * value. It is created and inserted to the queue when it
				 * becomes empty as explained above. Since a dummy node does not
				 * contain a real value, it must be skipped when nodes are
				 * deleted from the queue. The steps for skipping a dummy node
				 * are similar to those of a regular dequeue, except that no
				 * value is returned. When a dequeue method identifies that the
				 * head points to a dummy node and the tail does not, as in
				 * Figure 6 Part B in the paper, it modifies the head using a
				 * CAS to point to the node pointed by the prev pointer of this
				 * dummy node. Then it can continue to dequeue nodes.
				 */
				if (tail == header) {
					return null;
				} else {
					Node<E> fstNodePrev = header.prev;
					if (null == fstNodePrev) {
						fixList(tail, header);
						continue;
					}
					casHead(header, fstNodePrev);
				}
			}
		}
	}

	public E poll() {
		Node<E> tail, head, fstNodePrev;
		E val;
		while (true) {
			head = this.head;
			tail = this.tail;
			fstNodePrev = head.prev;
			val = head.value;
			if (head == this.head) {
				if (val != null) {
					if (tail != head) { // more than 1 node
						if (null != fstNodePrev) {
							if (casHead(head, fstNodePrev)) {
								fstNodePrev.next = null;
								int sizetmp1 = _size.get();
								_size.decrementAndGet();
								int sizetmp2 = _size.get();
								if (sizetmp2 < 0) {
									System.out.println("size = " + sizetmp1
											+ ":" + sizetmp2 + " , val = "
											+ val + ", dummy = " + dummy
											+ ", head = " + head + ", tail = "
											+ tail + ", fstNodePrev = "
											+ fstNodePrev);
								}
								if (val == null) {
									System.out.println("vaule = " + val);
								}

								notifyPut_();
								return val;
							}
						} else {
							fixList(tail, head);
							continue;
						}
					} else {
						Node<E> newdummy = new Node<E>();
						newdummy.next = tail;
						newdummy.prev = null;
						if (casTail(tail, newdummy)) {
							head.prev = newdummy;
						}

						continue;
					}
				} else { // head points to dummy, Figure 6.B
					if (tail == head) {
						// If a thread is in the middle of offer() (increased
						// _size, but not inserted the actual data yet, we
						// should wait for it to complete
						if (_size.get() > 0) {
							Thread.yield();
							continue;
						}
						return null;
					} else {
						if (null == fstNodePrev) {
							fixList(tail, head);
							continue;
						}
						casHead(head, fstNodePrev);
					}
				}
			}
		}
	}

	AtomicInteger _size;
	AtomicInteger _capacity;

	private final Object putQueue_ = new Object();
	private int putQueueLen_ = 0;

	private final Object getQueue_ = new Object();
	private int getQueueLen_ = 0;

	private static long WAIT_DURATION = 1000;

	private void notifyGet_() {
		// a notification may be lost in some cases - however
		// as none of the threads wait endlessly, a waiting thread
		// will either be notified, or will eventually wakeup
		if (getQueueLen_ > 0) {
			synchronized (getQueue_) {
				getQueue_.notify();
			}
		}
	}

	private void waitGet_(long timeout) throws InterruptedException {
		synchronized (getQueue_) {
			try {
				getQueueLen_++;

				if (_size.get() <= 0) {
					getQueue_.wait(timeout);
				}
			} catch (InterruptedException ex) {
				getQueue_.notify();
				throw ex;
			} finally {
				getQueueLen_--;
			}
		}
	}

	private void notifyPut_() {
		if (putQueueLen_ > 0) {
			synchronized (putQueue_) {
				putQueue_.notify();
			}
		}
	}

	private void waitPut_(long timeout) throws InterruptedException {
		synchronized (putQueue_) {
			try {
				putQueueLen_++;
				if (_size.get() >= _capacity.get()) {
					putQueue_.wait(timeout);
				}
			} catch (InterruptedException ex) {
				putQueue_.notify();
				throw ex;
			} finally {
				putQueueLen_--;
			}
		}
	}

	public int capacity() {
		return _capacity.get();
	}

	public void expand(int additionalCapacity) {
		if (additionalCapacity <= 0)
			throw new IllegalArgumentException();

		_capacity.addAndGet(additionalCapacity);

	}

	public boolean offer(E x, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (x == null)
			throw new NullPointerException();

		if (offer(x))
			return true;

		waitPut_(unit.toMillis(timeout));

		return offer(x);

	}

	public void put(E x) throws InterruptedException {
		if (x == null) {
			throw new IllegalArgumentException();
		}

		while (true) {
			if (offer(x))
				return;
			waitPut_(WAIT_DURATION);
		}
	}

	public E put(E x, long timeoutInMillis) throws InterruptedException {
		if (x == null) {
			throw new IllegalArgumentException();
		}

		if (offer(x)) {
			return x;
		}

		waitPut_(timeoutInMillis);
		if (offer(x)) {
			return x;
		} else
			return null;

	}

	public Object put(E x, long timeoutInMillis, int maximumCapacity)
			throws InterruptedException {
		if (x == null || maximumCapacity > _capacity.get()) {
			throw new IllegalArgumentException();
		}

		if (offer(x)) {
			return x;
		}

		waitPut_(timeoutInMillis);

		if (offer(x)) {
			return x;
		} else
			return null;
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		E res = poll();
		if (res != null)
			return res;

		if (timeout <= 0)
			return null;
		else {
			waitGet_(unit.toMillis(timeout));
			return poll();
		}
	}

	public E take() throws InterruptedException {
		while (true) {
			E res = poll();
			if (res != null)
				return res;

			waitGet_(WAIT_DURATION);
		}
	}

	public void dumpQueue() {
		Node<E> curNode, curNodeNext;
		curNode = tail;
		while ((head == this.head) && (curNode != head)) {
			curNodeNext = curNode.next;
			System.out.print(curNodeNext.value + " -> ");
			curNode = curNodeNext;
		}

	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException(); // Not supported
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		int i = 0;
		do {
			E tmp = poll();
			if (tmp != null) {
				i++;
				c.add(tmp);
			} else
				return i;
		} while (true);
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		int i = 0;
		do {
			E tmp = poll();
			if (tmp != null) {
				i++;
				c.add(tmp);
			} else
				return i;
		} while (i<maxElements);
		return i;
	}

	@Override
	public int remainingCapacity() {		
		return _capacity.get()-size();
	}
}
