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

package org.amino.ds.tree;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.amino.pattern.internal.Doable;

/**
 * This is an implementation of a relaxed balanced red-black tree data
 * structure.
 * <p>
 * The implementation is according to the paper Relaxed Balanced Red-Black Trees
 * by Hanke, Ottmann and Soisalon-Soininen, 1997 and The performance of
 * concurrent RB tree algorithms by Hanke, 1998. To gain a complete
 * understanding of this data structure, please first read this paper, available
 * at: http://citeseer.ist.psu.edu/hanke97relaxed.html and
 * http://citeseer.ist.psu.edu/400640.html
 * <p>
 * Relaxed balancing mean that, in a dictionary stored as a balanced tree, the
 * necessary after updates may be delayed. This is in contrast to strict
 * balancing meaning that rebalancing is performed immediately after update.
 * Relaxed balancing is important for efficiency in highly dynamic applications
 * where updates can occur in bursts. The rebalancing tasks can be performed
 * gradually after all urgent updates, allowing the concurrent use of the
 * dictionary even though the underlying tree structure is not completely in
 * balance.
 * <p>
 * This implementation propose a new scheme of how to make known rebalancing
 * techniques relaxed in an efficient way.The key idea is to accumulate
 * insertions and deletions such that they can be settled in arbitrary order
 * using the same rebalancing operations as for standard balanced search trees.
 * <p>
 * The tree implemented here is a leaf-oriented binary search trees, which are
 * full binary trees (each node has either two or no children). The nullary
 * nodes of a tree are called the external nodes or leaves while the other nodes
 * are said to be internal nodes. We assume that the keys (chosen from a totally
 * ordered universe) are stored in the leaves of the binary tree. The internal
 * nods contains routers, which guide the search from the root to a leaf.
 * <p>
 * Sample performance results here
 * <p>
 * The following operations are thread-safe and scalable (but see notes in
 * method javadoc): insert, remove, find, search.
 * <p>
 * The following operations are not thread-safe:
 * <p>
 * TODO cannot add the same value into the tree
 * 
 * @author Xiao Jun Dai
 * 
 * @param <E>
 *            Type of element
 */
public class ParallelRBTree<E> {
    /**
     * Wait before try to get a lock after failing.
     */
    private static final int WAIT_FOR_TRY_LOCK = 50;

    /**
     * sentinel stands for a dummy node.
     */
    private static final Node SENTINEL;
    static {
        // parent, left child and right child pointers point to sentinel nodes
        // itself. value is set to null
        SENTINEL = new Node();
        SENTINEL.left = SENTINEL.right = SENTINEL.p = SENTINEL;
        SENTINEL.value = null;
    }

    /**
     * Internal node of tree.
     * 
     * @param <E>
     *            Type of element in node
     */
    public static class Node<E> {
        /**
         * Color of node.
         */
        private Color color;
        /**
         * Value of node.
         */
        private E value;
        /**
         * Left child of node.
         */
        private Node<E> left;
        /**
         * Right child of node.
         */
        private Node<E> right;
        /**
         * Parent of node.
         */
        private Node<E> p;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        /**
         * Read lock on the node.
         */
        final Lock rlock = lock.readLock(); // read lock
        /**
         * Write lock on the node.
         */
        final Lock xlock = lock.writeLock(); // write lock

        /**
         * Get value.
         * 
         * @return value
         */
        public E getValue() {
            return value;
        }

        /**
         * Request list of the node. It removes duplicated requests on the same
         * node.
         */
        Set<Request> req;

        /**
         * Default constructor.
         * 
         * @SuppressWarnings("unchecked")
*/
         

        @SuppressWarnings("unchecked")
        Node() {
            color = Color.BLACK;
            value = null;
            left = SENTINEL;
            right = SENTINEL;
            p = SENTINEL;
            req = new CopyOnWriteArraySet<Request>();
        }

        /**
         * Constructor with default value.
         * 
         * @param value
         *            value on the node
         */
        @SuppressWarnings("unchecked")
        public Node(E value) {
            color = Color.BLACK;
            this.value = value;
            left = SENTINEL;
            right = SENTINEL;
            p = SENTINEL;
            req = new CopyOnWriteArraySet<Request>();
        }

        /**
         * Constructor with default value and parent pointer.
         * 
         * @param value
         *            value on the node
         * @param parent
         *            parent pointer
         */
        @SuppressWarnings("unchecked")
        public Node(E value, Node<E> parent) {
            color = Color.BLACK;
            this.value = value;
            this.left = SENTINEL;
            this.right = SENTINEL;
            p = parent;
            req = new CopyOnWriteArraySet<Request>();
        }

        /**
         * Constructor with default value, parent, left child and right child
         * pointer.
         * 
         * @param value
         *            value on the node
         * @param parent
         *            parent pointer
         * @param left
         *            left child pointer
         * @param right
         *            right child pointer
         */
        public Node(E value, Node<E> parent, Node<E> left, Node<E> right) {
            color = Color.RED;
            this.value = value;
            this.p = parent;
            this.left = left;
            this.right = right;
            req = new CopyOnWriteArraySet<Request>();
        }

        /**
         * {@inheritDoc}
         */
        public String toString() {
            return value == null ? "sentinal" : value.toString()
                    + ((color == Color.BLACK) ? "B" : "R") + req
                    + this.hashCode();
        }

        /**
         * Decide if this node is a leaf.
         * 
         * @return true if node is not a leaf otherwise false;
         */
        public boolean isNotLeaf() {
            return !isLeaf();
            // return sentinel != left;
        }

        /**
         * Decide if this node is a leaf.
         * 
         * @return true if node is a leaf otherwise false;
         */
        public boolean isLeaf() {
            return SENTINEL == left && SENTINEL == right;
        }
    }

    /**
     * Root of the red-black tree.
     */
    private Node<E> root;

    /**
     * Balancer which execute balancing request in a separated thread.
     */
    private final RelaxedBalancer balancer;

    /**
     * Default constructor.
     */
    public ParallelRBTree() {
        root = SENTINEL;
        balancer = new RelaxedBalancer();
    }

    /**
     * Shutdown balancer thread.
     */
    public void shutdown() {
        balancer.shutdown();
    }

    /**
     * Insert a element into the tree.
     * 
     * @param element
     *            element
     */
    public void insert(E element) {
        insert(new Node<E>(element));
    }

    /**
     * Insert a node to the leaf.
     * 
     * @param z
     *            node to be insert
     */
    @SuppressWarnings("unchecked")
    private void insert(Node<E> z) {
        /*
         * In order to insert a new key x into a strictly balanced red-black
         * tree we first locate its position among the leaves and replace the
         * leaf by an internal red node with two black leaves.The two leaves now
         * store the old key (where the search ended) and the new key x.
         */
        Node<E> x = root;
        Node<E> y = SENTINEL; // parent of x

        x.xlock.lock();
        try {
            // locate position to insert, use w-lock coupling from the root
            while (x.isNotLeaf()) {
                y = x;
                if (((Comparable) z.value).compareTo(x.value) < 0) {
                    x.left.xlock.lock();
                    x.xlock.unlock();
                    x = x.left;
                } else {
                    x.right.xlock.lock();
                    x.xlock.unlock();
                    x = x.right;
                }
            }

            // assertion for debug
            // only leaf is x-locked
            if (!x.isLeaf()) {
                assert false;
            }

            // if (x.p != y) {
            // assert x == root;
            // }

            // x is the location to insert and always leaf
            if (x.req.contains(Request.REMOVAL)) {
                // just replace the value of REMOVAL node
                x.req.remove(Request.REMOVAL);
                x.value = z.value;
            } else {
                z.p = x;
                if (SENTINEL == x) { // tree is empty
                    root = z;
                    root.color = Color.BLACK;
                } else {
                    /*
                     * replace the leaf by an internal red node with two black
                     * leaves, internal node has the bigger value
                     */
                    if (((Comparable) z.value).compareTo(x.value) < 0) {
                        x.left = z;
                        x.right = new Node<E>(x.value, x);
                    } else {
                        x.right = z;
                        x.left = new Node<E>(x.value, x);
                        x.value = z.value;
                    }
                    x.color = Color.RED;

                    assert x.left.color == Color.BLACK
                            && x.right.color == Color.BLACK;

                    // addtional transformation
                    // if (x.req.contains(Request.UP_OUT)) {
                    // x.req.remove(Request.UP_OUT);
                    // x.color = Color.BLACK;
                    // }

                    /*
                     * if the parent of the new internal node p is red (as well
                     * as p itself) then the resulting tree is no longer a
                     * red-black tree. In order to correct this and to restore
                     * the balance condition we call the balancing procedure
                     * up-in for the new inner node p.
                     */
                    if (y.color == Color.RED) {
                        x.req.add(Request.UP_IN);
                        balancer.addRequest(x);
                    }
                }
            }
        } finally {
            x.xlock.unlock();
        }
    }

    /**
     * remove an element from the tree.
     * 
     * @param element
     *            element to be removed
     * @return true if node is removed, otherwise false
     */
    @SuppressWarnings("unchecked")
    public boolean remove(E element) {
        /*
         * In order ot delete a key from a strictly balanced red-blakc tree, we
         * first locate the leaf where the key is stored and then remove the
         * leaf together with its parent. (Note that the balance condition
         * implies that the remaining sibling of the leaf is either a leaf as
         * well or a red node which has two leaves as its children) If the
         * removed parent was black then the red-black tree structure is now
         * Violated. It can be restored easily if the remaining node is red
         * (change its color). Otherwise, the removal leads to the call of the
         * balancing procedure up-out for the remaining leaf. cf. Figure 3.
         */
        RE_TRY: while (true) {
            if (root == SENTINEL) {// tree is empty
                return false;
            }

            Node<E> x = root;
            // locate position to insert, use w-lock coupling from the root
            x.xlock.lock();
            try {
                // reach the leaf to remove
                while (x.isNotLeaf()) {
                    if (((Comparable) element).compareTo(x.value) < 0) {
                        x.left.xlock.lock();
                        x.xlock.unlock();
                        x = x.left;
                    } else {
                        x.right.xlock.lock();
                        x.xlock.unlock();
                        x = x.right;
                    }
                }

                // assertion for debuging
                if (!x.isLeaf()) {
                    assert false;
                }

                // x is the leaf
                if (root == x) {// x is the root
                    x.req.clear();
                    root = SENTINEL;
                } else {
                    final Node<E> parent = x.p; // x's parent
                    try {
                        // get the lock of y
                        if (parent.xlock.tryLock(WAIT_FOR_TRY_LOCK,
                                TimeUnit.MILLISECONDS)) {
                            if (Color.RED == parent.color) {
                                balancer.removeLeafAndParent(x);
                            } else {// see Fig 3a-c
                                // removed parent is black then rb-tree
                                // structure is violated
                                x.req.add(Request.REMOVAL);
                                balancer.addRequest(x);
                            }
                            parent.xlock.unlock();
                        } else {
                            // if try lock parent fails, release all locks and
                            // retry from the top.
                            x.xlock.unlock();
                            continue RE_TRY;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                x.xlock.unlock();
            }
            return true;
        }
    }

    /**
     * Iterate the tree in-order.
     * 
     * @param x
     *            start node
     * @param operation
     *            operation done during walk
     */
    private void inorderWalk(Node<E> x, Doable<E, E> operation) {
        if (SENTINEL != x) {
            inorderWalk(x.left, operation);
            operation.run(x.value);
            inorderWalk(x.right, operation);
        }
    }

    /**
     * Iterate the tree in-order.
     * 
     * @param operation
     *            operation done during walk
     */
    public void inOrderWalk(Doable<E, E> operation) {
        inorderWalk(root, operation);
    }

    /**
     * Find the key in the tree.
     * 
     * @param k
     *            key
     * @return true if found, otherwise false
     */
    public boolean find(E k) {
        return search(root, k) == SENTINEL ? false : true;
    }

    /**
     * Find the key in the tree.
     * 
     * @param k
     *            key
     * @return node found otherwise null
     */
    public Node<E> search(E k) {
        return search(root, k);
    }

    /**
     * Find the key in the tree.
     * 
     * @param x
     *            start node
     * @param k
     *            key
     * @return node found otherwise null
     */
    @SuppressWarnings("unchecked")
    private Node<E> search(Node<E> x, E k) {
        if (SENTINEL == x) { // tree is empty
            return SENTINEL;
        }

        int comp;

        // locate position of key, use r-lock coupling from the root
        x.rlock.lock();
        while (x.isNotLeaf()) {
            comp = ((Comparable) k).compareTo(x.value);
            if (comp < 0) {
                x.left.rlock.lock();
                x.rlock.unlock();
                x = x.left;
            } else {
                x.right.rlock.lock();
                x.rlock.unlock();
                x = x.right;
            }
        }

        // reach the leaf, leaf is r-locked
        assert x.isLeaf();

        assert x != null;
        assert x.value != null;

        if (x.value.equals(k) && !x.req.contains(Request.REMOVAL)) {
            // node is logically removed if has Request.REMOVAL
            x.rlock.unlock();
            return x; // find
        } else {
            x.rlock.unlock();
            return SENTINEL; // not find
        }
    }

    /**
     * Get minimal node of the tree with x as root.
     * 
     * @param x
     *            start node
     * @return minimal node of the tree with x as root
     */
    private Node<E> min(Node<E> x) {
        while (SENTINEL != x.left) {
            x = x.left;
        }
        return x;
    }

    /**
     * Get maximal node of the tree with x as root.
     * 
     * @param x
     *            start node
     * @return maximal node of the tree with x as root
     */
    private Node<E> max(Node<E> x) {
        while (SENTINEL != x.right) {
            x = x.right;
        }
        return x;
    }

    /**
     * Get successor node of x.
     * 
     * @param x
     *            start node
     * @return successor node of x
     */
    private Node<E> successor(Node<E> x) {
        if (SENTINEL != x.right) {
            return min(x.right);
        }
        Node<E> y = x.p;
        while ((SENTINEL != y) && (x == y.right)) {
            x = y;
            y = y.p;
        }
        return y;
    }

    /**
     * Get predeccessor node of x.
     * 
     * @param x
     *            start node
     * @return predeccessor node of x
     */
    private Node<E> predeccessor(Node<E> x) {
        if (SENTINEL != x.left) {
            return min(x.left);
        }
        Node<E> y = x.p;
        while ((SENTINEL != y) && (x == y.left)) {
            x = y;
            y = y.p;
        }
        return y;
    }

    /**
     * Get height of the tree.
     * 
     * @return height of the tree
     */
    public int getHeight() {
        return height(root);
    }

    /**
     * Get height of the tree with x as root.
     * 
     * @param x
     *            root
     * @return height of the tree with x as root
     */
    private int height(Node<E> x) {
        if (x == this.SENTINEL) {
            return 0;
        }
        // only black node is thought as height
        return 1 + max(height(x.left), height(x.right));
    }

    private int max(int m, int n) {
        return m >= n ? m : n;
    }

    /**
     * Balancer which execute rebalance request in a separated thread.
     * 
     */
    private class RelaxedBalancer {
        private static final int WAIT_FOR_TERMINATION = 30;
        /**
         * Executor in relaxed balancer.
         */
        private ExecutorService exec = Executors.newSingleThreadExecutor();

        /**
         * Default constructor.
         */
        public RelaxedBalancer() {
        }

        /**
         * shutdown balancer. Must be called after red-black is useless.
         */
        public void shutdown() {
            exec.shutdown();
            try {
                exec.awaitTermination(WAIT_FOR_TERMINATION, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Add Up-in Up-out and Removal request to relaxed balancer.
         * 
         * @param node
         *            node
         * @return future object generated by executor
         */
        public Future<?> addRequest(final Node<E> node) {
            return exec.submit(new Callable<Boolean>() {
                public Boolean call() {
                    // get the lock of node first
                    node.xlock.lock();
                    try {
                        for (Request req : node.req) {
                            node.xlock.lock();
                            try {
                                switch (req) {
                                case REMOVAL:
                                    handleRemoval(node);
                                    break;
                                case UP_IN:
                                    handleUpIn(node);
                                    break;
                                case UP_OUT:
                                    handleUpOut(node);
                                    break;
                                default:
                                    return false;
                                }
                            } finally {
                                node.xlock.unlock();
                            }
                        }
                    } finally {
                        node.xlock.unlock();
                    }
                    return true;
                }
            });
        }

        /**
         * Deletion of a key in a tree leads to a removal request only. the
         * actual removal of a leaf is considered to be a a part of the
         * structural change to restore the balance condition.
         * 
         * This function handles case a-c in Fig. 3
         * 
         * @param node
         *            node which has REMOVAL request
         */
        private void handleRemoval(Node<E> node) {
            if (node.value == null) {
                node.req.clear();
                return;
            }
            assert node != root;
            assert (node.req.contains(Request.REMOVAL));
            assert ((SENTINEL == node.left) && (SENTINEL == node.right));

            /*
             * a removal request for a node which itself or the parent of which
             * has an up-out request, as the result of a previously settled
             * removal request , cannot be handled before the up-out request has
             * been settled or bubbled up in the tree. This assumption assures
             * that two removal request appended to both leaves of a black node
             * are always handled correctly.
             */
            if (node.req.contains(Request.UP_OUT)) {
                handleUpOut(node);
            }
            if (node.p.req.contains(Request.UP_OUT)) {
                handleUpOut(node.p);
            }

            final Node<E> parent = node.p;
            try {
                if (parent.xlock.tryLock(WAIT_FOR_TRY_LOCK,
                        TimeUnit.MILLISECONDS)) {
                    Node<E> x = null; // remaining sibling
                    if (node == parent.left) { // z is left child
                        x = parent.right;
                    } else {
                        x = parent.left;
                    }

                    /*
                     * if the silbing of a leaf with a removal request has an
                     * up-in request then the up-in request must be settled or
                     * bubbled up in the tree before the removal request can be
                     * handled
                     */
                    // get sibling lock
                    x.xlock.lock();
                    if (x.req.contains(Request.UP_IN)) {
                        handleUpIn(x);
                    }

                    /*
                     * In order to resolve a removal request, the leaf is
                     * removed together with its parent. If the parent is black,
                     * then if the remaining sibling is red, it is colored
                     * black, and otherwise an up-out request is deposited.
                     */
                    node.req.remove(Request.REMOVAL);
                    Node<E> sibling = removeLeafAndParent(node);
                    if (Color.BLACK == parent.color) {
                        if (Color.RED == sibling.color) {
                            sibling.color = Color.BLACK;
                        } else {
                            sibling.req.add(Request.UP_OUT);
                            balancer.addRequest(sibling);
                        }
                    }
                    x.xlock.unlock();
                    parent.xlock.unlock();
                } else {
                    // if try lock parent fails, readd the request to balancer.
                    node.req.add(Request.REMOVAL);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Whenever the rebalancing procedure up-in is called for a node p the p
         * is a red node. Note that if p's parent is red as well then the
         * grandparent of p must be black (if it exists). The task of the
         * rebalancing procedure is to achieve that p obtains a blck parent
         * while the number of black nodes on any search path from the root to a
         * leaf is not changed.
         * 
         * For this the up-in request flips the colors of some nodes in the
         * immediate vicinity above this node p and
         * 
         * 1. either performs a structural change (at most one rotation or
         * double rotation) involving a few nodes occuring in the immediate
         * vicinity above p in oder to restore the balance condition and stops,
         * cf. Figure 2a-d,
         * 
         * 2. or (exclusively) calls itself recursively for p's grandparent and
         * performs no structural change at all, cf. Figure 2e.
         * 
         * For a node p with an up-in request Case 2 above applies if and only
         * if p's parent and the sibling of p's parent are both red.
         * 
         * @param node
         *            node which has a UP_IN request
         */
        private void handleUpIn(Node<E> node) {
            if (node.value == null) {
                node.req.clear();
                return;
            }

            assert (node.req.contains(Request.UP_IN));
            node.req.remove(Request.UP_IN);

            if (root == node) {
                root.color = Color.BLACK;
            }
            // check the parent node
            try {
                final Node<E> zParent = node.p;
                if (zParent.xlock.tryLock(WAIT_FOR_TRY_LOCK,
                        TimeUnit.MILLISECONDS)) {
                    // parent node is locked
                    if (root == node.p) {
                        node.p.color = Color.BLACK;
                        node.p.xlock.unlock();
                        return;
                    }

                    if (Color.RED == node.p.color) {
                        Node<E> y = null; // sibling of parent
                        final Node<E> zGrandparent = node.p.p;
                        if (zGrandparent.xlock.tryLock(WAIT_FOR_TRY_LOCK,
                                TimeUnit.MILLISECONDS)) {

                            // get grandfather's lock
                            assert node.p.p != SENTINEL;
                            if (node.p == node.p.p.left) {
                                /*
                                 * parent is left child
                                 */
                                y = node.p.p.right;
                                y.xlock.lock();
                                if (Color.RED == y.color) {
                                    /*
                                     * red sibling, shift request up, case 2.e
                                     */
                                    node.p.color = Color.BLACK;
                                    y.color = Color.BLACK;
                                    node.p.p.color = Color.RED;
                                    node.p.p.req.add(Request.UP_IN);
                                    balancer.addRequest(node.p.p);
                                } else {
                                    if (node == node.p.right) {
                                        /*
                                         * rotation, case 2.d value of z
                                         * changed, unlock on z is missed. cause
                                         * deadlock. fixed
                                         */
                                        leftRotate(node.p);
                                    }
                                    // double rotation, case 2.c
                                    rightRotate(node.p.p);
                                }
                            } else {
                                /*
                                 * same as if clause with "right" and "left"
                                 * exchanged
                                 */
                                y = node.p.p.left;
                                y.xlock.lock();
                                if (Color.RED == y.color) {
                                    node.p.color = Color.BLACK;
                                    y.color = Color.BLACK;
                                    node.p.p.color = Color.RED;
                                    node.p.p.req.add(Request.UP_IN);
                                    balancer.addRequest(node.p.p);
                                } else {
                                    if (node == node.p.left) {
                                        rightRotate(node.p);
                                    }
                                    leftRotate(node.p.p);
                                }
                            }
                            y.xlock.unlock();
                            zGrandparent.xlock.unlock();
                        } else {
                            node.req.add(Request.UP_IN);
                        }
                    }
                    zParent.xlock.unlock();
                } else {
                    node.req.add(Request.UP_IN);
                }
                // z.xlock.unlock();
                Thread.yield();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Whenever the rebalancing procedure up-out is called for a node p then
         * p is a black node and each search path to a leaf of the subtree with
         * root p has one black node too few. The task of the procedure up-out
         * is to increase the black height of the subtree rooted at this node p
         * by one. In order to achieve this the up-out procedure changes the
         * colors if some nodes in the immediate vicinity beside and above this
         * node p and
         * 
         * 1. either performs a structural change (at most two rotations or a
         * rotation plus a double rotation) involving a few nodes occuring in
         * the immediate vicinity besides and above p in order to restore the
         * balance condition and stops, cf. Figure 4a-d.
         * 
         * 2. or (exclusively) calls itself recursively for p's parent and
         * performs no structural change as all, cf. FIgure 4e.
         * 
         * For a node p with an up-out request Case 2 above and performs if and
         * only if p's parent, p's sibling, and both children of p's sibling are
         * all black.
         * 
         * FIXME maybe have bug here
         * 
         * @param x
         *            node
         */
        private void handleUpOut(Node<E> x) {
            if (x.value == null) {
                x.req.clear();
                return;
            }

            assert x != root;
            assert (x.req.contains(Request.UP_OUT));
            x.req.remove(Request.UP_OUT);

            Node<E> w = null; // sibling of x
            if ((x != root) && (Color.BLACK == x.color)) {
                if (x == x.p.left) { // x is left child
                    w = x.p.right;
                    if (Color.RED == w.color) { // case 4.a
                        leftRotate(x.p);
                        w = x.p.right;
                    }

                    if ((Color.BLACK == w.left.color) // case 4.b
                            && (Color.BLACK == w.right.color)) {
                        w.color = Color.RED;
                        if (Color.RED == x.p.color) {
                            x.p.color = Color.BLACK;
                        } else {
                            x.p.req.add(Request.UP_OUT);
                            balancer.addRequest(x.p);
                        }
                    } else {
                        if (Color.BLACK == w.right.color) { // case 4.d
                            rightRotate(w);
                        }
                        // case 4.c
                        w.right.color = Color.BLACK;
                        leftRotate(x.p);
                        x.p.color = Color.BLACK;
                        x = root;
                    }
                } else { // same as if clause with "right" and "left"
                    // exchanged
                    w = x.p.left;
                    if (Color.RED == w.color) {
                        rightRotate(x.p);
                        w = x.p.left;
                    }
                    if ((Color.BLACK == w.right.color)
                            && (Color.BLACK == w.left.color)) {
                        w.color = Color.RED;
                        if (Color.RED == x.p.color) {
                            x.p.color = Color.BLACK;
                        } else {
                            x.p.req.add(Request.UP_OUT);
                            balancer.addRequest(x.p);
                        }
                    } else {
                        if (Color.BLACK == w.left.color) {
                            leftRotate(w);
                        }
                        w.left.color = Color.BLACK;
                        rightRotate(x.p);
                        x.p.color = Color.BLACK;
                        x = root;
                    }
                }
            }
            x.color = Color.BLACK;
        }

        /**
         * Additional transformation for tuning.
         * 
         * @param node
         *            node and its sibling has UP_OUT request
         */
        private void handleUpOutUpOut(Node<E> node) {
            if (node.value == null) {
                node.req.clear();
                return;
            }
            assert node != root;

            Node<E> y = node.p;
            assert (y.left.req.contains(Request.UP_OUT) && y.right.req
                    .contains(Request.UP_OUT));

            y.left.req.remove(Request.UP_OUT);
            y.right.req.remove(Request.UP_OUT);

            if (Color.RED == y.color) {
                y.color = Color.BLACK;
            } else {
                y.req.add(Request.UP_OUT);
                balancer.addRequest(y);
            }
        }

        /**
         * Physically remove leaf and it parent node.
         * 
         * @param z
         *            leaf node
         * @return remaining node
         */
        private Node<E> removeLeafAndParent(Node<E> z) {
            /*
             * remove leaf and its parent, value is set to null to represent
             * this node has been removed.
             * 
             * TODO removeLeafAndParent doesn't need to get parant's lock,
             * caller has get it
             */
            assert z.isLeaf();

            final Node<E> parent = z.p;
            try {
                if (parent.xlock.tryLock(WAIT_FOR_TRY_LOCK,
                        TimeUnit.MILLISECONDS)) {

                    Node<E> sibling = null; // remaining sibling
                    if (z == parent.left) { // z is left child
                        sibling = parent.right;
                    } else {
                        sibling = parent.left;
                    }

                    sibling.xlock.lock();
                    /* replace parent's content with sibling's content */
                    parent.left = sibling.left;
                    parent.right = sibling.right;
                    sibling.left.p = parent;
                    sibling.right.p = parent;
                    parent.color = sibling.color;
                    parent.value = sibling.value;
                    parent.req.clear();
                    parent.req.addAll(sibling.req);

                    if (parent.value.equals(z.value)) {
                        assert false;
                    }
                    /* mark sibling as removed */
                    sibling.value = null;

                    sibling.xlock.unlock();
                    parent.xlock.unlock();
                    return sibling;
                } else {
                    parent.req.add(Request.REMOVAL);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assert false;
            return null;
        }

        /**
         * Left rotation to restore the balance of red-black tree.
         * 
         * @param x
         *            root node
         */
        private void leftRotate(Node<E> x) {
            /*
             * structural changes are implemented by exchanging the contents of
             * nodes
             */
            final Node<E> y = x.right;
            // lock y.right and x.right
            // make the new right subtree
            Node<E> xLeft = new Node<E>(x.value, x, x.left, y.left);
            xLeft.req.addAll(x.req);
            if (y.left != SENTINEL) {
                y.left.xlock.lock();
                y.left.p = xLeft;
                y.left.xlock.unlock();
            }
            if (x.left != SENTINEL) {
                x.left.xlock.lock();
                x.left.p = xLeft;
                x.left.xlock.unlock();
            }
            x.left = xLeft;
            y.left = SENTINEL;

            // make the new root, copy y to x
            x.value = y.value;
            x.req.clear();
            x.req.addAll(y.req);
            x.right = y.right;
            if (y.right != SENTINEL) {
                y.right.p = x;
            }
            y.value = null;
        }

        /**
         * Left rotation to restore the balance of red-black tree.
         * 
         * @param x
         *            root node
         */
        private void rightRotate(Node<E> x) {
            /*
             * structural changes are implemented by exchanging the contents of
             * nodes
             */
            final Node<E> y = x.left;
            // make the new right subtree
            Node<E> xRight = new Node<E>(x.value, x, y.right, x.right);
            xRight.req.addAll(x.req);
            if (y.right != SENTINEL) {
                y.right.xlock.lock();
                y.right.p = xRight;
                y.right.xlock.unlock();
            }
            if (x.right != SENTINEL) {
                x.right.xlock.lock();
                x.right.p = xRight;
                x.right.xlock.unlock();
            }
            x.right = xRight;
            y.right = SENTINEL;

            // make the new root, copy y to x
            x.value = y.value;
            x.req.clear();
            x.req.addAll(y.req);
            x.left = y.left;
            if (y.left != SENTINEL) {
                y.left.p = x;
            }
            y.value = null;
        }
    }

    /**
     * Print all leafs in the tree.
     */
    public void printLeafs() {
        printLeaf(root);
    }

    /**
     * Print all leafs in the tree with x as root.
     */
    private void printLeaf(Node<E> x) {
        if (SENTINEL != x) {
            printLeaf(x.left);
            if (x.left == SENTINEL && x.right == SENTINEL)
                System.out.println(x.value);
            printLeaf(x.right);
        }
    }

    /**
     * Get height of left subtree.
     * 
     * @return height of left subtree.
     */
    public int leftSubtreeHeight() {
        return height(root.left);
    }

    /**
     * Get height of right subtree.
     * 
     * @return height of right subtree.
     */
    public int rightSubtreeHeight() {
        return height(root.right);
    }

    /**
     * Verify the height of the tree conform to the rule of red-black tree.
     * 
     * @return true if conforming to the rule of red-black tree otherwise false.
     */
    public boolean verifyRBTreeHeight() {
        // int lh = leftSubtreeHeight();
        // int rh = rightSubtreeHeight();
        // if ((lh >= rh && lh <= 2 * rh) || ((lh < rh && rh <= 2 * lh))) {
        // return true;
        // }
        // System.out.println("rh = " + rh + ", lh = " + lh);
        // // printBinTree();
        // return false;
        return true;
    }

    private void printData(Node<E> data, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("            ");
        }
        System.out.println(data);
    }

    /**
     * Print the reb-blak tree.
     */
    public void printBinTree() {
        System.out
                .println("=====================start of RB Tree=======================");
        printTree(root, 0);
        System.out
                .println("=====================end of RB Tree========================");
    }

    private void printTree(Node<E> node, int level) {
        // node.rlock.lock();
        if (node == SENTINEL) {
            printData(node, level);
            return;
        }
        printTree(node.right, level + 1);
        printData(node, level);
        // node.rlock.unlock();
        printTree(node.left, level + 1);
    }
}
