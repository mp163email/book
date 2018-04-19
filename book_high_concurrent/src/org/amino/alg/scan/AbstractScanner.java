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
 * Abstract class for searching byte arrays.
 *
 * @author donawa
 *
 */
public abstract class AbstractScanner implements Scanner {
	/**
	 * {@inheritDoc}
	 */
	public int binarySearch(byte[] a, byte v) {
		return binarySearch(a,0,a.length,v);
	}

	/**
	 * {@inheritDoc}
	 */
	public int[] findAll(byte[] a, byte v) {
		return findAll(a,v,0,a.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int[] findAll(byte[] a, byte[] v) {
		return findAll(a,v,0,a.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int findAny(byte[] a, byte v) {
		return findAny(a,v,0,a.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int findAny(byte[] a, byte[] v) {
		return findAny(a,v,0,a.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int findFirst(byte[] a, byte v) {
		return findNext(a,v,0);
	}

	/**
	 * {@inheritDoc}
	 */
	public int findFirst(byte[] a, byte[] v) {
		return findNext(a,v,0);
	}

	/**
	 * {@inheritDoc}
	 */
	public int findLast(byte[] a, byte v) {
		return findPrevious(a,v,a.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int findLast(byte[] a, byte[] v) {
		return findPrevious(a,v,a.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int replaceAll(byte[] a, byte v, byte r) {
		return replaceAll(a,v,r,0,a.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public int replaceAll(byte[] a, byte[] v, byte[] r) {
		return replaceAll(a,v,r,0,a.length);
	}
}
