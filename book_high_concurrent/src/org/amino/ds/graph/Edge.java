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
 * Edge in the graph.
 *
 * @author Zhi Gan
 *
 * @param <E>
 *            type element in node
 */
public class Edge<E> {
    private final Node<E> start;
    private final Node<E> end;
    private double weight;

    /**
     * Constructor an edge with default edge 1.
     *
     * @param start
     *            start node
     * @param end
     *            end node
     */
    public Edge(Node<E> start, Node<E> end) {
        this.start = start;
        this.end = end;
        weight = 1;
    }

    /**
     * Construct an edge with weight.
     *
     * @param start
     *            start node
     * @param end
     *            end node
     * @param weight
     *            weight on edge
     *
     */
    public Edge(Node<E> start, Node<E> end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    /**
     * @return start node
     */
    public Node<E> getStart() {
        return start;
    }

    /**
     * @return end node
     */
    public Node<E> getEnd() {
        return end;
    }

    /**
     * @return weight of this edge
     */
    public double getWeight() {
        return weight;
    }

    /**
     * set weight of this edge.
     * @param weight weight to be set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
