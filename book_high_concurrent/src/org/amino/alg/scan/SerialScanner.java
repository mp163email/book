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
 * Searching and comparing strings.
 * 
 * @author donawa
 * 
 */
class SerialScanner extends AbstractScanner {

	/**
	 * {@inheritDoc}
	 */
	public int binarySearch(byte[] a, int from, int to, byte v) {
		// return java.util.Arrays.binarySearch(a,from,to,v); not available in
		// EE

		int range = to - from;
		if (range < 5) {
			for (int i = from; i < from + range; ++i) {
				if (a[i] == v)
					return i;
			}
			return -1;
		}
		int pivot = from + range / 2;
		byte pivotValue = a[pivot];

		if (v < pivotValue)
			return binarySearch(a, from, pivot, v);
		else
			return binarySearch(a, pivot + 1, to, v);

	}

	/**
	 * {@inheritDoc}
	 */
	public int[] findAll(byte[] a, byte v, int from, int to) {
		int[] resultArray = null;
		int resultIndex = 0;
		for (int i = from; i < to; ++i) {
			if (a[i] == v) {
				if (null == resultArray)
					resultArray = new int[to - i];
				resultArray[resultIndex++] = i;
			}
		}

		// Trim the array if necessary
		if (resultArray != null && resultArray.length > resultIndex) {
			int[] temp = new int[resultIndex];
			System.arraycopy(resultArray, 0, temp, 0, temp.length);
			resultArray = temp;
		}
		return resultArray;
	}

	/**
	 * {@inheritDoc}
	 */
	public int[] findAll(byte[] a, byte[] v, int from, int to) {

		int[] resultArray = null;
		int resultIndex = 0;
		to = to - (v.length - 1); // search sequence must lie within searched
									// region
		for (int i = from; i < to; ++i) {
			boolean foundMatch = true;
			for (int j = 0; j < v.length; ++j) {
				if (a[i + j] != v[j]) {
					foundMatch = false;
					break;
				}
			}
			// entire sequence matched
			if (foundMatch) {
				if (null == resultArray)
					resultArray = new int[to - i];

				resultArray[resultIndex++] = i;
				i += v.length - 1; // avoid matching overlapping strings
			}
		}

		// Trim the array if necessary
		if (resultArray != null && resultArray.length > resultIndex) {
			int[] temp = new int[resultIndex];
			System.arraycopy(resultArray, 0, temp, 0, temp.length);
			resultArray = temp;
		}
		return resultArray;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findAny(byte[] a, byte v, int from, int to) {
		// Cannot assume a[] is sorted.
		for (int i = from; i < to; ++i)
			if (a[i] == v)
				return i;
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findAny(byte[] a, byte[] v, int from, int to) {

		for (int i = from; i < to; ++i) {
			boolean substringMatch = true;
			for (int j = 0; j < v.length; ++j) {
				if (a[i + j] != v[j]) {
					substringMatch = false;
					break;
				}
			}
			// we have a match
			if (substringMatch)
				return i;
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findNext(byte[] a, byte v, int index) {

		return findNext(a, v, index, a.length);
	}

	/**
	 * Find the next index of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param from
	 *            The starting index for the search.
	 * @param to
	 *            The ending index for the search.
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	protected int findNext(byte[] a, byte v, int from, int to) {
		for (int i = from; i < to; ++i) {
			if (a[i] == v) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findNext(byte[] a, byte[] v, int index) {
		return findNext(a, v, index, a.length);
	}

	/**
	 * Find the next index of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param from
	 *            The starting index for the search.
	 * @param to
	 *            The ending index for the search.
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	protected int findNext(byte[] a, byte[] v, int from, int to) {
		for (int i = from; i < to; ++i) {
			boolean foundMatch = true;
			for (int j = 0; j < v.length; ++j) {
				if (a[i + j] != v[j]) {
					foundMatch = false;
					break;
				}
			}
			if (foundMatch) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findPrevious(byte[] a, byte v, int index) {
		return findPrevious(a, v, 0, index);
	}

	/**
	 * Find the previous index of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param from
	 *            The starting index for the search (exclusive, searching
	 *            backward).
	 * @param to
	 *            The ending index for the search (exclusive, searching
	 *            backward).
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	protected int findPrevious(byte[] a, byte v, int from, int to) {

		for (int i = to - 1; i >= from; --i) {
			if (a[i] == v)
				return i;
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findPrevious(byte[] a, byte[] v, int index) {
		return findPrevious(a, v, 0, index);
	}

	/**
	 * Find the previous index of the given value in the array.
	 * 
	 * @param a
	 *            The array to search.
	 * @param v
	 *            The value to search for.
	 * @param from
	 *            The starting index for the search (exclusive, searching
	 *            backward).
	 * @param to
	 *            The ending index for the search (exclusive, searching
	 *            backward).
	 * 
	 * @return The index of the occurrence or a negative value if not found.
	 */
	protected int findPrevious(byte[] a, byte[] v, int from, int to) {
		for (int i = to - v.length; i >= from; --i) {
			boolean foundMatch = true;
			for (int j = 0; j < v.length; ++j) {
				if (a[i + j] != v[j]) {
					foundMatch = false;
					break;
				}
			}
			if (foundMatch) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public int replaceAll(byte[] a, byte v, byte r, int from, int to) {
		int[] locations = findAll(a, v, from, to);
		if (null == locations)
			return 0;

		for (int i = 0; i < locations.length; ++i) {
			int index = locations[i];
			assert (a[index] == v);
			a[index] = r;
		}

		return locations.length;
	}

	/**
	 * {@inheritDoc}
	 */
	public int replaceAll(byte[] a, byte[] v, byte[] r, int from, int to) {
		assert (v.length == r.length);
		int[] locations = findAll(a, v, from, to);
		if (null == locations)
			return 0;

		for (int i = 0; i < locations.length; ++i) {
			int index = locations[i];
			boolean foundMatch = true;
			for (int j = 0; j < v.length; ++j) {
				if (a[index + j] != v[j]) {
					foundMatch = false;
					break;
				}

			}
			if (foundMatch) {
				for (int j = 0; j < v.length; ++j) {
					assert (a[index + j] == v[j]);
					a[index + j] = r[j];
				}
			}

		}

		return locations.length;
	}

}