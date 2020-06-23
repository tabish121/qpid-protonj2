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
package org.apache.qpid.proton4j.test.driver.matchers.transport;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.apache.qpid.proton4j.test.driver.codec.primitives.Symbol;
import org.apache.qpid.proton4j.test.driver.codec.primitives.UnsignedInteger;
import org.apache.qpid.proton4j.test.driver.codec.primitives.UnsignedShort;
import org.apache.qpid.proton4j.test.driver.codec.transport.Begin;
import org.apache.qpid.proton4j.test.driver.codec.util.TypeMapper;
import org.apache.qpid.proton4j.test.driver.matchers.ListDescribedTypeMatcher;
import org.hamcrest.Matcher;

public class BeginMatcher extends ListDescribedTypeMatcher {

    public BeginMatcher() {
        super(Begin.Field.values().length, Begin.DESCRIPTOR_CODE, Begin.DESCRIPTOR_SYMBOL);
    }

    @Override
    protected Class<?> getDescribedTypeClass() {
        return Begin.class;
    }

    //----- Type specific with methods that perform simple equals checks

    public BeginMatcher withRemoteChannel(int remoteChannel) {
        return withRemoteChannel(equalTo(UnsignedShort.valueOf((short) remoteChannel)));
    }

    public BeginMatcher withRemoteChannel(UnsignedShort remoteChannel) {
        return withRemoteChannel(equalTo(remoteChannel));
    }

    public BeginMatcher withNextOutgoingId(int nextOutgoingId) {
        return withNextOutgoingId(equalTo(UnsignedInteger.valueOf(nextOutgoingId)));
    }

    public BeginMatcher withNextOutgoingId(long nextOutgoingId) {
        return withNextOutgoingId(equalTo(UnsignedInteger.valueOf(nextOutgoingId)));
    }

    public BeginMatcher withNextOutgoingId(UnsignedInteger nextOutgoingId) {
        return withNextOutgoingId(equalTo(nextOutgoingId));
    }

    public BeginMatcher withIncomingWindow(int incomingWindow) {
        return withIncomingWindow(equalTo(UnsignedInteger.valueOf(incomingWindow)));
    }

    public BeginMatcher withIncomingWindow(long incomingWindow) {
        return withIncomingWindow(equalTo(UnsignedInteger.valueOf(incomingWindow)));
    }

    public BeginMatcher withIncomingWindow(UnsignedInteger incomingWindow) {
        return withIncomingWindow(equalTo(incomingWindow));
    }

    public BeginMatcher withOutgoingWindow(int outgoingWindow) {
        return withOutgoingWindow(equalTo(UnsignedInteger.valueOf(outgoingWindow)));
    }

    public BeginMatcher withOutgoingWindow(long outgoingWindow) {
        return withOutgoingWindow(equalTo(UnsignedInteger.valueOf(outgoingWindow)));
    }

    public BeginMatcher withOutgoingWindow(UnsignedInteger outgoingWindow) {
        return withOutgoingWindow(equalTo(outgoingWindow));
    }

    public BeginMatcher withHandleMax(int handleMax) {
        return withHandleMax(equalTo(UnsignedInteger.valueOf(handleMax)));
    }

    public BeginMatcher withHandleMax(long handleMax) {
        return withHandleMax(equalTo(UnsignedInteger.valueOf(handleMax)));
    }

    public BeginMatcher withHandleMax(UnsignedInteger handleMax) {
        return withHandleMax(equalTo(handleMax));
    }

    public BeginMatcher withOfferedCapabilities(String... offeredCapabilities) {
        return withOfferedCapabilities(equalTo(TypeMapper.toSymbolArray(offeredCapabilities)));
    }

    public BeginMatcher withOfferedCapabilities(Symbol... offeredCapabilities) {
        return withOfferedCapabilities(equalTo(offeredCapabilities));
    }

    public BeginMatcher withDesiredCapabilities(String... desiredCapabilities) {
        return withDesiredCapabilities(equalTo(TypeMapper.toSymbolArray(desiredCapabilities)));
    }

    public BeginMatcher withDesiredCapabilities(Symbol... desiredCapabilities) {
        return withDesiredCapabilities(equalTo(desiredCapabilities));
    }

    public BeginMatcher withPropertiesMap(Map<Symbol, Object> properties) {
        return withProperties(equalTo(properties));
    }

    public BeginMatcher withProperties(Map<String, Object> properties) {
        return withProperties(equalTo(TypeMapper.toSymbolKeyedMap(properties)));
    }

    //----- Matcher based with methods for more complex validation

    public BeginMatcher withRemoteChannel(Matcher<?> m) {
        addFieldMatcher(Begin.Field.REMOTE_CHANNEL, m);
        return this;
    }

    public BeginMatcher withNextOutgoingId(Matcher<?> m) {
        addFieldMatcher(Begin.Field.NEXT_OUTGOING_ID, m);
        return this;
    }

    public BeginMatcher withIncomingWindow(Matcher<?> m) {
        addFieldMatcher(Begin.Field.INCOMING_WINDOW, m);
        return this;
    }

    public BeginMatcher withOutgoingWindow(Matcher<?> m) {
        addFieldMatcher(Begin.Field.OUTGOING_WINDOW, m);
        return this;
    }

    public BeginMatcher withHandleMax(Matcher<?> m) {
        addFieldMatcher(Begin.Field.HANDLE_MAX, m);
        return this;
    }

    public BeginMatcher withOfferedCapabilities(Matcher<?> m) {
        addFieldMatcher(Begin.Field.OFFERED_CAPABILITIES, m);
        return this;
    }

    public BeginMatcher withDesiredCapabilities(Matcher<?> m) {
        addFieldMatcher(Begin.Field.DESIRED_CAPABILITIES, m);
        return this;
    }

    public BeginMatcher withProperties(Matcher<?> m) {
        addFieldMatcher(Begin.Field.PROPERTIES, m);
        return this;
    }
}
