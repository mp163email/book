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
 * Status of MCAS processing.
 */
enum Status {

    /**
     * In the middle of MCAS.
     */
    UNDECIDED,
    /**
     * MCAS is successful.
     */
    SUCCESSFUL,
    /**
     * MCAS is failed.
     */
    FAILED
}

/**
 * This is an implementation of a software Multi-CAS.
 * <p>
 * The implementation is according to the technical report Practical
 * lock-freedom by Keir Fraser, 2004. To gain a complete understanding of this
 * data structure, please first read this paper, available at:
 * http://www.cl.cam.ac.uk/techreports/UCAM-CL-TR-579.pdf
 * <p>
 * MCAS extends the single-word CAS primitive to operate on multiple locations
 * simultaneously. More precisely, MCAS is defined to operate on N distinct
 * memory locations (ai), expected values (ei), and new values (ni): each ai is
 * updated to value ni if and only if each ai contains the expected value ei
 * before the operation.
 * <p>
 * Consider the following two requirements that must be satisfied by any valid
 * MCAS design. Firstly, it should appear to execute atomically: a successful
 * MCAS must instantaneously replace the expected value in each specified memory
 * location with its new value. Secondly, if an MCAS invocation fails because
 * some location does not contain the expected value then it must be able to
 * 'undo' any updates it has made so far and leave all memory locations as they
 * were before it began executing.
 * <p>
 * Both these requirements are satisfied by using a two-phase algorithm. The
 * first phase gains ownership of each data location involved in the operation.
 * If the operation successfully gains ownership of every location then it is
 * deemed successful and all updates atomically become visible. This decision
 * point is followed by a second phase in which each data location is updated to
 * its new value if the first phase succeeded, or reverted to its old value if
 * the first phase failed. Illustrative snapshots of an example MCAS operation
 * are shown in Figure 3.1. Note that this two-phase structure has a further
 * advantage in that an in-progress operation can arrange to 'describe itself'
 * to any remote operation that it may obstruct: this allows recursive helping
 * to be used to obtain the required lock-free progress guarantee.
 * <p>
 * Sample performance results here
 * <p>
 * The following operations are thread-safe and scalable (but see notes in
 * method javadoc): mcasRead, mcas, ccasRead, ccas.
 * <p>
 * The following operations are not thread-safe:
 * <p>
 * 
 * @author Xiao Jun Dai
 * 
 */
final class MultiCAS {
    /**
     * Utility classes should not have a public or default constructor.
     */
    private MultiCAS() {
    }

    /**
     * MCAS descriptor installed in the memory. Each MCAS operation creates a
     * descriptor which fully describes the updates to be made (a set of (ai,
     * ei, ni) tuples) and the current status of the operation (undecided,
     * failed, or successful).
     * 
     */
    private static class MCASDesc {
        /**
         * Number of memory address to multi-cas.
         */
        int numOfAddr;
        /**
         * Array of objects. Because in Java, memory address cannot be access
         * directly, use object address and offset of field in the object
         * instead.
         */
        Object[] obj;
        /**
         * Array of offset from object.
         */
        long[] offset;
        /**
         * Expected values.
         */
        Object[] e;
        /**
         * New values.
         */
        Object[] n;
        /**
         * Status of multi-cas.
         */
        volatile Status status;

        /**
         * Offset of status used by Unsafe.
         */
        static final long STATUS_OFFSET;

        static {
            try {
                STATUS_OFFSET = UNSAFE.objectFieldOffset(MCASDesc.class
                        .getDeclaredField("status"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Constructor.
         * 
         * @param nAddr
         *            Number of memory address to multi-cas.
         * @param obj
         *            Array of objects. Because in Java, memory address cannot
         *            be access directly, use object address and offset of field
         *            in the object instead.
         * @param offset
         *            Array of offset from object.
         * @param e
         *            Expected values.
         * @param n
         *            New values.
         */
        public MCASDesc(int nAddr, Object[] obj, long[] offset, Object[] e,
                Object[] n) {
            this.numOfAddr = nAddr;
            this.obj = obj;
            this.offset = offset;
            this.e = e;
            this.n = n;
            this.status = Status.UNDECIDED;
        }
    }

    /**
     * CCAS descriptor install in the memory. CCAS means conditional CAS. CCAS
     * uses a second conditional memory location to control the execution of a
     * normal CAS operation. If the contents of the conditional location are
     * zero then the operation proceeds, otherwise CCAS has no effect.
     */
    private static class CCASDesc {
        /**
         * Object to CAS.
         */
        Object obj;
        /**
         * Offset of field in object.
         */
        long offset;
        /**
         * Expected value.
         */
        Object e;
        /**
         * New value.
         */
        Object n;
        /**
         * Condition.
         */
        Status cond;

        /**
         * Constructor.
         * 
         * @param obj
         *            Object to CAS.
         * @param offset
         *            Offset of field in object.
         * @param e
         *            Expected value.
         * @param n
         *            New value.
         * @param cond
         *            Condition.
         */
        public CCASDesc(Object obj, long offset, Object e, Object n, Status cond) {
            this.obj = obj;
            this.offset = offset;
            this.e = e;
            this.n = n;
            this.cond = cond;
        }
    }

    /**
     * Unsafe object wrapper to use sun.mics.Unsafe.
     */
    private static final Unsafe UNSAFE = UnsafeWrapper.getUnsafe();

    /**
     * update N distinct memory locations obj + offset from expected values to
     * new values atomically.
     * 
     * @param nAddr
     *            number of memory field to be compare and set
     * @param obj
     *            array of object address
     * @param offset
     *            array of offset for every object
     * @param e
     *            array of expected value
     * @param n
     *            array of new value
     * @return true if mcas is successful, otherwise false
     */
    static boolean mcas(int nAddr, Object[] obj, long[] offset, Object[] e,
            Object[] n) {
        /* Generate a new mcas descriptor. */
        MCASDesc d = new MCASDesc(nAddr, obj, offset, e, n);
        // System.out.println("before sort address");
        // for (int i = 0; i < N; i++) {
        // System.out.println(obj[i] + ":" + ((ObjectID) obj[i]).getID());
        // }

        /*
         * Memory locations must be sorted into address order. Note that MCAS
         * must acquire update locations in address order. This ensures that
         * recursive helping eventually results in system-wide progress because
         * each level of recursion must be caused by a conflict at a strictly
         * higher memory address than the previous level. Recursive helping is
         * therefore bounded by the number of memory locations in the shared
         * heap. To ensure that updates are ordered correctly it sorts the
         * update locations before calling MCASHelp. The sort can be omitted if
         * the caller ensures that addresses are specified in some global total
         * order . If addresses are not ordered then a recursive loop may be
         * entered.
         */
//        addressSort(d, nAddr);

        // System.out.println("after sort address");
        // for (int i = 0; i < N; i++) {
        // System.out.println(obj[i] + ":" + ((ObjectID) obj[i]).getID());
        // }

        return mcasHelp(d);
    }

    /**
     * Sort memory locations in order. Object id is the key to sort.
     * 
     * TODO use sort() in jdk.
     * 
     * @param d
     *            mcas descriptor
     * @param nAddr
     *            number of memory address to be sorted
     */
    private static void addressSort(MCASDesc d, int nAddr) {
        Object[] obj = d.obj;
        long[] offset = d.offset;
        Object[] e = d.e;
        Object[] n = d.n;
        int i;

        int key;

        /* temporary variables */
        Object tempO;
        long tempOff;
        Object tempE;
        Object tempN;

        for (int j = 1; j < nAddr; j++) {
            tempO = obj[j];
            tempOff = offset[j];
            tempE = e[j];
            tempN = n[j];

            /* Object id is the key to sort */
            key = ((ObjectID) tempO).id;
            i = j - 1;

            while (i >= 0 && ((ObjectID) obj[i]).id > key) {
                obj[i + 1] = obj[i];
                offset[i + 1] = offset[i];
                e[i + 1] = e[i];
                n[i + 1] = n[i];

                i--;
            }

            obj[i + 1] = tempO;
            offset[i + 1] = tempOff;
            e[i + 1] = tempE;
            n[i + 1] = tempN;
        }
    }

    /**
     * Read from locations which may be subject to concurrentMCAS operations.
     * 
     * @param obj
     *            Object be read
     * @param offset
     *            offset of field in object
     * @return read value
     */
    static Object mcasRead(Object obj, long offset) {
        Object v;
        for (v = ccasRead(obj, offset); isMCASDesc(v); v = ccasRead(obj, offset)) {
            mcasHelp((MCASDesc) v);
        }
        return v;
    }

    /**
     * Helper function of mcas.
     * 
     * @param d
     *            mcas descriptor.
     * @return true if mcas success, otherwise false.
     */
    private static boolean mcasHelp(MCASDesc d) {
        /*
         * The first phase of the MCAS algorithm attempts to update each
         * location ai from its expected value, ei, to a reference to the
         * operation's descriptor. This allows processes to distinguish
         * currently-owned memory locations, enables recursive helping of
         * incomplete operations, and permits atomic update of 'logical' memory
         * values at the MCAS decision point. Note that only owned locations can
         * contain references to descriptors; furthermore, descriptors
         * themselves will never be subject to MCAS updates.
         * 
         * The current logical (or application) value of an owned location is
         * found by interrogating the MCAS descriptor that is installed there.
         * The descriptor is searched to find the appropriate address, ai. The
         * current logical value is then either ei, if the descriptor status is
         * currently undecided or failed, or ni, if the status is successful.
         * All locations that are not currently owned store their logical value
         * directly, allowing direct access with no further computation or
         * memory accesses. To determine whether a location is currently owned,
         * IsMCASDesc(p) is used to discover whether the given pointer p is a
         * reference to an MCAS descriptor.
         */
        Object v;
        Status desired = Status.FAILED;
        boolean success;
        int nAddr = d.numOfAddr;

        /* PHASE 1: Attempt to acquire each location in turn */
        decision_point: while (true) {
            for (int i = 0; i < nAddr; i++) {
                while (true) {
                    ccas(d.obj[i], d.offset[i], d.e[i], d, d.status);
                    // if CCAS fails
                    v = UNSAFE.getObject(d.obj[i], d.offset[i]);
                    // try {
                    // Thread.sleep(ran.nextInt(100));
                    // } catch (InterruptedException e) {
                    // e.printStackTrace();
                    // }
                    if ((v == d.e[i]) && (d.status == Status.UNDECIDED)) {
                        continue;
                    }
                    if (v == d) {/* Move to next location */
                        break;
                    }
                    if (!isMCASDesc(v)) {
                        break decision_point;
                    }
                    mcasHelp((MCASDesc) v);
                }
            }
            desired = Status.SUCCESSFUL;
            break decision_point;
        }

        /* decision point */
        UNSAFE.compareAndSwapObject(d, MCASDesc.STATUS_OFFSET,
                Status.UNDECIDED, desired);

        /* PHASE 2: Release each location that we hold */
        success = (d.status == Status.SUCCESSFUL);
        for (int i = 0; i < nAddr; ++i) {
            UNSAFE.compareAndSwapObject(d.obj[i], d.offset[i], d,
                    success ? d.n[i] : d.e[i]);
        }
        return success;
    }

    /**
     * Discover whether the given pointer p is a reference to an MCAS descriptor
     * to determine whether a location is currently owned.
     * 
     * @param p
     *            Given object.
     * @return true if a location is currently installed a descriptorl,
     *         otherwise false
     */
    private static boolean isMCASDesc(Object p) {
        return p instanceof MCASDesc;
    }

    /**
     * Conditional CAS. CCAS uses a second conditional memory location to
     * control the execution of a normal CAS operation. If the contents of the
     * conditional location are zero then the operation proceeds, otherwise CCAS
     * has no effect.
     * <p>
     * The use of CCAS in MCAS requires that the undecided status value is
     * represented by zero, thus allowing shared-memory locations to be acquired
     * only if the outcome of the MCAS operation is not yet decided. This
     * prevents a phase- one update from occurring 'too late': if a normal CAS
     * were used then each memory location might be updated more than once
     * because a helping process could incorrectly reacquire a location after
     * theMCAS operation has already succeeded. This can happen if a CAS to
     * install the descriptor is delayed, and in the meantime the memory
     * location is modified back to the expected value: this is commonly called
     * the ABA problem.
     * 
     * @param obj
     *            object be ccas
     * @param offset
     *            file offset of object
     * @param e
     *            expected value
     * @param n
     *            new value
     * @param cond
     *            condition
     */
    static void ccas(Object obj, long offset, Object e, Object n, Status cond) {
        /*
         * The pseudocode design begins by installing a CCAS descriptor in the
         * location to be updated. This ensures that the location's logical
         * value is the expected value while the conditional location is tested,
         * so that a successful CCAS operation linearises (atomically occurs)
         * when the conditional location is read from. If the update location
         * doesn't contain the expected value then CCAS fails; if it contains
         * another CCAS descriptor then that operation is helped to complete
         * before retrying. If the update location is successfully acquired, the
         * conditional location is tested. Depending on the contents of this
         * location, the descriptor is either replaced with the new value, or
         * with the original expected value . CAS is used so that this update is
         * performed exactly once even when the CCAS operation is helped to
         * complete by other processes
         */
        CCASDesc d = new CCASDesc(obj, offset, e, n, cond);
        Object v;

        while (!UNSAFE.compareAndSwapObject(d.obj, d.offset, d.e, d)) {
            v = UNSAFE.getObject(d.obj, d.offset);
            if (!isCCASDesc(v)) {
                return;
            }
            ccasHelp((CCASDesc) v);
        }
        ccasHelp(d);
    }

    /**
     * Read from locations which may be subject to concurrent CCAS operations.
     * 
     * @param obj
     *            obejct read
     * @param offset
     *            field offset of object
     * @return object read
     */
    static Object ccasRead(Object obj, long offset) {
        Object v;
        for (v = UNSAFE.getObject(obj, offset); isCCASDesc(v); v = UNSAFE
                .getObject(obj, offset)) {
            ccasHelp((CCASDesc) v);
        }
        return v;
    }

    /**
     * Helper function of ccas.
     * 
     * @param d
     *            ccas descritpor.
     */
    private static void ccasHelp(CCASDesc d) {
        boolean success = (d.cond == Status.UNDECIDED);
        UNSAFE.compareAndSwapObject(d.obj, d.offset, d, success ? d.n : d.e);
    }

    private static boolean isCCASDesc(Object v) {
        return v instanceof CCASDesc;
    }
}