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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * This is a thread-safe and lock-free dictionary. This lock free concurrent
 * dictionary implementation is based on the algorithm defined in the follwoing
 * paper: Scalable and Lock-Free Concurrent Dictionaries By Hakan Sundell and
 * Philippas Tsigas
 * 
 * @author raja
 * 
 * @param <K>
 *            type of key in the dictionary
 * @param <V>
 *            type of value in the dictionary
 */
public class LockFreeDictionary<K, V> implements Map<K, V> {

	/**
	 * Internal Dictionary node class.
	 * 
	 * @param <K>
	 *            type of key in dictionary
	 * @param <V>
	 *            type of value in dictionary
	 */
	private static class Node<K, V> {
		int level, validLevel, version;
		AtomicMarkableReference<V> data;
		Node<K, V> prev;
		ArrayList<AtomicMarkableReference<Node<K, V>>> next;
		K key;

		/**
		 * default constructor.
		 */
		public Node() {
			this.data = new AtomicMarkableReference<V>(null, false);
			this.prev = null;
			this.next = new ArrayList<AtomicMarkableReference<Node<K, V>>>(
					MAXLEVEL);
			this.version = 0;
			this.key = null;
			// FIXME level is not initialized
		}

		/**
		 * @param l
		 *            random level
		 * @param k
		 *            key
		 * @param d
		 *            data
		 */
		public Node(int l, K k, V d) {
			this.data = new AtomicMarkableReference<V>(d, false);
			this.next = new ArrayList<AtomicMarkableReference<Node<K, V>>>(
					MAXLEVEL);
			this.level = l;
			this.key = k;
			this.version = 0;

		}
	}

	/**
	 * Pair of Node.
	 * 
	 * @param <K>
	 *            type of key in dictionary
	 * @param <V>
	 *            type of value in dictionary
	 */
	private static class NodePair<K, V> {
		Node<K, V> n1;
		Node<K, V> n2;

		/**
		 * @param nn1
		 *            the first node
		 * @param nn2
		 *            the seconde node
		 */
		public NodePair(Node<K, V> nn1, Node<K, V> nn2) {
			n1 = nn1;
			n2 = nn2;
		}
	}

	/**
	 * Node definition for dictionary.
	 * 
	 * @param <V>
	 *            type of element in node
	 */
	private static class NodeData<V> {
		V d;
		boolean b;

		/**
		 * @param dd
		 *            data
		 * @param bb
		 */
		public NodeData(V dd, boolean bb) {
			d = dd;
			b = bb;
		}
	}

	/**
	 * Max level.
	 */
	static final int MAXLEVEL = 10;
	/**
	 * 
	 */
	static final double SLCONST = 0.50;
	/**
	 * Head pointer.
	 */
	Node<K, V> head = new Node<K, V>();
	/**
	 * Tail pointer.
	 */
	Node<K, V> tail = new Node<K, V>();
	/**
	 * Random number generator.
	 */
	static final Random RAND_GEN = new Random();

	private final Comparator<? super K> comparator;

	/**
	 * default constructor.
	 */
	public LockFreeDictionary() {
		for (int i = 0; i < MAXLEVEL; i++) {
			head.next.add(new AtomicMarkableReference<Node<K, V>>(tail, false));
		}

		comparator = null;
	}

	/**
	 * @param cmp
	 *            customized comparator
	 */
	public LockFreeDictionary(Comparator<? super K> cmp) {
		for (int i = 0; i < MAXLEVEL; i++) {
			head.next.add(new AtomicMarkableReference<Node<K, V>>(tail, false));
		}

		comparator = cmp;
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		// throw new UnsupportedOperationException();

		for (int i = 0; i < MAXLEVEL; i++) {
			head.next.add(new AtomicMarkableReference<Node<K, V>>(tail, false));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		return findKey((K) key) != null;
		// if (findKey((K) key) == null) {
		// return false;
		// } else {
		// return true;
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean containsValue(Object value) {
		return findValue((V) value) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Map.Entry<K, V>> entrySet() {

		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		return findKey((K) key);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<K> keySet() {

		Node<K, V> n1, n2;
		NodePair<K, V> tn1, tn2;
		ConcurrentSkipListSet<K> rs = new ConcurrentSkipListSet<K>();

		if (this.isEmpty())
			return rs;

		n1 = head.next.get(0).getReference();
		rs.add(n1.key);
		tn1 = readNext(n1, 0);
		n1 = tn1.n1;
		n2 = tn1.n2;
		while (n2 != tail) {
			rs.add(n1.key);
			n1 = n2;
			tn2 = readNext(n1, 0);
			n1 = tn2.n1;
			n2 = tn2.n2;
		}	
		rs.add(n1.key);

		return rs;
		// throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public V put(K key, V value) {

		NodeData<V> n;

		n = insert(key, value);
		if (n.b) {
			return null;
		} else {
			return n.d;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		return deleteKey((K) key);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		Node<K, V> n1, n2;
		NodePair<K, V> tn1, tn2;
		int sz;

		if (this.isEmpty())
			return 0;

		n1 = head.next.get(0).getReference();
		tn1 = readNext(n1, 0);
		n1 = tn1.n1;
		n2 = tn1.n2;
		sz = 1;
		while (n2 != tail) {
			n1 = n2;
			tn2 = readNext(n1, 0);
			n1 = tn2.n1;
			n2 = tn2.n2;
			sz++;
		}

		return sz;
		// throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {

		return this.head.next.get(0).getReference() == tail;
		// if (this.head.next.get(0).getReference() == tail) {
		// return true;
		// } else {
		// return false;
		// }
	}

	private Node<K, V> readnode(Node<K, V> n, int ll) {
		// REVIEW consistency?
		if (n.next.get(ll).isMarked())
			return null;
		else
			return n.next.get(ll).getReference();
	}

	private NodePair<K, V> readNext(Node<K, V> n1, int ll) {

		Node<K, V> n2, nn2;

		if (n1.data.isMarked()) {
			nn2 = helpDelete(n1, ll);
			n1 = nn2;
		}

		n2 = readnode(n1, ll);
		while (n2 == null) {
			nn2 = helpDelete(n1, ll);
			n1 = nn2;
			n2 = readnode(n1, ll);
		}

		return new NodePair<K, V>(n1, n2);
	}

	private NodePair<K, V> scanKey(Node<K, V> n1, int ll, K k) {

		Node<K, V> n2;
		NodePair<K, V> tn1, tn2;

		tn1 = readNext(n1, ll);
		n1 = tn1.n1;
		n2 = tn1.n2;
		// while ((n2 != tail) && (n2.key. < k)) {
		while ((n2 != tail) && (compare(k, n2.key) > 0)) {
			n1 = n2;
			tn2 = readNext(n1, ll);
			n1 = tn2.n1;
			n2 = tn2.n2;
		}

		return new NodePair<K, V>(n1, n2);
	}

	private Node<K, V> helpDelete(Node<K, V> n, int ll) {

		int i = 0;
		Node<K, V> prev, last, n2;
		NodePair<K, V> tn1;
		AtomicMarkableReference<Node<K, V>> tempn1;

		// Mark all the next pointers of the node to be deleted
		for (i = ll; i <= n.validLevel - 1; i++) {
			do {
				tempn1 = n.next.get(i);
				n2 = tempn1.getReference();
			} while (!tempn1.compareAndSet(n2, n2, false, true)
					&& !tempn1.isMarked());
		}

		// Get the previous pointer
		prev = n.prev;
		if ((prev == null) || (ll >= prev.validLevel)) {
			prev = head;
		}

		while (true) {
			if (n.next.get(ll).getReference() == null)
				break;

			for (i = prev.validLevel - 1; i >= ll; i--) {
				prev = searchLevel(prev, i, n.key);
			}

			tn1 = scanKey(prev, ll, n.key);
			last = tn1.n2;
			prev = tn1.n1;
			if ((last != n) || (n.next.get(ll).getReference() == null))
				break;

			if (prev.next.get(ll).compareAndSet(n,
					n.next.get(ll).getReference(), false, false)) {
				n.next.get(ll).set(null, true);
				break;
			}

			if (n.next.get(ll).getReference() == null)
				break;
		}

		return prev;
	}

	private int randomLevel() {
		return RAND_GEN.nextInt(MAXLEVEL - 1);
	}

	/**
	 * @param k1
	 *            first key
	 * @param k2
	 *            second key
	 * @return -1 if k1 is less than k2; 0 if k1 equals k2; otherwise 1
	 */
	private int compare(K k1, K k2) {

		if ((k1 == null) && (k2 == null))
			return 0;
		if (k1 == null)
			return -1;
		else if (k2 == null)
			return 1;
		else {
			if (comparator == null)
				return ((Comparable<? super K>) k1).compareTo(k2);
			else
				return comparator.compare(k1, k2);
		}

	}

	private Node<K, V> searchLevel(Node<K, V> last, int ll, K k) {
		Node<K, V> stop = null;

		Node<K, V> n1 = last;

		while (true) {
			Node<K, V> n2 = n1.next.get(ll).getReference();
			if (n2 == null) {
				if (n1 == last) {
					last = helpDelete(last, ll);
				}
				n1 = last;
			} else if ((compare(n2.key, k) >= 0) || (n2 == tail)) {
				if (((n1.validLevel > ll) || (n1 == last) || (n1 == stop))
						&& (compare(n1.key, k) < 0)
						&& (compare(n1.key, last.key) >= 0)) {
					if (n1.validLevel <= ll) {
						n1 = last;
						NodePair<K, V> tn1 = scanKey(n1, ll, k);
						n1 = tn1.n1;
						n2 = tn1.n2;
					}
					return n1;
				}
				stop = n1;

				if (last.data.isMarked())
					last = helpDelete(last, ll);
				n1 = last;
			} else if (compare(n2.key, last.key) > 0) {
				n1 = n2;
			} else {
				if (last.data.isMarked())
					last = helpDelete(last, ll);
				n1 = last;
			}

		}

	}

	/**
	 * @param k
	 *            key
	 * @return value with key
	 */
	private V findKey(K k) {
		NodePair<K, V> tn1;

		Node<K, V> last = head;
		for (int i = MAXLEVEL - 1; i >= 0; i--) {
			last = searchLevel(last, i, k);
		}
		tn1 = scanKey(last, 0, k);
		last = tn1.n1;
		Node<K, V> n2 = tn1.n2;
		V val = n2.data.getReference();
		if ((compare(n2.key, k) != 0) || (n2.data.isMarked()))
			return null;
		return val;
	}

	private NodeData<V> insert(K k, V d) {
		int level, i;
		Node<K, V> n1, n2, newNode;
		ArrayList<Node<K, V>> savedNodes = new ArrayList<Node<K, V>>();
		NodePair<K, V> tn1;
		V olddata;

		// [0,MAXLEVEL], MAXLEVEL+1 nodes
		for (int ii = 0; ii <= MAXLEVEL; ii++) {
			savedNodes.add(new Node<K, V>());
		}

		level = randomLevel();
		newNode = new Node<K, V>(level, k, d);

		savedNodes.set(MAXLEVEL, head);

		for (i = MAXLEVEL - 1; i >= 0; i--) {
			savedNodes.set(i, searchLevel(savedNodes.get(i + 1), i, k));
		}

		int kk = 0;
		n1 = savedNodes.get(0);
		while (true) {
			tn1 = scanKey(n1, 0, k);
			n1 = tn1.n1;
			n2 = tn1.n2;

			if ((!n2.data.isMarked()) && (n2.data.getReference() != null)
					&& (compare(n2.key, k) == 0)) {
				olddata = n2.data.getReference();
				if (n2.data.compareAndSet(n2.data.getReference(), d, false,
						false)) {
					return new NodeData<V>(olddata, false);
				} else
					continue;
			}

			if (kk == 0) {
				newNode.next.add(new AtomicMarkableReference<Node<K, V>>(n2,
						false));
				kk++;
			} else {
				newNode.next.set(0, new AtomicMarkableReference<Node<K, V>>(n2,
						false));
			}

			if (n1.next.get(0).compareAndSet(n2, newNode, false, false)) {
				break;
			}

		}

		newNode.version = newNode.version + 1;
		newNode.validLevel = 1;

		// FIXME i should be [1,level) not [1,level - 1)
		for (i = 1; i < level - 1; i++) {
			n1 = savedNodes.get(i);
			kk = 0;
			while (true) {
				tn1 = scanKey(n1, i, k);
				n1 = tn1.n1;
				n2 = tn1.n2;

				if (kk == 0) {
					newNode.next.add(new AtomicMarkableReference<Node<K, V>>(
							n2, false));
					kk++;
				} else {
					newNode.next.set(i,
							new AtomicMarkableReference<Node<K, V>>(n2, false));
				}

				if (newNode.data.isMarked())
					break;

				if (n1.next.get(i).compareAndSet(n2, newNode, false, false)) {
					newNode.validLevel = i + 1;
					break;
				}
			}
		}

		if (newNode.data.isMarked())
			newNode = helpDelete(newNode, 0);

		return new NodeData<V>(null, true);
	}

	private V deleteKey(K k) {
		return delete(k, false, null);
	}

	private V delete(K k, boolean delVal, V dd) {
		int i;
		Node<K, V> n1, n2, prev, last;
		ArrayList<Node<K, V>> savedNodes = new ArrayList<Node<K, V>>();
		// V val = null;
		NodePair<K, V> tn1;
		AtomicMarkableReference<Node<K, V>> tempn1;

		for (int ii = 0; ii <= MAXLEVEL; ii++) {
			savedNodes.add(new Node<K, V>());
		}
		savedNodes.set(MAXLEVEL, head);
		for (i = MAXLEVEL - 1; i >= 0; i--)
			savedNodes.set(i, searchLevel(savedNodes.get(i + 1), i, k));
		tn1 = scanKey(savedNodes.get(0), 0, k);
		savedNodes.set(0, tn1.n1);
		n1 = tn1.n2;

		while (true) {
			if (!delVal)
				dd = n1.data.getReference();
			if ((compare(n1.key, k) == 0) // FIXME need to judge n1 != head &&
											// n1 != tail?
					&& ((!delVal) || (n1.data.getReference() == dd))
					&& !n1.data.isMarked()) { // FIXME should compare
												// value.isMarked()
				if (n1.data.compareAndSet(n1.data.getReference(), n1.data
						.getReference(), false, true)) {
					n1.prev = savedNodes.get((n1.level - 1) / 2);
					break;
				} else
					continue;
			}
			return null;
		}

		// FIXME validLevel should be level?
		for (i = 0; i <= n1.validLevel - 1; i++) {
			do {
				tempn1 = n1.next.get(i);
				n2 = tempn1.getReference();
			} while (!tempn1.compareAndSet(n2, n2, false, true)
					&& !tempn1.isMarked()); // FIXME isMarked should be put
											// before CAS?
		}

		for (i = n1.validLevel - 1; i >= 0; i--) {
			prev = savedNodes.get(i);
			while (true) {
				if (n1.next.get(i).getReference() == null)
					break;
				tn1 = scanKey(prev, i, n1.key);
				last = tn1.n2;
				prev = tn1.n1;

				if ((last != n1) || (n1.next.get(i).getReference() == null))
					break;
				if (prev.next.get(i).compareAndSet(n1,
						n1.next.get(i).getReference(), false, false)) {
					n1.next.get(i).set(null, true);
					break;
				}
				if (n1.next.get(i).getReference() == null)
					break;

			}
		}

		return dd;
	}

	private K findValue(V d) {
		return fdValue(d, false);
	}

	/**
	 * @param d
	 *            value
	 * @return key with value d
	 */
	public K deleteValue(V d) {
		return fdValue(d, true);
	}

	private K fdValue(V d, boolean del) {

		Node<K, V> last, n1, n2;
		int j = 16, step = 0, v = 0, v2 = 0;
		boolean ok;
		AtomicMarkableReference<Node<K, V>> tempn1;
		NodePair<K, V> tn1;
		K k1, k2 = null;

		last = head;

		n1 = last;
		k1 = n1.key;
		step = 0;
		n_jump: while (true) {
			ok = false;
			v = n1.version;
			tempn1 = n1.next.get(0);
			n2 = tempn1.getReference();
			if (tempn1.isMarked() && (n2 != null)) { // FIXME should be not
														// isMarked()
				v2 = n2.version;
				k2 = n2.key;
				if ((compare(n1.key, k1) == 0) && (n1.validLevel > 0)
						&& (n1.next.get(0).getReference() == n2)
						&& (n1.version == v) && (n2.validLevel > 0)
						&& (n2.version == v2)) // FIXME miss node2->key == key2
					ok = true;
			}
			if (!ok) {
				tn1 = readNext(last, 0);
				n1 = tn1.n2;
				n2 = tn1.n2;
				last = tn1.n1;
				k1 = n2.key;
				k2 = n2.key;
				v2 = n2.version;
				last = n2;
				step = 0;
			}
			if (n2 == tail) {
				return null;
			}
			if (n2.data.getReference() == d) {
				if (n2.version == v2 && (!del) || (delete(k2, true, d) == d))
					return k2;
			} else if (++step > j) { // FIXME should be ++step >= j
				if ((n2.validLevel == 0) || (compare(n2.key, k2) != 0)) {
					tn1 = readNext(last, 0);
					n2 = tn1.n2;
					last = tn1.n1;
					if (j >= 4)
						j = j / 2;
				} else {
					j = j + j / 2;
				}
				last = n2;
				n1 = last;
				k1 = n1.key;
				step = 0;
				continue n_jump;
			} else {
				k1 = k2;
				n1 = n2;
			}
		}
	}
}
