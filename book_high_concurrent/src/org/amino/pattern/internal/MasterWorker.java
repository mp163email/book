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

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This is a simple master/worker pattern.
 * 
 * The master thread creates an instance of the pattern, provides work items to
 * be processed, and is signaled upon completion. The master thread may wait for
 * completion (synchronous) or may poll for completion (asynchronous).
 * 
 * A master/worker pattern may be static or dynamic. In a static master/worker,
 * execution proceeds in two distinct phases - first all of the work items are
 * submitted and then the workers execute by consuming work items. Work items
 * may not be submitted once execution has begun. In a dynamic master/worker,
 * submission of work and the execution of workers proceeds concurrently.
 * Furthermore, a worker task may opt to add new work items during its
 * execution.
 * 
 * @param <S>
 *            indicates the type of the input work item.
 * @param <T>
 *            indicates the type of the output work item.
 * 
 * @author blainey
 * 
 */
public interface MasterWorker<S, T> {
    /**
     * Get the number of active workers.
     * 
     * @return The number of workers that are part of this pattern.
     */
    int numWorkers();

    /**
     * An abstract type used to hold keys for result values.
     * 
     */
    abstract class ResultKey {
    }

    /**
     * Submit a work item for processing. The call is non-blocking. If, for any
     * reason, the work item cannot be immediately submitted for execution, then
     * a false result will be returned.
     * 
     * @param w
     *            Work item.
     * 
     * @return Reference to a result key if work item was successfully
     *         submitted. null if the work item could not immediately be
     *         submitted for processing.
     * 
     */
    ResultKey submit(S w);

    /**
     * Submit a work item for processing and block until it is either submitted
     * successfully or the specified timeout period has expired.
     * 
     * @param w
     *            Work item.
     * @param timeout
     *            Time out value.
     * @param unit
     *            Time out unit.
     * 
     * @return Reference to a result key if work item was successfully
     *         submitted. null if the work item could not be submitted, if the
     *         time out period expired, or if the thread was interrupted while
     *         waiting.
     * 
     */
    ResultKey submit(S w, long timeout, TimeUnit unit);

    /**
     * Indicate to the master/worker that there is not more work coming. In a
     * static master/worker, this is implied by beginning execution.
     */
    void finished();

    /**
     * Begin processing of the work items submitted. If the master/worker is
     * static, then further work items may be submitted for processing after
     * execution has started. The calling thread blocks until all work items
     * have been processed, the specified timeout value has been reached or if
     * execution was terminated for some abnormal reason.
     * 
     * In the event of successful execution, all worker threads will be in a
     * completed state prior to control returning to the calling thread. In the
     * event of timeout or abnormal termination, worker threads will be
     * interrupted using the Thread.interrupt() method.
     * 
     * Upon successful completion, results may be obtained using the result
     * methods.
     * 
     * @param timeout
     *            The timeout value.
     * @param unit
     *            The time units.
     * 
     * @return <ul>
     *         <li>true if execution completes normally
     *         <li>false if the master/worker is not ready to execute (because
     *         it hasn't compleed a prior execution)
     *         </ul>
     * 
     * @throws TimeoutException
     *             If the specified timeout period expired before completion of
     *             all work items.
     * 
     * @throws InterruptedException
     *             If execution of any of the threads was interrupted.
     * 
     * @throws ExecutionException
     *             If any of the worker threads encounted some runtime
     *             exception.
     */
    boolean execute(long timeout, TimeUnit unit) throws TimeoutException,
            ExecutionException, InterruptedException;

    /**
     * Begin processing of the work items submitted. If the master/worker is
     * static, then further work items may be submitted for processing after
     * execution has started. Control returns to the calling thread once all
     * tasks have begun execution. The calling thread may subsequently poll or
     * wait for completion.
     * 
     * @return <ul>
     *         <li>true if execution started normally
     *         <li>false if the master/worker is not ready to execute (because
     *         it hasn't compleed a prior execution)
     *         </ul>
     */
    boolean execute();

    /**
     * Determine if a master/worker is static.
     * 
     * @return <ul>
     *         <li>true if the master/worker is static
     *         <li>false if the master/worker is dynamic
     *         </ul>
     */
    boolean isStatic();

    /**
     * Poll an executing master/worker for completion.
     * 
     * @return <ul>
     *         <li>true if the master/worker is complete
     *         <li>false if it is executing or not yet started
     *         </ul>
     */
    boolean isCompleted();

    /**
     * Get ready to begin a new execution of the pattern. This puts the
     * master/worker back into a state equivalent to just following initial
     * construction. It is a faster alternative to creating a new one.
     * 
     * @return <ul>
     *         <li>true if the master/worker has been restarted
     *         <li>false if the master/worker has not yet completed previously
     *         submitted work
     *         </ul>
     */
    /*
     * boolean restart();
     */

    /**
     * Wait until all workers have completed or the specified timeout period
     * expires.
     * 
     * @param timeout
     *            time out
     * @param unit
     *            time unit
     * @return <ul>
     *         <li>true if all tasks are complete
     *         <li>false otherwise (timeout or some other abnormal condition)
     *         </ul>
     */
    boolean waitForCompletion(long timeout, TimeUnit unit);

    /**
     * Wait until all workers have completed.
     * 
     * @return <ul>
     *         <li>true if all tasks are complete
     *         <li>false otherwise (some abnormal condition has arisen)
     *         </ul>
     */
    boolean waitForCompletion();

    /**
     * Obtain the results from the processing of a work item.
     * 
     * @param k
     *            A result key, obtained from a prior call to "submit"
     * 
     * @return The results produced by a worker task, or null if the results are
     *         not available.
     */
    T result(ResultKey k);

    /**
     * Obtain all of the results from the processing work items.
     * 
     * @return The results produced by all worker tasks, or null if one or more
     *         workers are still active.
     */
    Collection<T> getAllResults();

    /**
     * Shutdown the master/worker. This releases resources that may be held by
     * the master/worker.
     * 
     */
    void shutdown();
}
