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

package org.amino;

/**
 * Controlling Runtime class for Amino project. Controls many aspects of
 * underlying implementation of Amino packages.
 * 
 * @author donawa
 * 
 */
public final class Runtime {
    /**
     * Utility classes should not have a public or default constructor.
     */
    private Runtime() {
    }

    private static int versionMajor = 0;
    private static int versionMinor = 1;
    private static volatile int availableWorkerThreads = -1;
    private static volatile int maxAvailableWorkerThreads = -1;
    private static volatile boolean manageWorkerThreads = false;

    /**
     * Return major version number.
     * 
     * @return Major version number
     */
    public static int getMajorVersion() {
        return versionMajor;
    }

    /**
     * Return minor version number.
     * 
     * @return Minor version number
     */
    public static int getMinorVersion() {
        return versionMinor;
    }

    /**
     * Return current version of Amino package.
     * 
     * @return Version string in the form "Major.Minor".
     */
    public static String getVersion() {
        return new String(versionMajor + "." + versionMinor);
    }

    /**
     * Return whether Amino Runtime is managing worker threads.
     * 
     * @return true if the Amino Runtime settings have an effect on worker
     *         threads within the Amino packages
     */
    public static boolean manageWorkerThreads() {
        return manageWorkerThreads;
    }

    /**
     * Used to determine whether the Amino Runtime controls how many worker
     * threads are active at any one time within Amino packages. Useful to
     * minimize thread spawning in a recursively parallel environment.
     * 
     * @param b
     *            true if Amino packages should check the Runtime settings for
     *            how they use worker threads.
     */
    public static void setManageWorkerThreads(boolean b) {
        manageWorkerThreads = b;
    }

    /**
     * Return number of available worker threads.
     * 
     * @return current number of threads available to work within Amino packages
     */
    public static int getNumberAvailableWorkerThreads() {
        if (availableWorkerThreads < 0)
            availableWorkerThreads = getMaxAvailableWorkerThreads();
        return availableWorkerThreads;
    }

    /**
     * Return maximum number of worker threads.
     * 
     * @return maximum number of threads available to work within Amino packages
     */
    public static int getMaxAvailableWorkerThreads() {
        if (maxAvailableWorkerThreads < 0)
            maxAvailableWorkerThreads = java.lang.Runtime.getRuntime()
                    .availableProcessors();
        return maxAvailableWorkerThreads;
    }

    /**
     * Set upper limit of threads available to work within Amino packages.
     * 
     * @param n
     *            Maximum number of threads to be available for work within
     *            Amino packages.
     */
    public static void setMaxAvailableWorkerThreads(int n) {
        if (n > 0)
            maxAvailableWorkerThreads = n;
    }

    /**
     * Decrease count of threads available to participate in work if
     * manageWorkerThreads returns true.
     * 
     * @param numberThreads
     *            Number of threads to remove from total count of available
     *            worker threads
     * @return Number of threads actually reserved for work
     * @see #manageWorkerThreads()
     */
    public static synchronized int reserveThreads(int numberThreads) {
        if (!manageWorkerThreads())
            return numberThreads;
        if (numberThreads < 0)
            return 0;
        if (numberThreads > getMaxAvailableWorkerThreads())
            numberThreads = getMaxAvailableWorkerThreads();

        availableWorkerThreads -= numberThreads;
        return numberThreads;
    }

    /**
     * Increase count of threads available to participate in work. If
     * manageWorkerThreads returns true, then at most
     * getMaxAvailableWorkerThreads() - getNumberAvailableWorkerThreads() can be
     * released.
     * 
     * @param numberThreads
     *            Number of additional threads now available
     * @return Total amount of threads actually released
     * @see #getMaxAvailableWorkerThreads()
     * @see #getMaxAvailableWorkerThreads()
     * @see #manageWorkerThreads()
     */
    public static synchronized int releaseThreads(int numberThreads) {
        if (!manageWorkerThreads())
            return numberThreads;
        if (numberThreads < 0)
            return 0;

        int maxThreads = getMaxAvailableWorkerThreads();
        if (availableWorkerThreads + numberThreads > maxThreads)
            numberThreads = maxThreads - availableWorkerThreads;
        availableWorkerThreads += numberThreads;
        return numberThreads;
    }
}