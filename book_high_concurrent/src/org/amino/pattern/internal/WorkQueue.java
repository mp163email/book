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

import java.util.concurrent.TimeUnit;

/**
 * Classes for queuing work items..
 * @author blainey
 *
 * @param <S> input type.
 */
public interface WorkQueue<S> {
	/**
	 * Submit a work item to the work queue.
	 * @param w work item.
	 * @return key identifying submitted work item.
	 */
	MasterWorker.ResultKey submit (S w);

	/**
	 * Submit a work item to the work queue.
	 * @param w Work item.
	 * @param timeout Time out value.
	 * @param unit    Time unit.
	 * @return key identifying submitted work item.
	 */
	MasterWorker.ResultKey submit (S w, long timeout, TimeUnit unit);
}
