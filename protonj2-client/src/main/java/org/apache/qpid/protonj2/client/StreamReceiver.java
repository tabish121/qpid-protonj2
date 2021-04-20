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
package org.apache.qpid.protonj2.client;

import java.util.concurrent.TimeUnit;

import org.apache.qpid.protonj2.client.exceptions.ClientException;
import org.apache.qpid.protonj2.types.transport.Transfer;

/**
 * A receiver of large message content that is delivered in multiple {@link Transfer} frames from
 * the remote.
 */
public interface StreamReceiver extends Receiver {

    /**
     * Blocking receive method that waits forever for the remote to provide a {@link StreamReceiverMessage} for consumption.
     * <p>
     * Receive calls will only grant credit on their own if a credit window is configured in the
     * {@link StreamReceiverOptions} which is done by default.  If the client application has configured
     * no credit window than this method will not grant any credit when it enters the wait for new
     * incoming messages.
     *
     * @return a new {@link Delivery} received from the remote.
     *
     * @throws ClientException if the {@link StreamReceiver} or its parent is closed when the call to receive is made.
     */
    @Override
    StreamDelivery receive() throws ClientException;

    /**
     * Blocking receive method that waits the given time interval for the remote to provide a
     * {@link StreamReceiverMessage} for consumption.  The amount of time this method blocks is based on the
     * timeout value. If timeout is equal to <code>-1</code> then it blocks until a Delivery is
     * received. If timeout is equal to zero then it will not block and simply return a
     * {@link StreamReceiverMessage} if one is available locally.  If timeout value is greater than zero then it
     * blocks up to timeout amount of time.
     * <p>
     * Receive calls will only grant credit on their own if a credit window is configured in the
     * {@link StreamReceiverOptions} which is done by default.  If the client application has not configured
     * a credit window or granted credit manually this method will not automatically grant any credit
     * when it enters the wait for a new incoming {@link StreamReceiverMessage}.
     *
     * @param timeout
     *      The timeout value used to control how long the receive method waits for a new {@link Delivery}.
     * @param unit
     *      The unit of time that the given timeout represents.
     *
     * @return a new {@link StreamReceiverMessage} received from the remote.
     *
     * @throws ClientException if the {@link StreamReceiver} or its parent is closed when the call to receive is made.
     */
    @Override
    StreamDelivery receive(long timeout, TimeUnit unit) throws ClientException;

    /**
     * Non-blocking receive method that either returns a message is one is immediately available or
     * returns null if none is currently at hand.
     *
     * @return a new {@link StreamReceiverMessage} received from the remote or null if no pending deliveries are available.
     *
     * @throws ClientException if the {@link StreamReceiver} or its parent is closed when the call to try to receive is made.
     */
    @Override
    StreamDelivery tryReceive() throws ClientException;

    /**
     * {@inheritDoc}
     *
     * @param credits
     *      credit The number of credits to add to the {@link StreamReceiver} link.
     *
     * @return this {@link StreamReceiver} instance.
     *
     * @throws ClientException if an error occurs while attempting to add new {@link StreamReceiver} link credit.
     */
    @Override
    StreamReceiver addCredit(int credits) throws ClientException;

}
