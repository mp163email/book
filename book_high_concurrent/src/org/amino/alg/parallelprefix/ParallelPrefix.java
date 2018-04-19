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

package org.amino.alg.parallelprefix;

/**
 * Interface for parallel prefix (scan) operation. Support for single dimension
 * arrays of type byte, char, short, integer, long, float and double, plus
 * generic arrays, using binary or unary operators.
 * 
 * @param <T>
 *            Generic type for use if array is of non-primitives. Otherwise it
 *            is unused.
 * @author donawa
 * 
 */
public interface ParallelPrefix<T> {

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param inputArray
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(int[] inputArray, int[] outputArray, BinaryOp<T> op);

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param array
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(char[] array, char[] outputArray, BinaryOp<T> op);

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param array
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(byte[] array, byte[] outputArray, BinaryOp<T> op);

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param array
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(short[] array, short[] outputArray, BinaryOp<T> op);

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param array
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(long[] array, long[] outputArray, BinaryOp<T> op);

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param array
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(float[] array, float[] outputArray, BinaryOp<T> op);

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param array
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(double[] array, double[] outputArray, BinaryOp<T> op);

    /**
     * Perform a parallel-prefix operation on input array 'array'. Input array
     * is not modified. Result will be
     * {a[0],op(a[0],a[1]),op(a[1],a[2]),...,op(a[n-1],a[n])}
     * 
     * @param array
     *            Single dimension input array
     * @param outputArray
     *            Single dimension destination array. Can be the same as
     *            inputArray
     * @param op
     *            binary operator
     */
    void scan(T[] array, T[] outputArray, BinaryOp<T> op);
}
