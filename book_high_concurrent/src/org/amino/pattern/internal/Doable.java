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
 * Interface for worker in pattern framework. The worker might be executed by
 * several threads without any synchronization. It's the duty of implementer of
 * this interface to ensure run() method is thread-safe.
 * 
 * @author Zhi Gan (ganzhi@gmail.com)
 * 
 * @param <I>
 *            input type.
 * @param <O>
 *            result type.
 * 
 */
public interface Doable<I, O> {
    /**
     * Run the work item.
     * 
     * @param input
     *            input action
     * @return result.
     */
    O run(I input);
}
