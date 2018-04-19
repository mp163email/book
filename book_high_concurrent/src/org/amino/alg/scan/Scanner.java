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
 * Interface for searching byte arrays.
 * 
 * @author donawa
 * 
 */
public interface Scanner {
	/**
	 * Find the index of the first occurrence of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * 
	 * @return The index of the first occurrence or a negative value if not
	 *         found.
	 */
	int findFirst(byte[] a, byte v);

	/**
	 * Find the index of the last occurrence of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * 
	 * @return The index of the last occurrence or a negative value if not
	 *         found.
	 */
	int findLast(byte[] a, byte v);

	/**
	 * Find the next index of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param index
	 *            The starting index for the search.
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	int findNext(byte[] a, byte v, int index);

	/**
	 * Find the previous index of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param index
	 *            The starting index for the search (exclusive, searching
	 *            backward).
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	int findPrevious(byte[] a, byte v, int index);

	/**
	 * Find the index of the any occurrence of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * 
	 * @return The index of the an occurrence or a negative value if not found.
	 */
	int findAny(byte[] a, byte v);

	/**
	 * Find the index of the any occurrence of the given value in an array
	 * segment.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param from
	 *            starting index for search (inclusive)
	 * @param to
	 *            ending index for search (exclusive)
	 * 
	 * @return The index of the an occurrence or a negative value if not found.
	 */
	int findAny(byte[] a, byte v, int from, int to);

	/**
	 * Find the indices of all occurrences of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * 
	 * @return The array of indices of occurrences or null if none found.
	 */
	int[] findAll(byte[] a, byte v);

	/**
	 * Replace all occurrences of the search value with the replacement value.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param r
	 *            The replacement value.
	 * 
	 * @return The number of occurrences found and replaced.
	 */
	int replaceAll(byte[] a, byte v, byte r);

	/**
	 * Find the indices of all occurrences of the given value in an array
	 * segment.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param from
	 *            starting index for search (inclusive)
	 * @param to
	 *            ending index for search (exclusive)
	 * 
	 * @return The array of indices of occurrences or null if none found.
	 */
	int[] findAll(byte[] a, byte v, int from, int to);

	/**
	 * Replace all occurrences of the search value with the replacement value.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * @param r
	 *            The replacement sequence.
	 * @param from
	 *            starting index for search (inclusive)
	 * @param to
	 *            ending index for search (exclusive)
	 * 
	 * @return The number of occurrences found and replaced.
	 */
	int replaceAll(byte[] a, byte v, byte r, int from, int to);

	/**
	 * Find the index of the first occurrence of the given sequence in the
	 * array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * 
	 * @return The index of the first occurrence or a negative value if not
	 *         found.
	 */
	int findFirst(byte[] a, byte[] v);

	/**
	 * Find the index of the last occurrence of the given sequence in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * 
	 * @return The index of the last occurrence or a negative value if not
	 *         found.
	 */
	int findLast(byte[] a, byte[] v);

	/**
	 * Find the next index of the given sequence in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * @param index
	 *            The starting index for the search (inclusive).
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	int findNext(byte[] a, byte[] v, int index);

	/**
	 * Find the previous index of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param index
	 *            The starting index for the search (exclusive, searching
	 *            backward).
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	int findPrevious(byte[] a, byte[] v, int index);

	/**
	 * Find the index of the any occurrence of the given sequence in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * 
	 * @return The index of the an occurrence or a negative value if not found.
	 */
	int findAny(byte[] a, byte[] v);

	/**
	 * Find the index of the any occurrence of the given sequence in an array
	 * segment.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * @param from
	 *            starting index for search (inclusive)
	 * @param to
	 *            ending index for search (exclusive)
	 * 
	 * @return The index of the an occurrence or a negative value if not found.
	 */
	int findAny(byte[] a, byte[] v, int from, int to);

	/**
	 * Find the index of the any occurrence of the given sequence in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * 
	 * @return The array of indices of occurrences or null if none found.
	 */
	int[] findAll(byte[] a, byte[] v);

	/**
	 * Replace all occurrences of the search sequence with the replacement
	 * sequence. The search and replacement sequence must be identical in
	 * length.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * @param r
	 *            The replacement sequence.
	 * 
	 * @return The number of occurrences found and replaced.
	 */
	int replaceAll(byte[] a, byte[] v, byte[] r);

	/**
	 * Find the index of the any occurrence of the given sequence in an array
	 * segment.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * @param from
	 *            starting index for search (inclusive)
	 * @param to
	 *            ending index for search (exclusive)
	 * 
	 * @return The array of indices of occurrences or null if none found.
	 */
	int[] findAll(byte[] a, byte[] v, int from, int to);

	/**
	 * Replace all occurrences of the search sequence with the replacement
	 * sequence.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The sequence to search for.
	 * @param r
	 *            The replacement sequence.
	 * @param from
	 *            starting index for search (inclusive)
	 * @param to
	 *            ending index for search (exclusive)
	 * 
	 * @return The number of occurrences found and replaced.
	 */
	int replaceAll(byte[] a, byte[] v, byte[] r, int from, int to);

	/**
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for
	 * @return index of the search value if it succeeds; otherwise,
	 *         (-(<i>insertion point</i>) - 1)
	 */
	int binarySearch(byte[] a, byte v);

	/**
	 * Search a byte value from byte-array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param from
	 *            starting index for search (inclusive)
	 * @param to
	 *            ending index for search (exclusive)
	 * @param v
	 *            The value to search for
	 * @return index of the search value if it succeeds; otherwise,
	 *         (-(<i>insertion point</i>) - 1).
	 */
	int binarySearch(byte[] a, int from, int to, byte v);
}
