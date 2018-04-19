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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class represents adjacent list, which is a frequently used method for
 * storing graph.
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            Type of elements contained in AdjacentList
 */
public class AdjacentList<E> implements Comparable<AdjacentList<E>> {

	private AtomicReference<Thread> owner;
	private Node<E> headNode;
	private List<AdjacentNode<E>> linkedNodes;

	/**
	 * Constructs an adjacent list with specified node as the head node.
	 * 
	 * @param head
	 *            the head node of this adjacent list
	 */
	public AdjacentList(Node<E> head) {
		headNode = head;
		linkedNodes = new ArrayList<AdjacentNode<E>>();
		owner = new AtomicReference<Thread>();

	}

	/**
	 * @return the nodes adjacent to head node
	 */
	public List<AdjacentNode<E>> getLinkedNodes() {
		return linkedNodes;
	}

	/**
	 * @return head node of this adjacent list
	 */
	public Node<E> getHeadNode() {
		return headNode;
	}

	/**
	 * add a specified edge into this adjacent list.
	 * 
	 * @param e
	 *            edge to add
	 * @return true if successful, or false
	 */
	public boolean addEdge(Edge<E> e) {
		if (!e.getStart().equals(headNode))
			return false;

		AdjacentNode<E> adjNode = new AdjacentNode<E>(e.getEnd(), e.getWeight());
		return linkedNodes.add(adjNode);
	}

	/**
	 * add a edge into this adjacent list, with specified node and weight.
	 * 
	 * @param node
	 *            the node connected with the head node of this adjacent list
	 * @param weight
	 *            weight of the edge to add
	 * @return true if successful, or else
	 */
	public boolean addEdge(Node<E> node, double weight) {

		AdjacentNode<E> adj = new AdjacentNode<E>(node, weight);
		return linkedNodes.add(adj);

	}

	// mutual operation
	/**
	 * remove a specified edge from this adjacent list.
	 * 
	 * @param e
	 *            the edge to remove
	 * @return true if successful, or false
	 */
	public boolean removeEdge(Edge<E> e) {
		if (!e.getStart().equals(headNode))
			return false;

		AdjacentNode<E> adjNode;
		for (int i = 0, len = linkedNodes.size(); i < len; ++i) {
			adjNode = linkedNodes.get(i);
			if (adjNode.getNode().equals(e.getEnd())
					&& adjNode.getWeight() == e.getWeight()) {
				linkedNodes.remove(i);
				return true;
			}
		}
		return false;
	}

	// remove all the edge to n
	/**
	 * remove all edges connected to specified node.
	 * 
	 * @param n
	 *            the node with edges to be removed
	 * @return true if any edge removed, or false
	 */
	public boolean removeEdge(Node<E> n) {
		AdjacentNode<E> adj;
		boolean remove = false;

		Iterator<AdjacentNode<E>> iter = linkedNodes.iterator();
		while (iter.hasNext()) {
			adj = iter.next();
			if (adj.getNode().equals(n)) {
				iter.remove();
				remove = true;
			}
		}
		return remove;
	}

	// remove all the edge to n
	/**
	 * remove specified edge from this adjacent list.
	 * 
	 * @param e
	 *            the edge to remove
	 * @return true if successful, or false
	 */
	public boolean removeEdge(E e) {
		AdjacentNode<E> adj;
		boolean remove = false;

		Iterator<AdjacentNode<E>> iter = linkedNodes.iterator();
		while (iter.hasNext()) {
			adj = iter.next();
			if (adj.getNode().getValue().equals(e)) {
				iter.remove();
				remove = true;
			}
		}
		return remove;
	}

	/**
	 * @return true if ownership got, or false
	 */
	public boolean getOwnership() {
		Thread t = Thread.currentThread();
		Thread owner_t = owner.get();
		if (owner_t == t)
			return true;

		if (owner_t != null)
			return false;

		return owner.compareAndSet(null, t);
	}

	/**
	 * free the ownership.
	 * 
	 * @return true if ownership freed
	 */
	public boolean freeOwnership() {
		Thread t = Thread.currentThread();
		if (owner.get() == t) {
			owner.set(null);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(AdjacentList<E> o) {
		return headNode.compareTo(o.headNode);
	}

	/**
	 * return all the edge linked to node n.
	 * 
	 * @param n
	 *            the target node
	 * @return the collection of edges linked to node n
	 */

	public Collection<Edge<E>> getEdge(Node<E> n) {
		Collection<Edge<E>> es = new ArrayList<Edge<E>>();
		AdjacentNode<E> adj;
		for (int i = 0; i < linkedNodes.size(); ++i) {
			adj = linkedNodes.get(i);
			if (adj.getNode().equals(n)) {
				Edge<E> e = new Edge<E>(headNode, n, adj.getWeight());
				es.add(e);
			}
		}
		return es;
	}

	/**
	 * return all the edges whose one end has the value t in this adjacent list.
	 * 
	 * @param t
	 *            target value
	 * @return the collection of edges
	 */
	public Collection<Edge<E>> getEdge(E t) {
		Collection<Edge<E>> es = new ArrayList<Edge<E>>();
		AdjacentNode<E> adj;
		for (int i = 0; i < linkedNodes.size(); ++i) {
			adj = linkedNodes.get(i);
			if (adj.getNode().getValue().equals(t)) {
				Edge<E> e = new Edge<E>(headNode, adj.getNode(), adj
						.getWeight());
				es.add(e);
			}
		}
		return es;
	}

	/**
	 * finds in this adjacent list whether there is an edge that has specified
	 * node value.
	 * 
	 * @param e
	 *            target value
	 * @return true if edge exists, or false
	 */
	public boolean containsEdge(E e) {
		AdjacentNode<E> adj;
		for (int i = 0; i < linkedNodes.size(); ++i) {
			adj = linkedNodes.get(i);
			if (adj.getNode().getValue().equals(e)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * finds whether the head node of this adjacent list links to the specified
	 * node e.
	 * 
	 * @param e
	 *            target node
	 * @return true if the edge exists, or false
	 */
	public boolean containsEdge(Node<E> e) {
		AdjacentNode<E> adj;
		for (int i = 0; i < linkedNodes.size(); ++i) {
			adj = linkedNodes.get(i);
			if (adj.getNode().equals(e)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get all linked edges implied by this adjacent list.
	 * 
	 * @return the collection of edges
	 */
	public Collection<Edge<E>> getLinkedEdges() {
		Collection<Edge<E>> cs = new ArrayList<Edge<E>>();

		AdjacentNode<E> adj;
		Edge<E> e;
		for (int i = 0; i < linkedNodes.size(); ++i) {
			adj = linkedNodes.get(i);
			e = new Edge<E>(headNode, adj.getNode(), adj.getWeight());
			cs.add(e);
		}

		return cs;
	}
}
