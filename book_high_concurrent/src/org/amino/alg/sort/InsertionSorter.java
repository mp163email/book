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

/**
 * This class implements the basic Insertion sorting algorithm. Insertion sort
 * is a simple comparison sorting algorithm that can be used for small lists of
 * data. It is efficient for small sets of data. For larger lists more efficient
 * algorithms like quicksort, heapsort etc. should be used.
 */

public class InsertionSorter extends DefaultSorter {

    /**
     * 
     * @param a
     *            is the byte array to be sorted in an ascending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(byte[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortUp(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the byte array to be sorted in a descending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(byte[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortDown(a, from, to);
    }

    private void insertionSortUp(byte[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final byte t = a[j];

            for (; j > from && a[j - 1] > t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    private void insertionSortDown(byte[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final byte t = a[j];

            for (; j > from && a[j - 1] < t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    /**
     * 
     * @param a
     *            is the character array to be sorted in an ascending order
     *            using Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(char[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortUp(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the character array to be sorted in a descending order
     *            using Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(char[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortDown(a, from, to);
    }

    private void insertionSortUp(char[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final char t = a[j];

            for (; j > from && a[j - 1] > t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    private void insertionSortDown(char[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final char t = a[j];

            for (; j > from && a[j - 1] < t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    /**
     * 
     * @param a
     *            is the short array to be sorted in an ascending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(short[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortUp(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the short array to be sorted in a descending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(short[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortDown(a, from, to);
    }

    private void insertionSortUp(short[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final short t = a[j];

            for (; j > from && a[j - 1] > t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    private void insertionSortDown(short[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final short t = a[j];

            for (; j > from && a[j - 1] < t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    /**
     * 
     * @param a
     *            is the int array to be sorted in an ascending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(int[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortUp(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the int array to be sorted in a descending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(int[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortDown(a, from, to);
    }

    private void insertionSortUp(int[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final int t = a[j];

            for (; j > from && a[j - 1] > t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    private void insertionSortDown(int[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final int t = a[j];

            for (; j > from && a[j - 1] < t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    /**
     * 
     * @param a
     *            is the long array to be sorted in an ascending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(long[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortUp(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the long array to be sorted in a descending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(long[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortDown(a, from, to);
    }

    private void insertionSortUp(long[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final long t = a[j];

            for (; j > from && a[j - 1] > t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    private void insertionSortDown(long[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final long t = a[j];

            for (; j > from && a[j - 1] < t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    /**
     * 
     * @param a
     *            is the float array to be sorted in an ascending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(float[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortUp(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the float array to be sorted in a descending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(float[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortDown(a, from, to);
    }

    private void insertionSortUp(float[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final float t = a[j];

            for (; j > from && a[j - 1] > t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    private void insertionSortDown(float[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final float t = a[j];

            for (; j > from && a[j - 1] < t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    /**
     * 
     * @param a
     *            is the double array to be sorted in an ascending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(double[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortUp(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the double array to be sorted in a descending order using
     *            Insertion Sort algorithm
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(double[] a, int from, int to) {
        /* check for invalid arguments */
        if (from > to)
            throw new IllegalArgumentException();

        /* do insertion sort */
        insertionSortDown(a, from, to);
    }

    private void insertionSortUp(double[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final double t = a[j];

            for (; j > from && a[j - 1] > t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }

    private void insertionSortDown(double[] a, int from, int to) {
        for (int i = from; i < to; i++) {
            int j = i;
            final double t = a[j];

            for (; j > from && a[j - 1] < t; j--)
                a[j] = a[j - 1];

            a[j] = t;
        }
    }
}
