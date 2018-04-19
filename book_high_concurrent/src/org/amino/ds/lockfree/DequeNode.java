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

package org.amino.ds.lockfree;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal Deque node class. This class is used by both EBDeque and
 * LockFreeDeque.
 * 
 * @param <E>
 *            type of element inside node
 */
class DequeNode<E> {
	/**
	 * Data on the node.
	 */
	final E data;
	/**
	 * Right pointer.
	 */
	AtomicReference<DequeNode<E>> right;
	/**
	 * Left Pointer.
	 */
	AtomicReference<DequeNode<E>> left;

	/**
	 * @param d
	 *            default value of element
	 */
	public DequeNode(E d) {
		this.data = d;
		this.right = new AtomicReference<DequeNode<E>>();
		this.left = new AtomicReference<DequeNode<E>>();
	}

	/**
	 * @param r
	 *            right node of node
	 */
	public void setRight(DequeNode<E> r) {
		this.right.set(r);
	}

	/**
	 * @param l
	 *            left node of node
	 */
	public void setLeft(DequeNode<E> l) {
		this.left.set(l);
	}

}
