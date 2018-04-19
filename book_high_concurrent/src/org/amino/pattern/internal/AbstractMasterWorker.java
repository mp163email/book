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

package org.amino.pattern.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @param <S>
 *            input type.
 * @param <T>
 *            result type.
 */
abstract class AbstractMasterWorker<S, T> implements MasterWorker<S, T> {
//    private final boolean debug = false;

    /**
     * @author ganzhi
     * 
     */
    protected class WorkItem {
        private final S value;
        private final ResultKey key;

        /**
         * 
         * @param v
         *            work item value
         * @param k
         *            work item key. Identifies work item so result can be
         *            retrieved once a worker thread has completed.
         */
        public WorkItem(S v, ResultKey k) {
            value = v;
            key = k;
        }

        /**
         * 
         * @return value
         */
        public S value() {
            return value;
        }

        /**
         * 
         * @return keys holder for result values
         */
        public ResultKey key() {
            return key;
        }
    }

    /**
     * This class is used to track the state of the workers.
     * 
     * Workers can be in a number of states depending on the state of the
     * master/worker container and on the availability of work.
     * 
     * Workers transition through a number of states in their lifetime as
     * follows:
     * 
     * created (constructor) --> started (execute)
     * 
     * started --> working (work found on queues) --> waiting (no work found on
     * queues)
     * 
     * working --> waiting (no work left on queues) --> complete (no work left
     * on queues and none pending)
     * 
     * waiting --> working (new work available)
     * 
     * complete (thread death)
     * 
     * A worker is considered "active" if it is not in the created or complete
     * state.
     * 
     * @author blainey
     * 
     */
    protected static class WorkerPool {
		//        private static final boolean DEBUG = false;

        private Thread[] threads;
        private Map<Thread, Integer> threadMap;
        private int[] inState, stateOf;
        private final int nworkers;

        private static final int CREATED = 0, STARTED = 1, WORKING = 2,
                WAITING = 3, COMPLETE = 4, NUM_STATES = 5;

        private final Lock lock = new ReentrantLock();

        /* used to block workers waiting for new work */
        private final Condition waitingForWork = lock.newCondition();

        /* used to block master waiting for workers to complete */
        private final Condition completion = lock.newCondition();

        /**
         * Create a new workpool with n workers.
         * 
         * @param n
         *            number of workers.
         */
        public WorkerPool(int n) {
            nworkers = n;
            threads = new Thread[nworkers];
            threadMap = new HashMap<Thread, Integer>();
            stateOf = new int[nworkers];
            inState = new int[NUM_STATES];
        }

        /**
         * @return number of workers
         */
        public int numWorkers() {
            return nworkers;
        }

        /**
         * Create a new worker.
         * 
         * @param i
         *            Worker index (0..nworkers)
         * @param r
         *            A Runnable for the worker to execute.
         */
        public void createWorker(int i, Runnable r) {
            try {
                lock.lock();

                assert threads[i] == null;
                threads[i] = new Thread(r);
                threadMap.put(threads[i], i);
                stateOf[i] = CREATED;
                inState[CREATED]++;
            } finally {
                lock.unlock();
            }
        }

        /**
         * @return index of current thread
         */
        public int threadIndex() {
            Thread thisThread = Thread.currentThread();
            if (threadMap.containsKey(thisThread)) {
                return threadMap.get(thisThread);
            } else {
                return -1;
            }
        }

        /**
         * Activate a worker that has been created.
         * 
         * @param i
         *            Worker index.
         */
        private void activateWorker(int i) {
            /*
             * assert threads[i] != null; assert stateOf[i] == CREATED;
             */
            threads[i].start();
            stateOf[i] = STARTED;
            inState[CREATED]--;
            inState[STARTED]++;
        }

        /**
         * Activate all workers.
         */
        public void activateAll() {
            try {
                lock.lock();

                for (int i = 0; i < nworkers; i++) {
                    activateWorker(i);
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * Begin the process of fetching and processing work.
         */
        public void startWork() {
            startWork(threadMap.get(Thread.currentThread()));
        }

        /**
         * Begin the process of fetching and processing work.
         * 
         * @param i
         *            Worker index.
         */
        public void startWork(int i) {
            try {
                lock.lock();

                /*
                 * assert stateOf[i] == STARTED; stateOf[i] = WORKING;
                 */
                inState[STARTED]--;
                inState[WORKING]++;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Check if a thread has been started.
         * 
         * @return true if any thread has been started
         */
        public boolean anyStarted() {
            try {
                lock.lock();
                if (inState[CREATED] != nworkers) {
                    return true;
                }
            } finally {
                lock.unlock();
            }

            return false;
        }

        private boolean pendingWork = true;

        /**
         * Signal to the worker pool that there is no more work pending from the
         * master/worker container.
         * 
         */
        public void noMorePendingWork() {
            try {
                lock.lock();
                pendingWork = false;
                waitingForWork.signal();
            } finally {
                lock.unlock();
            }
        }

        /**
         * This is a flag indicating that workers should complete upon being
         * awoken from waiting for work.
         */
        private boolean stopWaiting = false;

        /**
         * Put a worker into waiting state. The worker will block until either
         * new work becomes available or the worker has been instructed to
         * terminate. In the event that all other workers are already waiting
         * and there is no more work pending from the master/worker container,
         * this worker will not wait.
         * 
         * @return <ul>
         *         <li>true, if the worker should continue processing work.
         *         <li>false, if the worker should complete
         *         </ul>
         */
        public boolean waitForWork() {
            int i = -1;

            try {
                lock.lock();

                i = threadMap.get(Thread.currentThread());

                // if (debug) System.out.println("Thread " + i +
                // " waitForWork");

                assert stateOf[i] == WORKING;
                if (!pendingWork && (inState[WAITING] == nworkers - 1)) {
                    // if (debug) System.out.println("Thread " + i +
                    // " last man standing");

                    // Tell the other waiting workers to complete
                    stopWaiting = true;
                    waitingForWork.signalAll();

                    // Complete this worker
                    return false;
                }

                stateOf[i] = WAITING;
                inState[WORKING]--;
                inState[WAITING]++;

                // if (debug) System.out.println("Thread " + i +
                // " wait on condition");

                waitingForWork.await();

                // if (debug) System.out.println("Thread " + i +
                // " awakes, stopWaiting=" + stopWaiting);

                stateOf[i] = WORKING;
                inState[WORKING]++;
                inState[WAITING]--;

                return !stopWaiting;
            } catch (InterruptedException e) {
                // if (debug) System.out.println("Thread " + i +
                // " interrupted while waiting");
                stateOf[i] = WORKING;
                inState[WORKING]++;
                inState[WAITING]--;
                return false;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Signal that new work is available.
         * 
         */
        public void newWorkAvailable() {
            try {
                lock.lock();
                waitingForWork.signal();
            } finally {
                lock.unlock();
            }
        }

        /**
         * This method is used by the master to wait until all threads are in
         * complete state.
         * 
         * @return true if all threads completed successfully false if the
         *         master is interrupted while waiting
         */
        public boolean waitForCompletion() {
            try {
                // if (debug) System.out.println("Master: wait for completion");

                lock.lock();

                if (inState[COMPLETE] == nworkers) {
                    return true;
                }

                completion.await();

                return true;
            } catch (InterruptedException e) {
                return false;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Wait for completion or until the specified timeout expires.
         * 
         * @param timeout
         *            Time out value.
         * @param unit
         *            Time unit.
         * @return true if all threads completed successfully false if the
         *         timeout expires or the master is interrupted while waiting
         */
        public boolean waitForCompletion(long timeout, TimeUnit unit) {
            try {
                lock.lock();

                if (inState[COMPLETE] == nworkers)
                    return true;

                return completion.await(timeout, unit);
            } catch (InterruptedException e) {
                return false;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Check if all workers have completed.
         * 
         * @return true iff all workers have completed.
         */
        public boolean isCompleted() {
            try {
                lock.lock();
                return inState[COMPLETE] == nworkers;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Move a worker to the complete state.
         * 
         */
        public void complete() {
            try {
                lock.lock();

                final int i = threadMap.get(Thread.currentThread());
                assert stateOf[i] == WORKING;
                stateOf[i] = COMPLETE;
                inState[COMPLETE]++;
                inState[WORKING]--;

                // Signal master if waiting and this is the last worker to
                // complete
                if (inState[COMPLETE] == nworkers)
                    completion.signal();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Shut down the pool, interrupting threads as necessary.
         * 
         */
        public void shutdown() {
            try {
                lock.lock();
                for (int i = 0; i < nworkers; i++)
                    threads[i].interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    /*
     * private int loungingThreads = 0; private boolean expired = false; private
     * ReentrantLock loungeLock = new ReentrantLock(); private Condition
     * timeToWork = loungeLock.newCondition(); private Condition loungeFull =
     * loungeLock.newCondition(); protected final boolean useLounge = false;
     * 
     * protected boolean waitInLounge() { if (debug)
     * System.out.println("Enter lounge");
     * 
     * // Go wait in the staff lounge try { loungeLock.lock();
     * 
     * // Enter the lounge loungingThreads++;
     * 
     * // If we're the last thread to arrive, signal that the lounge is full if
     * (loungingThreads == numWorkers()) { if (debug)
     * System.out.println("Last thread to arrive, signal that the lounge is full"
     * ); loungeFull.signal(); }
     * 
     * // Wait until it's time to go to work if (debug)
     * System.out.println("Wait to be called to work"); while (true) {
     * timeToWork.await(); // After 5 seconds of waiting in the lounge, just
     * kill the master/worker //if (!timeToWork.await(5,TimeUnit.SECONDS)) {
     * //expired = true; //timeToWork.signalAll(); //return false; //} //if
     * (expired) return false; if (executionStarted) break; }
     * 
     * if (debug) System.out.println("Wake up .. time for work!");
     * 
     * // Leave the lounge loungingThreads--;
     * 
     * return true; } catch (InterruptedException e) { if (debug)
     * System.out.println("Interrupted while in the lounge");
     * 
     * // Leave the lounge loungingThreads--;
     * 
     * Thread.currentThread().interrupt(); // Reset interrupt flag
     * 
     * return false; } finally { loungeLock.unlock(); } }
     * 
     * protected void wakeUpLounge() { try { loungeLock.lock();
     * timeToWork.signalAll(); } finally { loungeLock.unlock(); } }
     * 
     * protected boolean waitForLoungeFull() { try { loungeLock.lock(); if
     * (loungingThreads < numWorkers()) loungeFull.await(); return true; } catch
     * (InterruptedException e) { return false; } finally { loungeLock.unlock();
     * } }
     */

    /*
     * private int waitingWorkers = 0; private volatile boolean stopWaiting =
     * false; private boolean finishedSubmit = false; private ReentrantLock
     * waitingLock = new ReentrantLock(); private Condition waitingForWork =
     * waitingLock.newCondition();
     */

    /*
     * protected boolean waitForWork() { }
     */

    /*
     * protected void newWorkAvailable() { try { waitingLock.lock();
     * waitingForWork.signal(); } finally { waitingLock.unlock(); } }
     */

    /**
     * worker pool.
     */
    protected WorkerPool workerPool;

    /**
     * @param n
     *            number of workers
     */
    public AbstractMasterWorker(int n) {
        workerPool = new WorkerPool(n);
    }

    /**
     * {@inheritDoc}
     */
    public int numWorkers() {
        return workerPool.numWorkers();
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute() {
        // if (debug) System.out.println("Master: Begin execution");

        if (workerPool.anyStarted())
            return false;

        /*
         * if (useLounge) { // In case there are some laggard threads, lets give
         * them a chance to get to the lounge if (!waitForLoungeFull()) return
         * false; }
         */

        workerPool.activateAll();

        /*
         * try { activeLock.lock(); activeWorkers = nworkers; } finally {
         * activeLock.unlock(); }
         */

        /*
         * if (useLounge) wakeUpLounge();
         */

        return true;
    }

    public boolean isCompleted() {
        return workerPool.isCompleted();
    }

    /**
     * {@inheritDoc}
     */
    public boolean waitForCompletion(long timeout, TimeUnit unit) {
        return workerPool.waitForCompletion(timeout, unit);
        /*
         * if (isCompleted()) return true;
         * 
         * try { activeLock.lock();
         * completion.awaitNanos(unit.toNanos(timeout)); return isCompleted(); }
         * finally { activeLock.unlock(); }
         */
    }

    /**
     * {@inheritDoc}
     */
    public boolean waitForCompletion() {
        return workerPool.waitForCompletion();
        /*
         * if (debug) System.out.println("Master: wait for completion"); if
         * (isCompleted()) { if (debug)
         * System.out.println("Master: complete at outset"); return true; }
         * 
         * try { activeLock.lock(); completion.await(); if (debug)
         * System.out.println("Master: complete after waiting"); return
         * isCompleted(); } catch (InterruptedException e) { return false; }
         * finally { activeLock.unlock(); }
         */
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute(long timeout, TimeUnit unit)
            throws TimeoutException, ExecutionException, InterruptedException {
        if (!execute())
            return false;
        return waitForCompletion(timeout, unit);
    }

    /**
     * Result cache.
     */
    protected java.util.Map<ResultKey, T> resultMap = new ConcurrentHashMap<ResultKey, T>();

    private static AtomicInteger sid = new AtomicInteger(0);

    /**
     * @author blainey
     * 
     */
    protected class ResultKeyImpl extends ResultKey {
        private int id;

        /**
         * create a new key.
         */
        public ResultKeyImpl() {
            this.id = sid.getAndIncrement();
        }
    }

    /**
     * {@inheritDoc}
     */
    public T result(ResultKey k) {
        ResultKeyImpl key = (ResultKeyImpl) k;
        return resultMap.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<T> getAllResults() {
        if (!isCompleted())
            return null;
        return resultMap.values();
    }

    /**
     * {@inheritDoc}
     */
    public ResultKey submit(S w, long timeout, TimeUnit unit) {
        // Since the work queue is non-blocking, we can just go ahead and add
        // the work
        // item without worrying about timeouts.
        return submit(w);
    }

    /**
     * {@inheritDoc}
     */
    public void finished() {
        workerPool.noMorePendingWork();
        /*
         * if (debug) System.out.println("Master: finished submit");
         * 
         * try { waitingLock.lock();
         * 
         * if (debug) System.out.println("Set finished flag"); finishedSubmit =
         * true;
         * 
         * // Check if all workers are waiting try { activeLock.lock(); if
         * (debug)
         * System.out.println("waiting="+waitingWorkers+",active="+activeWorkers
         * ); if (waitingWorkers == activeWorkers) { // If so, wake one up to
         * realize that we are done // submitting new work.
         * waitingForWork.signal(); } } finally { activeLock.unlock(); } }
         * finally { waitingLock.unlock(); }
         */
    }

    /*
     * public boolean restart() { if (debug)
     * System.out.println("Master: restart");
     * 
     * if (!useLounge) { shutdown(); return false; }
     * 
     * try { activeLock.lock(); if (activeWorkers != 0) { shutdown(); return
     * false; } } finally { activeLock.unlock(); }
     * 
     * try { loungeLock.lock();
     * 
     * if (expired) return false;
     * 
     * executionStarted = false; stopWaiting = false; finishedSubmit = false;
     * waitingWorkers = 0;
     * 
     * resultMap.clear(); } finally { loungeLock.unlock(); }
     * 
     * return true; }
     */

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        workerPool.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    protected void finalize() throws IOException {
        shutdown();
    }
}
