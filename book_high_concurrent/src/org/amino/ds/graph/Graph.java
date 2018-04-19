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
 * A Graph provides basic operations to create, search and change itself.
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            Type of elements
 */
public interface Graph<E> extends Collection<E>, Cloneable {
    /**
     * get all nodes whose value equal to <code>e</code>.
     * 
     * @param e
     *            value to be got
     * @return a collection of nodes
     */
    Collection<Node<E>> getNodes(E e);

    /**
     * get all the nodes in the graph.
     * 
     * @return a collection of all the nodes in the graph
     */
    Collection<Node<E>> getAllNodes();

    /**
     * get all all the edges which start from node start and end with node end.
     * 
     * @param start
     *            the start node of the edge
     * @param end
     *            the end node of the edge
     * @return collection of all the edges which start from node start and end
     *         with node end
     */
    Collection<Edge<E>> getEdges(Node<E> start, Node<E> end);

    /**
     * get all the edge start from the nodes which contain value start and end.
     * to nodes which contain value end
     * 
     * @param start
     *            start value
     * @param end
     *            end value
     * @return collection of all the edge start from the nodes which contain
     *         value start and end to nodes which contain value end
     */
    Collection<Edge<E>> getEdges(E start, E end);

    /**
     * Add a node to graph, which contains value e. If the tree already contains
     * <code>e</code>, return the existing node instead of creating a new one. <br>
     * FIXME: This method doesn't tell if a node is created or not. We need to
     * add an <code>boolean addIfAbsent(E e)</code> method?
     * 
     * @param e
     *            the value to add.
     * 
     * @return a node in graph which contains the value
     */
    Node<E> addNode(E e);

    /**
     * Add a node to graph, which contains value e. If the tree already contains
     * <code>e</code>, return the existing node instead of creating a new one. <br>
     * 
     * @param node
     *            the node to add
     * 
     * @return return adding <code>node</code> if succeed. If there is already a
     *         graph node has the same value as <code>node</code>, return node
     *         in graph.
     */
    Node<E> addNode(Node<E> node);

    /**
     * add all the nodes to graph.
     * 
     * @param nodes
     *            nodes to be added
     * @return true if the operation is successful
     */
    boolean addAllNodes(Collection<Node<E>> nodes);

    /**
     * add one edge to graph.
     * 
     * @param start
     *            value in start node. A new node will be added to graph is this
     *            value is not in graph yet.
     * @param end
     *            value in end node. A new node will be added to graph is this
     *            value is not in graph yet.
     * @param weight
     *            weight of this edge
     * 
     * @return true if the operation is successful
     */
    boolean addEdge(E start, E end, double weight);

    /**
     * add one edge to graph with weight.
     * 
     * @param start
     *            start node. A new node will be added to graph is this value is
     *            not in graph yet.
     * @param end
     *            end node. A new node will be added to graph is this value is
     *            not in graph yet.
     * @param weight
     *            weight of this edge
     * 
     * @return true if the operation is successful
     */
    boolean addEdge(Node<E> start, Node<E> end, double weight);

    /**
     * Add an edge to this graph.
     * 
     * @param edge
     *            adding edge
     * @return true if succeed
     */
    boolean addEdge(Edge<E> edge);

    /**
     * get all nodes which directly linked to <code>node</code>.
     * 
     * @param node
     *            start node
     * @return collection of all linked nodes
     */
    Collection<AdjacentNode<E>> getLinkedNodes(Node<E> node);

    /**
     * get all edges directly linked to the specified <code>node</code>.
     * 
     * @param node
     *            start node
     * @return collection of all linked edges
     */
    Collection<Edge<E>> getLinkedEdges(Node<E> node);

    /**
     * remove all the edges which start from <code>start</code> and end to
     * <code> end</code>.
     * 
     * @param start
     *            start node
     * @param end
     *            end node
     * @return true if the operation is successful
     */
    boolean removeEdge(Node<E> start, Node<E> end);

    /**
     * remove all the edges which start from start and end to end.
     * 
     * @param edge
     *            edge removed
     * @return true if the operation is successful
     */
    boolean removeEdge(Edge<E> edge);

    /**
     * remove all the edges which start from start and end to end.
     * 
     * @param start
     *            start value
     * @param end
     *            end value
     * @return true if the operation is successful
     */
    boolean removeEdge(E start, E end);

    /**
     * remove the node from the graph.
     * 
     * @param node
     *            node removed
     * @return true if the operation is successful
     */
    boolean removeNode(Node<E> node);

    /**
     * Returns whether this graph contains an edge between start and end.
     * 
     * @param start
     *            starting node of edge
     * @param end
     *            ending node of edge
     * @return true if this graph contains edge between start and end, false
     *         otherwise.
     */
    boolean containsEdge(E start, E end);

    /**
     * Clone this graph.
     * 
     * @return a graph
     * @throws CloneNotSupportedException
     *             clone not supported
     */
    Graph<E> clone() throws CloneNotSupportedException;

    /**
     * whether this graph contains the specified node.
     * 
     * @param start
     *            the node
     * @return true if this graph contains the node
     */
    boolean containsNode(Node<E> start);
}
