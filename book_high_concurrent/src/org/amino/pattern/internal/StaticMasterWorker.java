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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.amino.scheduler.internal.AbstractScheduler;

/**
 * Classes for a static MasterWorker, where upper bound of master workers is
 * fixed once work is initiated.
 * 
 * @param <S>
 *            input type.
 * @param <T>
 *            result type.
 */
class StaticMasterWorker<S, T> extends AbstractMasterWorker<S, T> {
    /**
     * working queue.
     */
    protected Queue<WorkItem> workQ = new ConcurrentLinkedQueue<WorkItem>();

    /**
     * @author ganzhi
     * 
     */
    private class WorkWrapper implements Runnable {
        private Doable<S, T> w;

        public void run() {
            while (true) {
                /*
                 * // Go wait in the staff lounge if (!waitInLounge()) break;
                 */

                workerPool.startWork();
                try {
                    while (true) {
                        final WorkItem input = workQ.poll();
                        if (input == null)
                            break;

                        final T output = w.run(input.value());
                        resultMap.put(input.key(), output);
                    }
                } finally {
                    workerPool.complete();
                    break;
                }
            }
        }

        /**
         * 
         * @param w
         *            work item
         */
        public WorkWrapper(Doable<S, T> w) {
            this.w = w;
        }
    }

    /**
     * 
     * @param r
     *            work item
     */
    public StaticMasterWorker(Doable<S, T> r) {
        this(r, AbstractScheduler.defaultNumberOfWorkers());
    }

    /**
     * 
     * @param r
     *            work item
     * @param numWorkers
     *            size of worker pool.
     */
    public StaticMasterWorker(Doable<S, T> r, int numWorkers) {
        super(numWorkers);

        Runnable run = new WorkWrapper(r);
        for (int i = 0; i < numWorkers; i++)
            workerPool.createWorker(i, run);
    }

    public boolean isStatic() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public ResultKey submit(S w) {
        if (workerPool.anyStarted())
            return null;

        ResultKey key = new ResultKeyImpl();
        boolean added = workQ.offer(new WorkItem(w, key));

        /*
         * The queue is unbounded so should fail to add new entries only in out
         * of memory situations
         */
        assert added;

        return key;
    }
}
