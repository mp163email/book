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

/**
 * 
 * @author Zhi Gan
 *
 */
public enum ParallelType {
	/**
	 * The annotated method is not thread safe. It's caller's duty to correctly
	 * synchronization before call this method.
	 */
	ThreadUnSafe,

	/**
	 * The annotated method is thread safe. It can be called in multi-threaded
	 * program.
	 */
	ThreadSafe,

	/**
	 * The annotated method is thread safe. And its implementation is not based
	 * on lock. Scalability of this method should be better than ThreadUnSafe
	 * and ThreadSafe methods.
	 */
	LockFree
}
