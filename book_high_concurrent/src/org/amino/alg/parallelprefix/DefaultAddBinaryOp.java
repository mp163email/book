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
 * Provide basic implementation for addition binary operation used in parallel
 * prefix package. For primitive data types, the parameterized type is ignored.
 * The user must extend this class to do something intelligent with the
 * non-primitive type
 * 
 * @param <T>
 *            Non-primitive type of elements being manipulated.
 * @author donawa
 * @see org.amino.alg.parallelprefix.ParallelPrefix
 * 
 */
public abstract class DefaultAddBinaryOp<T> implements BinaryOp<T> {
	/**
	  * {@inheritDoc}
	  */
	public byte transform(byte a, byte b) {
		return (byte) (a + b);
	}

	/**
	  * {@inheritDoc}
	  */
	public char transform(char a, char b) {
		return (char) (a + b);
	}

	/**
	  * {@inheritDoc}
	  */
	public short transform(short a, short b) {
		return (short) (a + b);
	}

	/**
	  * {@inheritDoc}
	  */
	public int transform(int a, int b) {
		return a + b;
	}

	/**
	  * {@inheritDoc}
	  */
	public long transform(long a, long b) {
		return a + b;
	}

	/**
	  * {@inheritDoc}
	  */
	public float transform(float a, float b) {
		return a + b;
	}

	/**
	  * {@inheritDoc}
	  */
	public double transform(double a, double b) {
		return a + b;
	}

	/**
	  * {@inheritDoc}
	  */
	public T transform(T a, T b) {
		assert (false);
		return null;
	} // extend if used
}
