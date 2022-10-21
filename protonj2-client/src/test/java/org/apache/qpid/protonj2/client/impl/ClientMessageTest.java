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
package org.apache.qpid.protonj2.client.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.qpid.protonj2.client.AdvancedMessage;
import org.apache.qpid.protonj2.client.Message;
import org.apache.qpid.protonj2.client.exceptions.ClientException;
import org.apache.qpid.protonj2.types.messaging.AmqpSequence;
import org.apache.qpid.protonj2.types.messaging.AmqpValue;
import org.apache.qpid.protonj2.types.messaging.ApplicationProperties;
import org.apache.qpid.protonj2.types.messaging.Data;
import org.apache.qpid.protonj2.types.messaging.Footer;
import org.apache.qpid.protonj2.types.messaging.Header;
import org.apache.qpid.protonj2.types.messaging.MessageAnnotations;
import org.apache.qpid.protonj2.types.messaging.Properties;
import org.apache.qpid.protonj2.types.messaging.Section;
import org.junit.jupiter.api.Test;

/**
 * Test the API of {@link ClientMessage}
 */
class ClientMessageTest {

    @Test
    public void testCreateEmpty() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        assertFalse(message.hasProperties());
        assertFalse(message.hasFooters());
        assertFalse(message.hasAnnotations());
    }

    @Test
    public void testCreateEmptyAdvanced() throws ClientException {
        AdvancedMessage<String> message = ClientMessage.createAdvancedMessage();

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        assertFalse(message.hasProperties());
        assertFalse(message.hasFooters());
        assertFalse(message.hasAnnotations());

        assertNull(message.header());
        assertNull(message.annotations());
        assertNull(message.applicationProperties());
        assertNull(message.footer());

        Header header = new Header();
        Properties properties = new Properties();
        MessageAnnotations ma = new MessageAnnotations(new LinkedHashMap<>());
        ApplicationProperties ap = new ApplicationProperties(new LinkedHashMap<>());
        Footer ft = new Footer(new LinkedHashMap<>());

        message.header(header);
        message.properties(properties);
        message.annotations(ma);
        message.applicationProperties(ap);
        message.footer(ft);

        assertSame(header, message.header());
        assertSame(properties, message.properties());
        assertSame(ma, message.annotations());
        assertSame(ap, message.applicationProperties());
        assertSame(ft, message.footer());
    }

    @Test
    public void testCreateWithBody() {
        ClientMessage<String> message = ClientMessage.create(new AmqpValue<>("test"));

        assertNotNull(message.body());
        assertNotNull(message.bodySections());
        assertFalse(message.bodySections().isEmpty());

        assertEquals("test", message.body());

        message.forEachBodySection(value -> {
            assertEquals(new AmqpValue<>("test"), value);
        });
    }

    @Test
    public void testToAdvancedMessageReturnsSameInstance() throws ClientException {
        Message<String> message = ClientMessage.create(new AmqpValue<>("test"));

        assertNotNull(message.body());

        AdvancedMessage<String> advanced = message.toAdvancedMessage();

        assertSame(message, advanced);

        assertNotNull(advanced.bodySections());
        assertFalse(advanced.bodySections().isEmpty());

        assertEquals("test", advanced.body());

        advanced.forEachBodySection(value -> {
            assertEquals(new AmqpValue<>("test"), value);
        });

        assertEquals(0, advanced.messageFormat());
        advanced.messageFormat(17);
        assertEquals(17, advanced.messageFormat());
    }

    @Test
    public void testSetGetHeaderFields() {
        ClientMessage<String> message = ClientMessage.create();

        assertEquals(Header.DEFAULT_DURABILITY, message.durable());
        assertEquals(Header.DEFAULT_FIRST_ACQUIRER, message.firstAcquirer());
        assertEquals(Header.DEFAULT_DELIVERY_COUNT, message.deliveryCount());
        assertEquals(Header.DEFAULT_TIME_TO_LIVE, message.timeToLive());
        assertEquals(Header.DEFAULT_PRIORITY, message.priority());

        message.durable(true);
        message.firstAcquirer(true);
        message.deliveryCount(10);
        message.timeToLive(11);
        message.priority((byte) 12);

        assertEquals(true, message.durable());
        assertEquals(true, message.firstAcquirer());
        assertEquals(10, message.deliveryCount());
        assertEquals(11, message.timeToLive());
        assertEquals(12, message.priority());
    }

    @Test
    public void testSetGetMessagePropertiesFields() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.messageId());
        assertNull(message.userId());
        assertNull(message.to());
        assertNull(message.subject());
        assertNull(message.replyTo());
        assertNull(message.correlationId());
        assertNull(message.contentType());
        assertNull(message.contentEncoding());
        assertEquals(0, message.creationTime());
        assertEquals(0, message.absoluteExpiryTime());
        assertNull(message.groupId());
        assertEquals(0, message.groupSequence());
        assertNull(message.replyToGroupId());

        message.messageId("message-id");
        message.userId("user-id".getBytes(StandardCharsets.UTF_8));
        message.to("to");
        message.subject("subject");
        message.replyTo("replyTo");
        message.correlationId("correlationId");
        message.contentType("contentType");
        message.contentEncoding("contentEncoding");
        message.creationTime(32);
        message.absoluteExpiryTime(64);
        message.groupId("groupId");
        message.groupSequence(128);
        message.replyToGroupId("replyToGroupId");

        assertEquals("message-id", message.messageId());
        assertEquals("user-id", new String(message.userId(), StandardCharsets.UTF_8));
        assertEquals("to", message.to());
        assertEquals("subject", message.subject());
        assertEquals("replyTo", message.replyTo());
        assertEquals("subject", message.subject());
        assertEquals("correlationId", message.correlationId());
        assertEquals("contentType", message.contentType());
        assertEquals("contentEncoding", message.contentEncoding());
        assertEquals(32, message.creationTime());
        assertEquals(64, message.absoluteExpiryTime());
        assertEquals("groupId", message.groupId());
        assertEquals(128, message.groupSequence());
        assertEquals("replyToGroupId", message.replyToGroupId());
    }

    @Test
    public void testBodySetGet() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        assertNotNull(message.body("test"));
        assertEquals("test", message.body());

        message.forEachBodySection(value -> {
            assertEquals(new AmqpValue<>("test"), value);
        });

        message.clearBodySections();

        assertEquals(0, message.bodySections().size());
        assertNull(message.body());

        final AtomicInteger count = new AtomicInteger();
        message.bodySections().forEach(value -> {
            count.incrementAndGet();
        });

        assertEquals(0, count.get());
    }

    @Test
    public void testForEachMethodsOnEmptyMessage() {
        ClientMessage<String> message = ClientMessage.create();

        assertFalse(message.hasProperties());
        assertFalse(message.hasFooters());
        assertFalse(message.hasAnnotations());

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        message.forEachBodySection(value -> {
            fail("Should not invoke any consumers since Message is empty");
        });

        message.forEachProperty((key, value) -> {
            fail("Should not invoke any consumers since Message is empty");
        });

        message.forEachFooter((key, value) -> {
            fail("Should not invoke any consumers since Message is empty");
        });

        message.forEachAnnotation((key, value) -> {
            fail("Should not invoke any consumers since Message is empty");
        });
    }

    @Test
    public void testSetMultipleBodySections() {
        ClientMessage<String> message = ClientMessage.create();

        List<Section<?>> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new Data(new byte[] { 1 }));
        expected.add(new Data(new byte[] { 2 }));

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        message.bodySections(expected);

        assertEquals(expected.size(), message.bodySections().size());

        final AtomicInteger count = new AtomicInteger();
        message.forEachBodySection(value -> {
            assertEquals(expected.get(count.get()), value);
            count.incrementAndGet();
        });

        assertEquals(expected.size(), count.get());

        count.set(0);
        message.bodySections().forEach(value -> {
            assertEquals(expected.get(count.get()), value);
            count.incrementAndGet();
        });

        assertEquals(expected.size(), count.get());

        message.bodySections(Collections.emptyList());

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        message.bodySections(expected);

        assertEquals(expected.size(), message.bodySections().size());

        message.bodySections(null);

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());
    }

    @Test
    public void testSetMultipleBodySectionsWithNullClearsOldSingleBodySection() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        message.body("test");

        assertNotNull(message.body());
        assertNotNull(message.bodySections());
        assertFalse(message.bodySections().isEmpty());

        message.bodySections(null);

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());
        assertEquals(0, message.bodySections().size());
    }

    @Test
    public void testAddMultipleBodySectionsPreservesOriginal() {
        ClientMessage<byte[]> message = ClientMessage.create();

        List<Data> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 1 }));
        expected.add(new Data(new byte[] { 2 }));
        expected.add(new Data(new byte[] { 3 }));

        message.body(new byte[] { 0 });

        assertNotNull(message.body());

        for (Data value : expected) {
            message.addBodySection(value);
        }

        assertEquals(expected.size() + 1, message.bodySections().size());

        final AtomicInteger counter = new AtomicInteger();
        message.bodySections().forEach(section -> {
            assertTrue(section instanceof Data);
            final Data dataView = (Data) section;
            assertEquals(counter.get(), dataView.getBuffer().getByte(0));
            assertEquals(counter.getAndIncrement(), dataView.getBinary().asByteArray()[0]);
        });
    }

    @Test
    public void testAddMultipleBodySections() {
        ClientMessage<byte[]> message = ClientMessage.create();

        List<Data> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new Data(new byte[] { 1 }));
        expected.add(new Data(new byte[] { 2 }));

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        for (Data value : expected) {
            message.addBodySection(value);
        }

        assertEquals(expected.size(), message.bodySections().size());

        final AtomicInteger count = new AtomicInteger();
        message.forEachBodySection(value -> {
            assertEquals(expected.get(count.get()), value);
            count.incrementAndGet();
        });

        assertEquals(expected.size(), count.get());

        count.set(0);
        message.bodySections().forEach(value -> {
            assertEquals(expected.get(count.get()), value);
            count.incrementAndGet();
        });

        assertEquals(expected.size(), count.get());

        message.clearBodySections();

        assertEquals(0, message.bodySections().size());

        count.set(0);
        message.bodySections().forEach(value -> {
            count.incrementAndGet();
        });

        assertEquals(0, count.get());

        for (Data value : expected) {
            message.addBodySection(value);
        }

        // setting a single body value should clear any previous sections.
        assertEquals(expected.size(), message.bodySections().size());
        message.body(new byte[] { 3 });
        assertEquals(1, message.bodySections().size());
        expected.set(0, new Data(new byte[] { 3 }));

        Iterator<?> expectations = expected.iterator();
        message.bodySections().forEach(section -> {
            assertEquals(section, expectations.next());
        });

        message.body(null);
        assertNull(message.body());
        assertEquals(0, message.bodySections().size());
    }

    @Test
    public void testMixSingleAndMultipleSectionAccess() {
        ClientMessage<byte[]> message = ClientMessage.create();

        List<Data> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new Data(new byte[] { 1 }));
        expected.add(new Data(new byte[] { 2 }));

        assertNull(message.body());
        assertNotNull(message.bodySections());
        assertTrue(message.bodySections().isEmpty());

        message.body(expected.get(0).getValue());

        assertArrayEquals(expected.get(0).getValue(), message.body());
        assertNotNull(message.bodySections());
        assertFalse(message.bodySections().isEmpty());
        assertEquals(1, message.bodySections().size());

        message.addBodySection(expected.get(1));

        assertArrayEquals(expected.get(0).getValue(), message.body());
        assertNotNull(message.bodySections());
        assertFalse(message.bodySections().isEmpty());
        assertEquals(2, message.bodySections().size());

        message.addBodySection(expected.get(2));

        assertArrayEquals(expected.get(0).getValue(), message.body());
        assertNotNull(message.bodySections());
        assertFalse(message.bodySections().isEmpty());
        assertEquals(3, message.bodySections().size());

        final AtomicInteger count = new AtomicInteger();
        message.bodySections().forEach(value -> {
            assertEquals(expected.get(count.get()), value);
            count.incrementAndGet();
        });

        assertEquals(expected.size(), count.get());
    }

    @Test
    public void testSetMultipleBodySectionsValidatesDefaultFormat() {
        ClientMessage<Object> message = ClientMessage.create();

        List<Section<?>> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new AmqpValue<>("test"));
        expected.add(new AmqpSequence<>(new ArrayList<>()));

        assertThrows(IllegalArgumentException.class, () -> message.bodySections(expected));
    }

    @Test
    public void testAddMultipleBodySectionsValidatesDefaultFormat() {
        ClientMessage<Object> message = ClientMessage.create();

        final List<Section<?>> expected1 = new ArrayList<>();
        expected1.add(new Data(new byte[] { 0 }));
        expected1.add(new AmqpValue<>("test"));
        expected1.add(new AmqpSequence<>(new ArrayList<>()));

        assertThrows(IllegalArgumentException.class, () -> expected1.forEach(section -> message.addBodySection(section)));

        message.clearBodySections();

        final List<Section<?>> expected2 = new ArrayList<>();
        expected2.add(new AmqpSequence<>(new ArrayList<>()));
        expected2.add(new Data(new byte[] { 0 }));
        expected2.add(new AmqpValue<>("test"));

        assertThrows(IllegalArgumentException.class, () -> expected2.forEach(section -> message.addBodySection(section)));

        message.clearBodySections();

        final List<Section<?>> expected3 = new ArrayList<>();
        expected3.add(new AmqpValue<>("test"));
        expected3.add(new AmqpSequence<>(new ArrayList<>()));
        expected3.add(new Data(new byte[] { 0 }));

        assertThrows(IllegalArgumentException.class, () -> expected3.forEach(section -> message.addBodySection(section)));
    }

    @Test
    public void testReplaceOriginalWithSetBodySectionDoesNotThrowValidationErrorIfValid() {
        ClientMessage<Object> message = ClientMessage.create();

        message.body("string");  // AmqpValue

        List<Section<?>> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));

        assertDoesNotThrow(() -> message.bodySections(expected));
    }

    @Test
    public void testReplaceOriginalWithSetBodySectionClearsOriginal() {
        ClientMessage<Object> message = ClientMessage.create();

        message.body("string");  // AmqpValue

        List<Section<?>> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new Data(new byte[] { 1 }));

        assertEquals("string", message.body());
        assertEquals(1, message.bodySections().size());

        message.bodySections(expected);

        assertEquals(expected.size(), message.bodySections().size());
    }

    @Test
    public void testReplaceOriginalWithSetBodySectionDoesThrowValidationErrorIfInValid() {
        ClientMessage<Object> message = ClientMessage.create();

        message.body("string");  // AmqpValue

        List<Section<?>> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new AmqpValue<>("test"));
        expected.add(new AmqpSequence<>(new ArrayList<>()));

        assertThrows(IllegalArgumentException.class, () -> message.bodySections(expected));
    }

    @Test
    public void testAddAdditionalBodySectionsValidatesDefaultFormat() {
        ClientMessage<Object> message = ClientMessage.create();

        message.body("string");  // AmqpValue

        assertThrows(IllegalArgumentException.class, () -> message.addBodySection(new Data(new byte[] { 0 })));
    }

    @Test
    public void testSetMultipleBodySectionsWithNonDefaultMessageFormat() {
        ClientMessage<Object> message = ClientMessage.create().messageFormat(1);

        List<Section<?>> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new AmqpValue<>("test"));
        expected.add(new AmqpSequence<>(new ArrayList<>()));

        assertDoesNotThrow(() -> message.bodySections(expected));

        final AtomicInteger count = new AtomicInteger();
        message.bodySections().forEach(value -> {
            assertEquals(expected.get(count.get()), value);
            count.incrementAndGet();
        });

        assertEquals(expected.size(), count.get());
    }

    @Test
    public void testAddMultipleBodySectionsWithNonDefaultMessageFormat() {
        ClientMessage<Object> message = ClientMessage.create().messageFormat(1);

        List<Section<?>> expected = new ArrayList<>();
        expected.add(new Data(new byte[] { 0 }));
        expected.add(new AmqpValue<>("test"));
        expected.add(new AmqpSequence<>(new ArrayList<>()));

        assertDoesNotThrow(() -> message.bodySections(expected));

        final AtomicInteger count = new AtomicInteger();
        message.bodySections().forEach(value -> {
            assertEquals(expected.get(count.get()), value);
            count.incrementAndGet();
        });

        assertEquals(expected.size(), count.get());
    }

    @Test
    public void testMessageAnnotation() {
        ClientMessage<String> message = ClientMessage.create();

        final Map<String, String> expectations = new HashMap<>();
        expectations.put("test1", "1");
        expectations.put("test2", "2");

        assertFalse(message.hasAnnotations());
        assertFalse(message.hasAnnotation("test1"));

        assertNotNull(message.annotation("test1", "1"));
        assertNotNull(message.annotation("test1"));

        assertTrue(message.hasAnnotations());
        assertTrue(message.hasAnnotation("test1"));

        assertNotNull(message.annotation("test2", "2"));
        assertNotNull(message.annotation("test2"));

        final AtomicInteger count = new AtomicInteger();

        message.forEachAnnotation((k, v) -> {
            assertTrue(expectations.containsKey(k));
            assertEquals(v, expectations.get(k));
            count.incrementAndGet();
        });

        assertEquals(expectations.size(), count.get());

        assertEquals("1", message.removeAnnotation("test1"));
        assertEquals("2", message.removeAnnotation("test2"));
        assertNull(message.removeAnnotation("test1"));
        assertNull(message.removeAnnotation("test2"));
        assertNull(message.removeAnnotation("test3"));
        assertFalse(message.hasAnnotations());
        assertFalse(message.hasAnnotation("test1"));
        assertFalse(message.hasAnnotation("test2"));

        message.forEachAnnotation((k, v) -> {
            fail("Should not be any remaining Message Annotations");
        });
    }

    @Test
    public void testApplicationProperty() {
        ClientMessage<String> message = ClientMessage.create();

        final Map<String, String> expectations = new HashMap<>();
        expectations.put("test1", "1");
        expectations.put("test2", "2");

        assertFalse(message.hasProperties());
        assertFalse(message.hasProperty("test1"));

        assertNotNull(message.property("test1", "1"));
        assertNotNull(message.property("test1"));

        assertTrue(message.hasProperties());
        assertTrue(message.hasProperty("test1"));

        assertNotNull(message.property("test2", "2"));
        assertNotNull(message.property("test2"));

        final AtomicInteger count = new AtomicInteger();

        message.forEachProperty((k, v) -> {
            assertTrue(expectations.containsKey(k));
            assertEquals(v, expectations.get(k));
            count.incrementAndGet();
        });

        assertEquals(expectations.size(), count.get());

        assertEquals("1", message.removeProperty("test1"));
        assertEquals("2", message.removeProperty("test2"));
        assertNull(message.removeProperty("test1"));
        assertNull(message.removeProperty("test2"));
        assertNull(message.removeProperty("test3"));
        assertFalse(message.hasProperties());
        assertFalse(message.hasProperty("test1"));
        assertFalse(message.hasProperty("test2"));

        message.forEachProperty((k, v) -> {
            fail("Should not be any remaining Application Properties");
        });
    }

    @Test
    public void testFooter() {
        ClientMessage<String> message = ClientMessage.create();

        final Map<String, String> expectations = new HashMap<>();
        expectations.put("test1", "1");
        expectations.put("test2", "2");

        assertFalse(message.hasFooters());
        assertFalse(message.hasFooter("test1"));

        assertNotNull(message.footer("test1", "1"));
        assertNotNull(message.footer("test1"));

        assertTrue(message.hasFooters());
        assertTrue(message.hasFooter("test1"));

        assertNotNull(message.footer("test2", "2"));
        assertNotNull(message.footer("test2"));

        final AtomicInteger count = new AtomicInteger();

        message.forEachFooter((k, v) -> {
            assertTrue(expectations.containsKey(k));
            assertEquals(v, expectations.get(k));
            count.incrementAndGet();
        });

        assertEquals(expectations.size(), count.get());

        assertEquals("1", message.removeFooter("test1"));
        assertEquals("2", message.removeFooter("test2"));
        assertNull(message.removeFooter("test1"));
        assertNull(message.removeFooter("test2"));
        assertNull(message.removeFooter("test3"));
        assertFalse(message.hasFooters());
        assertFalse(message.hasFooter("test1"));
        assertFalse(message.hasFooter("test2"));

        message.forEachFooter((k, v) -> {
            fail("Should not be any remaining footers");
        });
    }

    @Test
    public void testGetUserIdHandlesNullPropertiesOrNullUserIDInProperties() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.properties());
        assertNull(message.userId());

        message.properties(new Properties());

        assertNull(message.userId());
    }

    @Test
    public void testApplicationPropertiesAccessorHandlerNullMapOrEmptyMap() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.applicationProperties());
        assertNull(message.property("test"));
        assertFalse(message.hasProperty("test"));
        assertFalse(message.hasProperties());

        message.applicationProperties(new ApplicationProperties(null));

        assertNotNull(message.applicationProperties());
        assertNull(message.property("test"));
        assertFalse(message.hasProperty("test"));
        assertFalse(message.hasProperties());
    }

    @Test
    public void testFooterAccessorHandlerNullMapOrEmptyMap() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.footer());
        assertNull(message.footer("test"));
        assertFalse(message.hasFooter("test"));
        assertFalse(message.hasFooters());

        message.footer(new Footer(null));

        assertNotNull(message.footer());
        assertNull(message.footer("test"));
        assertFalse(message.hasFooter("test"));
        assertFalse(message.hasFooters());
    }

    @Test
    public void testMessageAnnotationsAccessorHandlerNullMapOrEmptyMap() {
        ClientMessage<String> message = ClientMessage.create();

        assertNull(message.annotations());
        assertNull(message.annotation("test"));
        assertFalse(message.hasAnnotation("test"));
        assertFalse(message.hasAnnotations());

        message.annotations(new MessageAnnotations(null));

        assertNotNull(message.annotations());
        assertNull(message.annotation("test"));
        assertFalse(message.hasAnnotation("test"));
        assertFalse(message.hasAnnotations());
    }
}
