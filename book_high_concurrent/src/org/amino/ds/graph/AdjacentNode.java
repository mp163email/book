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
 * Objects of this class is used to express nodes in an adjacent list.
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            Type of elements contained in AdjacentNode
 */
public class AdjacentNode<E> {
	private final Node<E> node;
	private final double weight;

	/**
	 * @return the "node" of this adjacent node, adjacent to head node
	 */
	public Node<E> getNode() {
		return node;
	}

	/**
	 * @return weight of this adjacent node, namely, weight of the edge from
	 *         head node to this node
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Constructs an adjacent node, with specified node and weight.
	 * 
	 * @param node
	 *            the node
	 * @param weight
	 *            weight of the node
	 */
	public AdjacentNode(Node<E> node, double weight) {
		this.node = node;
		this.weight = weight;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof AdjacentNode))
			return false;
		AdjacentNode adj = (AdjacentNode) o;
		return node.equals(adj.node) && weight == adj.weight;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return node.toString();
	}

}
