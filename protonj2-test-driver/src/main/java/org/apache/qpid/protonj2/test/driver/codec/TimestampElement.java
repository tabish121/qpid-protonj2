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

import java.util.Date;

import io.netty5.buffer.Buffer;

class TimestampElement extends AtomicElement<Date> {

    private final Date value;

    TimestampElement(Element<?> parent, Element<?> prev, Date d) {
        super(parent, prev);
        value = d;
    }

    @Override
    public int size() {
        return isElementOfArray() ? 8 : 9;
    }

    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public Codec.DataType getDataType() {
        return Codec.DataType.TIMESTAMP;
    }

    @Override
    public int encode(Buffer buffer) {
        int size = size();
        if (size > buffer.implicitCapacityLimit() - buffer.capacity()) {
            return 0;
        }
        if (size == 9) {
            buffer.writeByte((byte) 0x83);
        }
        buffer.writeLong(value.getTime());

        return size;
    }
}
