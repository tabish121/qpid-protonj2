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

class NullElement extends AtomicElement<Void> {

    NullElement(Element<?> parent, Element<?> prev) {
        super(parent, prev);
    }

    @Override
    public int size() {
        return isElementOfArray() ? 0 : 1;
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public Codec.DataType getDataType() {
        return Codec.DataType.NULL;
    }

    @Override
    public int encode(Buffer buffer) {
        if (buffer.writableBytes() > 0 && !isElementOfArray()) {
            buffer.writeByte((byte) 0x40);
            return 1;
        }
        return 0;
    }
}
