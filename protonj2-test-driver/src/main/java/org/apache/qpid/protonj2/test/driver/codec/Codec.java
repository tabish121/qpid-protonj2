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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.qpid.protonj2.test.driver.codec.primitives.Binary;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Decimal128;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Decimal32;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Decimal64;
import org.apache.qpid.protonj2.test.driver.codec.primitives.DescribedType;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Symbol;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedByte;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedInteger;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedLong;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedShort;

import io.netty5.buffer.Buffer;

public interface Codec {

    public static final class Factory {

        public static Codec create() {
            return new CodecImpl();
        }
    }

    enum DataType {
        NULL,
        BOOL,
        UBYTE,
        BYTE,
        USHORT,
        SHORT,
        UINT,
        INT,
        CHAR,
        ULONG,
        LONG,
        TIMESTAMP,
        FLOAT,
        DOUBLE,
        DECIMAL32,
        DECIMAL64,
        DECIMAL128,
        UUID,
        BINARY,
        STRING,
        SYMBOL,
        DESCRIBED,
        ARRAY,
        LIST,
        MAP
    }

    void free();

    void clear();

    long size();

    void rewind();

    DataType next();

    DataType prev();

    boolean enter();

    boolean exit();

    DataType type();

    long encodedSize();

    long encode(Buffer buffer);

    long decode(Buffer buffer);

    void putList();

    void putMap();

    void putArray(boolean described, DataType type);

    void putDescribed();

    void putNull();

    void putBoolean(boolean b);

    void putUnsignedByte(UnsignedByte ub);

    void putByte(byte b);

    void putUnsignedShort(UnsignedShort us);

    void putShort(short s);

    void putUnsignedInteger(UnsignedInteger ui);

    void putInt(int i);

    void putChar(int c);

    void putUnsignedLong(UnsignedLong ul);

    void putLong(long l);

    void putTimestamp(Date t);

    void putFloat(float f);

    void putDouble(double d);

    void putDecimal32(Decimal32 d);

    void putDecimal64(Decimal64 d);

    void putDecimal128(Decimal128 d);

    void putUUID(UUID u);

    void putBinary(Binary bytes);

    void putBinary(byte[] bytes);

    void putString(String string);

    void putSymbol(Symbol symbol);

    void putObject(Object o);

    void putJavaMap(Map<Object, Object> map);

    void putJavaList(List<Object> list);

    void putDescribedType(DescribedType dt);

    long getList();

    long getMap();

    long getArray();

    boolean isArrayDescribed();

    DataType getArrayType();

    boolean isDescribed();

    boolean isNull();

    boolean getBoolean();

    UnsignedByte getUnsignedByte();

    byte getByte();

    UnsignedShort getUnsignedShort();

    short getShort();

    UnsignedInteger getUnsignedInteger();

    int getInt();

    int getChar();

    UnsignedLong getUnsignedLong();

    long getLong();

    Date getTimestamp();

    float getFloat();

    double getDouble();

    Decimal32 getDecimal32();

    Decimal64 getDecimal64();

    Decimal128 getDecimal128();

    UUID getUUID();

    Binary getBinary();

    String getString();

    Symbol getSymbol();

    Object getObject();

    Map<Object, Object> getJavaMap();

    List<Object> getJavaList();

    Object[] getJavaArray();

    DescribedType getDescribedType();

    String format();

}
