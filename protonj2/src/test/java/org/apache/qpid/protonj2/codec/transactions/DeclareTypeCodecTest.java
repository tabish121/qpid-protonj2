/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.protonj2.codec.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.buffer.ProtonBufferAllocator;
import org.apache.qpid.protonj2.buffer.ProtonBufferInputStream;
import org.apache.qpid.protonj2.codec.CodecTestSupport;
import org.apache.qpid.protonj2.codec.DecodeException;
import org.apache.qpid.protonj2.codec.EncodingCodes;
import org.apache.qpid.protonj2.codec.StreamTypeDecoder;
import org.apache.qpid.protonj2.codec.TypeDecoder;
import org.apache.qpid.protonj2.codec.decoders.transactions.DeclareTypeDecoder;
import org.apache.qpid.protonj2.codec.encoders.transactions.DeclareTypeEncoder;
import org.apache.qpid.protonj2.types.transactions.Declare;
import org.junit.jupiter.api.Test;

/**
 * Test for handling Declare serialization
 */
public class DeclareTypeCodecTest extends CodecTestSupport {

    @Test
    public void testTypeClassReturnsCorrectType() throws IOException {
        assertEquals(Declare.class, new DeclareTypeDecoder().getTypeClass());
        assertEquals(Declare.class, new DeclareTypeEncoder().getTypeClass());
    }

    @Test
    public void testDescriptors() throws Exception {
        DeclareTypeDecoder decoder = new DeclareTypeDecoder();
        DeclareTypeEncoder encoder = new DeclareTypeEncoder();

        assertEquals(Declare.DESCRIPTOR_CODE, decoder.getDescriptorCode());
        assertEquals(Declare.DESCRIPTOR_CODE, encoder.getDescriptorCode());
        assertEquals(Declare.DESCRIPTOR_SYMBOL, decoder.getDescriptorSymbol());
        assertEquals(Declare.DESCRIPTOR_SYMBOL, encoder.getDescriptorSymbol());
    }

    @Test
    public void testEncodeDecodeType() throws Exception {
        doTestEncodeDecodeType(false);
    }

    @Test
    public void testEncodeDecodeTypeFromStream() throws Exception {
        doTestEncodeDecodeType(true);
    }

    private void doTestEncodeDecodeType(boolean fromStream) throws Exception {
        final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

        Declare input = new Declare();

        encoder.writeObject(buffer, encoderState, input);

        final Declare result;
        if (fromStream) {
            final InputStream stream = new ProtonBufferInputStream(buffer);
            result = (Declare) streamDecoder.readObject(stream, streamDecoderState);
        } else {
            result = (Declare) decoder.readObject(buffer, decoderState);
        }

        assertNull(result.getGlobalId());
    }

    @Test
    public void testSkipValue() throws IOException {
        doTestSkipValue(false);
    }

    @Test
    public void testSkipValueFromStream() throws IOException {
        doTestSkipValue(true);
    }

    private void doTestSkipValue(boolean fromStream) throws IOException {
        final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

        Declare declare = new Declare();

        for (int i = 0; i < 10; ++i) {
            encoder.writeObject(buffer, encoderState, declare);
        }

        encoder.writeObject(buffer, encoderState, declare);

        final InputStream stream;
        if (fromStream) {
            stream = new ProtonBufferInputStream(buffer);
        } else {
            stream = null;
        }

        for (int i = 0; i < 10; ++i) {
            if (fromStream) {
                StreamTypeDecoder<?> typeDecoder = streamDecoder.readNextTypeDecoder(stream, streamDecoderState);
                assertEquals(Declare.class, typeDecoder.getTypeClass());
                typeDecoder.skipValue(stream, streamDecoderState);
            } else {
                TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
                assertEquals(Declare.class, typeDecoder.getTypeClass());
                typeDecoder.skipValue(buffer, decoderState);
            }
        }

        final Object result;
        if (fromStream) {
            result = streamDecoder.readObject(stream, streamDecoderState);
        } else {
            result = decoder.readObject(buffer, decoderState);
        }

        assertNotNull(result);
        assertTrue(result instanceof Declare);

        Declare value = (Declare) result;
        assertNull(value.getGlobalId());
    }

    @Test
    public void testSkipValueWithInvalidMap32Type() throws IOException {
        doTestSkipValueWithInvalidMapType(EncodingCodes.MAP32, false);
    }

    @Test
    public void testSkipValueWithInvalidMap8Type() throws IOException {
        doTestSkipValueWithInvalidMapType(EncodingCodes.MAP8, false);
    }

    @Test
    public void testSkipValueWithInvalidMap32TypeFromStream() throws IOException {
        doTestSkipValueWithInvalidMapType(EncodingCodes.MAP32, true);
    }

    @Test
    public void testSkipValueWithInvalidMap8TypeFromStream() throws IOException {
        doTestSkipValueWithInvalidMapType(EncodingCodes.MAP8, true);
    }

    private void doTestSkipValueWithInvalidMapType(byte mapType, boolean fromStream) throws IOException {
        final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

        buffer.writeByte((byte) 0); // Described Type Indicator
        buffer.writeByte(EncodingCodes.SMALLULONG);
        buffer.writeByte(Declare.DESCRIPTOR_CODE.byteValue());
        if (mapType == EncodingCodes.MAP32) {
            buffer.writeByte(EncodingCodes.MAP32);
            buffer.writeInt((byte) 0);  // Size
            buffer.writeInt((byte) 0);  // Count
        } else {
            buffer.writeByte(EncodingCodes.MAP8);
            buffer.writeByte((byte) 0);  // Size
            buffer.writeByte((byte) 0);  // Count
        }

        if (fromStream) {
            final InputStream stream = new ProtonBufferInputStream(buffer);
            StreamTypeDecoder<?> typeDecoder = streamDecoder.readNextTypeDecoder(stream, streamDecoderState);
            assertEquals(Declare.class, typeDecoder.getTypeClass());

            try {
                typeDecoder.skipValue(stream, streamDecoderState);
                fail("Should not be able to skip type with invalid encoding");
            } catch (DecodeException ex) {}
        } else {
            TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
            assertEquals(Declare.class, typeDecoder.getTypeClass());

            try {
                typeDecoder.skipValue(buffer, decoderState);
                fail("Should not be able to skip type with invalid encoding");
            } catch (DecodeException ex) {}
        }
    }

    @Test
    public void testDecodedWithInvalidMap32Type() throws IOException {
        doTestDecodeWithInvalidMapType(EncodingCodes.MAP32, false);
    }

    @Test
    public void testDecodeWithInvalidMap8Type() throws IOException {
        doTestDecodeWithInvalidMapType(EncodingCodes.MAP8, false);
    }

    @Test
    public void testDecodedWithInvalidMap32TypeFromStream() throws IOException {
        doTestDecodeWithInvalidMapType(EncodingCodes.MAP32, true);
    }

    @Test
    public void testDecodeWithInvalidMap8TypeFromStream() throws IOException {
        doTestDecodeWithInvalidMapType(EncodingCodes.MAP8, true);
    }

    private void doTestDecodeWithInvalidMapType(byte mapType, boolean fromStream) throws IOException {
        final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

        buffer.writeByte((byte) 0); // Described Type Indicator
        buffer.writeByte(EncodingCodes.SMALLULONG);
        buffer.writeByte(Declare.DESCRIPTOR_CODE.byteValue());
        if (mapType == EncodingCodes.MAP32) {
            buffer.writeByte(EncodingCodes.MAP32);
            buffer.writeInt((byte) 0);  // Size
            buffer.writeInt((byte) 0);  // Count
        } else {
            buffer.writeByte(EncodingCodes.MAP8);
            buffer.writeByte((byte) 0);  // Size
            buffer.writeByte((byte) 0);  // Count
        }

        if (fromStream) {
            final InputStream stream = new ProtonBufferInputStream(buffer);
            try {
                streamDecoder.readObject(stream, streamDecoderState);
                fail("Should not decode type with invalid encoding");
            } catch (DecodeException ex) {}
        } else {
            try {
                decoder.readObject(buffer, decoderState);
                fail("Should not decode type with invalid encoding");
            } catch (DecodeException ex) {}
        }
    }

    @Test
    public void testEncodeDecodeArray() throws IOException {
        doTestEncodeDecodeArray(false);
    }

    @Test
    public void testEncodeDecodeArrayFromStream() throws IOException {
        doTestEncodeDecodeArray(true);
    }

    private void doTestEncodeDecodeArray(boolean fromStream) throws IOException {
        final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

        Declare[] array = new Declare[3];

        array[0] = new Declare();
        array[1] = new Declare();
        array[2] = new Declare();

        encoder.writeObject(buffer, encoderState, array);

        final Object result;
        if (fromStream) {
            final InputStream stream = new ProtonBufferInputStream(buffer);
            result = streamDecoder.readObject(stream, streamDecoderState);
        } else {
            result = decoder.readObject(buffer, decoderState);
        }

        assertTrue(result.getClass().isArray());
        assertEquals(Declare.class, result.getClass().getComponentType());

        Declare[] resultArray = (Declare[]) result;

        for (int i = 0; i < resultArray.length; ++i) {
            assertNotNull(resultArray[i]);
            assertTrue(resultArray[i] instanceof Declare);
            assertNull(array[i].getGlobalId());
        }
    }

    @Test
    public void testDecodeWithNotEnoughListEntriesList8() throws IOException {
        doTestDecodeWithNotEnoughListEntriesList32(EncodingCodes.LIST8, false);
    }

    @Test
    public void testDecodeWithNotEnoughListEntriesList32() throws IOException {
        doTestDecodeWithNotEnoughListEntriesList32(EncodingCodes.LIST32, false);
    }

    @Test
    public void testDecodeWithNotEnoughListEntriesList8FromStream() throws IOException {
        doTestDecodeWithNotEnoughListEntriesList32(EncodingCodes.LIST8, true);
    }

    @Test
    public void testDecodeWithNotEnoughListEntriesList32FromStream() throws IOException {
        doTestDecodeWithNotEnoughListEntriesList32(EncodingCodes.LIST32, true);
    }

    private void doTestDecodeWithNotEnoughListEntriesList32(byte listType, boolean fromStream) throws IOException {
        ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

        buffer.writeByte((byte) 0); // Described Type Indicator
        buffer.writeByte(EncodingCodes.SMALLULONG);
        buffer.writeByte(Declare.DESCRIPTOR_CODE.byteValue());
        if (listType == EncodingCodes.LIST32) {
            buffer.writeByte(EncodingCodes.LIST32);
            buffer.writeInt(64);  // Size
            buffer.writeInt(-1);  // Count
        } else if (listType == EncodingCodes.LIST8) {
            buffer.writeByte(EncodingCodes.LIST8);
            buffer.writeByte((byte) 64);  // Size
            buffer.writeByte((byte) 0xFF);  // Count
        } else {
            buffer.writeByte(EncodingCodes.LIST0);
        }

        if (fromStream) {
            InputStream stream = new ProtonBufferInputStream(buffer);
            try {
                streamDecoder.readObject(stream, streamDecoderState);
                fail("Should not decode type with invalid min entries");
            } catch (DecodeException ex) {}
        } else {
            try {
                decoder.readObject(buffer, decoderState);
                fail("Should not decode type with invalid min entries");
            } catch (DecodeException ex) {}
        }
    }

    @Test
    public void testDecodeWithToManyListEntriesList8() throws IOException {
        doTestDecodeWithToManyListEntriesList32(EncodingCodes.LIST8, false);
    }

    @Test
    public void testDecodeWithToManyListEntriesList32() throws IOException {
        doTestDecodeWithToManyListEntriesList32(EncodingCodes.LIST32, false);
    }

    @Test
    public void testDecodeWithToManyListEntriesList8FromStream() throws IOException {
        doTestDecodeWithToManyListEntriesList32(EncodingCodes.LIST8, true);
    }

    @Test
    public void testDecodeWithToManyListEntriesList32FromStream() throws IOException {
        doTestDecodeWithToManyListEntriesList32(EncodingCodes.LIST32, true);
    }

    private void doTestDecodeWithToManyListEntriesList32(byte listType, boolean fromStream) throws IOException {
        ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

        buffer.writeByte((byte) 0); // Described Type Indicator
        buffer.writeByte(EncodingCodes.SMALLULONG);
        buffer.writeByte(Declare.DESCRIPTOR_CODE.byteValue());
        if (listType == EncodingCodes.LIST32) {
            buffer.writeByte(EncodingCodes.LIST32);
            buffer.writeInt(128);  // Size
            buffer.writeInt(127);  // Count
        } else if (listType == EncodingCodes.LIST8) {
            buffer.writeByte(EncodingCodes.LIST8);
            buffer.writeByte((byte) 128);  // Size
            buffer.writeByte((byte) 127);  // Count
        }

        if (fromStream) {
            InputStream stream = new ProtonBufferInputStream(buffer);
            try {
                streamDecoder.readObject(stream, streamDecoderState);
                fail("Should not decode type with invalid min entries");
            } catch (DecodeException ex) {}
        } else {
            try {
                decoder.readObject(buffer, decoderState);
                fail("Should not decode type with invalid min entries");
            } catch (DecodeException ex) {}
        }
    }
}
