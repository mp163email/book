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

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class for Amino schedulers.
 * 
 */
public abstract class AbstractScheduler extends AbstractExecutorService
		implements Scheduler {

	/**
	 * Get work item identified by id.
	 * 
	 * @param id
	 *            id for work item
	 * @return Runnable method
	 */
	protected abstract Runnable getWork(int id);

	private volatile int waitingWorkers;
	/**
	 * lock for synchronization.
	 */
	Object newWork = new Object();

	/**
	 * Wait for new workload.
	 * 
	 * @param id
	 *            thread id
	 */
	protected void waitNewWork(int id) {
		synchronized (newWork) {
			if (!isShutdown)
				try {
					waitingWorkers++;
					newWork.wait();
					waitingWorkers--;
				} catch (InterruptedException e) {
					return;
				}
		}
	}

	/**
	 * Wakeup waiting workers when new workload arrived.
	 */
	protected void signalNewWork() {
		if (waitingWorkers > 0)
			synchronized (newWork) {
				newWork.notifyAll();
			}
	}

	private volatile int activeWorkers;
	private final ReentrantLock activeLock = new ReentrantLock();
	private final Condition termination = activeLock.newCondition();

	/**
	 * @author ganzhi
	 * 
	 */
	protected final class WorkThread extends Thread {
		private final int id;

		/**
		 * 
		 * @param id
		 *            Set identifying integer id for this work thread.
		 */
		public WorkThread(int id) {
			this.id = id;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			try {
				while (true) {
					if (isShutdownNow)
						break;

					Runnable work = getWork(id);
					if (work != null)
						work.run();
					else if (isShutdown)
						break;
					else
						waitNewWork(id);
				}
			} finally {
				try {
					activeLock.lock();
					activeWorkers--;
					termination.signal();
				} finally {
					activeLock.unlock();
				}
			}
		}
	}

	private Thread[] threads;

	/**
	 * 
	 * @param numWorkers
	 *            maximum number of worker threads to create.
	 */
	public AbstractScheduler(int numWorkers) {
		this.numWorkers = numWorkers;

		threads = new Thread[numWorkers];
		activeWorkers = 0;
		waitingWorkers = 0;
		for (int i = 0; i < numWorkers; i++) {
			threads[i] = new WorkThread(i);
		}
	}

	private int numWorkers;

	/**
	 * start to execute all workers.
	 */
	protected void startWorkers() {
		activeWorkers = numWorkers;
		for (int i = 0; i < numWorkers; i++) {
			threads[i].start();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int numWorkers() {
		return numWorkers;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTerminated() {
		return activeWorkers == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		if (isTerminated())
			return true;

		long nanos = unit.toNanos(timeout);
		try {
			activeLock.lock();
			do {
				if (nanos <= 0)
					return false;
				nanos = termination.awaitNanos(nanos);
				if (isTerminated())
					return true;
			} while (true);
		} finally {
			activeLock.unlock();
		}
	}

	private volatile boolean isShutdown = false;

	/**
	 * {@inheritDoc}
	 */
	public boolean isShutdown() {
		return isShutdown;
	}

	private void interruptWorkers() {
		for (Thread t : threads)
			t.interrupt();
	}

	/**
	 * 
	 */
	public void shutdown() {
		isShutdown = true;
		signalNewWork();
	}

	private volatile boolean isShutdownNow = false;

	/**
	 * 
	 * @return List of remaining work items that have not yet finished running.
	 */
	protected abstract List<Runnable> getOutstandingWork();

	/**
	 * {@inheritDoc}
	 */
	public List<Runnable> shutdownNow() {
		isShutdown = true;
		isShutdownNow = true;
		interruptWorkers();
		return getOutstandingWork();
	}

	private RejectedExecutionHandler handler;

	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return handler;
	}

	public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
		this.handler = handler;
	}

	/**
	 * Submit a new work item.
	 * 
	 * @param command
	 *            work item to run
	 * @throws InterruptedException 
	 */
	protected abstract void addWork(Runnable command) throws InterruptedException;

	/**
	 * {@inheritDoc}
	 */
	public void execute(Runnable command) {
		if (!isShutdown) {
			try {
				addWork(command);
			} catch (InterruptedException e) {
				handler.rejectedExecution(command, null);
			}
			signalNewWork();
		} else if (handler != null)
			handler.rejectedExecution(command, null);
	}

	private static int defaultNumberOfWorkers;

	static {
		defaultNumberOfWorkers = Runtime.getRuntime().availableProcessors();
	}

	/**
	 * 
	 * @return default number of worker threads.
	 */
	public static int defaultNumberOfWorkers() {
		return defaultNumberOfWorkers;
	}

	public static void setNumberOfWorkers(int workers) {
		defaultNumberOfWorkers = workers;
	}
}
