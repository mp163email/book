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

package org.amino.mcas;

import sun.misc.Unsafe;

/**
 * This is an implementation of a lock-free binary search tree.
 * <p>
 * The implementation is according to the technical report Practical
 * lock-freedom by Keir Fraser, 2004. To gain a complete understanding of this
 * data structure, please first read this paper, available at:
 * http://www.cl.cam.ac.uk/techreports/UCAM-CL-TR-579.pdf
 * <p>
 * Lock-free binary search trees (BSTs) are complicated by the problem of
 * deleting a node with two non-empty subtrees. The classical algorithm replaces
 * the deleted node with either the smallest node in its right subtree or the
 * largest node in its left subtree; these nodes can be easily removed from
 * their current position as they have at most one subtree. Implementing this
 * without adding extra synchronization to search operations is difficult
 * because it requires the replacement node to be atomically removed from its
 * current location and inserted in place of the deleted node.
 * <p>
 * The problem of making an atomic update to multiple memory locations, to
 * effect the simultaneous deletion and reinsertion, is solved by using MCAS.
 * Although this ensures that all the updated memory locations change to their
 * new value at the same instant in time, this is insufficient to ensure
 * consistency of concurrent search operations (see Fig. 4.5 in the paper).
 * Instead Fraser use a threaded tree representation in which pointers to empty
 * subtrees are instead linked to the immediate predecessor or successor node in
 * the tree (see Fig. 4.6 in the paper).
 * <p>
 * Sample performance results here
 * <p>
 * The following operations are thread-safe and scalable (but see notes in
 * method javadoc): find, remove, update.
 * <p>
 * The following operations are not thread-safe:
 * 
 * @param <T>
 *            Type of key
 * @param <V>
 *            Type of value
 * 
 * @author Xiao Jun Dai
 */
public class LockFreeBSTree<T, V> {
    private static final Unsafe UNSAFE = UnsafeWrapper.getUnsafe();

    /**
     * Root of the tree.
     */
    private Node<T, V> root;

    /**
     * Present the max value in the tree.
     */
    private final Node<T, V> maxDummyNode = new ThreadNode<T, V>(
            new Node<T, V>());

    /**
     * Present the min value in the tree.
     */
    private final Node<T, V> minDummyNode = new ThreadNode<T, V>(
            new Node<T, V>());

    /**
     * Internal node definition of tree.
     * 
     * @param <T>
     *            type of key in node
     * @param <V>
     *            type of value in node
     */
    private static class Node<T, V> extends ObjectID {
        T key; // immutable
        V value;
        ThreadNode<T, V> tnode;
        volatile Node<T, V> left;
        volatile Node<T, V> right;

        static final long VALUE_OFFSET;
        static final long LEFT_OFFSET;
        static final long RIGHT_OFFSET;

        static {
            try {
                VALUE_OFFSET = UNSAFE.objectFieldOffset(Node.class
                        .getDeclaredField("value"));
                LEFT_OFFSET = UNSAFE.objectFieldOffset(Node.class
                        .getDeclaredField("left"));
                RIGHT_OFFSET = UNSAFE.objectFieldOffset(Node.class
                        .getDeclaredField("right"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Node() {
            key = null;
            value = null;
            left = null;
            right = null;
            tnode = null;
        }

        Node(T key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            tnode = null;
        }

        /**
         * @return true if p is marked as a thread
         */
        public boolean isThread() {
            return this instanceof ThreadNode;
        }

        /**
         * @return node with the thread wrapper
         */
        public Node<T, V> thread() {
            if (null == tnode) {
                tnode = new ThreadNode<T, V>(this);
            }
            return tnode;
        }

        /**
         * @return node without the thread wrapper
         */
        public Node<T, V> unthread() {
            return ((ThreadNode<T, V>) this).node;
        }
    }

    /**
     * Definition of threaded node use to point to the root of tree.
     * 
     * @param <T>
     *            type of key in node
     * @param <V>
     *            type of value in node
     */
    private static class ThreadNode<T, V> extends Node<T, V> {
        Node<T, V> node;

        ThreadNode(Node<T, V> node) {
            this.node = node;
        }
    }

    /**
     * internal definition of node pair.
     * 
     * @param <T>
     *            type of
     * @param <V>
     */
    private static class Pair<T, V> {
        Node<T, V> prev;
        Node<T, V> curr;

        Pair(Node<T, V> p, Node<T, V> c) {
            prev = p;
            curr = c;
        }
    }

    /**
     * Default constructor.
     */
    public LockFreeBSTree() {
        root = new Node<T, V>();
    }

    /**
     * Helper function of find.
     * 
     * @param root
     *            root node
     * @param key
     * @return tuple (p, n) consisting of the node n with key k, and its parent
     *         p.
     */
    @SuppressWarnings("unchecked")
    private Pair<T, V> search(Node<T, V> root, T key) {
        Node<T, V> c;
        retry: while (true) {
            Node<T, V> p = root;
            Node<T, V> n = (Node<T, V>) MultiCAS.mcasRead(root,
                    Node.LEFT_OFFSET);

            // empty tree
            if (null == n) {
                return new Pair<T, V>(p, n);
            }

            /*
             * The loop on traverses the tree in the usual manner, checking for
             * concurrent MCAS operations on the search path, and retrying from
             * the root if the search traverses a deleted node.
             */
            while (!n.isThread()) {
                int comp = ((Comparable) key).compareTo(n.key);
                if (comp < 0)
                    c = (Node<T, V>) MultiCAS.mcasRead(n, Node.LEFT_OFFSET);
                else if (comp > 0)
                    c = (Node<T, V>) MultiCAS.mcasRead(n, Node.RIGHT_OFFSET);
                else
                    return new Pair<T, V>(p, n);
                /* We retry if we read from a stale link. */
                if (c == null) {
                    continue retry;
                }
                p = n;
                n = c;
            }
            /*
             * If the thread matches, retry to find parent. The test is executed
             * only if k was not found in the tree. In that case, the thread
             * link found at the end of the search is followed to check if it
             * leads to a node with key k. If so, the search must be retried
             * because, although the required node has been found, it is not
             * possible to find its parent without restarting from the root of
             * the tree.
             */
            if (key == n.unthread().key) {
                continue retry;
            }
            return new Pair<T, V>(p, n);
        }
    }

    /**
     * Find key in the tree.
     * 
     * @param key
     *            key to search
     * 
     * @return value with key,, otherwise null
     */
    public V find(T key) {
        return find(root, key);
    }

    /**
     * Find key in the tree from root.
     * 
     * @param root
     *            root of tree to search
     * @param key
     *            key to search
     * @return value with key
     */
    @SuppressWarnings("unchecked")
    private V find(Node<T, V> root, T key) {
        Node<T, V> n = search(root, key).curr;
        /*
         * The key field of a BST node can be read directly as the key is never
         * modified after a node is initialized. Reads from pointer locations,
         * including the subtree and value fields of a node, must use MCASRead
         * in case an MCAS operation is currently in progress. Another
         * possibility is that a field is read after the node is deleted from
         * the tree. I handle this by setting all the pointer fields of a
         * deleted node to an otherwise unused value (null). This allows a read
         * to detect when it has read from a defunct node and take appropriate
         * action, such as retrying its access from the tree root.
         */
        return n.isThread() ? null : (V) MultiCAS
                .mcasRead(n, Node.VALUE_OFFSET);
    }

    /**
     * update value with key in the tree.
     * 
     * @param key
     *            key to be updated
     * @param value
     *            new value
     * 
     * @return oldValue if key is found, otherwise return null
     */
    public V update(T key, V value) {
        return update(root, key, value);
    }

    @SuppressWarnings("unchecked")
    private V update(Node<T, V> root, T key, V value) {
        /*
         * There are two cases to consider when updating a (key, value) mapping.
         * If bst search finds an existing mapping for key, it attempts to
         * directly modify that node's value field. If no current mapping is
         * found then it inserts a newly-allocated node into the tree. CAS is
         * used in both cases because only one memory location needs to be
         * updated.
         */
        Node<T, V> node = new Node<T, V>(key, value);
        V oldValue;
        Node<T, V> n;
        Node<T, V> p;
        Pair<T, V> pair;
        retry: while (true) {
            do {
                pair = search(root, key);
                p = pair.prev;
                n = pair.curr;

                // empty tree
                if (null == n) {
                    node.left = minDummyNode;
                    node.right = maxDummyNode;
                    if (!UNSAFE.compareAndSwapObject(p, Node.LEFT_OFFSET, n,
                            node)) {
                        continue retry;
                    } else {
                        return null;
                    }
                }

                if (!n.isThread()) {
                    do {
                        oldValue = (V) MultiCAS.mcasRead(n, Node.VALUE_OFFSET);
                        if (oldValue == null) {
                            continue retry;
                        }
                    } while (!UNSAFE.compareAndSwapObject(n, Node.VALUE_OFFSET,
                            oldValue, value));
                    return oldValue;
                }

                /*
                 * if a node has been moved up the tree due to a deletion, the
                 * search may no longer have found the correct position to
                 * insert the new node. It is instructive to note that lookups
                 * and deletions do not need to worry about this type of
                 * inconsistency. This may cause lookups and deletions to fail
                 * to find a node even though a matching one has been inserted
                 * in a different subtree. The failing operation is still
                 * linearisable because the inserted node must have appeared
                 * after the failing operation began executing. This is because
                 * the failing operation began executing before the deletion
                 * which caused the inconsistency; but the insertion of the new
                 * node must be linearised after that deletion. The failing
                 * operation can therefore be linearised before the new node was
                 * inserted.
                 */
                if (((Comparable) p.key).compareTo((Comparable) key) < 0) {
                    if ((n != maxDummyNode)
                            && ((Comparable) n.unthread().key)
                                    .compareTo((Comparable) key) < 0)
                        continue retry;
                    node.left = p.thread();
                    node.right = n;
                } else {
                    if ((n != minDummyNode)
                            && ((Comparable) n.unthread().key)
                                    .compareTo((Comparable) key) > 0)
                        continue retry;
                    node.left = n;
                    node.right = p.thread();
                }
            } while (!UNSAFE.compareAndSwapObject(p, ((Comparable) p.key)
                    .compareTo((Comparable) key) < 0 ? Node.RIGHT_OFFSET
                    : Node.LEFT_OFFSET, n, node));
            return null;
        }
    }

    /**
     * Remove key in the tree. Deletion is the most time-consuming operation to
     * implement because of the number of different tree configurations which
     * must be handled.
     * 
     * @param key
     *            key to be removed
     * 
     * @return value with key, otherwise null
     */
    public V remove(T key) {
        return remove(root, key);
    }

    /**
     * Remove key in the tree.
     * 
     * @param root
     *            root of tree
     * @param key
     *            key to be removed
     * @return value with key
     */
    @SuppressWarnings("unchecked")
    private V remove(Node<T, V> root, T key) {
        /*
         * Fig. 4.8 in the paper shows all the different tree configurations
         * that deletion may have to deal with, and the correct transformation
         * for each case. Although somewhat cumbersome, the implementation of
         * each transformation is straightforward: traverse the tree to find the
         * nodes involved, and retry the operation if a garbage node is
         * traversed or if the tree structure changes under the operation's
         * feet.
         */
        Pair<T, V> pair;
        Node<T, V> p;
        Node<T, V> d;

        Node<T, V> pred;
        Node<T, V> ppred = null;
        Node<T, V> cpred;
        Node<T, V> succ;
        Node<T, V> psucc = null;
        Node<T, V> csucc;
        Node<T, V> succR;
        Node<T, V> predL;

        retry: while (true) {
            pair = search(root, key);
            p = pair.prev;
            d = pair.curr;

            if ((null == d) || d.isThread()) {
                return null;
            }

            /* Read contents of node: retry if node is garbage */
            Node<T, V> dl = (Node<T, V>) MultiCAS.mcasRead(d, Node.LEFT_OFFSET);
            Node<T, V> dr = (Node<T, V>) MultiCAS
                    .mcasRead(d, Node.RIGHT_OFFSET);
            V dv = (V) MultiCAS.mcasRead(d, Node.VALUE_OFFSET);

            if ((dl == null) || (dr == null) || (dv == null))
                continue retry;

            /* deep into the left branch */
            if (((p == root) || (((Comparable) p.key)
                    .compareTo((Comparable) d.key) > 0))) {
                if (!dl.isThread() && !dr.isThread()) {
                    /* Find predecessor, and its parent (pred, ppred) */
                    pred = d;
                    cpred = dl;
                    while (!cpred.isThread()) {
                        ppred = pred;
                        pred = cpred;
                        cpred = (Node<T, V>) MultiCAS.mcasRead(pred,
                                Node.RIGHT_OFFSET);
                        if (cpred == null)
                            continue retry;
                    }

                    /* Find successor, and its parent (cuss, psucc) */
                    succ = d;
                    csucc = dr;
                    while (!csucc.isThread()) {
                        psucc = succ;
                        succ = csucc;
                        csucc = (Node<T, V>) MultiCAS.mcasRead(succ,
                                Node.LEFT_OFFSET);
                        if (csucc == null)
                            continue retry;
                    }

                    /* prepare array for MCAS */
                    Object[] obj = new Object[8];
                    long[] offset = new long[8];
                    Object[] oldValue = new Object[8];
                    Object[] newValue = new Object[8];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = succ;
                    offset[3] = Node.LEFT_OFFSET;
                    oldValue[3] = d.thread();
                    newValue[3] = dl;

                    obj[4] = p;
                    offset[4] = Node.LEFT_OFFSET;
                    oldValue[4] = d;
                    newValue[4] = succ;

                    obj[5] = pred;
                    offset[5] = Node.RIGHT_OFFSET;
                    oldValue[5] = d.thread();
                    newValue[5] = succ.thread();

                    if (succ == dr) { /* Case 4, Fig. 4.8 */
                        if (!MultiCAS.mcas(6, obj, offset, oldValue, newValue)) {
                            continue retry;
                        }
                    } else {
                        succR = (Node<T, V>) MultiCAS.mcasRead(succ,
                                Node.RIGHT_OFFSET);
                        obj[6] = succ;
                        offset[6] = Node.RIGHT_OFFSET;
                        oldValue[6] = succR;
                        newValue[6] = dr;

                        assert psucc != null;
                        obj[7] = psucc;
                        offset[7] = Node.LEFT_OFFSET;
                        oldValue[7] = succ;
                        newValue[7] = succR.isThread() ? succ.thread() : succR;

                        if (!MultiCAS.mcas(8, obj, offset, oldValue,
                                newValue)) {
                            continue retry;
                        }
                    }
                } else if (dl.isThread() && !dr.isThread()) {
                    /* Case 2, Fig. 4.8 */
                    /* Find successor, and its parent (cuss, psucc) */
                    succ = d;
                    csucc = dr;
                    while (!csucc.isThread()) {
                        psucc = succ;
                        succ = csucc;
                        csucc = (Node<T, V>) MultiCAS.mcasRead(succ,
                                Node.LEFT_OFFSET);
                        if (csucc == null)
                            continue retry;
                    }

                    /* prepare array for MCAS */
                    Object[] obj = new Object[5];
                    long[] offset = new long[5];
                    Object[] oldValue = new Object[5];
                    Object[] newValue = new Object[5];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = succ;
                    offset[3] = Node.LEFT_OFFSET;
                    oldValue[3] = d.thread();
                    newValue[3] = dl;

                    obj[4] = p;
                    offset[4] = Node.LEFT_OFFSET;
                    oldValue[4] = d;
                    newValue[4] = dr;

                    if (!MultiCAS.mcas(5, obj, offset, oldValue, newValue)) {
                        continue retry;
                    }
                } else if (!dl.isThread() && dr.isThread()) {
                    /* Case 3, Fig. 4.8 */
                    /* Find predecessor, and its parent (pred, ppred) */
                    pred = d;
                    cpred = dl;
                    while (!cpred.isThread()) {
                        ppred = pred;
                        pred = cpred;
                        cpred = (Node<T, V>) MultiCAS.mcasRead(pred,
                                Node.RIGHT_OFFSET);
                        if (cpred == null)
                            continue retry;
                    }

                    /* prepare array for MCAS */
                    Object[] obj = new Object[5];
                    long[] offset = new long[5];
                    Object[] oldValue = new Object[5];
                    Object[] newValue = new Object[5];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = p;
                    offset[3] = Node.LEFT_OFFSET;
                    oldValue[3] = d;
                    newValue[3] = dl;

                    obj[4] = pred;
                    offset[4] = Node.RIGHT_OFFSET;
                    oldValue[4] = d.thread();
                    newValue[4] = dr;

                    /* Case 2, Fig. 4.8 */
                    if (!MultiCAS.mcas(5, obj, offset, oldValue, newValue)) {
                        continue retry;
                    }
                } else if (dl.isThread() && dr.isThread()) {
                    /* Case 1, Fig. 4.8 */
                    /* prepare array for MCAS */
                    Object[] obj = new Object[4];
                    long[] offset = new long[4];
                    Object[] oldValue = new Object[4];
                    Object[] newValue = new Object[4];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = p;
                    offset[3] = Node.LEFT_OFFSET;
                    oldValue[3] = d;
                    newValue[3] = dl;

                    /* Case 1, Fig. 4.8 */
                    if (!MultiCAS.mcas(4, obj, offset, oldValue, newValue)) {
                        continue retry;
                    }
                } else {
                    /* cannot reach here */
                    assert false;
                }
            } else {/* All symmetric and simpler cases omitted. */
                if (!dl.isThread() && !dr.isThread()) {
                    /* Find predecessor, and its parent (pred, ppred) */
                    pred = d;
                    cpred = dl;
                    while (!cpred.isThread()) {
                        ppred = pred;
                        pred = cpred;
                        cpred = (Node<T, V>) MultiCAS.mcasRead(pred,
                                Node.RIGHT_OFFSET);
                        if (cpred == null)
                            continue retry;
                    }

                    /* Find successor, and its parent (cuss, psucc) */
                    succ = d;
                    csucc = dr;
                    while (!csucc.isThread()) {
                        psucc = succ;
                        succ = csucc;
                        csucc = (Node<T, V>) MultiCAS.mcasRead(succ,
                                Node.LEFT_OFFSET);
                        if (csucc == null)
                            continue retry;
                    }

                    /* prepare array for MCAS */
                    Object[] obj = new Object[8];
                    long[] offset = new long[8];
                    Object[] oldValue = new Object[8];
                    Object[] newValue = new Object[8];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = succ;
                    offset[3] = Node.LEFT_OFFSET;
                    oldValue[3] = d.thread();
                    newValue[3] = pred.thread();

                    obj[4] = p;
                    offset[4] = Node.RIGHT_OFFSET;
                    oldValue[4] = d;
                    newValue[4] = pred;

                    obj[5] = pred;
                    offset[5] = Node.RIGHT_OFFSET;
                    oldValue[5] = d.thread();
                    newValue[5] = dr;

                    if (pred == dl) { /* Case 4, Fig. 4.8 */
                        if (!MultiCAS.mcas(6, obj, offset, oldValue, newValue)) {
                            continue retry;
                        }
                    } else {
                        predL = (Node<T, V>) MultiCAS.mcasRead(pred,
                                Node.LEFT_OFFSET);
                        obj[6] = pred;
                        offset[6] = Node.LEFT_OFFSET;
                        oldValue[6] = predL;
                        newValue[6] = dl;

                        assert ppred != null;
                        obj[7] = ppred;
                        offset[7] = Node.RIGHT_OFFSET;
                        oldValue[7] = pred;
                        if (pred == null || predL == null) {
                            System.out.println("predL = " + predL);
                            System.out.println("pred = " + pred);
                            System.out.println("ppred = " + ppred);
                        }
                        newValue[7] = predL.isThread() ? pred.thread() : predL;

                        if (!MultiCAS.mcas(8, obj, offset, oldValue,
                                newValue)) {
                            continue retry;
                        }
                    }
                } else if (dl.isThread() && !dr.isThread()) {
                    /*
                     * Case 3, Fig. 4.8
                     */
                    /* Find successor, and its parent (cuss, psucc) */
                    succ = d;
                    csucc = dr;
                    while (!csucc.isThread()) {
                        psucc = succ;
                        succ = csucc;
                        csucc = (Node<T, V>) MultiCAS.mcasRead(succ,
                                Node.LEFT_OFFSET);
                        if (csucc == null)
                            continue retry;
                    }

                    /* prepare array for MCAS */
                    Object[] obj = new Object[5];
                    long[] offset = new long[5];
                    Object[] oldValue = new Object[5];
                    Object[] newValue = new Object[5];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = succ;
                    offset[3] = Node.LEFT_OFFSET;
                    oldValue[3] = d.thread();
                    newValue[3] = dl;

                    obj[4] = p;
                    offset[4] = Node.RIGHT_OFFSET;
                    oldValue[4] = d;
                    newValue[4] = dr;

                    if (!MultiCAS.mcas(5, obj, offset, oldValue, newValue)) {
                        continue retry;
                    }

                } else if (!dl.isThread() && dr.isThread()) {
                    /* Case 2, Fig. 4.8 */
                    /* Find predecessor, and its parent (pred, ppred) */
                    pred = d;
                    cpred = dl;
                    while (!cpred.isThread()) {
                        ppred = pred;
                        pred = cpred;
                        cpred = (Node<T, V>) MultiCAS.mcasRead(pred,
                                Node.RIGHT_OFFSET);
                        if (cpred == null)
                            continue retry;
                    }

                    /* prepare array for MCAS */
                    Object[] obj = new Object[5];
                    long[] offset = new long[5];
                    Object[] oldValue = new Object[5];
                    Object[] newValue = new Object[5];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = p;
                    offset[3] = Node.RIGHT_OFFSET;
                    oldValue[3] = d;
                    newValue[3] = dl;

                    obj[4] = pred;
                    offset[4] = Node.RIGHT_OFFSET;
                    oldValue[4] = d.thread();
                    newValue[4] = dr;

                    /* Case 2, Fig. 4.8 */
                    if (!MultiCAS.mcas(5, obj, offset, oldValue, newValue)) {
                        continue retry;
                    }

                } else if (dl.isThread() && dr.isThread()) {
                    /* Case 1, Fig. 4.8 */
                    /* prepare array for MCAS */
                    Object[] obj = new Object[4];
                    long[] offset = new long[4];
                    Object[] oldValue = new Object[4];
                    Object[] newValue = new Object[4];

                    obj[0] = d;
                    offset[0] = Node.LEFT_OFFSET;
                    oldValue[0] = dl;
                    newValue[0] = null;

                    obj[1] = d;
                    offset[1] = Node.RIGHT_OFFSET;
                    oldValue[1] = dr;
                    newValue[1] = null;

                    obj[2] = d;
                    offset[2] = Node.VALUE_OFFSET;
                    oldValue[2] = dv;
                    newValue[2] = null;

                    obj[3] = p;
                    offset[3] = Node.RIGHT_OFFSET;
                    oldValue[3] = d;
                    newValue[3] = dr;

                    /* Case 1, Fig. 4.8 */
                    if (!MultiCAS.mcas(4, obj, offset, oldValue, newValue)) {
                        continue retry;
                    }
                } else {
                    /* cannot reach here */
                    assert false;
                }
            }
            return dv;
        }
    }

    /**
     * Is the tree empty?
     * 
     * @return true if the tree is empty, otherwise false
     */
    public boolean isEmpty() {
        return root.right == null;
    }
}
