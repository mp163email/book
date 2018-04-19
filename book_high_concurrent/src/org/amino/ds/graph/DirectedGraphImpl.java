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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Sample implementation of interface DirectedGraph. This graph is based on
 * adjacent list expression.
 * 
 * @param <E>
 *            Type of element in graph nodes
 */
public class DirectedGraphImpl<E> extends AbstractGraph<E> implements
		DirectedGraph<E> {

	/**
	 * {@inheritDoc}
	 */
	public boolean addEdge(Node<E> start, Node<E> end, double weight) {
		start = addNode(start);
		end = addNode(end);

		boolean add = false;
		AdjacentList<E> al = mainList.get(start.getValue());

		if (!simpleContentionManager(al))
			return false;
		add = al.addEdge(end, weight);
		al.freeOwnership();
		return add;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEdge(Node<E> start, Node<E> end) {
		return removeEdge(start.getValue(), end.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEdge(E start, E end) {
		AdjacentList<E> al = mainList.get(start);
		if (al == null)
			return false;

		boolean remove = false;
		if (!simpleContentionManager(al))
			return false;
		remove = al.removeEdge(end);
		al.freeOwnership();
		return remove;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEdge(Edge<E> edge) {
		return removeEdge(edge.getStart().getValue(), edge.getEnd().getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addEdge(Edge<E> edge) {
		return addEdge(edge.getStart(), edge.getEnd(), edge.getWeight());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addEdge(E start, E end, double weight) {
		addNode(start);
		Node<E> e = addNode(end);

		boolean add = false;
		AdjacentList<E> al = mainList.get(start);

		if (!simpleContentionManager(al))
			return false;
		add = al.addEdge(e, weight);
		al.freeOwnership();
		return add;

	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Node<E>> getDestinations(Node<E> node) {
		AdjacentList<E> al = mainList.get(node.getValue());
		if (al == null)
			return null;

		List<AdjacentNode<E>> adj = al.getLinkedNodes();
		List<Node<E>> nodes = new ArrayList<Node<E>>();

		for (int i = 0, len = adj.size(); i < len; ++i) {
			nodes.add(adj.get(i).getNode());
		}

		return nodes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Edge<E>> getIncoming(Node<E> node) {
		Enumeration<AdjacentList<E>> enu = mainList.elements();
		Collection<Edge<E>> incoming = new ArrayList<Edge<E>>();

		AdjacentList<E> al;
		AdjacentNode<E> adj;
		Edge<E> e;
		while (enu.hasMoreElements()) {
			al = enu.nextElement();
			List<AdjacentNode<E>> ln = al.getLinkedNodes();
			for (int i = 0; i < ln.size(); ++i) {
				adj = ln.get(i);
				if (adj.getNode().equals(node)) {
					e = new Edge(al.getHeadNode(), node, adj.getWeight());
					incoming.add(e);
				}
			}
		}
		return incoming;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Edge<E>> getOutgoing(Node<E> node) {
		AdjacentList<E> al = mainList.get(node.getValue());
		if (al == null)
			return null;
		return al.getLinkedEdges();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Node<E>> getSources(Node<E> node) {
		Enumeration<AdjacentList<E>> enu = mainList.elements();
		Collection<Node<E>> source = new ArrayList<Node<E>>();

		AdjacentList<E> al;
		AdjacentNode<E> adj;
//		AdjacentNode<E> ret;
		while (enu.hasMoreElements()) {
			al = enu.nextElement();
			List<AdjacentNode<E>> ln = al.getLinkedNodes();
			for (int i = 0; i < ln.size(); ++i) {
				adj = ln.get(i);
				if (adj.getNode().equals(node)) {
					source.add(al.getHeadNode());
				}
			}
		}
		return source;
	}

	/**
	 * {@inheritDoc}
	 */
	public Graph<E> clone() {

		Graph<E> g = new DirectedGraphImpl<E>();
		Collection<Node<E>> nodes = getAllNodes();
		Iterator<Node<E>> iter = nodes.iterator();

		while (iter.hasNext()) {
			Node<E> n = iter.next();
			E start = n.getValue();
			Collection<AdjacentNode<E>> adj = getLinkedNodes(n);
			Iterator<AdjacentNode<E>> adjIter = adj.iterator();
			while (adjIter.hasNext()) {
				AdjacentNode<E> adjNode = adjIter.next();
				g.addEdge(start, adjNode.getNode().getValue(), adjNode
						.getWeight());
			}
		}

		return g;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeNode(Node<E> node) {
		Enumeration<AdjacentList<E>> enu = mainList.elements();

		AdjacentList<E> al;

		while (enu.hasMoreElements()) {
			al = enu.nextElement();
			if (al.getHeadNode().equals(node))
				continue;
			if (!simpleContentionManager(al))
				return false;

			al.removeEdge(node);
			al.freeOwnership();
		}
		mainList.remove(node);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<AdjacentNode<E>> getWeightDestinations(Node<E> start) {
		AdjacentList<E> al = mainList.get(start.getValue());
		if (al == null)
			return null;
		return al.getLinkedNodes();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<AdjacentNode<E>> getWeightSources(Node<E> end) {
		Enumeration<AdjacentList<E>> enu = mainList.elements();
		Collection<AdjacentNode<E>> source = new ArrayList<AdjacentNode<E>>();

		AdjacentList<E> al;
		AdjacentNode<E> adj;
		AdjacentNode<E> ret;
		while (enu.hasMoreElements()) {
			al = enu.nextElement();
			List<AdjacentNode<E>> ln = al.getLinkedNodes();
			for (int i = 0; i < ln.size(); ++i) {
				adj = ln.get(i);
				if (adj.getNode().equals(end)) {
					ret = new AdjacentNode<E>(al.getHeadNode(), adj.getWeight());
					source.add(ret);
				}
			}
		}
		return source;
	}
}
