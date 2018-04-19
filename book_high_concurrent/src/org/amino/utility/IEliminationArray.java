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
 * A global elimination array interface.
 * 
 * <pre>
 * A Scalable Lock-free Stack Algorithm
 * Danny Hendler                Nir Shavit            Lena Yerushalmi
 * School of Computer Science Tel-Aviv University &amp; School of Computer Science
 *  Tel-Aviv University     Sun Microsystems           Tel-Aviv University
 *  Tel Aviv, Israel 69978      Laboratories          Tel Aviv, Israel 69978
 *  hendlerd@post.tau.ac.il    shanir@sun.com          lenay@post.tau.ac.il
 * </pre>
 * 
 * @author Zhi Gan (ganzhi@gmail.com)
 */
public interface IEliminationArray {

    /**
     * Try to add element without touching the central data structure. If this
     * operation can successfully locate a removing thread, it will succeed.
     * Else, it will sleep for several milliseconds and waiting to be located by
     * removing threads.
     * 
     * @param obj
     *            the adding object
     * @param backOff
     *            time in millisecond for sleeping if match haven't been found
     *            immediately.
     * @return true if match happened between this method and tryRemove(int)
     * 
     * @throws InterruptedException
     *             throw exception if interrupted
     */
    boolean tryAdd(Object obj, int backOff) throws InterruptedException;

    /**
     * Try to remove element without touching central data structure. If this
     * operation can successfully locate a adding thread, it will succeed. Else,
     * it will sleep for several milliseconds and waiting to be located by
     * adding threads.
     * 
     * @param backOff
     *            time in millisecond for sleeping if match haven't been found
     *            immediately.
     * @return null if no match. Argument to tryAdd() method if successful match
     * 
     * @throws InterruptedException
     *             throw exception if be interrupted
     */
    Object tryRemove(int backOff) throws InterruptedException;

}