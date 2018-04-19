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
import java.util.Iterator;

/**
 * Undirected graph. In this graph, if there is an edge between node <i>A</i>
 * and node <i>B</i>, we can traverse from <i>A</i> to <i>B</i> and from
 * <i>B</i> to <i>A</i>. It's the same to say edge <i>A-B</i> or <i>B-A</i>.
 * 
 * This graph doesn't allow duplicated key for all nodes. The constrain comes
 * from internal structure contains a ConcurrentHashMap which uses
 * indexed by key.
 * 
 * The internal expression of graph is based on adjacent list. If we consider
 * symmetric property of adjacent list expression, we can save half of the
 * memory. But this trick will down grade time efficiency. We still store the
 * <b>complete</b> adjacent list.
 * 
 * @author Zhi Gan
 * 
 * @param <E>
 *            type of element inside the graph node
 */
public class UndirectedGraph<E> extends AbstractGraph<E> {

    /**
     * {@inheritDoc}
     */
    public boolean addEdge(E start, E end, double weight) {
        Node<E> s = addNode(start);
        Node<E> e = addNode(end);

        AdjacentList<E> alStart = mainList.get(start);
        AdjacentList<E> alEnd = mainList.get(end);

        AdjacentList[] targets = { alStart, alEnd };
        if (!simpleContentionManager(targets))
            return false;

        boolean b = true;
        if (!alStart.addEdge(e, weight))
            b = false;
        if (!alEnd.addEdge(s, weight))
            b = false;

        freeMultiOwnerShip(targets);
        return b;

    }

    /**
     * {@inheritDoc}
     */
    public boolean addEdge(Node<E> start, Node<E> end, double weight) {
        return addEdge(start.getValue(), end.getValue(), weight);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addEdge(Edge<E> edge) {
        return addEdge(edge.getStart().getValue(), edge.getEnd().getValue(),
                edge.getWeight());
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeEdge(Edge<E> edge) {
        Node<E> start = edge.getStart();
        Node<E> end = edge.getEnd();

        Edge<E> e2 = new Edge<E>(end, start, edge.getWeight());
        AdjacentList<E> alStart = mainList.get(start.getValue());
        AdjacentList<E> alEnd = mainList.get(end.getValue());

        if (alStart == null || alEnd == null)
            return false;

        AdjacentList[] targets = { alStart, alEnd };
        if (!simpleContentionManager(targets))
            return false;

        boolean b = alStart.removeEdge(edge) && alEnd.removeEdge(e2);

        freeMultiOwnerShip(targets);
        return b;
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
        AdjacentList<E> alStart = mainList.get(start);
        AdjacentList<E> alEnd = mainList.get(end);

        if (alStart == null || alEnd == null)
            return false;

        AdjacentList[] owner = { alStart, alEnd };
        if (!simpleContentionManager(owner))
            return false;
        else {
            boolean b = (alStart.removeEdge(end) && alEnd.removeEdge(start));
            freeMultiOwnerShip(owner);
            return b;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeNode(Node<E> node) {
        Collection<AdjacentNode<E>> lns = getLinkedNodes(node);
        Iterator<AdjacentNode<E>> iter = lns.iterator();
		//        Node<E> cur;

        while (iter.hasNext()) {
            Node<E> adj = iter.next().getNode();
            AdjacentList<E> adjlist = mainList.get(adj.getValue());
            // the node has been removed by other thread
            if (adjlist == null)
                continue;

            if (!simpleContentionManager(adjlist))
                return false;

            adjlist.removeEdge(adj);
            adjlist.freeOwnership();

        }
        mainList.remove(node.getValue());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Graph<E> clone() {

        Graph<E> g = new UndirectedGraph<E>();

        Collection<Node<E>> nodes = getAllNodes();
        Iterator<Node<E>> iter = nodes.iterator();

        while (iter.hasNext()) {
            Node<E> n = iter.next();
            g.addNode(n.getValue());
        }

        iter = nodes.iterator();
        while (iter.hasNext()) {
            Node<E> n = iter.next();

            AdjacentList<E> list = (AdjacentList<E>) ((AbstractGraph<E>) g).mainList
                    .get(n.getValue());

            Collection<AdjacentNode<E>> adj = getLinkedNodes(n);
            Iterator<AdjacentNode<E>> adjIter = adj.iterator();
            while (adjIter.hasNext()) {
                AdjacentNode<E> adjNode = adjIter.next();

                Node<E> node = ((AbstractGraph<E>) g).mainList.get(
                        adjNode.getNode().getValue()).getHeadNode();
                list.addEdge(node, adjNode.getWeight());
            }
        }

        return g;
    }

}
