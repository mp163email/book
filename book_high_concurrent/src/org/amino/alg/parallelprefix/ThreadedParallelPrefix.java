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
 * Threaded implementation of parallel-prefix operation.
 * <p>
 * Inspired by Guy E. Blelloch "Prefix Sums and Their Applications". In John H.
 * Reif (Ed.), Synthesis of Parallel Algorithms, Morgan Kaufmann, 1990.
 * <p>
 * <h3>Implementation Details</h3>
 * There are three distinct passes: Two parallel reductions, and a recusive scan
 * call. It is assumed that there are many more array elements than worker
 * threads, and that there is at most a 1-to-1 mapping of threads to processing
 * cores.
 * <p>
 * In the first pass, the input array is divided into M/N sequential chunks,
 * where M is the number of elements in the array, and N the number of worker
 * threads. A partial reduction is performed in parallel, the results going into
 * a temporary work array of size M/N, with each element corresponding to a
 * particular worker thread.
 * <p>
 * A recursive call to scan this work array is then performed.
 * <p>
 * Finally, each thread then adds the corresponding value to each of it's
 * elements.
 * 
 * @author donawa
 * 
 * @param <T>
 *            Element type of array, if array is a non-primitive type.
 *            Irrelevant if array is of a primitive type.
 * 
 * @see ParallelPrefix
 */
public class ThreadedParallelPrefix<T> extends AbstractParallelPrefix<T> {

    private int numberThreads;
    private int minArraySize;
    /**
     * minimum size.
     * 
     * TODO find a better number than this guess.
     */
    public static final int DEFAULT_MINIMUM_ARRAY_SIZE = 10000;

    // for
    // parallelization
    // to be
    // profitable

    /**
     * 
     * @param size
     *            minimum size of array for non-serial scan to be used.
     */
    public void setMinimumArraySize(int size) {
        minArraySize = size;
    }

    /**
     * 
     * @return minimum size of input array required for threaded (non-serial)
     *         scan to be employed.
     */
    public int getMinimumArraySize() {
        return minArraySize;
    }

    /**
     * 
     * @param nthreads
     *            Number of threads to use in parallelizing the scan operation.
     */
    public ThreadedParallelPrefix(int nthreads) {
        numberThreads = nthreads;
        minArraySize = DEFAULT_MINIMUM_ARRAY_SIZE;
    }

    /**
     * Set number of worker threads to the number of availableProcessors.
     */
    public ThreadedParallelPrefix() {
        numberThreads = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Class for performing partial reductions for Parallel-prefix operations
     * across a pool of threads.
     * 
     * @author donawa
     * @see ThreadedParallelPrefix
     * @see BinaryOp
     */
    public class PartialReduction {
        private int numOfThreads;

        /**
         * 
         * @param n
         *            number of worker threads to use in the reduction
         */
        public PartialReduction(int n) {
            numOfThreads = n;
        }

        /**
         * Reduce across an array of integers.
         * 
         * @param inputArray
         *            input array
         * @param reducedArray
         *            contains intermediate reduction results
         * @param op
         *            reduction operator
         * @return array of partial reduced results
         */
        public int[] reduce(final int[] inputArray, final int[] reducedArray,
                final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final int[] partialReduction = new int[numThreads];
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;

                threadPool[i] = new Thread() {
                    public void run() {
                        int result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            return partialReduction;
        }

        /**
         * For every element in parallelReduction except the first one, spawn
         * off a worker thread and with the value of that element, modify each
         * element of outputArray with it using the op method.
         * 
         * @param partialReduction
         *            partial reductions from a previous scan
         * @param outputArray
         *            destination array containing intermediate results
         * @param op
         *            modifying function
         */
        public void modify(final int[] partialReduction,
                final int[] outputArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);

            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }

        /**
         * Reduce across an array of float numbers.
         * 
         * @param inputArray
         *            input array
         * @param reducedArray
         *            contains intermediate reduction results
         * @param op
         *            reduction operator
         * @return array of partial reduced results
         */
        public float[] reduce(final float[] inputArray,
                final float[] reducedArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final float[] partialReduction = new float[numThreads];
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;
                threadPool[i] = new Thread() {
                    public void run() {
                        float result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
            return partialReduction;
        }

        /**
         * For every element in parallelReduction except the first one, spawn
         * off a worker thread and with the value of that element, modify each
         * element of outputArray with it using the op method.
         * 
         * @param partialReduction
         *            partial reductions from a previous scan
         * @param outputArray
         *            destination array containing intermediate results
         * @param op
         *            modifying function
         */
        public void modify(final float[] partialReduction,
                final float[] outputArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final float value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }

        /**
         * @see #reduce(int[], int[], BinaryOp)
         */
        private double[] reduce(final double[] inputArray,
                final double[] reducedArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final double[] partialReduction = new double[numThreads];
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;
                threadPool[i] = new Thread() {
                    public void run() {
                        double result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
            return partialReduction;
        }

        /**
         * @see #modify(int[], int[], BinaryOp)
         */
        private void modify(final double[] partialReduction,
                final double[] outputArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final double value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }

        /**
         * @see #reduce(int[], int[], BinaryOp)
         */
        private char[] reduce(final char[] inputArray,
                final char[] reducedArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final char[] partialReduction = new char[numThreads];
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;
                threadPool[i] = new Thread() {
                    public void run() {
                        char result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
            return partialReduction;
        }

        /**
         * @see #modify(int[], int[], BinaryOp)
         */
        private void modify(final char[] partialReduction,
                final char[] outputArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final char value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }

        /**
         * @see #reduce(int[], int[], BinaryOp)
         */
        private byte[] reduce(final byte[] inputArray,
                final byte[] reducedArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final byte[] partialReduction = new byte[numThreads];
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;
                threadPool[i] = new Thread() {
                    public void run() {
                        byte result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
            return partialReduction;
        }

        /**
         * @see #modify(int[], int[], BinaryOp)
         */
        private void modify(final byte[] partialReduction,
                final byte[] outputArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final byte value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }

        /**
         * @see #reduce(int[], int[], BinaryOp)
         */
        private long[] reduce(final long[] inputArray,
                final long[] reducedArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final long[] partialReduction = new long[numThreads];
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;
                threadPool[i] = new Thread() {
                    public void run() {
                        long result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
            return partialReduction;
        }

        /**
         * @see #modify(int[], int[], BinaryOp)
         */
        private void modify(final long[] partialReduction,
                final long[] outputArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final long value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }

        /**
         * @see #reduce(int[], int[], BinaryOp)
         */
        private short[] reduce(final short[] inputArray,
                final short[] reducedArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final short[] partialReduction = new short[numThreads];
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;
                threadPool[i] = new Thread() {
                    public void run() {
                        short result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
            return partialReduction;
        }

        /**
         * @see #modify(int[], int[], BinaryOp)
         */
        private void modify(final short[] partialReduction,
                final short[] outputArray, final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final short value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }

        /**
         * @see #reduce(int[], int[], BinaryOp)
         */
        @SuppressWarnings("unchecked")
        private T[] reduce(final T[] inputArray, final T[] reducedArray,
                final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            final T[] partialReduction = (T[]) new Object[numThreads]; // Very
            // annoying!
            int chunkSize = inputArray.length / numThreads;
            Thread[] threadPool = new Thread[numThreads];
            int start = 0;
            for (int i = 0; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= inputArray.length)
                    end = inputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final int threadId = i;
                threadPool[i] = new Thread() {
                    public void run() {
                        T result = inputArray[currentStart];
                        reducedArray[currentStart] = result;
                        for (int i = currentStart + 1; i <= currentEnd; ++i) {
                            reducedArray[i] = op.transform(reducedArray[i - 1],
                                    inputArray[i]);
                        }
                        partialReduction[threadId] = reducedArray[currentEnd];
                    };
                };
                start += chunkSize;
            }

            for (int i = 0; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 0; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
            return partialReduction;
        }

        /**
         * @see #modify(int[], int[], BinaryOp)
         */
        private void modify(final T[] partialReduction, final T[] outputArray,
                final BinaryOp<T> op) {
            int numThreads = org.amino.Runtime.reserveThreads(numOfThreads);
            Thread[] threadPool = new Thread[numThreads];
            int chunkSize = outputArray.length / numThreads;
            assert (partialReduction.length == numThreads);
            int start = chunkSize; // skip the first chunk since nothing should
            // be added to it
            for (int i = 1; i < numThreads; ++i) {
                int end = start + chunkSize - 1;
                if (end >= outputArray.length)
                    end = outputArray.length - 1;
                final int currentEnd = end;
                final int currentStart = start;
                final T value = partialReduction[i - 1];
                threadPool[i] = new Thread() {
                    public void run() {
                        for (int i = currentStart; i <= currentEnd; ++i) {
                            outputArray[i] = op
                                    .transform(outputArray[i], value);
                        }
                    };
                };
                start += chunkSize;
            }

            for (int i = 1; i < numThreads; ++i)
                threadPool[i].start();
            try {
                for (int i = 1; i < numThreads; ++i)
                    threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            org.amino.Runtime.releaseThreads(numThreads);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void scan(int[] inputArray, int[] outputArray, BinaryOp<T> op) {
        // No point incurring overhead of threads if array is too small,
        // or if we have less than three threads.
        if (inputArray.length < getMinimumArraySize() /* || _numberThreads < 3 */) {
            super.scan(inputArray, outputArray, op);
            return;
        }
        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        int[] partialReduction = pr.reduce(inputArray, outputArray, op);

		// if (false) {
		// System.out.println("Partial Reduction:");
		// for (int i = 0; i < partialReduction.length; ++i) {
		// System.out.print("p[" + i + "]=" + partialReduction[i] + " ");
		// }
		//        }

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);

    }

    /**
     * {@inheritDoc}
     */
    public void scan(float[] inputArray, float[] outputArray, BinaryOp<T> op) {
        if (inputArray.length < getMinimumArraySize() /* || _numberThreads < 3 */) {
            super.scan(inputArray, outputArray, op);
            return;
        }
        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        float[] partialReduction = pr.reduce(inputArray, outputArray, op);

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);
    }

    /**
     * {@inheritDoc}
     */
    public void scan(double[] inputArray, double[] outputArray, BinaryOp<T> op) {
        if (inputArray.length < getMinimumArraySize() /* || _numberThreads < 3 */) {
            super.scan(inputArray, outputArray, op);
            return;
        }
        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        double[] partialReduction = pr.reduce(inputArray, outputArray, op);

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);
    }

    /**
     * {@inheritDoc}
     */
    public void scan(char[] inputArray, char[] outputArray, BinaryOp<T> op) {
        if (inputArray.length < getMinimumArraySize() /* || _numberThreads < 3 */) {
            super.scan(inputArray, outputArray, op);
            return;
        }
        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        char[] partialReduction = pr.reduce(inputArray, outputArray, op);

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);
    }

    /**
     * {@inheritDoc}
     */
    public void scan(byte[] inputArray, byte[] outputArray, BinaryOp<T> op) {
        if (inputArray.length < getMinimumArraySize() /* || _numberThreads < 3 */) {
            super.scan(inputArray, outputArray, op);
            return;
        }
        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        byte[] partialReduction = pr.reduce(inputArray, outputArray, op);

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);
    }

    /**
     * {@inheritDoc}
     */
    public void scan(short[] inputArray, short[] outputArray, BinaryOp<T> op) {
        if (inputArray.length < getMinimumArraySize() /* || _numberThreads < 3 */) {
            super.scan(inputArray, outputArray, op);
            return;
        }
        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        short[] partialReduction = pr.reduce(inputArray, outputArray, op);

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);
    }

    /**
     * {@inheritDoc}
     */
    public void scan(long[] inputArray, long[] outputArray, BinaryOp<T> op) {
        if (inputArray.length < getMinimumArraySize() /* || _numberThreads < 3 */) {
            super.scan(inputArray, outputArray, op);
            return;
        }
        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        long[] partialReduction = pr.reduce(inputArray, outputArray, op);

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);
    }

    /**
     * {@inheritDoc}
     */
    public void scan(T[] inputArray, T[] outputArray, BinaryOp<T> op) {

        if (inputArray.length < getMinimumArraySize()) {
            super.scan(inputArray, outputArray, op);
            return;
        }

        int numThreads = numberThreads;
        if (inputArray.length > numberThreads)
            numThreads = inputArray.length / 2;
        PartialReduction pr = new PartialReduction(numThreads);
        T[] partialReduction = pr.reduce(inputArray, outputArray, op);

        scan(partialReduction, partialReduction, op);
        pr.modify(partialReduction, outputArray, op);
    }
}
