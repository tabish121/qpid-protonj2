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

package org.apache.qpid.protonj2.buffer.netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferComponent;
import io.netty5.buffer.ByteCursor;
import io.netty5.buffer.ComponentIterator;
import io.netty5.buffer.ComponentIterator.Next;
import io.netty5.util.Send;

/**
 * Adapts a {@link ProtonBuffer} instance to a Netty 5 {@link Buffer}
 */
public class ProtonBufferToNetty5Adapter implements Buffer {

    private ProtonBuffer resource;

    public ProtonBufferToNetty5Adapter(ProtonBuffer resource) {
        this.resource = resource;
    }

    @Override
    public void close() {
        resource.close();
    }

    @Override
    public Send<Buffer> send() {
        final ProtonBuffer transferred = resource.transfer();

        return new Send<Buffer>() {

            @Override
            public Buffer receive() {
                return new ProtonBufferToNetty5Adapter(transferred);
            }

            @Override
            public void close() {
                transferred.close();
            }

            @Override
            public boolean referentIsInstanceOf(Class<?> cls) {
                return cls.isAssignableFrom(ProtonBufferToNetty5Adapter.class);
            }
        };
    }

    @Override
    public boolean isAccessible() {
        return !resource.isClosed();
    }

    @Override
    public Buffer compact() {
        resource.compact();
        return this;
    }

    @Override
    public Buffer makeReadOnly() {
        resource.convertToReadOnly();
        return this;
    }

    @Override
    public boolean readOnly() {
        return resource.isReadOnly();
    }

    @Override
    public Buffer fill(byte value) {
        resource.fill(value);
        return this;
    }

    @Override
    public boolean isDirect() {
        return resource.isDirect();
    }

    @Override
    public int capacity() {
        return resource.capacity();
    }

    @Override
    public Buffer implicitCapacityLimit(int limit) {
        resource.implicitGrowthLimit(limit);
        return this;
    }

    @Override
    public int implicitCapacityLimit() {
        return resource.implicitGrowthLimit();
    }

    @Override
    public int readerOffset() {
        return resource.getReadOffset();
    }

    @Override
    public Buffer readerOffset(int offset) {
        resource.setReadOffset(offset);
        return this;
    }

    @Override
    public int writerOffset() {
        return resource.getWriteOffset();
    }

    @Override
    public Buffer writerOffset(int offset) {
        resource.setWriteOffset(offset);
        return this;
    }

    @Override
    public Buffer ensureWritable(int size, int minimumGrowth, boolean allowCompaction) {
        resource.ensureWritable(size, minimumGrowth, allowCompaction);
        return this;
    }

    @Override
    public void copyInto(int srcPos, byte[] dest, int destPos, int length) {
        resource.copyInto(srcPos, dest, destPos, length);
    }

    @Override
    public void copyInto(int srcPos, ByteBuffer dest, int destPos, int length) {
        resource.copyInto(srcPos, dest, destPos, length);
    }

    @Override
    public void copyInto(int srcPos, Buffer dest, int destPos, int length) {
        // TODO Auto-generated method stub
    }

    @Override
    public Buffer copy(int offset, int length, boolean readOnly) {
        return new ProtonBufferToNetty5Adapter(resource.copy(offset, length));
    }

    @Override
    public Buffer split(int splitOffset) {
        return new ProtonBufferToNetty5Adapter(resource.split(splitOffset));
    }

    @Override
    public int bytesBefore(byte needle) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int bytesBefore(Buffer needle) {
        // TODO Auto-generated method stub
        return 0;
    }

    //----- Buffer input and output APIs

    @Override
    public int transferTo(WritableByteChannel channel, int length) throws IOException {
        return resource.transferTo(channel, length);
    }

    @Override
    public int transferFrom(FileChannel channel, long position, int length) throws IOException {
        return resource.transferFrom(channel, position, length);
    }

    @Override
    public int transferFrom(ReadableByteChannel channel, int length) throws IOException {
        return resource.transferFrom(channel, length);
    }

    //----- Buffer components and cursors API

    @Override
    public ByteCursor openCursor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteCursor openCursor(int fromOffset, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteCursor openReverseCursor(int fromOffset, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int countComponents() {
        return resource.componentCount();
    }

    @Override
    public int countReadableComponents() {
        return resource.readableComponentCount();
    }

    @Override
    public int countWritableComponents() {
        return resource.writableComponentCount();
    }

    @Override
    public <T extends BufferComponent & Next> ComponentIterator<T> forEachComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    //----- Primitive indexed set API

    @Override
    public Buffer setByte(int woff, byte value) {
        resource.setByte(woff, value);
        return this;
    }

    @Override
    public Buffer setUnsignedByte(int woff, int value) {
        resource.setByte(woff, (byte)(value & 0xFF));
        return this;
    }

    @Override
    public Buffer setChar(int woff, char value) {
        resource.setChar(woff, value);
        return this;
    }

    @Override
    public Buffer setShort(int woff, short value) {
        resource.setShort(woff, value);
        return this;
    }

    @Override
    public Buffer setUnsignedShort(int woff, int value) {
        resource.setUnsignedShort(woff, (short)(value & 0x0000FFFF));
        return null;
    }

    @Override
    public Buffer setMedium(int woff, int value) {
        resource.setByte(woff, (byte) (value >>> 16));
        resource.setByte(woff + 1, (byte) (value >>> 8));
        resource.setByte(woff + 2, (byte) (value >>> 0));

        return this;
    }

    @Override
    public Buffer setUnsignedMedium(int woff, int value) {
        resource.setByte(woff, (byte) (value >>> 16));
        resource.setByte(woff + 1, (byte) (value >>> 8));
        resource.setByte(woff + 2, (byte) (value >>> 0));

        return this;
    }

    @Override
    public Buffer setInt(int woff, int value) {
        resource.setInt(woff, value);
        return this;
    }

    @Override
    public Buffer setUnsignedInt(int woff, long value) {
        resource.setUnsignedInt(woff, (int)(value & 0x00000000FFFFFFFFl));
        return this;
    }

    @Override
    public Buffer setLong(int woff, long value) {
        resource.setLong(woff, value);
        return this;
    }

    @Override
    public Buffer setFloat(int woff, float value) {
        resource.setFloat(woff, value);
        return this;
    }

    @Override
    public Buffer setDouble(int woff, double value) {
        resource.setDouble(woff, value);
        return this;
    }

    //----- Primitive relative write API

    @Override
    public Buffer writeByte(byte value) {
        resource.writeByte(value);
        return this;
    }

    @Override
    public Buffer writeUnsignedByte(int value) {
        resource.writeByte((byte) (value & 0xFF));
        return this;
    }

    @Override
    public Buffer writeShort(short value) {
        resource.writeShort(value);
        return this;
    }

    @Override
    public Buffer writeUnsignedShort(int value) {
        resource.writeShort((short) (value & 0x00FF));
        return this;
    }

    @Override
    public Buffer writeChar(char value) {
        resource.writeChar(value);
        return this;
    }

    @Override
    public Buffer writeMedium(int value) {
        resource.writeByte((byte) (value >>> 16));
        resource.writeByte((byte) (value >>> 8));
        resource.writeByte((byte) (value >>> 0));

        return this;
    }

    @Override
    public Buffer writeUnsignedMedium(int value) {
        resource.writeByte((byte) (value >>> 16));
        resource.writeByte((byte) (value >>> 8));
        resource.writeByte((byte) (value >>> 0));

        return this;
    }

    @Override
    public Buffer writeInt(int value) {
        resource.writeInt(value);
        return this;
    }

    @Override
    public Buffer writeUnsignedInt(long value) {
        resource.writeInt((int)(value & 0x00000000FFFFFFFFl));
        return this;
    }

    @Override
    public Buffer writeLong(long value) {
        resource.writeLong(value);
        return this;
    }

    @Override
    public Buffer writeFloat(float value) {
        resource.writeFloat(value);
        return this;
    }

    @Override
    public Buffer writeDouble(double value) {
        resource.writeDouble(value);
        return this;
    }

    @Override
    public Buffer writeCharSequence(CharSequence source, Charset charset) {
        resource.writeCharSequence(source, charset);
        return this;
    }

    //----- Primitive indexed get API

    @Override
    public byte getByte(int index) {
        return resource.getByte(index);
    }

    @Override
    public char getChar(int index) {
        return resource.getChar(index);
    }

    @Override
    public int getUnsignedByte(int index) {
        return resource.getUnsignedByte(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return resource.getUnsignedShort(index);
    }

    @Override
    public short getShort(int index) {
        return resource.getShort(index);
    }

    @Override
    public int getMedium(int index) {
        return (getByte(index)) << 16 |
               (getByte(index + 1) & 0xFF) << 8 |
               (getByte(index + 2) & 0xFF) << 0;
    }

    @Override
    public int getUnsignedMedium(int index) {
        return ((getByte(index)) << 16 |
                (getByte(index + 1) & 0xFF) << 8 |
                (getByte(index + 2) & 0xFF) << 0) & 0xFFFFFF;
    }

    @Override
    public int getInt(int index) {
        return resource.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return resource.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        return resource.getLong(index);
    }

    @Override
    public float getFloat(int index) {
        return resource.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return resource.getDouble(index);
    }

    //----- Primitive relative read API

    @Override
    public byte readByte() {
        return resource.readByte();
    }

    @Override
    public int readUnsignedByte() {
        return resource.readUnsignedByte();
    }

    @Override
    public char readChar() {
        return resource.readChar();
    }

    @Override
    public short readShort() {
        return resource.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return readShort() & 0x0000FFFF;
    }

    @Override
    public int readMedium() {
        return (readByte()) << 16 |
               (readByte() & 0xFF) << 8 |
               (readByte() & 0xFF) << 0;
    }

    @Override
    public int readUnsignedMedium() {
        return ((readByte()) << 16 |
                (readByte() & 0xFF) << 8 |
                (readByte() & 0xFF) << 0) & 0xFFFFFF;
    }

    @Override
    public int readInt() {
        return resource.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return readInt() & 0x00000000FFFFFFFFl;
    }

    @Override
    public long readLong() {
        return resource.readLong();
    }

    @Override
    public float readFloat() {
        return resource.readFloat();
    }

    @Override
    public double readDouble() {
        return resource.readDouble();
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return resource.readCharSequence(length, charset);
    }
}
