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
package org.apache.qpid.protonj2.test.driver.codec;

import io.netty5.buffer.Buffer;

class FloatElement extends AtomicElement<Float> {

    private final float value;

    FloatElement(Element<?> parent, Element<?> prev, float f) {
        super(parent, prev);
        value = f;
    }

    @Override
    public int size() {
        return isElementOfArray() ? 4 : 5;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public Codec.DataType getDataType() {
        return Codec.DataType.FLOAT;
    }

    @Override
    public int encode(Buffer buffer) {
        int size = size();
        if (buffer.implicitCapacityLimit() >= size) {
            if (size == 5) {
                buffer.writeByte((byte) 0x72);
            }
            buffer.writeFloat(value);
            return size;
        } else {
            return 0;
        }
    }
}
