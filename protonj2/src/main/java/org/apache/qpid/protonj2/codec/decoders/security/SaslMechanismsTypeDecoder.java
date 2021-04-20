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
package org.apache.qpid.protonj2.codec.decoders.security;

import java.io.InputStream;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.codec.DecodeException;
import org.apache.qpid.protonj2.codec.DecoderState;
import org.apache.qpid.protonj2.codec.StreamDecoderState;
import org.apache.qpid.protonj2.codec.StreamTypeDecoder;
import org.apache.qpid.protonj2.codec.TypeDecoder;
import org.apache.qpid.protonj2.codec.decoders.AbstractDescribedTypeDecoder;
import org.apache.qpid.protonj2.codec.decoders.primitives.ListTypeDecoder;
import org.apache.qpid.protonj2.types.Symbol;
import org.apache.qpid.protonj2.types.UnsignedLong;
import org.apache.qpid.protonj2.types.security.SaslMechanisms;

/**
 * Decoder of AMQP SaslChallenge type values from a byte stream.
 */
public final class SaslMechanismsTypeDecoder extends AbstractDescribedTypeDecoder<SaslMechanisms> {

    private static final int REQUIRED_SASL_MECHANISMS_LIST_ENTRIES = 1;

    @Override
    public UnsignedLong getDescriptorCode() {
        return SaslMechanisms.DESCRIPTOR_CODE;
    }

    @Override
    public Symbol getDescriptorSymbol() {
        return SaslMechanisms.DESCRIPTOR_SYMBOL;
    }

    @Override
    public Class<SaslMechanisms> getTypeClass() {
        return SaslMechanisms.class;
    }

    @Override
    public SaslMechanisms readValue(ProtonBuffer buffer, DecoderState state) throws DecodeException {
        final TypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(buffer, state);

        return readProperties(buffer, state, checkIsExpectedTypeAndCast(ListTypeDecoder.class, decoder));
    }

    @Override
    public SaslMechanisms[] readArrayElements(ProtonBuffer buffer, DecoderState state, int count) throws DecodeException {
        final TypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(buffer, state);

        final SaslMechanisms[] result = new SaslMechanisms[count];
        for (int i = 0; i < count; ++i) {
            result[i] = readProperties(buffer, state, checkIsExpectedTypeAndCast(ListTypeDecoder.class, decoder));
        }

        return result;
    }

    @Override
    public void skipValue(ProtonBuffer buffer, DecoderState state) throws DecodeException {
        final TypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(buffer, state);

        checkIsExpectedType(ListTypeDecoder.class, decoder);

        decoder.skipValue(buffer, state);
    }

    private SaslMechanisms readProperties(ProtonBuffer buffer, DecoderState state, ListTypeDecoder listDecoder) throws DecodeException {
        final SaslMechanisms mechanisms = new SaslMechanisms();

        @SuppressWarnings("unused")
        final int size = listDecoder.readSize(buffer);
        final int count = listDecoder.readCount(buffer);

        if (count != REQUIRED_SASL_MECHANISMS_LIST_ENTRIES) {
            throw new DecodeException("SASL Mechanisms must contain at least one mechanisms entry: " + count);
        } else {
            mechanisms.setSaslServerMechanisms(state.getDecoder().readMultiple(buffer, state, Symbol.class));
        }

        return mechanisms;
    }

    @Override
    public SaslMechanisms readValue(InputStream stream, StreamDecoderState state) throws DecodeException {
        final StreamTypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(stream, state);

        return readProperties(stream, state, checkIsExpectedTypeAndCast(ListTypeDecoder.class, decoder));
    }

    @Override
    public SaslMechanisms[] readArrayElements(InputStream stream, StreamDecoderState state, int count) throws DecodeException {
        final StreamTypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(stream, state);

        final SaslMechanisms[] result = new SaslMechanisms[count];
        for (int i = 0; i < count; ++i) {
            result[i] = readProperties(stream, state, checkIsExpectedTypeAndCast(ListTypeDecoder.class, decoder));
        }

        return result;
    }

    @Override
    public void skipValue(InputStream stream, StreamDecoderState state) throws DecodeException {
        final StreamTypeDecoder<?> decoder = state.getDecoder().readNextTypeDecoder(stream, state);

        checkIsExpectedType(ListTypeDecoder.class, decoder);

        decoder.skipValue(stream, state);
    }

    private SaslMechanisms readProperties(InputStream stream, StreamDecoderState state, ListTypeDecoder listDecoder) throws DecodeException {
        final SaslMechanisms mechanisms = new SaslMechanisms();

        @SuppressWarnings("unused")
        final int size = listDecoder.readSize(stream);
        final int count = listDecoder.readCount(stream);

        if (count != REQUIRED_SASL_MECHANISMS_LIST_ENTRIES) {
            throw new DecodeException("SASL Mechanisms must contain at least one mechanisms entry: " + count);
        } else {
            mechanisms.setSaslServerMechanisms(state.getDecoder().readMultiple(stream, state, Symbol.class));
        }

        return mechanisms;
    }
}
