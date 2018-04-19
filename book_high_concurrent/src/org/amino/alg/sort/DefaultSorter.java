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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * This is the simple default sorter class. Lots of room for improving the
 * implementation. Uses the array and list sorting routines. Provides functions
 * to sort by ascending and descending orders.
 */

public class DefaultSorter extends AbstractSorter {

    /**
     * 
     * @param a
     *            is the byte array to be sorted in a descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(byte[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * 
     * @param a
     *            is the character array to be sorted in a descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(char[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * 
     * @param a
     *            is the short array to be sorted in a descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(short[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * 
     * @param a
     *            is the int array to be sorted in a descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(int[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * 
     * @param a
     *            is the long array to be sorted in a descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(long[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * 
     * @param a
     *            is the float array to be sorted in a descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(float[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * 
     * @param a
     *            is the double array to be sorted in a descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void reverse(double[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is the array of Comparable objects to be sorted in a
     *            descending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public <T extends Comparable<T>> void reverse(T[] a, int from, int to) {
        Arrays.sort(a, from, to);
        final int len = to - from + 1;
        for (int i = from, j = to - 1; i < from + len / 2; i++, j--)
            swap(a, i, j);
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is the List of Comparable objects to be sorted in a descending
     *            order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public <T extends Comparable<T>> void reverse(List<T> a, int from, int to) {
        List<T> aa = a.subList(from, to);
        Collections.sort(aa);
        ListIterator<T> ali = a.listIterator(to);
        ListIterator<T> aali = aa.listIterator();
        for (int i = from; i < to; i++) {
            ali.previous();
            ali.set(aali.next());
        }
    }

    /**
     * 
     * @param a
     *            is the byte array to be sorted in an ascending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(byte[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the character array to be sorted in an ascending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */

    public void sort(char[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the short array to be sorted in an ascending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */

    public void sort(short[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the int array to be sorted in an ascending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */

    public void sort(int[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the long array to be sorted in an ascending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */

    public void sort(long[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the float array to be sorted in an ascending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */

    public void sort(float[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * 
     * @param a
     *            is the double array to be sorted in an ascending order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public void sort(double[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is an array of Comparable objects to be sorted in an ascending
     *            order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public <T extends Comparable<T>> void sort(T[] a, int from, int to) {
        Arrays.sort(a, from, to);
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is an array of Comparable objects to be sorted
     * @param c
     *            is the Comparator
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public <T extends Comparable<T>> void sort(T[] a, int from, int to,
            Comparator<T> c) {
        Arrays.sort(a, from, to, c);
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is a List of Comparable objects to be sorted in an ascending
     *            order
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public <T extends Comparable<T>> void sort(List<T> a, int from, int to) {
        List<T> aa = a.subList(from, to);
        Collections.sort(aa);
        ListIterator<T> ali = a.listIterator(to);
        ListIterator<T> aali = aa.listIterator();
        for (int i = from; i < to; i++) {
            ali.next();
            ali.set(aali.next());
        }
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is a List of Comparable objects to be sorted
     * @param c
     *            is the Comparator
     * @param from
     *            is the start index of array to be sorted
     * @param to
     *            is the end index of array to be sorted
     * 
     */
    public <T extends Comparable<T>> void sort(List<T> a, int from, int to,
            Comparator<T> c) {
        List<T> aa = a.subList(from, to);
        Collections.sort(aa, c);
        ListIterator<T> ali = a.listIterator(to);
        ListIterator<T> aali = aa.listIterator();
        for (int i = from; i < to; i++) {
            ali.next();
            ali.set(aali.next());
        }
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is a List of Comparable objects to be sorted in an ascending
     *            order
     * 
     */
    public <T extends Comparable<T>> void sort(List<T> a) {
        Collections.sort(a);
    }

    /**
     * @param <T>
     *            data type
     * @param a
     *            is a List of Comparable objects to be sorted
     * @param c
     *            is the Comparator
     * 
     */
    public <T extends Comparable<T>> void sort(List<T> a, Comparator<T> c) {
        Collections.sort(a, c);
    }

    /**
     * 
     * @param p
     *            is the returned permuted index vector
     * @param a
     *            is the array to be sorted, based on which the permuted index
     *            vector is generated
     * @param from
     *            is the start index
     * @param to
     *            is the end index
     * 
     */
    public void sortp(int[] p, int[] a, int from, int to) {
        // FIXME: Just a simple bubble sort for now to keep it simple
        for (int i = from; i < to; i++)
            p[i - from] = i;
        for (int i = 0; i < to - from - 1; i++)
            for (int j = i + 1; i < to - from; j++)
                if (a[p[i]] > a[p[j]])
                    swap(p, i, j);
    }
}
