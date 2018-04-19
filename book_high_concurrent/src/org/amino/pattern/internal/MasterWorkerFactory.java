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

/**
 * Classes for a MasterWorker Factory.
 * 
 * @author blainey
 * 
 */
public final class MasterWorkerFactory {
    
    /**
     * Utility classes should not have a public or default constructor.
     */
    private MasterWorkerFactory() {

    }

    /**
     * 
     * @param <X>
     *            input type
     * @param <Y>
     *            result type
     * @param r
     *            work item
     * @return StaticMasterWorker
     */
    public static <X, Y> MasterWorker<X, Y> newStatic(Doable<X, Y> r) {
        return new StaticMasterWorker<X, Y>(r);
    }

    /**
     * 
     * @param <X>
     *            input type
     * @param <Y>
     *            result type
     * @param r
     *            work item
     * @param numWorkers
     *            number of workers (threads)
     * @return StaticMasterWorker
     */
    public static <X, Y> MasterWorker<X, Y> newStatic(Doable<X, Y> r,
            int numWorkers) {
        return new StaticMasterWorker<X, Y>(r, numWorkers);
    }

    /**
     * 
     * @param <X>
     *            input type
     * @param <Y>
     *            result type
     * @param r
     *            work item
     * @return DynamicMasterWorker
     */
    public static <X, Y> MasterWorker<X, Y> newDynamic(DynamicWorker<X, Y> r) {
        return new DynamicMasterWorker<X, Y>(r);
    }

    /**
     * 
     * @param <X>
     *            input type
     * @param <Y>
     *            result type
     * @param r
     *            work item
     * @param numWorkers
     *            number of workers
     * @return DynamicMasterWorker
     */
    public static <X, Y> MasterWorker<X, Y> newDynamic(DynamicWorker<X, Y> r,
            int numWorkers) {
        return new DynamicMasterWorker<X, Y>(r, numWorkers);
    }
}
