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

package org.amino.alg.scan;

/**
 * Implementation of parallel scanner.
 * 
 */
public class ParallelScanner extends DefaultScanner {

    private int numberThreads;
    private static final int MINIMUM_PROBLEM_SIZE = 100;

    /**
     * Creates a new parallel scanner, with the specified number of threads
     * "nthreads".
     * 
     * @param nthreads
     *            number of threads to do parallel scan
     */
    public ParallelScanner(int nthreads) {
        numberThreads = nthreads;
    }

    /**
     * Creates a new parallel scanner, with default number of threads that
     * equals to the the number of logical processors.
     */
    public ParallelScanner() {
        numberThreads = org.amino.Runtime.getMaxAvailableWorkerThreads();
    }

    /**
     * Take an array of integer arrays and return a 'flattened' version in a
     * single array. If it's large enough, parallelize it.
     * 
     * @param input
     *            array of integer arrays. Some elements can be null
     * @return single dimension array of all the individual arrays concatenated
     */
    private int[] flatten(final int[][] input) {
        int totalSize = 0;

        int numberParallelChunks = input.length;

        for (int i = 0; i < input.length; ++i) {
            if (null != input[i]) {
                totalSize += input[i].length;
            }
        }
        if (0 == totalSize)
            return null;
        final int[] resultArray = new int[totalSize];

        if (numberParallelChunks * MINIMUM_PROBLEM_SIZE < totalSize) {
            int index = 0;
            Thread[] threadPool = new Thread[numberParallelChunks];
            for (int i = 0; i < input.length; ++i) {
                final int currentIndex = index;
                if (null != input[i]) {
                    final int[] srcArray = input[i];

                    threadPool[i] = new Thread() {
                        public void run() {
                            System.arraycopy(srcArray, 0, resultArray,
                                    currentIndex, srcArray.length);
                        }
                    };
                    index += srcArray.length;
                }
                return resultArray;
            }
            for (int i = 0; i < threadPool.length; ++i)
                if (null != threadPool[i])
                    threadPool[i].start();
            try {
                for (int i = 0; i < threadPool.length; ++i)
                    if (null != threadPool[i])
                        threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }

        int currentIndex = 0;
        for (int i = 0; i < input.length; ++i) {
            if (null != input[i]) {

                int[] srcArray = input[i];
                System.arraycopy(srcArray, 0, resultArray, currentIndex,
                        srcArray.length);
                currentIndex += srcArray.length;
            }
        }

        return resultArray;
    }

    /**
     * {@inheritDoc}
     */
    public int[] findAll(final byte[] a, final byte v, int from, int to) {

        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findAll(a, v, from, to);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[][] tempResults = new int[numThreads][]; // Allocate list of
                                                           // arrays, one per
                                                           // thread
        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to;
            final int endIndex = end;
            final int threadId = i;
            threadPool[i] = new Thread() {
                public void run() {
                    tempResults[threadId] = serialScanner.findAll(a, v,
                            startIndex, endIndex);
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);

        return flatten(tempResults);

    }

    /**
     * Search backwards to find the last index searched in the preceeding chunk.
     * When parallelizing the search of the srcArray, it is important to handle
     * searchStrings which straddle chunks. This becomes tricky if there are
     * substring permutations. For example, let's say
     * [...,'x','a']['r','a','r','a','a',...] are two neighbouring chunks, and
     * we are searching for the string "ara". The correct result would point to
     * the string starting just after the element 'x', but a naive parallel
     * search would think the second element in it's chunk was the first
     * location. To handle this scenario, the thread for each chunk searches the
     * preceeding chunk to see if there is a possibility of overlap. If not,
     * then proceed with the naive search. If there is, then we must determine
     * the start of the last string. For example, if the string arararara is
     * searched for the substring ara, then it should see (ara)r(ara)ra, not any
     * other permutation, such as ar(ara)r(ara). In pathalogical cases, e.g. the
     * search string is something like arararara... and we search for "ararara",
     * then the method will devolve into serial traversals of the preceeding
     * portion of the array for each thread.
     * 
     * @param srcArray
     *            source byte array being searched
     * @param v
     *            search byte string
     * @param endIndex
     *            starting index of search backwards.
     * @return index where a call to findNext would return the next valid search
     *         string's index, starting at endIndex-(v.length-1).
     */
    private int findPreviousEndIndex(final byte[] srcArray, final byte[] v,
            int endIndex) {
        final DefaultScanner serialScanner = new DefaultScanner();

        int windowSize = 2 * v.length - 2;
        int prevIndex = endIndex - (v.length - 1);
        int searchStart = prevIndex;
        int searchEnd = prevIndex + windowSize;
        boolean noOverlap = true;
        if (searchStart < 0)
            searchStart = 0;

        while ((prevIndex = serialScanner.findPrevious(srcArray, v,
                searchStart, searchEnd)) >= 0) {
            searchStart = prevIndex - (v.length - 1);
            searchEnd = searchStart + windowSize;
            noOverlap = false;
        }
        // Fast check for no overlap
        if (noOverlap)
            return endIndex;

        int curIndex = searchStart - v.length;
        do {
            searchStart = curIndex + v.length;
            // search forward. Note it is possible that the start of the search
            // string
            // occurs exactly at the start of the chunk, and the preceeding
            // chunk finished
            // with exactly the substring. If this is the case, cut the forward
            // search short at
            // endIndex+ v.length-1 and will return -1, so endIndex will be
            // returned.
            curIndex = serialScanner.findNext(srcArray, v, searchStart,
                    endIndex + v.length - 1);
        } while (curIndex > 0 && curIndex + v.length < endIndex);
        if (curIndex < 0)
            return endIndex;
        return curIndex; // there is overlap--return starting index
    }

    /**
     * {@inheritDoc}
     */
    public int[] findAll(final byte[] a, final byte[] v, int from, int to) {
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findAll(a, v, from, to);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[][] tempResults = new int[numThreads][]; // Allocate list of
                                                           // arrays, one per
                                                           // thread
        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to;
            final int endIndex = end;
            final int threadId = i;

            threadPool[i] = new Thread() {
                public void run() {
                    int continuingIndex = threadId == 0 ? startIndex
                            : findPreviousEndIndex(a, v, startIndex);

                    tempResults[threadId] = serialScanner.findAll(a, v,
                            continuingIndex, endIndex);
                }
            };
            start += chunkSize;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);

        return flatten(tempResults);
    }

    /**
	 *
	 */
    private class BoxedInteger {
        BoxedInteger(int x) {
            value = x;
        }

        public volatile int value;
    }

    /**
     * {@inheritDoc}
     */
    public int findAny(final byte[] a, final byte v, int from, int to) {
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findAny(a, v, from, to);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        final BoxedInteger boxedInt = new BoxedInteger(-1);
        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to; // add any remainder to last thread
            final int endIndex = end;
            threadPool[i] = new Thread() {
                public void run() {
                    // Periodically check to see if another thread has already
                    // found an answer
                    final int chunkletSize = MINIMUM_PROBLEM_SIZE;
                    int numberChunklets = (endIndex - startIndex)
                            / chunkletSize;
                    int chunkletStart = startIndex;

                    for (int j = 0; j < numberChunklets; ++j) {
                        if (boxedInt.value >= 0) {
                            break; // another thread successfully found an
                                   // element
                        }
                        int chunkletStop = chunkletStart + chunkletSize;
                        if (chunkletStop > endIndex)
                            chunkletStop = endIndex;

                        int result = serialScanner.findAny(a, v, chunkletStart,
                                chunkletStop);

                        chunkletStart = chunkletStop;

                        if (result >= 0 && boxedInt.value < 0) {
                            synchronized (boxedInt) {
                                if (boxedInt.value < 0)
                                    boxedInt.value = result;
                            }
                            break;
                        }
                    }
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);

        return boxedInt.value;
    }

    /**
     * {@inheritDoc}
     */
    public int findAny(final byte[] a, final byte[] v, int from, int to) {
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findAny(a, v, from, to);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        final BoxedInteger boxedInt = new BoxedInteger(-1);
        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize + v.length;
            if (end + chunkSize > to)
                end = to;
            final int endIndex = end;
            final int threadId = i;
            threadPool[i] = new Thread() {
                public void run() {
                    int continuingIndex = threadId == 0 ? startIndex
                            : findPreviousEndIndex(a, v, startIndex);
                    // Periodically check to see if another thread has already
                    // found an answer
                    final int chunkletSize = MINIMUM_PROBLEM_SIZE;
                    int numberChunklets = (endIndex - continuingIndex)
                            / chunkletSize;
                    int chunkletStart = continuingIndex;

                    for (int j = 0; j < numberChunklets; ++j) {
                        if (boxedInt.value >= 0) {
                            break; // another thread successfully found an
                                   // element
                        }
                        int chunkletStop = chunkletStart + chunkletSize
                                + v.length;
                        if (chunkletStop > endIndex)
                            chunkletStop = endIndex;

                        int result = serialScanner.findAny(a, v, chunkletStart,
                                chunkletStop);

                        chunkletStart = chunkletStop;

                        if (result >= 0 && boxedInt.value < 0) {
                            synchronized (boxedInt) {
                                if (boxedInt.value < 0)
                                    boxedInt.value = result;
                            }
                            break;
                        }
                    }
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);

        return boxedInt.value;
    }

    /**
     * {@inheritDoc}
     */
    public int findNext(final byte[] a, final byte v, int index) {
        int from = index;
        int to = a.length;
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findNext(a, v, index);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[] tempResults = new int[numThreads];

        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to;
            final int endIndex = end;
            final int threadId = i;
            threadPool[i] = new Thread() {
                public void run() {
                    tempResults[threadId] = serialScanner.findNext(a, v,
                            startIndex, endIndex);
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);

        for (int i = 0; i < tempResults.length; ++i) {
            if (tempResults[i] >= 0)
                return tempResults[i];
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public int findNext(final byte[] a, final byte[] v, int index) {
        int from = index;
        int to = a.length;
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findNext(a, v, index);
        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[] tempResults = new int[numThreads];

        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize + v.length;
            if (end + chunkSize > to)
                end = to;
            final int threadId = i;
            final int endIndex = end;
            threadPool[i] = new Thread() {
                public void run() {
                    int continuingIndex = threadId == 0 ? startIndex
                            : findPreviousEndIndex(a, v, startIndex);
                    tempResults[threadId] = serialScanner.findNext(a, v,
                            continuingIndex, endIndex);
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        for (int i = 0; i < threadPool.length; ++i)
            try {
                threadPool[i].join();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        org.amino.Runtime.releaseThreads(numThreads);

        for (int i = 0; i < tempResults.length; ++i) {
            if (tempResults[i] >= 0)
                return tempResults[i];
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public int findPrevious(final byte[] a, final byte v, int index) {
        int from = 0;
        int to = index;
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findPrevious(a, v, index);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[] tempResults = new int[numThreads];

        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to;
            final int threadId = i;
            final int endIndex = end;
            threadPool[i] = new Thread() {
                public void run() {
                    tempResults[threadId] = serialScanner.findPrevious(a, v,
                            startIndex, endIndex);
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);

        for (int i = tempResults.length - 1; i >= 0; --i) {
            if (tempResults[i] >= 0)
                return tempResults[i];
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public int findPrevious(final byte[] a, final byte[] v, int index) {
        int from = 0;
        int to = index;
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.findPrevious(a, v, index);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[] tempResults = new int[numThreads];

        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {

            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to;
            final int endIndex = end;
            // need to handle case of v[] straddling two chunks
            if (start - v.length < 0)
                start = 0;
            else
                start -= v.length;
            final int startIndex = start;
            final int threadId = i;
            threadPool[i] = new Thread() {
                public void run() {
                    int continuingIndex = threadId == 0 ? startIndex
                            : findPreviousEndIndex(a, v, startIndex);
                    tempResults[threadId] = serialScanner.findPrevious(a, v,
                            continuingIndex, endIndex);
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);

        for (int i = tempResults.length - 1; i >= 0; --i) {
            if (tempResults[i] >= 0)
                return tempResults[i];
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public int replaceAll(final byte[] a, final byte v, final byte r, int from,
            int to) {
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.replaceAll(a, v, r, from, to);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[] tempResults = new int[numThreads];
        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to;
            final int endIndex = end;
            final int threadId = i;
            threadPool[i] = new Thread() {
                public void run() {
                    tempResults[threadId] = serialScanner.replaceAll(a, v, r,
                            startIndex, endIndex);
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);
        int totalResult = 0;
        for (int i = 0; i < tempResults.length; ++i)
            totalResult += tempResults[i];
        return totalResult;
    }

    /**
     * {@inheritDoc}
     */
    public int replaceAll(final byte[] a, final byte[] v, final byte[] r,
            int from, int to) {
        final int range = to - from;
        if (range < MINIMUM_PROBLEM_SIZE)
            return super.replaceAll(a, v, r, from, to);

        final int numThreads = org.amino.Runtime.reserveThreads(numberThreads);
        final int[] tempResults = new int[numThreads]; // Allocate list of
                                                       // arrays, one per thread
        final DefaultScanner serialScanner = new DefaultScanner();
        final int chunkSize = range / numThreads;
        Thread[] threadPool = new Thread[numThreads];

        int start = from;
        int end = -1;
        for (int i = 0; i < numThreads; ++i) {
            final int startIndex = start;
            end = start + chunkSize;
            if (end + chunkSize > to)
                end = to;
            final int endIndex = end;
            final int threadId = i;
            threadPool[i] = new Thread() {
                public void run() {
                    int continuingIndex = threadId == 0 ? startIndex
                            : findPreviousEndIndex(a, v, startIndex);
                    tempResults[threadId] = serialScanner.replaceAll(a, v, r,
                            continuingIndex, endIndex);
                }
            };
            start = end;
        }
        for (int i = 0; i < threadPool.length; ++i)
            threadPool[i].start();
        try {
            for (int i = 0; i < threadPool.length; ++i)
                threadPool[i].join();
        } catch (Exception e) {
        	e.printStackTrace();
        }

        org.amino.Runtime.releaseThreads(numThreads);
        int totalResult = 0;
        for (int i = 0; i < tempResults.length; ++i)
            totalResult += tempResults[i];
        return totalResult;
    }
}