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
package org.apache.qpid.proton4j.engine;

import org.apache.qpid.proton4j.amqp.security.SaslPerformative;
import org.apache.qpid.proton4j.amqp.transport.AMQPHeader;
import org.apache.qpid.proton4j.amqp.transport.Performative;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;

/**
 * Listen for events generated from the Engine
 */
public interface EngineHandler {

    default void handlerAdded(EngineHandlerContext context) throws Exception {}

    default void handlerRemoved(EngineHandlerContext context) throws Exception {}

    /**
     * Called when the engine is started to allow handlers to prepare for use based on
     * the configuration state at start of the engine.
     *
     * Each handler that implements this method should forward the event on to the next
     * handler unless there is an error that will stop the engine in which case the handler
     * can short circuit starting any handlers following this one and singal that the engine
     * is failed.
     *
     * @param context
     *      The context for this handler which can be used to forward the event to the next handler
     */
    default void engineStarting(EngineHandlerContext context) {
        context.fireEngineStarting();
    }

    /**
     * Called when the engine state has changed and handlers may need to update their internal state
     * to respond to the change or prompt some new work based on the change, e.g state changes from
     * not writable to writable.
     *
     * @param context
     *      The context for this handler which can be used to forward the event to the next handler
     */
    default void handleEngineStateChanged(EngineHandlerContext context) {
        context.fireEngineStateChanged();
    }

    // Read events

    default void handleRead(EngineHandlerContext context, ProtonBuffer buffer) {
        context.fireRead(buffer);
    }

    default void handleRead(EngineHandlerContext context, HeaderFrame header) {
        context.fireRead(header);
    }

    default void handleRead(EngineHandlerContext context, SaslFrame frame) {
        context.fireRead(frame);
    }

    default void handleRead(EngineHandlerContext context, ProtocolFrame frame) {
        context.fireRead(frame);
    }

    // Write events

    default void handleWrite(EngineHandlerContext context, AMQPHeader header) {
        context.fireWrite(header);
    }

    // TODO - To track exactly what is written here we may want to return the bytes written for each frame
    //        this would allow for somewhat easier tracking of bytes written for idle timeout handling.
    // TODO - In order to better control output from session level flow control we may want to pass the max
    //        frame size for these frame level writes and let it either be the "Max Frame Size" or the
    //        "Max you are allowed to write" value.

    default void handleWrite(EngineHandlerContext context, Performative performative, int channel, ProtonBuffer payload, Runnable payloadToLarge) {
        context.fireWrite(performative, channel, payload, payloadToLarge);
    }

    default void handleWrite(EngineHandlerContext context, SaslPerformative performative) {
        context.fireWrite(performative);
    }

    default void handleWrite(EngineHandlerContext context, ProtonBuffer buffer) {
        context.fireWrite(buffer);
    }

    // Error events

    default void transportEncodingError(EngineHandlerContext context, Throwable e) {
        context.fireEncodingError(e);
    }

    default void transportDecodingError(EngineHandlerContext context, Throwable e) {
        context.fireDecodingError(e);
    }

    default void transportFailed(EngineHandlerContext context, Throwable e) {
        context.fireFailed(e);
    }
}
