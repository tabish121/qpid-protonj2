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
package org.apache.qpid.protonj2.test.driver.expectations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.qpid.protonj2.test.driver.AMQPTestDriver;
import org.apache.qpid.protonj2.test.driver.ScriptedExpectation;
import org.apache.qpid.protonj2.test.driver.actions.AMQPHeaderInjectAction;
import org.apache.qpid.protonj2.test.driver.actions.ByteBufferInjectAction;
import org.apache.qpid.protonj2.test.driver.codec.transport.AMQPHeader;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferAllocator;

/**
 * Expectation entry for AMQP Headers
 */
public class AMQPHeaderExpectation implements ScriptedExpectation {

    private final AMQPHeader expected;
    private final AMQPTestDriver driver;

    public AMQPHeaderExpectation(AMQPHeader expected, AMQPTestDriver driver) {
        this.expected = expected;
        this.driver = driver;
    }

    public AMQPHeaderInjectAction respondWithAMQPHeader() {
        AMQPHeaderInjectAction response = new AMQPHeaderInjectAction(driver, AMQPHeader.getAMQPHeader());
        driver.addScriptedElement(response);
        return response;
    }

    public AMQPHeaderInjectAction respondWithSASLHeader() {
        AMQPHeaderInjectAction response = new AMQPHeaderInjectAction(driver, AMQPHeader.getSASLHeader());
        driver.addScriptedElement(response);
        return response;
    }

    public ByteBufferInjectAction respondWithBytes(byte[] buffer) {
        ByteBufferInjectAction response = new ByteBufferInjectAction(driver, BufferAllocator.onHeapUnpooled().copyOf(buffer));
        driver.addScriptedElement(response);
        return response;
    }

    public ByteBufferInjectAction respondWithBytes(Buffer buffer) {
        ByteBufferInjectAction response = new ByteBufferInjectAction(driver, buffer);
        driver.addScriptedElement(response);
        return response;
    }

    @Override
    public void handleAMQPHeader(AMQPHeader header, AMQPTestDriver driver) {
        assertThat("AMQP Header should match expected.", expected, equalTo(header));
    }

    @Override
    public void handleSASLHeader(AMQPHeader header, AMQPTestDriver driver) {
        assertThat("SASL Header should match expected.", expected, equalTo(header));
    }
}
