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

import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.amino.ds.lockfree.LockFreeDeque;
import org.amino.scheduler.internal.AbstractScheduler;

/**
 * This is a dynamic master/worker pattern. Like MasterWorker, this patterns
 * allows a master to create workers and submit work for processing. Unlike
 * MasterWorker, both the master and the workers may submit additional work for
 * processing after execution of workers has begun.
 * 
 * The type parameter S indicates the type of the input work item. The type
 * parameter T indicates the type of the output.
 * 
 * @param <S>
 *            type of the input work item.
 * @param <T>
 *            the type of the output.
 * @author blainey
 * 
 */
class DynamicMasterWorker<S, T> extends AbstractMasterWorker<S, T> {
//    private final boolean debug = false;

    /**
     * Working queue.
     */
    protected Deque<WorkItem>[] workQ;

    /**
     * @author ganzhi
     * 
     */
    private class WorkWrapper implements Runnable {
        private final DynamicWorker<S, T> w;
        private final WorkQueue<S> thisQ;
        private final int id;

        private WorkItem stealWork() {
            // Attempt to steal work from other queues

            /*
             * There are several ways that stealing can happen. In general, work
             * is stolen from the tail of the deque when it is non-empty The
             * order of traversal of deques is important as it can lead to
             * different degrees of cache utilization. For now, we choose to
             * steal work in a deterministic round-robin manner. Other options
             * include random deque selection (which may lead to more uniform
             * stealing) or deque selection which is somehow sensitive to actual
             * thread placement on the machine (eg. ideally want to steal from
             * deques for threads which share some level of cache).
             */
            int nworkers = numWorkers();
            for (int i = (id + 1) % nworkers; i != id; i = (i + 1) % nworkers) {
                WorkItem r = workQ[i].pollLast();
                if (r != null)
                    return r;
            }

            return null;
        }

        public void run() {
            // if (debug) System.out.println("Thread " + id + " start");

            while (true) {
                /*
                 * if (useLounge) { // Go wait in the staff lounge. if (debug)
                 * System.out.println("Thread " + id + " wait in lounge");
                 * 
                 * if (!waitInLounge()) { if (debug)
                 * System.out.println("Thread " + id + " time to die"); // Uh
                 * oh. We're out of a job. Nothing left to live for. break; } }
                 */

                workerPool.startWork();

                try {
                    while (true) {
                        // if (debug) System.out.println("Thread " + id +
                        // " get work from own queue");

                        // Get some work from my queue.
                        WorkItem input = workQ[id].poll();

                        if (input == null) {
                            // if (debug) System.out.println("Thread " + id +
                            // " stealWork");

                            // Try to steal some work from another queue.
                            input = stealWork();

                            if (input == null) {
                                // if (debug) System.out.println("Thread " + id
                                // + " waitForWork");

                                // No work available for now.
                                // Wait to be notified of new work.
                                if (workerPool.waitForWork())
                                    continue;

                                if (Thread.interrupted())
                                    return;

                                // if (debug) System.out.println("Thread " + id
                                // + " no more work for the day");
                                // Either no new work or we've been interrupted,
                                // so we're done for the day.
                                break;
                            }
                        }

                        // if (debug) System.out.println("Thread " + id +
                        // " beginning work item");

                        // Do the work.
                        final T output = w.run(input.value(), thisQ);

                        // Post the results.
                        resultMap.put(input.key(), output);
                    }
                } finally {
                    // if (debug) System.out.println("Thread " + id +
                    // " signalling completion");

                    // No more work to do today. Punch out.
                    workerPool.complete();
                }

                break;
            }

            // if (debug) System.out.println("Thread " + id + " exit");
        }

        /**
         * 
         * @param w
         *            work item.
         * @param x
         *            id
         */
        public WorkWrapper(DynamicWorker<S, T> w, int x) {
            this.w = w;
            this.id = x;

            this.thisQ = new WorkQueue<S>() {
                public ResultKey submit(S w, long timeout, TimeUnit unit) {
                    // Since the work queue is non-blocking, we can just go
                    // ahead and add the work
                    // item without worrying about timeouts.
                    return submit(w);
                }

                public ResultKey submit(S w) {
                    // if (debug) System.out.println("Thread " + id +
                    // " submit new work");

                    ResultKey key = new ResultKeyImpl();

                    boolean added = workQ[id].offer(new WorkItem(w, key));
                    assert added; // workQ is an unbounded queue

                    // Inform a waiting thread that there is new work to do.
                    workerPool.newWorkAvailable();

                    return key;
                }
            };
        }
    }

    /**
     * 
     * @param r
     *            work item.
     */
    public DynamicMasterWorker(DynamicWorker<S, T> r) {
        this(r, AbstractScheduler.defaultNumberOfWorkers());
    }

//    private Map<Thread, Integer> threadMap;

    /**
     * 
     * @param r
     *            work item
     * @param numWorkers
     *            number of worker threads
     */
    public DynamicMasterWorker(DynamicWorker<S, T> r, int numWorkers) {
        super(numWorkers);

        workQ = new Deque[numWorkers];
        for (int i = 0; i < numWorkers; i++) {
            workQ[i] = new LockFreeDeque<WorkItem>();
            workerPool.createWorker(i, new WorkWrapper(r, i));
        }
    }

    public boolean isStatic() {
        return false;
    }

    private AtomicInteger now = new AtomicInteger(0);

    private int nextQueue() {
        int old, q;
        while (true) {
            old = now.intValue();
            q = (old + 1) % numWorkers();
            if (now.weakCompareAndSet(old, q))
                break;
        }
        return q;
    }

    /**
     * {@inheritDoc}
     */
    public ResultKey submit(S w) {
        // if (debug) System.out.println("Master: submit work");

        // If the work is being submitted by a thread from this master/worker,
        // then
        // lets add it to the associated queue.
        int id = workerPool.threadIndex();
        // Otherwise, we just allocate work round-robin to the available
        // threads.
        if (id < 0)
            id = nextQueue();

        // Add the work to the appropriate queue.
        ResultKey key = new ResultKeyImpl();
        boolean added = workQ[id].offer(new WorkItem(w, key));

        // The queue is unbounded so should fail to add new entries only in out
        // of memory situations
        assert added;

        // Indicate to threads waiting for work that there is new work to do.
        workerPool.newWorkAvailable();

        return key;
    }
}
