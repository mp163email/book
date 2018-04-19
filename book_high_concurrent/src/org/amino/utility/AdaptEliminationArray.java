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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * This class implements similar functionality of {@link EliminationArray}. And
 * this class try to get some intelligence by adjusting size of elimination
 * array automatically.
 * 
 * If elimination fails frequently for two reasons, we will adjust size of
 * elimination array:
 * <ol>
 * <li>When finding match operation, frequently got empty slot from elimination
 * array</li>
 * <li>CAS operation frequently fails</li>
 * </ol>
 * 
 * For the 1st reason, we should decrement size of elimination array. For the
 * 2nd reason, we should enlarge elimination array. And this is the main logic
 * of this class.
 * 
 * FIXME: Now this class only count succ and fail, we should identify different
 * reasons of failure
 * 
 * @author Zhi Gan (ganzhi@gmail.com)
 */
public class AdaptEliminationArray implements IEliminationArray {
	/**
	 * Lower bound of size of both the two arrays.
	 */
	private static final int MINIMAL_EA_SIZE = 4;

	/**
	 * This number specify a threshold between two reasons of failure. Unless
	 * the difference between two kinds of failure is bigger than 30%, size of
	 * elimination array won't be changed.
	 */
	private static final float CHANGE_THRESHOLD = 1.3f;

	/**
	 * Failure is caused by getting an empty slot.
	 */
	private AtomicInteger failEmpty;

	/**
	 * Failure is caused by CAS failure.
	 */
	private AtomicInteger failCas;

	private AtomicReferenceArray addList;
	private AtomicReferenceArray removLlist;
	private volatile int arraySize;
	private final int lookahead;

	private static final Object TOMB_STONE = new Object();
	/**
	 * Mark the element in the array is already removed.
	 */
	private static final Object REMOVED = new Object();

	/**
	 * dump for debug.
	 */
	public void dump() {
		System.out.println("fail_empty: " + failEmpty + "fail_cas: " + failCas);
	}

	/**
	 * Create elimination array with specified size.
	 * 
	 * @param arraySize
	 *            the average size of internal array. Size of internal array
	 *            will vary between 1 and 2*arraySize
	 */
	public AdaptEliminationArray(int arraySize) {
		this.arraySize = arraySize;
		if (arraySize < MINIMAL_EA_SIZE)
			lookahead = arraySize;
		else
			lookahead = MINIMAL_EA_SIZE;
		this.addList = new AtomicReferenceArray(arraySize * 2);
		this.removLlist = new AtomicReferenceArray(arraySize * 2);

		failCas = new AtomicInteger(0);
		failEmpty = new AtomicInteger(0);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean tryAdd(Object obj, int backOff) throws InterruptedException {
		adjustArraySize();

		int start = FastRandom.rand();

		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;
			Object removeObj = removLlist.get(index);
			if (removeObj == TOMB_STONE) {
				// if some thread is waiting for removal, let's feed it with
				// added object and return success.
				if (removLlist.compareAndSet(index, TOMB_STONE, obj)) {
					return true;
				}
			} else if (removeObj == null)
				failEmpty.incrementAndGet();
		}

		// let's try to put adding object to the buffer and waiting for
		// removal threads
		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;
			Object oldAdd = addList.get(index);
			if (oldAdd == null) {
				if (addList.compareAndSet(index, null, obj)) {
					Thread.sleep(backOff);

					// now we check to see if added object is touched by
					// removing threads
					while (true) {
						Object newAdd = addList.get(index);

						if (newAdd == obj) {
							if (addList.compareAndSet(index, obj, null)) {
								return false;
							}
						} else {
							assert newAdd == REMOVED;
							addList.set(index, null);
							return true;
						}
					}
				}
			} else {
				failCas.incrementAndGet();
			}
		}

		Thread.sleep(backOff);
		return false;
	}

	private void adjustArraySize() {
		final int cycle = 200;
		int fCas = failCas.get();
		if (fCas > cycle) {
			int fEmpty = failEmpty.get();

			if (fEmpty > cycle) {

				int tmp = arraySize;
				if (fEmpty > (int) fCas * CHANGE_THRESHOLD) {
					tmp /= 2;
					if (tmp == 0)
						tmp = 1;

					failCas.set(0);
					failEmpty.set(0);
					arraySize = tmp;
				}

				if (fCas > (int) fCas * CHANGE_THRESHOLD) {
					tmp *= 2;
					if (tmp > addList.length())
						tmp = addList.length();

					failCas.set(0);
					failEmpty.set(0);
					arraySize = tmp;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object tryRemove(int backOff) throws InterruptedException {
		int start = FastRandom.rand();
		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;

			// Let's try to get some adding objects
			Object objAdd = addList.get(index);
			if (objAdd == null) {
				failEmpty.incrementAndGet();
			} else if (objAdd != REMOVED
					&& addList.compareAndSet(index, objAdd, REMOVED)) {
				return objAdd;
			}
		}

		// No match is found by looking at adding array. Let's wait in removing
		// array
		for (int i = 0; i < lookahead; i++) {
			int index = (start + i) % arraySize;
			Object oldRemove = removLlist.get(index);
			if (oldRemove == null) {
				if (removLlist.compareAndSet(index, null, TOMB_STONE)) {
					Thread.sleep(backOff);
					while (true) {
						Object newRemove = removLlist.get(index);
						if (newRemove != TOMB_STONE) {
							removLlist.set(index, null);
							return newRemove;
						} else {
							if (removLlist.compareAndSet(index, TOMB_STONE,
									null)) {
								return null;
							}
						}
					}
				}
			} else {
				failCas.incrementAndGet();
			}
		}

		Thread.sleep(backOff);
		return null;
	}
}
