/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.qpid.protonj2.test.driver.matchers.messaging;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.Date;
import java.util.HashMap;

import org.apache.qpid.protonj2.test.driver.codec.primitives.Binary;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Symbol;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedInteger;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedLong;
import org.hamcrest.Matcher;

/**
 * Generated by generate-message-section-matchers.xsl, which resides in this
 * package.
 */
public class PropertiesMatcher extends AbstractListSectionMatcher {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:properties:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000073L);

    /**
     * Note that the ordinal of the Field enumeration match the order specified in
     * the AMQP specification
     */
    public enum Field {
        MESSAGE_ID,
        USER_ID,
        TO,
        SUBJECT,
        REPLY_TO,
        CORRELATION_ID,
        CONTENT_TYPE,
        CONTENT_ENCODING,
        ABSOLUTE_EXPIRY_TIME,
        CREATION_TIME,
        GROUP_ID,
        GROUP_SEQUENCE,
        REPLY_TO_GROUP_ID,
    }

    public PropertiesMatcher(boolean expectTrailingBytes) {
        super(DESCRIPTOR_CODE, DESCRIPTOR_SYMBOL, new HashMap<Object, Matcher<?>>(), expectTrailingBytes);
    }

    //----- Type specific with methods that perform simple equals checks

    public PropertiesMatcher withMessageId(Object messageId) {
        return withMessageId(equalTo(messageId));
    }

    public PropertiesMatcher withUserId(byte[] userId) {
        return withUserId(equalTo(new Binary(userId)));
    }

    public PropertiesMatcher withUserId(Binary userId) {
        return withUserId(equalTo(userId));
    }

    public PropertiesMatcher withTo(String to) {
        return withTo(equalTo(to));
    }

    public PropertiesMatcher withSubject(String subject) {
        return withSubject(equalTo(subject));
    }

    public PropertiesMatcher withReplyTo(String replyTo) {
        return withReplyTo(equalTo(replyTo));
    }

    public PropertiesMatcher withCorrelationId(Object correlationId) {
        return withCorrelationId(equalTo(correlationId));
    }

    public PropertiesMatcher withContentType(String contentType) {
        return withContentType(equalTo(Symbol.valueOf(contentType)));
    }

    public PropertiesMatcher withContentType(Symbol contentType) {
        return withContentType(equalTo(contentType));
    }

    public PropertiesMatcher withContentEncoding(String contentEncoding) {
        return withContentEncoding(equalTo(Symbol.valueOf(contentEncoding)));
    }

    public PropertiesMatcher withContentEncoding(Symbol contentEncoding) {
        return withContentEncoding(equalTo(contentEncoding));
    }

    public PropertiesMatcher withAbsoluteExpiryTime(int absoluteExpiryTime) {
        return withAbsoluteExpiryTime(equalTo(new Date(absoluteExpiryTime)));
    }

    public PropertiesMatcher withAbsoluteExpiryTime(long absoluteExpiryTime) {
        return withAbsoluteExpiryTime(equalTo(new Date(absoluteExpiryTime)));
    }

    public PropertiesMatcher withAbsoluteExpiryTime(Long absoluteExpiryTime) {
        if (absoluteExpiryTime == null) {
            return withAbsoluteExpiryTime(nullValue());
        } else {
            return withAbsoluteExpiryTime(equalTo(new Date(absoluteExpiryTime)));
        }
    }

    public PropertiesMatcher withCreationTime(int creationTime) {
        return withCreationTime(equalTo(new Date(creationTime)));
    }

    public PropertiesMatcher withCreationTime(long creationTime) {
        return withCreationTime(equalTo(new Date(creationTime)));
    }

    public PropertiesMatcher withCreationTime(Long creationTime) {
        if (creationTime == null) {
            return withCreationTime(nullValue());
        } else {
            return withCreationTime(equalTo(new Date(creationTime)));
        }
    }

    public PropertiesMatcher withGroupId(String groupId) {
        return withGroupId(equalTo(groupId));
    }

    public PropertiesMatcher withGroupSequence(int groupSequence) {
        return withGroupSequence(equalTo(UnsignedInteger.valueOf(groupSequence)));
    }

    public PropertiesMatcher withGroupSequence(long groupSequence) {
        return withGroupSequence(equalTo(UnsignedInteger.valueOf(groupSequence)));
    }

    public PropertiesMatcher withGroupSequence(Long groupSequence) {
        if (groupSequence == null) {
            return withGroupSequence(nullValue());
        } else {
            return withGroupSequence(equalTo(UnsignedInteger.valueOf(groupSequence.longValue())));
        }
    }

    public PropertiesMatcher withReplyToGroupId(String replyToGroupId) {
        return withReplyToGroupId(equalTo(replyToGroupId));
    }

    //----- Matcher based with methods for more complex validation

    public PropertiesMatcher withMessageId(Matcher<?> m) {
        getMatchers().put(Field.MESSAGE_ID, m);
        return this;
    }

    public PropertiesMatcher withUserId(Matcher<?> m) {
        getMatchers().put(Field.USER_ID, m);
        return this;
    }

    public PropertiesMatcher withTo(Matcher<?> m) {
        getMatchers().put(Field.TO, m);
        return this;
    }

    public PropertiesMatcher withSubject(Matcher<?> m) {
        getMatchers().put(Field.SUBJECT, m);
        return this;
    }

    public PropertiesMatcher withReplyTo(Matcher<?> m) {
        getMatchers().put(Field.REPLY_TO, m);
        return this;
    }

    public PropertiesMatcher withCorrelationId(Matcher<?> m) {
        getMatchers().put(Field.CORRELATION_ID, m);
        return this;
    }

    public PropertiesMatcher withContentType(Matcher<?> m) {
        getMatchers().put(Field.CONTENT_TYPE, m);
        return this;
    }

    public PropertiesMatcher withContentEncoding(Matcher<?> m) {
        getMatchers().put(Field.CONTENT_ENCODING, m);
        return this;
    }

    public PropertiesMatcher withAbsoluteExpiryTime(Matcher<?> m) {
        getMatchers().put(Field.ABSOLUTE_EXPIRY_TIME, m);
        return this;
    }

    public PropertiesMatcher withCreationTime(Matcher<?> m) {
        getMatchers().put(Field.CREATION_TIME, m);
        return this;
    }

    public PropertiesMatcher withGroupId(Matcher<?> m) {
        getMatchers().put(Field.GROUP_ID, m);
        return this;
    }

    public PropertiesMatcher withGroupSequence(Matcher<?> m) {
        getMatchers().put(Field.GROUP_SEQUENCE, m);
        return this;
    }

    public PropertiesMatcher withReplyToGroupId(Matcher<?> m) {
        getMatchers().put(Field.REPLY_TO_GROUP_ID, m);
        return this;
    }

    public Object getReceivedMessageId() {
        return getReceivedFields().get(Field.MESSAGE_ID);
    }

    public Object getReceivedUserId() {
        return getReceivedFields().get(Field.USER_ID);
    }

    public Object getReceivedTo() {
        return getReceivedFields().get(Field.TO);
    }

    public Object getReceivedSubject() {
        return getReceivedFields().get(Field.SUBJECT);
    }

    public Object getReceivedReplyTo() {
        return getReceivedFields().get(Field.REPLY_TO);
    }

    public Object getReceivedCorrelationId() {
        return getReceivedFields().get(Field.CORRELATION_ID);
    }

    public Object getReceivedContentType() {
        return getReceivedFields().get(Field.CONTENT_TYPE);
    }

    public Object getReceivedContentEncoding() {
        return getReceivedFields().get(Field.CONTENT_ENCODING);
    }

    public Object getReceivedAbsoluteExpiryTime() {
        return getReceivedFields().get(Field.ABSOLUTE_EXPIRY_TIME);
    }

    public Object getReceivedCreationTime() {
        return getReceivedFields().get(Field.CREATION_TIME);
    }

    public Object getReceivedGroupId() {
        return getReceivedFields().get(Field.GROUP_ID);
    }

    public Object getReceivedGroupSequence() {
        return getReceivedFields().get(Field.GROUP_SEQUENCE);
    }

    public Object getReceivedReplyToGroupId() {
        return getReceivedFields().get(Field.REPLY_TO_GROUP_ID);
    }

    @Override
    protected Enum<?> getField(int fieldIndex) {
        return Field.values()[fieldIndex];
    }
}
