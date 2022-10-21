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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.buffer.ProtonBufferUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.ByteProcessor;

/**
 * Adapts a {@link ProtonBuffer} instance to a Netty 4 {@link ByteBuf}
 */
public class ProtonBufferToNetty4Adapter extends ByteBuf {

    private ProtonBuffer resource;

    public ProtonBufferToNetty4Adapter(ProtonBuffer resource) {
        this.resource = resource;
    }

    @Override
    public ByteBuf unwrap() {
        return this;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public boolean isDirect() {
        return resource.isDirect();
    }

    @Override
    public ByteBuf clear() {
        resource.setReadOffset(0);
        resource.setWriteOffset(0);

        return this;
    }

    @Override
    public int capacity() {
        return resource.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int maxCapacity() {
        return Integer.MAX_VALUE - 8;
    }

    @Override
    public ByteBufAllocator alloc() {
        if (resource instanceof Netty4ToProtonBufferAdapter) {
            return ((Netty4ToProtonBufferAdapter) resource).allocator().allocator();
        } else {
            return UnpooledByteBufAllocator.DEFAULT;
        }
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        // TODO
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return resource.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        resource.convertToReadOnly();
        return this;
    }

    @Override
    public int readableBytes() {
        return resource.getReadableBytes();
    }

    @Override
    public int writableBytes() {
        return resource.getWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return resource.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return resource.getReadableBytes() >= size;
    }

    @Override
    public boolean isWritable() {
        return resource.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return resource.getWritableBytes() >= size;
    }

    @Override
    public int readerIndex() {
        return resource.getReadOffset();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        resource.setReadOffset(readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return resource.getWriteOffset();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        resource.setWriteOffset(writerIndex);
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        // Ensure state allows for values outside range of current offsets
        resource.setWriteOffset(resource.capacity());

        resource.setReadOffset(readerIndex);
        resource.setWriteOffset(writerIndex);

        return this;
    }

    @Override
    public int maxWritableBytes() {
        return maxCapacity() - writerIndex();
    }

    @Override
    public ByteBuf markReaderIndex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf markWriterIndex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf discardReadBytes() {
        resource.compact();
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        resource.compact();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        ensureWritable(minWritableBytes, false);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        final int capacity = resource.capacity();

        resource.ensureWritable(minWritableBytes, minWritableBytes, force);

        if (resource.capacity() == capacity) {
            return 0;
        } else {
            return 2; // TODO
        }
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return ProtonBufferUtils.copyToCharSequence(resource, index, length, charset);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        final int writeOffset = resource.getWriteOffset();
        final byte[] bytes = sequence.toString().getBytes(charset);

        try {
            resource.setWriteOffset(index);
            resource.writeBytes(bytes);
        } finally {
            resource.setWriteOffset(writeOffset);
        }

        return bytes.length;
    }

    @Override
    public ByteBuf readBytes(int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readSlice(int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return resource.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ByteBuf skipBytes(int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ByteBuf writeZero(int length) {
        resource.fill((byte) 0);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        final int oldWriteOffset = resource.getWriteOffset();
        return resource.writeCharSequence(sequence, charset).getWriteOffset() - oldWriteOffset;
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int bytesBefore(byte value) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int bytesBefore(int length, byte value) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ByteBuf copy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf slice() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf retainedSlice() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf slice(int index, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf duplicate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf retainedDuplicate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int nioBufferCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ByteBuffer nioBuffer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString(Charset charset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        if (index == 0 && length == capacity()) {
            resource.fill((byte) 0);
        } else {
            for (int i = 0; i < length; ++i) {
                setByte(index + i, (byte) 0);
            }
        }

        return this;
    }

    //----- Primitive indexed write API

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        resource.setBoolean(index, value);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        resource.setByte(index, (byte)(value & 0xFF));
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        resource.setShort(index, (short)(value & 0x00FF));
        return this;
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        resource.setInt(index, value);
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        resource.setLong(index, value);
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        resource.setChar(index, (char)(value & 0x00FF));
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        resource.setFloat(index, value);
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        resource.setDouble(index, value);
        return this;
    }

    //----- Primitive indexed read API

    @Override
    public boolean getBoolean(int index) {
        return resource.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return resource.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return (short) resource.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return resource.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getUnsignedShort(int index) {
        return resource.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMedium(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMediumLE(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getUnsignedMedium(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInt(int index) {
        return resource.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getUnsignedInt(int index) {
        return resource.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getLong(int index) {
        return resource.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public char getChar(int index) {
        return resource.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return resource.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return resource.getDouble(index);
    }

    //----- Primitive relative write API

    @Override
    public ByteBuf writeBoolean(boolean value) {
        resource.writeBoolean(value);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        resource.writeByte((byte) value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        resource.writeShort((short) value);
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeInt(int value) {
        resource.writeInt(value);
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeLong(long value) {
        resource.writeLong(value);
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf writeChar(int value) {
        resource.writeChar((char) value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        resource.writeFloat(value);
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        resource.writeDouble(value);
        return this;
    }

    //----- Primitive relative read API

    @Override
    public short readShortLE() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readUnsignedShortLE() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readMediumLE() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readUnsignedMediumLE() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readIntLE() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long readUnsignedInt() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long readUnsignedIntLE() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long readLongLE() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean readBoolean() {
        return resource.readBoolean();
    }

    @Override
    public byte readByte() {
        return resource.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return (short) resource.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return resource.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return resource.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readUnsignedMedium() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readInt() {
        return resource.readInt();
    }

    @Override
    public long readLong() {
        return resource.readLong();
    }

    @Override
    public char readChar() {
        return resource.readChar();
    }

    @Override
    public float readFloat() {
        return resource.readFloat();
    }

    @Override
    public double readDouble() {
        return resource.readDouble();
    }

    //----- Buffer backing memory access API

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        return null;
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        return 0;
    }

    //----- JDK method overrides

    @Override
    public int hashCode() {
        return ProtonBufferUtils.hashCode(resource);
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String toString() {
        return "ProtonBufferToNetty4Adapter";
    }

    //----- Netty Buffer reference counting API

    @Override
    public ByteBuf retain(int increment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBuf retain() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int refCnt() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean release() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean release(int decrement) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ByteBuf touch() {
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        return this;
    }
}
