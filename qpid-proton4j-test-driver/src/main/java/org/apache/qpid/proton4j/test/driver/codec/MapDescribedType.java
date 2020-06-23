/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.proton4j.test.driver.codec;

import java.util.HashMap;
import java.util.Map;

import org.apache.qpid.proton4j.test.driver.codec.primitives.DescribedType;

/**
 * Basic Described type that contains a Map as the value.
 */
public abstract class MapDescribedType implements DescribedType {

    private final Map<Object, Object> fields;

    public MapDescribedType() {
        fields = new HashMap<>();
    }

    @Override
    public Map<Object, Object> getDescribed() {
        return fields;
    }

    @Override
    public String toString() {
        return "MapDescribedType [descriptor=" + getDescriptor() + " fields=" + fields + "]";
    }
}