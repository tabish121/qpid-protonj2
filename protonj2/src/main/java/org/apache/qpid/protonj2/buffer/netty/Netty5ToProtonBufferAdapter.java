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
import org.apache.qpid.protonj2.buffer.ProtonBufferComponent;
import org.apache.qpid.protonj2.buffer.ProtonBufferComponentAccessor;
import org.apache.qpid.protonj2.buffer.ProtonBufferUtils;
import org.apache.qpid.protonj2.resource.SharedResource;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferAllocator;
import io.netty5.buffer.BufferClosedException;
import io.netty5.buffer.BufferReadOnlyException;

/**
 * Wrapper class for Netty 5 Buffer instances which provides a generic way
 * for proton to interact with Netty 4 buffers.
 */
public final class Netty5ToProtonBufferAdapter extends SharedResource<ProtonBuffer>
    implements ProtonBuffer, ProtonBufferComponentAccessor, ProtonBufferComponent {

    private final Netty5ProtonBufferAllocator allocator;

    private static final Buffer CLOSED_BUFFER;

    static {
        CLOSED_BUFFER = BufferAllocator.onHeapUnpooled().allocate(0);
        CLOSED_BUFFER.close();
    }

    private Buffer resource;

    // TODO: Buffer compnent should be actual wrapper around netty component

    /**
     * Creates a new {@link Netty5ToProtonBufferAdapter} which wraps the given Netty {@link Buffer}.
     *
     * @param allocator
     *      The allocator that created this buffer wrapper
     * @param resource
     * 		The {@link Buffer} resource to wrap.
     */
    public Netty5ToProtonBufferAdapter(Netty5ProtonBufferAllocator allocator, Buffer resource) {
        this.resource = resource;
        this.allocator = allocator;
    }

    public Netty5ProtonBufferAllocator allocator() {
        return allocator;
    }

    @Override
    public Buffer unwrap() {
        ProtonBufferUtils.checkIsClosed(this);
        return resource;
    }

    @Override
    public ProtonBuffer convertToReadOnly() {
        resource.makeReadOnly();
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return resource.readOnly();
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public int componentCount() {
        return resource.countComponents();
    }

    @Override
    public int readableComponentCount() {
        return resource.countReadableComponents();
    }

    @Override
    public int writableComponentCount() {
        return resource.countWritableComponents();
    }

    @Override
    public boolean isDirect() {
        return resource.isDirect();
    }

    @Override
    public int implicitGrowthLimit() {
        return resource.implicitCapacityLimit();
    }

    @Override
    public ProtonBuffer implicitGrowthLimit(int limit) {
        resource.implicitCapacityLimit(limit);
        return this;
    }

    @Override
    public ProtonBuffer fill(byte value) {
        try {
            resource.fill(value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public int capacity() {
        return resource.capacity();
    }

    @Override
    public int getReadOffset() {
        return resource.readerOffset();
    }

    @Override
    public ProtonBuffer setReadOffset(int value) {
        resource.readerOffset(value);
        return this;
    }

    @Override
    public int getWriteOffset() {
        return resource.writerOffset();
    }

    @Override
    public ProtonBuffer setWriteOffset(int value) {
        try {
            resource.writerOffset(value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer compact() {
        try {
            resource.compact();
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public void copyInto(int offset, byte[] destination, int destOffset, int length) {
        try {
            resource.copyInto(offset, destination, destOffset, length);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public void copyInto(int offset, ByteBuffer destination, int destOffset, int length) {
        try {
            resource.copyInto(offset, destination, destOffset, length);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public void copyInto(int offset, ProtonBuffer destination, int destOffset, int length) {
        try {
            if (destination.unwrap() instanceof Buffer) {
                resource.copyInto(offset, (Buffer) destination.unwrap(), destOffset, length);
            } else {
                ProtonBufferUtils.checkIsReadOnly(destination);
                ProtonBufferUtils.checkIsClosed(this);

                // Try to reduce bounds-checking by using larger primitives when possible.
                for (; length >= Long.BYTES; length -= Long.BYTES, offset += Long.BYTES, destOffset += Long.BYTES) {
                    destination.setLong(destOffset, getLong(offset));
                }
                for (; length >= Integer.BYTES; length -= Integer.BYTES, offset += Integer.BYTES, destOffset += Integer.BYTES) {
                    destination.setInt(destOffset, getInt(offset));
                }
                for (; length > 0; length--, offset++, destOffset++) {
                    destination.setByte(destOffset, getByte(offset));
                }
            }
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public ProtonBuffer writeBytes(byte[] source, int offset, int length) {
        try {
            resource.writeBytes(source, offset, length);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }

        return this;
    }

    @Override
    public ProtonBuffer writeBytes(ByteBuffer source) {
        try {
            resource.writeBytes(source);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }

        return this;
    }

    @Override
    public ProtonBuffer ensureWritable(int size, int minimumGrowth, boolean allowCompaction) throws IndexOutOfBoundsException, IllegalArgumentException {
        try {
            resource.ensureWritable(size, minimumGrowth, allowCompaction);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }

        return this;
    }

    @Override
    public ProtonBuffer copy(int index, int length, boolean readOnly) throws IllegalArgumentException {
        try {
            return allocator.wrap(resource.copy(index, length, readOnly));
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public ProtonBuffer split(int splitOffset) {
        try {
            return allocator.wrap(resource.split(splitOffset));
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    //----- JDK Overrides

    @Override
    public String toString(Charset charset) {
        return resource.toString(charset);
    }

    @Override
    public String toString() {
        return "Netty5ToProtonBufferAdapter" +
               "{ read:" + (resource != null ? resource.readerOffset() : null) +
               ", write: " + (resource != null ? resource.writerOffset() : 0) +
               ", capacity: " + (resource != null ? resource.capacity() : 0) + "}";
    }

    @Override
    public int compareTo(ProtonBuffer buffer) {
        return ProtonBufferUtils.compare(this, buffer);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ProtonBuffer) {
            return ProtonBufferUtils.equals(this, (ProtonBuffer) other);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return ProtonBufferUtils.hashCode(this);
    }

    //----- Primitive Get API

    @Override
    public byte getByte(int index) {
        try {
            return resource.getByte(index);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public char getChar(int index) {
        try {
            return resource.getChar(index);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public short getShort(int index) {
        try {
            return resource.getShort(index);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public int getInt(int index) {
        try {
            return resource.getInt(index);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public long getLong(int index) {
        try {
            return resource.getLong(index);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    //----- Primitive Set API

    @Override
    public ProtonBuffer setByte(int index, byte value) {
        try {
            resource.setByte(index, value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer setChar(int index, char value) {
        try {
            resource.setChar(index, value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer setShort(int index, short value) {
        try {
            resource.setShort(index, value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer setInt(int index, int value) {
        try {
            resource.setInt(index, value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer setLong(int index, long value) {
        try {
            resource.setLong(index, value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    //----- Primitive Read API

    @Override
    public byte readByte() {
        try {
            return resource.readByte();
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public char readChar() {
        try {
            return resource.readChar();
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public short readShort() {
        try {
            return resource.readShort();
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public int readInt() {
        try {
            return resource.readInt();
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public long readLong() {
        try {
            return resource.readLong();
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    //----- Primitive Write API

    @Override
    public ProtonBuffer writeByte(byte value) {
        try {
            resource.writeByte(value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer writeChar(char value) {
        try {
            resource.writeChar(value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer writeShort(short value) {
        try {
            resource.writeShort(value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer writeInt(int value) {
        try {
            resource.writeInt(value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    @Override
    public ProtonBuffer writeLong(long value) {
        try {
            resource.writeLong(value);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
        return this;
    }

    //----- IO Handlers

    @Override
    public int transferTo(WritableByteChannel channel, int length) throws IOException {
        try {
            return resource.transferTo(channel, length);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public int transferFrom(ReadableByteChannel channel, int length) throws IOException {
        try {
            return resource.transferFrom(channel, length);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    @Override
    public int transferFrom(FileChannel channel, long position, int length) throws IOException {
        try {
            return resource.transferFrom(channel, position, length);
        } catch (RuntimeException e) {
            throw translateToProtonException(e);
        }
    }

    //----- Buff component access

    @Override
    public ProtonBufferComponentAccessor componentAccessor() {
        if (isClosed()) {
            throw ProtonBufferUtils.genericBufferIsClosed(this);
        }

        return (ProtonBufferComponentAccessor) acquire();
    }

    @Override
    public ProtonBufferComponent first() {
        return this;
    }

    @Override
    public ProtonBufferComponent next() {
        return null; // No next component even if underlying is a Netty composite.
    }

    //----- ProtonBufferComponent API Implementation

    @Override
    public boolean hasReadbleArray() {
        return false;
    }

    @Override
    public Netty5ToProtonBufferAdapter advanceReadOffset(int amount) {
        return (Netty5ToProtonBufferAdapter) ProtonBuffer.super.advanceReadOffset(amount);
    }

    @Override
    public byte[] getReadableArray() {
        return null;
    }

    @Override
    public int getReadableArrayOffset() {
        return 0;
    }

    @Override
    public int getReadableArrayLength() {
        return 0;
    }

    @Override
    public int getReadableBytes() {
        return resource != null ? resource.readableBytes() : 0;
    }

    @Override
    public ByteBuffer getReadableBuffer() {
        return null;
    }

    @Override
    public Netty5ToProtonBufferAdapter advanceWriteOffset(int amount) {
        return (Netty5ToProtonBufferAdapter) ProtonBuffer.super.advanceWriteOffset(amount);
    }

    @Override
    public boolean hasWritableArray() {
        return false;
    }

    @Override
    public byte[] getWritableArray() {
        return null;
    }

    @Override
    public int getWritableArrayOffset() {
        return 0;
    }

    @Override
    public int getWritableArrayLength() {
        return 0;
    }

    @Override
    public int getWritableBytes() {
        return resource.writableBytes();
    }

    @Override
    public ByteBuffer getWritableBuffer() {
        return ByteBuffer.allocate(0);
    }

    //----- Shared resource API

    @Override
    protected void releaseResourceOwnership() {
        if (resource != null && resource.isAccessible()) {
            resource.close();
            resource = CLOSED_BUFFER;
        }
    }

    @Override
    protected ProtonBuffer transferTheResource() {
        final Buffer transferred = resource;
        resource = CLOSED_BUFFER;

        return new Netty5ToProtonBufferAdapter(allocator, transferred);
    }

    @Override
    protected RuntimeException resourceIsClosedException() {
        return ProtonBufferUtils.genericBufferIsClosed(this);
    }

    //----- Support API for the buffer wrapper

    private RuntimeException translateToProtonException(RuntimeException e) {
        RuntimeException result = e;

        if (e instanceof BufferReadOnlyException) {
            result = ProtonBufferUtils.genericBufferIsReadOnly(this);
            result.addSuppressed(e);
        } else if (e instanceof BufferClosedException) {
            result = ProtonBufferUtils.genericBufferIsClosed(this);
            result.addSuppressed(e);
        }

        return result;
    }
}
