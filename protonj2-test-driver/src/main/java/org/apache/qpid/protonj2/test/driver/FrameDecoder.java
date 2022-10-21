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
package org.apache.qpid.protonj2.test.driver;

import org.apache.qpid.protonj2.test.driver.codec.Codec;
import org.apache.qpid.protonj2.test.driver.codec.security.SaslDescribedType;
import org.apache.qpid.protonj2.test.driver.codec.transport.AMQPHeader;
import org.apache.qpid.protonj2.test.driver.codec.transport.HeartBeat;
import org.apache.qpid.protonj2.test.driver.codec.transport.PerformativeDescribedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferAllocator;

class FrameDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(AMQPTestDriver.class);

    public static final byte AMQP_FRAME_TYPE = (byte) 0;
    public static final byte SASL_FRAME_TYPE = (byte) 1;

    public static final int FRAME_SIZE_BYTES = 4;

    private final AMQPTestDriver driver;
    private final Codec codec = Codec.Factory.create();
    private FrameParserStage stage = new HeaderParsingStage();

    // Parser stages used during the parsing process
    private final FrameSizeParsingStage frameSizeParser = new FrameSizeParsingStage();
    private final FrameBufferingStage frameBufferingStage = new FrameBufferingStage();
    private final FrameParserStage frameBodyParsingStage = new FrameBodyParsingStage();

    public FrameDecoder(AMQPTestDriver driver) {
        this.driver = driver;
    }

    public void ingest(Buffer buffer) throws AssertionError {
        try {
            // Parses in-incoming data and emit one complete frame before returning, caller should
            // ensure that the input buffer is drained into the engine or stop if the engine
            // has changed to a non-writable state.
            stage.parse(buffer);
        } catch (AssertionError ex) {
            transitionToErrorStage(ex);
            throw ex;
        } catch (Throwable throwable) {
            AssertionError error = new AssertionError("Frame decode failed.", throwable);
            transitionToErrorStage(error);
            throw error;
        }
    }

    /**
     * Resets the parser back to the expect a header state.
     */
    public void resetToExpectingHeader() {
        this.stage = new HeaderParsingStage();
    }

    //---- Methods to transition between stages

    private FrameParserStage transitionToFrameSizeParsingStage() {
        return stage = frameSizeParser.reset(0);
    }

    private FrameParserStage transitionToFrameBufferingStage(int frameSize) {
        return stage = frameBufferingStage.reset(frameSize);
    }

    private FrameParserStage initializeFrameBodyParsingStage(int frameSize) {
        return stage = frameBodyParsingStage.reset(frameSize);
    }

    private ParsingErrorStage transitionToErrorStage(AssertionError error) {
        if (!(stage instanceof ParsingErrorStage)) {
            stage = new ParsingErrorStage(error);
        }

        return (ParsingErrorStage) stage;
    }

    //----- Frame Parsing Stage definition

    private interface FrameParserStage {

        /**
         * Parse the incoming data and provide events to the parent Transport
         * based on the contents of that data.
         *
         * @param input
         *      The ByteBuf containing new data to be parsed.
         *
         * @throws AssertionError if an error occurs while parsing incoming data.
         */
        void parse(Buffer input) throws AssertionError;

        /**
         * Reset the stage to its defaults for a new cycle of parsing.
         *
         * @param frameSize
         *      The frameSize to use for this part of the parsing operation
         *
         * @return a reference to this parsing stage for chaining.
         */
        FrameParserStage reset(int frameSize);

    }

    //---- Built in FrameParserStages

    private class HeaderParsingStage implements FrameParserStage {

        private final byte[] headerBytes = new byte[AMQPHeader.HEADER_SIZE_BYTES];

        private int headerByte;

        @Override
        public void parse(Buffer incoming) throws AssertionError {
            while (incoming.readableBytes() > 0 && headerByte < AMQPHeader.HEADER_SIZE_BYTES) {
                headerBytes[headerByte++] = incoming.readByte();
            }

            if (headerByte == AMQPHeader.HEADER_SIZE_BYTES) {
                // Construct a new Header from the read bytes which will validate the contents
                AMQPHeader header = new AMQPHeader(headerBytes);

                // Transition to parsing the frames if any pipelined into this buffer.
                transitionToFrameSizeParsingStage();

                if (header.isSaslHeader()) {
                    driver.handleHeader(AMQPHeader.getSASLHeader());
                } else {
                    driver.handleHeader(AMQPHeader.getAMQPHeader());
                }
            }
        }

        @Override
        public HeaderParsingStage reset(int frameSize) {
            headerByte = 0;
            return this;
        }
    }

    private class FrameSizeParsingStage implements FrameParserStage {

        private int frameSize;
        private int multiplier = FRAME_SIZE_BYTES;

        @Override
        public void parse(Buffer input) throws AssertionError {
            while (input.readableBytes() > 0) {
                frameSize |= (input.readByte() & 0xFF) << (--multiplier * Byte.SIZE);
                if (multiplier == 0) {
                    break;
                }
            }

            if (multiplier == 0) {
                validateFrameSize();

                // Normalize the frame size to the reminder portion
                int length = frameSize - FRAME_SIZE_BYTES;

                if (input.readableBytes() < length) {
                    transitionToFrameBufferingStage(length);
                } else {
                    initializeFrameBodyParsingStage(length);
                }

                stage.parse(input);
            }
        }

        private void validateFrameSize() throws AssertionError {
            if (frameSize < 8) {
               throw new AssertionError(String.format(
                    "specified frame size %d smaller than minimum frame header size 8", frameSize));
            }

            if (frameSize > driver.getInboundMaxFrameSize()) {
                throw new AssertionError(String.format(
                    "specified frame size %d larger than maximum frame size %d", frameSize, driver.getInboundMaxFrameSize()));
            }
        }

        @Override
        public FrameSizeParsingStage reset(int frameSize) {
            multiplier = FRAME_SIZE_BYTES;
            this.frameSize = frameSize;
            return this;
        }
    }

    private class FrameBufferingStage implements FrameParserStage {

        private Buffer buffer;

        @Override
        public void parse(Buffer input) throws AssertionError {
            if (input.readableBytes() < buffer.writableBytes()) {
                buffer.writeBytes(input);
            } else {
                final int remaining = buffer.writableBytes();

                input.copyInto(input.readerOffset(), buffer, buffer.writerOffset(), remaining);
                input.skipReadableBytes(remaining);
                buffer.skipWritableBytes(remaining);

                // Now we can consume the buffer frame body.
                initializeFrameBodyParsingStage(buffer.readableBytes());
                try (Buffer frame = buffer.makeReadOnly()){
                    stage.parse(frame);
                } finally {
                    buffer = null;
                }
            }
        }

        @Override
        public FrameBufferingStage reset(int frameSize) {
            buffer = BufferAllocator.onHeapUnpooled().allocate(frameSize).implicitCapacityLimit(frameSize);
            return this;
        }
    }

    private class FrameBodyParsingStage implements FrameParserStage {

        private int frameSize;

        @Override
        public void parse(Buffer input) throws AssertionError {
            int dataOffset = (input.readByte() << 2) & 0x3FF;
            int frameSize = this.frameSize + FRAME_SIZE_BYTES;

            validateDataOffset(dataOffset, frameSize);

            int type = input.readByte() & 0xFF;
            short channel = input.readShort();

            // note that this skips over the extended header if it's present
            if (dataOffset != 8) {
                input.readerOffset(input.readerOffset() + dataOffset - 8);
            }

            final int frameBodySize = frameSize - dataOffset;

            Buffer payload = null;
            Object val = null;

            if (frameBodySize > 0) {
                int frameBodyStartIndex = input.readerOffset();

                try {
                    codec.decode(input);
                } catch (Exception e) {
                    throw new AssertionError("Decoder failed reading remote input:", e);
                }

                Codec.DataType dataType = codec.type();
                if (dataType != Codec.DataType.DESCRIBED) {
                    throw new IllegalArgumentException(
                        "Frame body type expected to be " + Codec.DataType.DESCRIBED + " but was: " + dataType);
                }

                try {
                    val = codec.getDescribedType();
                } finally {
                    codec.clear();
                }

                // Slice to the known Frame body size and use that as the buffer for any payload once
                // the actual Performative has been decoded.  The implies that the data comprising the
                // performative will be held as long as the payload buffer is kept.
                if (input.readableBytes() > 0) {
                    // Check that the remaining bytes aren't part of another frame.
                    int payloadSize = frameBodySize - (input.readerOffset() - frameBodyStartIndex);
                    if (payloadSize > 0) {
                        payload = input.copy(input.readerOffset(), payloadSize, true);
                        input.skipReadableBytes(payloadSize);
                    }
                }
            } else {
                LOG.trace("{} Read: CH[{}] : {} [{}]", driver.getName(), channel, HeartBeat.INSTANCE, payload);
                transitionToFrameSizeParsingStage();
                driver.handleHeartbeat(frameSize, channel);
                return;
            }

            if (type == AMQP_FRAME_TYPE) {
                PerformativeDescribedType performative = (PerformativeDescribedType) val;
                LOG.trace("{} Read: CH[{}] : {} [{}]", driver.getName(), channel, performative, payload);
                transitionToFrameSizeParsingStage();
                driver.handlePerformative(frameSize, performative, channel, payload);
            } else if (type == SASL_FRAME_TYPE) {
                SaslDescribedType performative = (SaslDescribedType) val;
                LOG.trace("{} Read: {} [{}]", driver.getName(), performative, payload);
                transitionToFrameSizeParsingStage();
                driver.handleSaslPerformative(frameSize, performative, channel, payload);
            } else {
                throw new AssertionError(String.format("unknown frame type: %d", type));
            }
        }

        @Override
        public FrameBodyParsingStage reset(int frameSize) {
            this.frameSize = frameSize;
            return this;
        }

        private void validateDataOffset(int dataOffset, int frameSize) {
            if (dataOffset < 8) {
                throw new AssertionError(String.format(
                    "specified frame data offset %d smaller than minimum frame header size %d", dataOffset, 8));
            }

            if (dataOffset > frameSize) {
                throw new AssertionError(String.format(
                    "specified frame data offset %d larger than the frame size %d", dataOffset, frameSize));
            }
        }
    }

    /*
     * If parsing fails the parser enters the failed state and remains there always throwing the given exception
     * if additional parsing is requested.
     */
    private static class ParsingErrorStage implements FrameParserStage {

        private final AssertionError parsingError;

        public ParsingErrorStage(AssertionError parsingError) {
            this.parsingError = parsingError;
        }

        @Override
        public void parse(Buffer input) throws AssertionError {
            throw parsingError;
        }

        @Override
        public ParsingErrorStage reset(int frameSize) {
            return this;
        }
    }
}
