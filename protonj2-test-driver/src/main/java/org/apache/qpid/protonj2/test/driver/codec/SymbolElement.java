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

import java.nio.charset.Charset;

import org.apache.qpid.protonj2.test.driver.codec.primitives.Symbol;

import io.netty5.buffer.Buffer;

class SymbolElement extends AtomicElement<Symbol> {

    private static final Charset ASCII = Charset.forName("US-ASCII");
    private final Symbol value;

    SymbolElement(Element<?> parent, Element<?> prev, Symbol s) {
        super(parent, prev);
        value = s;
    }

    @Override
    public int size() {
        final int length = value.getLength();

        if (isElementOfArray()) {
            final ArrayElement parent = (ArrayElement) parent();

            if (parent.constructorType() == ArrayElement.SMALL) {
                if (length > 255) {
                    parent.setConstructorType(ArrayElement.LARGE);
                    return 4 + length;
                } else {
                    return 1 + length;
                }
            } else {
                return 4 + length;
            }
        } else {
            if (length > 255) {
                return 5 + length;
            } else {
                return 2 + length;
            }
        }
    }

    @Override
    public Symbol getValue() {
        return value;
    }

    @Override
    public Codec.DataType getDataType() {
        return Codec.DataType.SYMBOL;
    }

    @Override
    public int encode(Buffer buffer) {
        int size = size();
        if (buffer.implicitCapacityLimit() - buffer.capacity() < size) {
            return 0;
        }
        if (isElementOfArray()) {
            final ArrayElement parent = (ArrayElement) parent();

            if (parent.constructorType() == ArrayElement.SMALL) {
                buffer.writeByte((byte) value.getLength());
            } else {
                buffer.writeInt(value.getLength());
            }
        } else if (value.getLength() <= 255) {
            buffer.writeByte((byte) 0xa3);
            buffer.writeByte((byte) value.getLength());
        } else {
            buffer.writeByte((byte) 0xb3);
            buffer.writeByte((byte) value.getLength());
        }
        buffer.writeBytes(value.toString().getBytes(ASCII));
        return size;
    }
}
