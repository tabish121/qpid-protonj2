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
package org.apache.qpid.protonj2.codec.messaging;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.buffer.ProtonBufferAllocator;
import org.apache.qpid.protonj2.buffer.ProtonBufferInputStream;
import org.apache.qpid.protonj2.codec.CodecTestSupport;
import org.apache.qpid.protonj2.codec.DecodeException;
import org.apache.qpid.protonj2.codec.EncodingCodes;
import org.apache.qpid.protonj2.codec.StreamTypeDecoder;
import org.apache.qpid.protonj2.codec.TypeDecoder;
import org.apache.qpid.protonj2.codec.decoders.messaging.SourceTypeDecoder;
import org.apache.qpid.protonj2.codec.encoders.messaging.SourceTypeEncoder;
import org.apache.qpid.protonj2.types.Symbol;
import org.apache.qpid.protonj2.types.UnsignedInteger;
import org.apache.qpid.protonj2.types.messaging.Accepted;
import org.apache.qpid.protonj2.types.messaging.Modified;
import org.apache.qpid.protonj2.types.messaging.Released;
import org.apache.qpid.protonj2.types.messaging.Source;
import org.apache.qpid.protonj2.types.messaging.TerminusDurability;
import org.apache.qpid.protonj2.types.messaging.TerminusExpiryPolicy;
import org.junit.jupiter.api.Test;

/**
 * Test for handling Source serialization
 */
public class SourceTypeCodecTest extends CodecTestSupport {

    @Test
    public void testTypeClassReturnsCorrectType() throws IOException {
        assertEquals(Source.class, new SourceTypeDecoder().getTypeClass());
        assertEquals(Source.class, new SourceTypeEncoder().getTypeClass());
    }

    @Test
    public void testDescriptors() throws IOException {
        assertEquals(Source.DESCRIPTOR_CODE, new SourceTypeDecoder().getDescriptorCode());
        assertEquals(Source.DESCRIPTOR_CODE, new SourceTypeEncoder().getDescriptorCode());
        assertEquals(Source.DESCRIPTOR_SYMBOL, new SourceTypeDecoder().getDescriptorSymbol());
        assertEquals(Source.DESCRIPTOR_SYMBOL, new SourceTypeEncoder().getDescriptorSymbol());
    }

   @Test
   public void testEncodeDecodeSourceType() throws Exception {
       doTestEncodeDecodeSourceType(false);
   }

   @Test
   public void testEncodeDecodeSourceTypeFromStream() throws Exception {
       doTestEncodeDecodeSourceType(true);
   }

   private void doTestEncodeDecodeSourceType(boolean fromStream) throws Exception {
      final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

      Source value = new Source();
      value.setAddress("test");
      value.setDurable(TerminusDurability.UNSETTLED_STATE);
      value.setTimeout(UnsignedInteger.MAX_VALUE);

      encoder.writeObject(buffer, encoderState, value);

      final Source result;
      if (fromStream) {
          final InputStream stream = new ProtonBufferInputStream(buffer);
          result = streamDecoder.readObject(stream, streamDecoderState, Source.class);
      } else {
          result = decoder.readObject(buffer, decoderState, Source.class);
      }

      assertEquals("test", result.getAddress());
      assertEquals(TerminusDurability.UNSETTLED_STATE, result.getDurable());
      assertEquals(UnsignedInteger.MAX_VALUE, result.getTimeout());
   }

   @Test
   public void testFullyPopulatedSource() throws Exception {
       doTestFullyPopulatedSource(false);
   }

   @Test
   public void testFullyPopulatedSourceFromStream() throws Exception {
       doTestFullyPopulatedSource(true);
   }

   private void doTestFullyPopulatedSource(boolean fromStream) throws Exception {
       final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

      Map<Symbol, Object> nodeProperties = new LinkedHashMap<>();
      nodeProperties.put(Symbol.valueOf("property-1"), "value-1");
      nodeProperties.put(Symbol.valueOf("property-2"), "value-2");
      nodeProperties.put(Symbol.valueOf("property-3"), "value-3");

      Map<Symbol, Object> filters = new LinkedHashMap<>();
      nodeProperties.put(Symbol.valueOf("filter-1"), "value-1");
      nodeProperties.put(Symbol.valueOf("filter-2"), "value-2");
      nodeProperties.put(Symbol.valueOf("filter-3"), "value-3");

      Source value = new Source();
      value.setAddress("test");
      value.setDurable(TerminusDurability.UNSETTLED_STATE);
      value.setExpiryPolicy(TerminusExpiryPolicy.SESSION_END);
      value.setTimeout(UnsignedInteger.valueOf(255));
      value.setDynamic(true);
      value.setDynamicNodeProperties(nodeProperties);
      value.setDistributionMode(Symbol.valueOf("mode"));
      value.setFilter(filters);
      value.setDefaultOutcome(Released.getInstance());
      value.setOutcomes(new Symbol[] {Symbol.valueOf("ACCEPTED"), Symbol.valueOf("REJECTED")});
      value.setCapabilities(new Symbol[] {Symbol.valueOf("RELEASED"), Symbol.valueOf("MODIFIED")});

      encoder.writeObject(buffer, encoderState, value);

      final Source result;
      if (fromStream) {
          final InputStream stream = new ProtonBufferInputStream(buffer);
          result = streamDecoder.readObject(stream, streamDecoderState, Source.class);
      } else {
          result = decoder.readObject(buffer, decoderState, Source.class);
      }

      assertEquals("test", result.getAddress());
      assertEquals(TerminusDurability.UNSETTLED_STATE, result.getDurable());
      assertEquals(TerminusExpiryPolicy.SESSION_END, result.getExpiryPolicy());
      assertEquals(UnsignedInteger.valueOf(255), result.getTimeout());
      assertEquals(true, result.isDynamic());
      assertEquals(nodeProperties, result.getDynamicNodeProperties());
      assertEquals(Symbol.valueOf("mode"), result.getDistributionMode());
      assertEquals(filters, result.getFilter());
      assertEquals(Released.getInstance(), result.getDefaultOutcome());

      assertArrayEquals(new Symbol[] {Symbol.valueOf("ACCEPTED"), Symbol.valueOf("REJECTED")}, result.getOutcomes());
      assertArrayEquals(new Symbol[] {Symbol.valueOf("RELEASED"), Symbol.valueOf("MODIFIED")}, result.getCapabilities());
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

       Source source = new Source();
       source.setAddress("address");
       source.setCapabilities(Symbol.valueOf("QUEUE"));

       for (int i = 0; i < 10; ++i) {
           encoder.writeObject(buffer, encoderState, source);
       }

       encoder.writeObject(buffer, encoderState, new Modified());

       final InputStream stream;

       if (fromStream) {
           stream = new ProtonBufferInputStream(buffer);
       } else {
           stream = null;
       }

       for (int i = 0; i < 10; ++i) {
           if (fromStream) {
               StreamTypeDecoder<?> typeDecoder = streamDecoder.readNextTypeDecoder(stream, streamDecoderState);
               assertEquals(Source.class, typeDecoder.getTypeClass());
               typeDecoder.skipValue(stream, streamDecoderState);
           } else {
               TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
               assertEquals(Source.class, typeDecoder.getTypeClass());
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
       assertTrue(result instanceof Modified);
       Modified modified = (Modified) result;
       assertFalse(modified.isUndeliverableHere());
       assertFalse(modified.isDeliveryFailed());
   }

   @Test
   public void testDecodeWithInvalidMap32Type() throws IOException {
       doTestDecodeWithInvalidMapType(EncodingCodes.MAP32, false);
   }

   @Test
   public void testDecodeWithInvalidMap8Type() throws IOException {
       doTestDecodeWithInvalidMapType(EncodingCodes.MAP8, false);
   }

   private void doTestDecodeWithInvalidMapType(byte mapType, boolean fromStream) throws IOException {
       final ProtonBuffer buffer = ProtonBufferAllocator.defaultAllocator().allocate();

       buffer.writeByte((byte) 0); // Described Type Indicator
       buffer.writeByte(EncodingCodes.SMALLULONG);
       buffer.writeByte(Source.DESCRIPTOR_CODE.byteValue());
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
       buffer.writeByte(Source.DESCRIPTOR_CODE.byteValue());
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
           final StreamTypeDecoder<?> typeDecoder = streamDecoder.readNextTypeDecoder(stream, streamDecoderState);
           assertEquals(Source.class, typeDecoder.getTypeClass());

           try {
               typeDecoder.skipValue(stream, streamDecoderState);
               fail("Should not be able to skip type with invalid encoding");
           } catch (DecodeException ex) {}
       } else {
           TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
           assertEquals(Source.class, typeDecoder.getTypeClass());

           try {
               typeDecoder.skipValue(buffer, decoderState);
               fail("Should not be able to skip type with invalid encoding");
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

       Source[] array = new Source[3];

       array[0] = new Source();
       array[1] = new Source();
       array[2] = new Source();

       array[0].setAddress("test-1").setDynamic(true).setDefaultOutcome(Accepted.getInstance());
       array[1].setAddress("test-2").setDynamic(false).setDefaultOutcome(Released.getInstance());
       array[2].setAddress("test-3").setDynamic(true).setDefaultOutcome(Accepted.getInstance());

       encoder.writeObject(buffer, encoderState, array);

       final Object result;
       if (fromStream) {
           final InputStream stream = new ProtonBufferInputStream(buffer);
           result = streamDecoder.readObject(stream, streamDecoderState);
       } else {
           result = decoder.readObject(buffer, decoderState);
       }

       assertTrue(result.getClass().isArray());
       assertEquals(Source.class, result.getClass().getComponentType());

       Source[] resultArray = (Source[]) result;

       for (int i = 0; i < resultArray.length; ++i) {
           assertNotNull(resultArray[i]);
           assertTrue(resultArray[i] instanceof Source);
           assertEquals(array[i].getAddress(), resultArray[i].getAddress());
           assertEquals(array[i].isDynamic(), resultArray[i].isDynamic());
           assertEquals(array[i].getDefaultOutcome(), resultArray[i].getDefaultOutcome());
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
       buffer.writeByte(Source.DESCRIPTOR_CODE.byteValue());
       if (listType == EncodingCodes.LIST32) {
           buffer.writeByte(EncodingCodes.LIST32);
           buffer.writeInt(128);  // Size
           buffer.writeInt(-1);  // Count, reads as negative as encoder treats these as signed ints.
       } else if (listType == EncodingCodes.LIST8) {
           buffer.writeByte(EncodingCodes.LIST8);
           buffer.writeByte((byte) 128);  // Size
           buffer.writeByte((byte) 0xFF);  // Count
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
       buffer.writeByte(Source.DESCRIPTOR_CODE.byteValue());
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
