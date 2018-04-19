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

import java.util.Collection;

/**
 * Interface of directed graph. In this graph, if there is an edge between node
 * <i>A</i> and node <i>B</i>, we can traverse only from <i>A</i> to <i>B</i>.
 * We can't traverse from <i>B</i> to <i>A</i>. It's different to say edge
 * <i>A-B</i> or <i>B-A</i>.
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            type of element in the node of the graph
 * 
 */
public interface DirectedGraph<E> extends Graph<E> {

	/**
	 * Get weighted edges started with start node.
	 * 
	 * @param start
	 *            start node
	 * @return collection of weighted edges started with start node
	 */
	Collection<AdjacentNode<E>> getWeightDestinations(Node<E> start);

	/**
	 * Get nodes started with start node.
	 * 
	 * @param start
	 *            start node
	 * @return collection of nodes started with start node
	 */
	Collection<Node<E>> getDestinations(Node<E> start);

	/**
	 * Get nodes end with end node.
	 * 
	 * @param end
	 *            end node
	 * @return collection of nodes ended with end node
	 */
	Collection<Node<E>> getSources(Node<E> end);

	/**
	 * Get weighted edges ended with end node.
	 * 
	 * @param end
	 *            end node
	 * @return collection of weighted edges ended with end node
	 */
	Collection<AdjacentNode<E>> getWeightSources(Node<E> end);

	/**
	 * Get edges ended with end node.
	 * 
	 * @param node
	 *            end node
	 * @return collection of edges ended with node
	 */
	Collection<Edge<E>> getIncoming(Node<E> node);

	/**
	 * Get edges started with start node.
	 * 
	 * @param node
	 *            start node
	 * @return collection of edges started with start node
	 */
	Collection<Edge<E>> getOutgoing(Node<E> node);
}
