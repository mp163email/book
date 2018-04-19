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

/* Emulate sorting interfaces from java.util.Arrays and java.util.Collections */

/**
 * <p>
 * <b>Interface Sorter</b>
 * </p>
 * 
 * <p>
 * An interface provided by various sorting classes.
 * </p>
 * 
 * <p>
 * Interface methods operate on arrays and List collections. Element types for
 * arrays may be builtin types or objects
 * </p>
 * 
 * @see java.util.Arrays
 * @see java.util.Collections
 */
public interface Sorter {
    /**
     * 
     * @param a
     *            array upon which to operate.
     * @see java.util.Arrays#sort(byte[])
     */
    void sort(byte[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     */
    void reverse(byte[] a);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index starting array index
     * @param to
     *            ending array index
     * @see java.util.Arrays#sort(byte[], int, int)
     */
    void sort(byte[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index starting array index
     * @param to
     *            ending array index ending array index
     */
    void reverse(byte[] a, int from, int to);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @see java.util.Arrays#sort(byte[])
     */
    void sort(char[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     */
    void reverse(char[] a);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index starting array index
     * @param to
     *            ending array index ending array index
     * @see java.util.Arrays#sort(byte[], int, int)
     */
    void sort(char[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index starting array index
     * @param to
     *            ending array index
     */
    void reverse(char[] a, int from, int to);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @see java.util.Arrays#sort(short[])
     */
    void sort(short[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     */
    void reverse(short[] a);

    /**
     * @see java.util.Arrays#sort(short[], int, int)
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    void sort(short[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    void reverse(short[] a, int from, int to);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @see java.util.Arrays#sort(short[])
     */
    void sort(int[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     */
    void reverse(int[] a);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @see java.util.Arrays#sort(short[], int, int)
     */
    void sort(int[] a, int from, int to);

    /**
     * Sort array and return a permuted index vector.
     * 
     * @param p
     *            the array
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    void sortp(int[] p, int[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    void reverse(int[] a, int from, int to);

    /**
     * @see java.util.Arrays#sort(short[])
     * @param a
     *            array upon which to operate.
     */
    void sort(long[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     */
    void reverse(long[] a);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @see java.util.Arrays#sort(short[], int, int)
     */
    void sort(long[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    void reverse(long[] a, int from, int to);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @see java.util.Arrays#sort(short[])
     */
    void sort(float[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     */
    void reverse(float[] a);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @see java.util.Arrays#sort(short[], int, int)
     */
    void sort(float[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * 
     */
    void reverse(float[] a, int from, int to);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @see java.util.Arrays#sort(short[])
     */
    void sort(double[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * 
     */
    void reverse(double[] a);

    /**
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @see java.util.Arrays#sort(short[], int, int)
     */
    void sort(double[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    void reverse(double[] a, int from, int to);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @see java.util.Arrays#sort(Object[])
     */
    <T extends Comparable<T>> void sort(T[] a);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     */
    <T extends Comparable<T>> void reverse(T[] a);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @see java.util.Arrays#sort(Object[], int, int)
     */
    <T extends Comparable<T>> void sort(T[] a, int from, int to);

    /**
     * Sort array using reverse natural (descending) order.
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    <T extends Comparable<T>> void reverse(T[] a, int from, int to);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param c
     *            comparator used to do comparison
     * @see java.util.Arrays#sort(Object[], Comparator)
     */
    <T extends Comparable<T>> void sort(T[] a, Comparator<T> c);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @param c
     *            comparator used to do comparison
     * @see java.util.Arrays#sort(Object[], int, int, Comparator)
     */
    <T extends Comparable<T>> void sort(T[] a, int from, int to, Comparator<T> c);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @see java.util.Collections#sort(List)
     */
    <T extends Comparable<T>> void sort(List<T> a);

    /**
     * Sort List using reverse natural (descending) order.
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     */
    <T extends Comparable<T>> void reverse(List<T> a);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @see java.util.Collections#sort(List)
     */
    <T extends Comparable<T>> void sort(List<T> a, int from, int to);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     */
    <T extends Comparable<T>> void reverse(List<T> a, int from, int to);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param c
     *            comparator used to do comparison
     * @see java.util.Collections#sort(List, Comparator)
     */
    <T extends Comparable<T>> void sort(List<T> a, Comparator<T> c);

    /**
     * 
     * @param <T>
     *            data type
     * @param a
     *            array upon which to operate.
     * @param from
     *            starting array index
     * @param to
     *            ending array index
     * @param c
     *            comparator used to do comparison
     * @see java.util.Collections#sort(List, Comparator)
     */
    <T extends Comparable<T>> void sort(List<T> a, int from, int to,
            Comparator<T> c);
}
