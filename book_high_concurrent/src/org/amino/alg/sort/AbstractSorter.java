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

import java.util.Comparator;
import java.util.List;

/**
 * This is the abstract sorting class that implements the Sorter interface. The
 * subclasses will provide the sort and reverse methods.
 */

public abstract class AbstractSorter implements Sorter {
    /**
     * 
     * @param a
     *            is the byte array to be sorted in an ascending order
     * 
     */
    public void sort(byte[] a) {
        sort(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the character array to be sorted in an ascending order
     * 
     */
    public void sort(char[] a) {
        sort(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the short array to be sorted in an ascending order
     * 
     */
    public void sort(short[] a) {
        sort(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the int array to be sorted in an ascending order
     * 
     */
    public void sort(int[] a) {
        sort(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the long array to be sorted in an ascending order
     * 
     */
    public void sort(long[] a) {
        sort(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the float array to be sorted in an ascending order
     * 
     */
    public void sort(float[] a) {
        sort(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the double array to be sorted in an ascending order
     * 
     */
    public void sort(double[] a) {
        sort(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the byte array to be sorted in a descending order
     * 
     */
    public void reverse(byte[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the character array to be sorted in a descending order
     * 
     */
    public void reverse(char[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the short array to be sorted in a descending order
     * 
     */
    public void reverse(short[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the int array to be sorted in a descending order
     * 
     */
    public void reverse(int[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the long array to be sorted in a descending order
     * 
     */
    public void reverse(long[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the float array to be sorted in a descending order
     * 
     */
    public void reverse(float[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * 
     * @param a
     *            is the double array to be sorted in a descending order
     * 
     */
    public void reverse(double[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Comparable<T>> void sort(T[] a) {
        sort(a, 0, a.length);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Comparable<T>> void sort(T[] a, Comparator<T> c) {
        sort(a, 0, a.length, c);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Comparable<T>> void reverse(T[] a) {
        reverse(a, 0, a.length);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Comparable<T>> void sort(List<T> a) {
        sort(a, 0, a.size());
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Comparable<T>> void sort(List<T> a, Comparator<T> c) {
        sort(a, 0, a.size(), c);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Comparable<T>> void reverse(List<T> a) {
        reverse(a, 0, a.size());
    }

    /* helper methods */
    /**
     * swap two specified positions in the array.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swap(byte[] a, int i, int j) {
        byte t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfGreater(byte[] a, int i, int j) {
        if (a[i] > a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfLess(byte[] a, int i, int j) {
        if (a[i] < a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swap(char[] a, int i, int j) {
        char t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfGreater(char[] a, int i, int j) {
        if (a[i] > a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfLess(char[] a, int i, int j) {
        if (a[i] < a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swap(short[] a, int i, int j) {
        short t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfGreater(short[] a, int i, int j) {
        if (a[i] > a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfLess(short[] a, int i, int j) {
        if (a[i] < a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfGreater(int[] a, int i, int j) {
        if (a[i] > a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfLess(int[] a, int i, int j) {
        if (a[i] < a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swap(long[] a, int i, int j) {
        long t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfGreater(long[] a, int i, int j) {
        if (a[i] > a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfLess(long[] a, int i, int j) {
        if (a[i] < a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swap(float[] a, int i, int j) {
        float t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfGreater(float[] a, int i, int j) {
        if (a[i] > a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfLess(float[] a, int i, int j) {
        if (a[i] < a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swap(double[] a, int i, int j) {
        double t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfGreater(double[] a, int i, int j) {
        if (a[i] > a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static void swapIfLess(double[] a, int i, int j) {
        if (a[i] < a[j])
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array.
     * 
     * @param <T>
     *            data type
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static <T> void swap(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /**
     * swap two specified positions in the array, if the first value is greater
     * than the second one.
     * 
     * @param <T>
     *            data type
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static <T extends Comparable<T>> void swapIfGreater(T[] a, int i,
            int j) {
        if (a[i].compareTo(a[j]) < 0)
            swap(a, i, j);
    }

    /**
     * swap two specified positions in the array, if the first value is less
     * than the second one.
     * 
     * @param <T>
     *            data type
     * @param a
     *            the array
     * @param i
     *            the first position
     * @param j
     *            the second position
     */
    protected static <T extends Comparable<T>> void swapIfLess(T[] a, int i,
            int j) {
        if (a[i].compareTo(a[j]) > 0)
            swap(a, i, j);
    }
}
