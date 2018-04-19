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
 *
 * @param <T> data type
 */
public abstract class AbstractParallelPrefix<T> implements ParallelPrefix<T> {


	
	/**
	 * {@inheritDoc}
	 */
	public void scan(int[] array,int[] outputArray, BinaryOp<T> op){
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void scan(char[] array,char[] outputArray, BinaryOp<T> op){
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void scan(byte[] array,byte[] outputArray, BinaryOp<T> op){
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void scan(long[] array,long[] outputArray, BinaryOp<T> op){
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void scan(float[] array,float[] outputArray, BinaryOp<T> op){
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void scan(double[] array,double[] outputArray, BinaryOp<T> op){
		// scan serial (slow) implementation.
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public void scan(short[] array,short[] outputArray, BinaryOp<T> op){
		// scan serial (slow) implementation.
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void scan(T[] array,T[] outputArray, BinaryOp<T> op){
		// scan serial (slow) implementation.
		outputArray[0] = array[0];
		for(int i = 1; i < array.length;++i){
			outputArray[i] = op.transform(outputArray[i-1],array[i]);
		}
	}

}

