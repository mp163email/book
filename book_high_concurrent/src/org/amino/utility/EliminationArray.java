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

package org.amino.utility;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A global elimination array class for several data structures. It can be used
 * to reducing number of modification to central data structure. The idea comes
 * from following observation:
 * 
 * <blockquote>If two threads execute push() or pop() operation on a stack,
 * there is no need to modify the stack at all. We can simply transfer object
 * from push() to the pop() and both operations succeed.</blockquote>
 * 
 * Two arrays are created to store two type of operations, which are inversion
 * of each other. It can be used to stack, deque, and even list. The algorithm
 * comes from following paper, but not exactly the same. <p/>
 * 
 * <pre>
 * A Scalable Lock-free Stack Algorithm
 * Danny Hendler                Nir Shavit            Lena Yerushalmi
 * School of Computer Science Tel-Aviv University &amp; School of Computer Science
 *  Tel-Aviv University     Sun Microsystems           Tel-Aviv University
 *  Tel Aviv, Israel 69978      Laboratories          Tel Aviv, Israel 69978
 *  hendlerd@post.tau.ac.il    shanir@sun.com          lenay@post.tau.ac.il
 * </pre>
 * 
 * 
 * @author Zhi Gan (ganzhi@gmail.com)
 */
public class EliminationArray implements IEliminationArray {
	/**
	 * In debug mode, we will statistic number of succ and fail elimination.
	 */
	private static final boolean DEBUG = false;
	private AtomicInteger succ, fail;

	/**
	 * Adding threads will put adding objects into this list and wait for match.
	 */
	@SuppressWarnings("unchecked")
	private AtomicReferenceArray addLlist;

	/**
	 * Removing threads will put {@link TOMB_STONE} into this list and wait for.
	 * match
	 */
	@SuppressWarnings("unchecked")
	private AtomicReferenceArray removeList;
	private int arraySize;

	/**
	 * Upper bound that a thread will try to find a match or wait for match.
	 */
	private final int lookahead;

	/**
	 * TOMB_STONE will be put into removing array by removing threads, which
	 * means a removing thread is waiting in that slot.
	 */
	private static final Object TOMB_STONE = new Object();

	/**
	 * REMOVED will be put into adding array by removing threads, which means a
	 * match has been found by a removing thread. Both adding thread and
	 * removing thread succeed.
	 */
	private static final Object REMOVED = new Object();

	/**
	 * dump for debug.
	 */
	public void dump() {
		if (succ != null && fail != null)
			System.out.println("" + succ.get() + " " + fail.get());
	}

	/**
	 * Create an EliminationArray object with specified size.
	 * 
	 * @param arraySize
	 *            Size of internal array
	 */
	public EliminationArray(int arraySize) {
		this.arraySize = arraySize;
		lookahead = 4;
		this.addLlist = new AtomicReferenceArray(arraySize);
		this.removeList = new AtomicReferenceArray(arraySize);

		if (DEBUG) {
			this.succ = new AtomicInteger(0);
			this.fail = new AtomicInteger(0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean tryAdd(Object obj, int backOff) throws InterruptedException {
		int start = FastRandom.rand();

		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;
			Object removeObj = removeList.get(index);
			// if some thread is waiting for removal,
			// let's feed it with
			// added object and return success.
			if (removeObj == TOMB_STONE
					&& removeList.compareAndSet(index, TOMB_STONE, obj)
					&& DEBUG) {
				succ.incrementAndGet();
				return true;
			}
		}

		// let's try to put adding object to the buffer and waiting for
		// removal threads
		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;
			Object oldAdd = addLlist.get(index);
			if (oldAdd == null && addLlist.compareAndSet(index, null, obj)) {
				Thread.sleep(backOff);

				// now we check to see if added object is touched by
				// removing threads
				while (true) {
					Object newAdd = addLlist.get(index);

					if (newAdd == obj) {
						if (addLlist.compareAndSet(index, obj, null)) {
							// No removing thread touched us, return
							// failure
							if (DEBUG)
								fail.incrementAndGet();

							return false;
						} else {
							assert newAdd == REMOVED;
							if (DEBUG)
								succ.incrementAndGet();
							/*
							 * current value should be REMOVED. change REMOVED
							 * to be null means this position is available
							 * again..
							 */
							addLlist.set(index, null);
							return true;
						}

					} else {
						assert newAdd == REMOVED;
						if (DEBUG)
							succ.incrementAndGet();
						/*
						 * current value should be REMOVED. change REMOVED to be
						 * null means this position is available again..
						 */
						addLlist.set(index, null);
						return true;
					}
				}
			}
		}

		Thread.sleep(backOff);
		if (DEBUG)
			fail.incrementAndGet();
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object tryRemove(int backOff) throws InterruptedException {
		int start = FastRandom.rand();
		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;

			// Let's try to get some adding objects
			Object objAdd = addLlist.get(index);
			if (objAdd != null && objAdd != REMOVED
					&& addLlist.compareAndSet(index, objAdd, REMOVED)) {
				if (DEBUG)
					succ.incrementAndGet();
				return objAdd;
			}
		}

		// No match is found by looking at adding array. Let's wait in removing
		// array
		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;
			Object oldRemove = removeList.get(index);
			if (oldRemove == null
					&& removeList.compareAndSet(index, null, TOMB_STONE)) {
				Thread.sleep(backOff);
				while (true) {
					Object newRemove = removeList.get(index);
					if (newRemove != TOMB_STONE) {
						if (DEBUG)
							succ.incrementAndGet();

						removeList.set(index, null);
						return newRemove;
					} else {
						if (removeList.compareAndSet(index, TOMB_STONE, null)) {
							if (DEBUG)
								fail.incrementAndGet();
							return null;
						}
					}
				}
			}
		}

		Thread.sleep(backOff);
		if (DEBUG)
			fail.incrementAndGet();
		return null;
	}
}
