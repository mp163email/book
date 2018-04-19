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
 * Interface for parallel prefix binary operation.
 *
 * @param <T> Type for non-primitive binary operations.
 * @author donawa
 * @see ParallelPrefix
 */
public interface BinaryOp<T> {
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	byte transform(byte a,byte b);
	
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	char transform(char a,char b);
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	short transform(short a,short b);
	
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	int transform(int a,int b);
	
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	long transform(long a,long b);
	
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	float transform(float a,float b);
	
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	double transform(double a,double b);
	
	/**
	 * Transform two bytes to one.
	 * 
	 * @param a op one
	 * @param b op two
	 * @return retsult
	 */
	T transform(T a, T b);
}
