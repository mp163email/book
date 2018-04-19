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

/**
 * type of request attached on node.
 *
 */
enum Request {
    /**
     * up-in request. represent the node and parent node are both red which is
     * conflict with the definition of red-black tree. The up-in procedure flips
     * the colors of some nodes in the immediate vicinity above this node p and
     *
     * 1. either performs a structural change (at most one rotation or double
     * rotation) involving a few nodes occuring in the immediate vicinity above
     * p in oder to restore the balance condition and stops, cf. Figure 2a-d,
     *
     * 2. or (exclusively) calls itself recursively for p's grandparent and
     * performs no structural change at all, cf. Figure 2e.
     */
    UP_IN,
    /**
     * up-out request. the task of the procedure up-out is to increase the black
     * height of the subtree rooted at this node p by one. In order to achieve
     * this the up-out procedure changes the colors if some nodes in the
     * immediate vicinity beside and above this node p and
     *
     * 1. either performs a structural change (at most two rotations or a
     * rotation plus a double rotation) involving a few nodes occuring in the
     * immediate vicinity besides and above p in order to restore the balance
     * condition and stops, cf. Figure 4a-d.
     *
     * 2. or (exclusively) calls itself recursively for p's parent and performs
     * no structural change as all, cf. FIgure 4e.
     *
     */
    UP_OUT,
    /**
     * the deletion of a key in a tree leads to a removal request only. the
     * actual removal of a leaf is considered to be a a part of the structural
     * change to restore the balance condition.
     */
    REMOVAL
}
