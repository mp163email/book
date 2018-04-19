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

package org.amino.alg.sort;

import org.amino.pattern.internal.DynamicWorker;
import org.amino.pattern.internal.MasterWorker;
import org.amino.pattern.internal.MasterWorkerFactory;
import org.amino.pattern.internal.WorkQueue;

/**
 * This class provides a parallel quicksort implementation using a dynamic
 * master-worker pattern.
 */

public class ParallelQuickSorterWorkStealing extends QuickSorter {
    private static final int IS_THRESHOLD = 64;
    private static final int PAR_THRESHOLD = 256;

    private QuickSorter qs = new QuickSorter();

    private int numWorkers = 0;

    /**
     * Constructor.
     */
    public ParallelQuickSorterWorkStealing() {
    }

    /**
     * Constructor.
     * 
     * @param n
     *            is the number of workers to be used for the parallel quicksort
     * 
     */
    public ParallelQuickSorterWorkStealing(int n) {
        numWorkers = n;
    }

    /*
     * Begin byte[] sort
     */
    /**
     * private internal class.
     * 
     */
    private class ByteWorkPacket {
        private byte[] data;
        private int from, to;

        /**
         * 
         * @param data
         *            is the byte array to be sorted by a worker
         * @param from
         *            is the start index of array to be sorted
         * @param to
         *            is the end index of array to be sorted
         * 
         */
        public ByteWorkPacket(byte[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public byte[] data() {
            return data;
        }
    }

    /**
     * 
     * interal class.
     * 
     */
    private class ByteSortUpWorker implements
            DynamicWorker<ByteWorkPacket, Integer> {
        private void sortPartition(byte[] data, int from, int to,
                WorkQueue<ByteWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new ByteWorkPacket(data, from, to));
                else
                    qs.sort(data, from, to);
            }

        }

        private void qsortUp(byte[] data, int from, int to,
                WorkQueue<ByteWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.sort(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            byte x = data[pivot];

            do {
                while (data[i] < x)
                    i++;
                while (data[j] > x)
                    j--;

                if (i <= j) {
                    byte t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the ByteWorkPacket, contain data to be sorted in an
         *            ascending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(ByteWorkPacket w, WorkQueue<ByteWorkPacket> wq) {
            qsortUp(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the byte array to be sorted in an ascending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(byte[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.sort(data, from, to);
        } else {
            MasterWorker<ByteWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new ByteSortUpWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new ByteSortUpWorker(),
                        numWorkers);
            }

            mw.submit(new ByteWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /**
     * 
     * internal class.
     * 
     */
    private class ByteSortDownWorker implements
            DynamicWorker<ByteWorkPacket, Integer> {
        private void sortPartition(byte[] data, int from, int to,
                WorkQueue<ByteWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new ByteWorkPacket(data, from, to));
                else
                    qs.reverse(data, from, to);
            }

        }

        private void qsortDown(byte[] data, int from, int to,
                WorkQueue<ByteWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.reverse(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            byte x = data[pivot];

            do {
                while (data[i] > x)
                    i++;
                while (data[j] < x)
                    j--;

                if (i <= j) {
                    byte t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the ByteWorkPacket, contain data to be sorted in a
         *            descending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(ByteWorkPacket w, WorkQueue<ByteWorkPacket> wq) {
            qsortDown(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the byte array to be sorted in a descending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(byte[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.reverse(data, from, to);
        } else {
            MasterWorker<ByteWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new ByteSortDownWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new ByteSortDownWorker(),
                        numWorkers);
            }

            mw.submit(new ByteWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /*
     * Begin char[] sort
     */
    /**
     * internal class.
     */
    private class CharWorkPacket {
        private char[] data;
        private int from, to;

        /**
         * 
         * @param data
         *            is the character array to be sorted by a worker
         * @param from
         *            is the start index of array to be sorted
         * @param to
         *            is the end index of array to be sorted
         * 
         */
        public CharWorkPacket(char[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public char[] data() {
            return data;
        }
    }

    /**
     * internal class.
     */
    private class ChaqsortUpUpWorker implements
            DynamicWorker<CharWorkPacket, Integer> {
        private void sortPartition(char[] data, int from, int to,
                WorkQueue<CharWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new CharWorkPacket(data, from, to));
                else
                    qs.sort(data, from, to);
            }

        }

        private void qsortUp(char[] data, int from, int to,
                WorkQueue<CharWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.sort(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            char x = data[pivot];

            do {
                while (data[i] < x)
                    i++;
                while (data[j] > x)
                    j--;

                if (i <= j) {
                    char t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the CharWorkPacket, contain data to be sorted in an
         *            ascending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(CharWorkPacket w, WorkQueue<CharWorkPacket> wq) {
            qsortUp(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the character array to be sorted in an ascending order
     *            using quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(char[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.sort(data, from, to);
        } else {
            MasterWorker<CharWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new ChaqsortUpUpWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new ChaqsortUpUpWorker(),
                        numWorkers);
            }

            mw.submit(new CharWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /**
     * internal class.
     */
    private class CharSortDownWorker implements
            DynamicWorker<CharWorkPacket, Integer> {
        private void sortPartition(char[] data, int from, int to,
                WorkQueue<CharWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new CharWorkPacket(data, from, to));
                else
                    qs.reverse(data, from, to);
            }

        }

        private void qsortDown(char[] data, int from, int to,
                WorkQueue<CharWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.reverse(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            char x = data[pivot];

            do {
                while (data[i] > x)
                    i++;
                while (data[j] < x)
                    j--;

                if (i <= j) {
                    char t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the CharWorkPacket, contain data to be sorted in a
         *            descending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(CharWorkPacket w, WorkQueue<CharWorkPacket> wq) {
            qsortDown(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the character array to be sorted in a descending order
     *            using quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(char[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.reverse(data, from, to);
        } else {
            MasterWorker<CharWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new CharSortDownWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new CharSortDownWorker(),
                        numWorkers);
            }

            mw.submit(new CharWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /*
     * Begin short[] sort
     */
    /**
     * internal class.
     */
    private class ShortWorkPacket {
        private short[] data;
        private int from, to;

        /**
         * 
         * @param data
         *            is the short array to be sorted by a worker
         * @param from
         *            is the start index of array to be sorted
         * @param to
         *            is the end index of array to be sorted
         * 
         */
        public ShortWorkPacket(short[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public short[] data() {
            return data;
        }
    }

    /**
     * internal class.
     */
    private class ShortSortUpWorker implements
            DynamicWorker<ShortWorkPacket, Integer> {
        private void sortPartition(short[] data, int from, int to,
                WorkQueue<ShortWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new ShortWorkPacket(data, from, to));
                else
                    qs.sort(data, from, to);
            }

        }

        private void qsortUp(short[] data, int from, int to,
                WorkQueue<ShortWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.sort(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            short x = data[pivot];

            do {
                while (data[i] < x)
                    i++;
                while (data[j] > x)
                    j--;

                if (i <= j) {
                    short t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the ShortWorkPacket, contain data to be sorted in an
         *            ascending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(ShortWorkPacket w, WorkQueue<ShortWorkPacket> wq) {
            qsortUp(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the short array to be sorted in an ascending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(short[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.sort(data, from, to);
        } else {
            MasterWorker<ShortWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new ShortSortUpWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new ShortSortUpWorker(),
                        numWorkers);
            }

            mw.submit(new ShortWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /**
     * internal class.
     */
    private class ShortSortDownWorker implements
            DynamicWorker<ShortWorkPacket, Integer> {
        private void sortPartition(short[] data, int from, int to,
                WorkQueue<ShortWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new ShortWorkPacket(data, from, to));
                else
                    qs.reverse(data, from, to);
            }

        }

        private void qsortDown(short[] data, int from, int to,
                WorkQueue<ShortWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.reverse(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            short x = data[pivot];

            do {
                while (data[i] > x)
                    i++;
                while (data[j] < x)
                    j--;

                if (i <= j) {
                    short t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the ShortWorkPacket, contain data to be sorted in a
         *            descending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(ShortWorkPacket w, WorkQueue<ShortWorkPacket> wq) {
            qsortDown(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the short array to be sorted in a descending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(short[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.reverse(data, from, to);
        } else {
            MasterWorker<ShortWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new ShortSortDownWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new ShortSortDownWorker(),
                        numWorkers);
            }

            mw.submit(new ShortWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /*
     * Begin int[] sort
     */
    /**
     * internal class.
     */
    private class IntWorkPacket {
        private int[] data;
        private int from, to;

        /**
         * 
         * @param data
         *            is the int array to be sorted by a worker
         * @param from
         *            is the start index of array to be sorted
         * @param to
         *            is the end index of array to be sorted
         * 
         */
        public IntWorkPacket(int[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public int[] data() {
            return data;
        }
    }

    /**
     * internal class.
     */
    private class IntSortUpWorker implements
            DynamicWorker<IntWorkPacket, Integer> {
        private void sortPartition(int[] data, int from, int to,
                WorkQueue<IntWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new IntWorkPacket(data, from, to));
                else
                    qs.sort(data, from, to);
            }

        }

        private void qsortUp(int[] data, int from, int to,
                WorkQueue<IntWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.sort(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            int x = data[pivot];

            do {
                while (data[i] < x)
                    i++;
                while (data[j] > x)
                    j--;

                if (i <= j) {
                    int t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the IntWorkPacket, contain data to be sorted in an
         *            ascending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(IntWorkPacket w, WorkQueue<IntWorkPacket> wq) {
            qsortUp(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the int array to be sorted in an ascending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(int[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.sort(data, from, to);
        } else {
            MasterWorker<IntWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new IntSortUpWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new IntSortUpWorker(),
                        numWorkers);
            }

            mw.submit(new IntWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /**
     * internal class.
     */
    private class IntSortDownWorker implements
            DynamicWorker<IntWorkPacket, Integer> {
        private void sortPartition(int[] data, int from, int to,
                WorkQueue<IntWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new IntWorkPacket(data, from, to));
                else
                    qs.reverse(data, from, to);
            }

        }

        private void qsortDown(int[] data, int from, int to,
                WorkQueue<IntWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.reverse(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            int x = data[pivot];

            do {
                while (data[i] > x)
                    i++;
                while (data[j] < x)
                    j--;

                if (i <= j) {
                    int t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the IntWorkPacket, contain data to be sorted in a
         *            descending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(IntWorkPacket w, WorkQueue<IntWorkPacket> wq) {
            qsortDown(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the int array to be sorted in a descending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(int[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.reverse(data, from, to);
        } else {
            MasterWorker<IntWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new IntSortDownWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new IntSortDownWorker(),
                        numWorkers);
            }

            mw.submit(new IntWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /*
     * Begin long[] sort
     */
    /**
     * internal class.
     */
    private class LongWorkPacket {
        private long[] data;
        private int from, to;

        /**
         * 
         * @param data
         *            is the long array to be sorted by a worker
         * @param from
         *            is the start index of array to be sorted
         * @param to
         *            is the end index of array to be sorted
         * 
         */
        public LongWorkPacket(long[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public long[] data() {
            return data;
        }
    }

    /**
     * internal class.
     */
    private class LongSortUpWorker implements
            DynamicWorker<LongWorkPacket, Integer> {
        private void sortPartition(long[] data, int from, int to,
                WorkQueue<LongWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new LongWorkPacket(data, from, to));
                else
                    qs.sort(data, from, to);
            }

        }

        private void qsortUp(long[] data, int from, int to,
                WorkQueue<LongWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.sort(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            long x = data[pivot];

            do {
                while (data[i] < x)
                    i++;
                while (data[j] > x)
                    j--;

                if (i <= j) {
                    long t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the LongWorkPacket, contain data to be sorted in an
         *            ascending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(LongWorkPacket w, WorkQueue<LongWorkPacket> wq) {
            qsortUp(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the long array to be sorted in an ascending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(long[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.sort(data, from, to);
        } else {
            MasterWorker<LongWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new LongSortUpWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new LongSortUpWorker(),
                        numWorkers);
            }

            mw.submit(new LongWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /**
     * internal class.
     */
    private class LongSortDownWorker implements
            DynamicWorker<LongWorkPacket, Integer> {
        private void sortPartition(long[] data, int from, int to,
                WorkQueue<LongWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new LongWorkPacket(data, from, to));
                else
                    qs.reverse(data, from, to);
            }

        }

        private void qsortDown(long[] data, int from, int to,
                WorkQueue<LongWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.reverse(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            long x = data[pivot];

            do {
                while (data[i] > x)
                    i++;
                while (data[j] < x)
                    j--;

                if (i <= j) {
                    long t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the LongWorkPacket, contain data to be sorted in a
         *            descending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(LongWorkPacket w, WorkQueue<LongWorkPacket> wq) {
            qsortDown(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the long array to be sorted in a descending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(long[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.reverse(data, from, to);
        } else {
            MasterWorker<LongWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new LongSortDownWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new LongSortDownWorker(),
                        numWorkers);
            }

            mw.submit(new LongWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /*
     * Begin float[] sort
     */
    /**
     * internal class.
     */
    private class FloatWorkPacket {
        private float[] data;
        private int from, to;

        /**
         * 
         * @param data
         *            is the float array to be sorted by a worker
         * @param from
         *            is the start index of array to be sorted
         * @param to
         *            is the end index of array to be sorted
         * 
         */
        public FloatWorkPacket(float[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public float[] data() {
            return data;
        }
    }

    /**
     * internal class.
     */
    private class FloatSortUpWorker implements
            DynamicWorker<FloatWorkPacket, Integer> {
        private void sortPartition(float[] data, int from, int to,
                WorkQueue<FloatWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new FloatWorkPacket(data, from, to));
                else
                    qs.sort(data, from, to);
            }
        }

        private void qsortUp(float[] data, int from, int to,
                WorkQueue<FloatWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.sort(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            float x = data[pivot];

            do {
                while (data[i] < x)
                    i++;
                while (data[j] > x)
                    j--;

                if (i <= j) {
                    float t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the FloatWorkPacket, contain data to be sorted in an
         *            ascending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(FloatWorkPacket w, WorkQueue<FloatWorkPacket> wq) {
            qsortUp(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the float array to be sorted in an ascending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(float[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.sort(data, from, to);
        } else {
            MasterWorker<FloatWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new FloatSortUpWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new FloatSortUpWorker(),
                        numWorkers);
            }

            mw.submit(new FloatWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /**
     * internal class.
     */
    private class FloatSortDownWorker implements
            DynamicWorker<FloatWorkPacket, Integer> {
        private void sortPartition(float[] data, int from, int to,
                WorkQueue<FloatWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new FloatWorkPacket(data, from, to));
                else
                    qs.reverse(data, from, to);
            }

        }

        private void qsortDown(float[] data, int from, int to,
                WorkQueue<FloatWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.reverse(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            float x = data[pivot];

            do {
                while (data[i] > x)
                    i++;
                while (data[j] < x)
                    j--;

                if (i <= j) {
                    float t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the FloatWorkPacket, contain data to be sorted in a
         *            descending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(FloatWorkPacket w, WorkQueue<FloatWorkPacket> wq) {
            qsortDown(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the float array to be sorted in a descending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(float[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.reverse(data, from, to);
        } else {
            MasterWorker<FloatWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new FloatSortDownWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new FloatSortDownWorker(),
                        numWorkers);
            }

            mw.submit(new FloatWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /*
     * Begin double[] sort
     */
    /**
     * internal class.
     */
    private class DoubleWorkPacket {
        private double[] data;
        private int from, to;

        /**
         * 
         * @param data
         *            is the double array to be sorted by a worker
         * @param from
         *            is the start index of array to be sorted
         * @param to
         *            is the end index of array to be sorted
         * 
         */
        public DoubleWorkPacket(double[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public double[] data() {
            return data;
        }
    }

    /**
     * internal class.
     */
    private class DoubleSortUpWorker implements
            DynamicWorker<DoubleWorkPacket, Integer> {
        private void sortPartition(double[] data, int from, int to,
                WorkQueue<DoubleWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new DoubleWorkPacket(data, from, to));
                else
                    qs.sort(data, from, to);
            }

        }

        private void qsortUp(double[] data, int from, int to,
                WorkQueue<DoubleWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.sort(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            double x = data[pivot];

            do {
                while (data[i] < x)
                    i++;
                while (data[j] > x)
                    j--;

                if (i <= j) {
                    double t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the DoubleWorkPacket, contain data to be sorted in an
         *            ascending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(DoubleWorkPacket w, WorkQueue<DoubleWorkPacket> wq) {
            qsortUp(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the double array to be sorted in an ascending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(double[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.sort(data, from, to);
        } else {
            MasterWorker<DoubleWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new DoubleSortUpWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new DoubleSortUpWorker(),
                        numWorkers);
            }

            mw.submit(new DoubleWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }

    /**
     * internal class.
     */
    private class DoubleSortDownWorker implements
            DynamicWorker<DoubleWorkPacket, Integer> {
        private void sortPartition(double[] data, int from, int to,
                WorkQueue<DoubleWorkPacket> wq) {
            final int size = to - from;
            if (size > 1) {
                if (size > PAR_THRESHOLD)
                    wq.submit(new DoubleWorkPacket(data, from, to));
                else
                    qs.reverse(data, from, to);
            }

        }

        private void qsortDown(double[] data, int from, int to,
                WorkQueue<DoubleWorkPacket> wq) {
            // if the length of the array section is small, use an insertion
            // sort
            if (to - from < IS_THRESHOLD) {
                is.reverse(data, from, to);
                return;
            }

            // select a pivot element, returning its index
            int pivot = selectPivot(data, from, to);

            // partition array in-place
            // FIXME: can this be parallelized effectively?
            int i = from, j = to - 1;
            double x = data[pivot];

            do {
                while (data[i] > x)
                    i++;
                while (data[j] < x)
                    j--;

                if (i <= j) {
                    double t = data[i];
                    data[i] = data[j];
                    data[j] = t;
                    i++;
                    j--;
                }
            } while (i <= j);

            // sort each partition
            sortPartition(data, from, j + 1, wq);
            sortPartition(data, i, to, wq);
        }

        /**
         * 
         * @param w
         *            is the DoubleWorkPacket, contain data to be sorted in a
         *            descending order
         * @param wq
         *            is the work queue
         * 
         */
        public Integer run(DoubleWorkPacket w, WorkQueue<DoubleWorkPacket> wq) {
            qsortDown(w.data(), w.from(), w.to(), wq);
            return 0;
        }
    }

    /**
     * 
     * @param data
     *            is the double array to be sorted in a descending order using
     *            quicksort
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(double[] data, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        if (to - from < PAR_THRESHOLD) {
            qs.reverse(data, from, to);
        } else {
            MasterWorker<DoubleWorkPacket, Integer> mw;

            if (numWorkers == 0) {
                mw = MasterWorkerFactory.newDynamic(new DoubleSortDownWorker());
            } else {
                mw = MasterWorkerFactory.newDynamic(new DoubleSortDownWorker(),
                        numWorkers);
            }

            mw.submit(new DoubleWorkPacket(data, from, to));
            mw.finished();
            mw.execute();
            mw.waitForCompletion();
            mw.shutdown();
        }
    }
}
