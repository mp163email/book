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

package org.amino.scheduler.internal;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.amino.ds.lockfree.LockFreeDeque;
import org.amino.utility.FastRandom;

/**
 * Classes for a work stealing scheduler.
 * 
 * @author blainey
 * 
 */
public class WorkStealingScheduler extends AbstractScheduler {
	private Deque<Runnable>[] workQ;
	private boolean randomStealing;

	/**
	 * 
	 */
	public WorkStealingScheduler() {
		this(defaultNumberOfWorkers());
	}

	/**
	 * 
	 * @param numWorkers
	 *            number of worker threads to create.
	 */
	public WorkStealingScheduler(int numWorkers) {
		this(numWorkers, false);
	}

	/**
	 * 
	 * @param numWorkers
	 *            number of worker threads to create.
	 * @param doRandom
	 *            randomly select thread for stealing work.
	 */
	@SuppressWarnings("unchecked")
	public WorkStealingScheduler(int numWorkers, boolean doRandom) {
		super(numWorkers);
		workQ = new Deque[numWorkers];
		for (int i = 0; i < numWorkers(); i++)
			workQ[i] = new LockFreeDeque<Runnable>();
		randomStealing = doRandom;
		startWorkers();
	}

	private int now = 0;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addWork(Runnable command) {
		workQ[now].add(command);
		now = (now + 1) % numWorkers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Runnable> getOutstandingWork() {
		List<Runnable> result = new ArrayList<Runnable>();
		int nworkers = numWorkers();
		for (int i = 0; i < nworkers; i++) {
			while (true) {
				Runnable r = workQ[i].poll();
				if (r == null)
					break;
				result.add(r);
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Runnable getWork(int id) {
		Runnable r = workQ[id].poll();
		if (r != null)
			return r;

		// Choose other queues at random to steal work
		int nworkers = numWorkers();
		for (int i = (id + 1) % nworkers; i != id; i = (i + 1) % nworkers) {
			if (randomStealing)
				r = workQ[FastRandom.nextInt(nworkers)].pollLast();

			else
				r = workQ[i].pollLast();
			if (r != null)
				return r;
		}

		return null;
	}

}

