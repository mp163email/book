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

package org.amino.ds.graph;

/**
 * Node in the graph.
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            type of element in node
 */
public class Node<E> implements Comparable<Node<E>> {
	private E value;

	/**
	 * used to compare two node.
	 */
	private int compare;

	/**
	 * Constructs a graph node, with specified element e.
	 * 
	 * @param e
	 *            element in node
	 */
	public Node(E e) {
		value = e;
	}

	/**
	 * @return the value of this node
	 */
	public E getValue() {
		return value;
	}

	/**
	 * set the value of this node.
	 * 
	 * @param value
	 *            the value to be set
	 */
	public void setValue(E value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Node<E> o) {
		if (compare > o.compare)
			return 1;
		else if (compare == o.compare)
			return 0;
		else
			return -1;
	}

	/**
	 * Get the value used to compare.
	 * 
	 * @return the compare that is used to compare two nodes
	 */
	int getCompare() {
		return compare;
	}

	/**
	 * Set the value used to compare.
	 * 
	 * @param compareV
	 *            set the value of this node. This value is used by compareTo()
	 *            method.
	 */
	void setCompare(int compareV) {
		this.compare = compareV;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object n) {
		if (!(n instanceof Node))
			return false;
		return this.value.equals(((Node) n).value);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return value.toString();
	}
}
