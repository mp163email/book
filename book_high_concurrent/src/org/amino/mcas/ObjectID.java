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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * a object with a id used for global sequential.
 *
 */
public class ObjectID {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    /**
     * Object id.
     */
    int id;

    /**
     * default constructor.
     */
    public ObjectID() {
//        System.out.println("ObjectID++");
        id = ID_GENERATOR.getAndIncrement();
    }
}
