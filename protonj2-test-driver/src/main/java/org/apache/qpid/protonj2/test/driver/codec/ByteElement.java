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

class ByteElement extends AtomicElement<Byte> {

    private final byte value;

    ByteElement(Element<?> parent, Element<?> prev, byte b) {
        super(parent, prev);
        value = b;
    }

    @Override
    public int size() {
        return isElementOfArray() ? 1 : 2;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public Codec.DataType getDataType() {
        return Codec.DataType.BYTE;
    }

    @Override
    public int encode(Buffer buffer) {
        if (isElementOfArray()) {
            if (buffer.writableBytes() > 0) {
                buffer.writeByte(value);
                return 1;
            }
        } else {
            if ((buffer.implicitCapacityLimit() - buffer.capacity()) >= 2) {
                buffer.writeByte((byte) 0x51);
                buffer.writeByte(value);
                return 2;
            }
        }
        return 0;
    }
}
