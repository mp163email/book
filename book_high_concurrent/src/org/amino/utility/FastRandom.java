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
package org.amino.utility;

/**
 * This class provides fast random method. The quality of random number is not
 * as good as standard library. And it's also not thread-safe, which means two
 * threads might get the same random number if data race occurs.
 * 
 * @author Zhi Gan (ganzhi@gmail.com)
 * 
 */
public final class FastRandom {
    private static int seed = 5;

    /**
     * Utility classes should not have a public or default constructor.
     */
    private FastRandom() {
    }

    /**
     * Generate a pseudo-random integer. The range of generated number is [0,
     * 24001)
     * 
     * @return a pseudo-random integer which lies between [0, 24001)
     */
    public static int rand() {
        seed = (seed * 12000 + 5) % 24001;
        return seed;
    }

    /**
     * Generate a pseudo-random integer. The range of generated number is [0,
     * upper]
     * 
     * @param upper
     *            Upper bound of generated random number
     * @return a pseudo-random integer which lies between [0, min(24001, upper))
     */
    public static int nextInt(int upper) {
        seed = (seed * 12000 + 5) % 24001;
        return seed % upper;
    }
}
