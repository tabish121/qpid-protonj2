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

import java.util.concurrent.Future;
import java.util.function.BiConsumer;

import org.apache.qpid.protonj2.client.exceptions.ClientException;

/**
 * Represents and AMQP Receiver link that provides asynchronous delivery of
 * incoming deliveries from the remote to a registered delivery handler.
 */
public interface Listener extends Link<Listener>  {

    /**
     * Adds credit to the {@link Listener} link for use when there receiver has not been configured
     * with a credit window.  When credit window is configured credit replenishment is automatic and
     * calling this method will result in an exception indicating that the operation is invalid.
     * <p>
     * If the {@link Listener} is draining and this method is called an exception will be thrown
     * to indicate that credit cannot be replenished until the remote has drained the existing link
     * credit.
     *
     * @param credits
     *      The number of credits to add to the {@link Listener} link.
     *
     * @return this {@link Listener} instance.
     *
     * @throws ClientException if an error occurs while attempting to add new {@link Listener} link credit.
     */
    Listener addCredit(int credits) throws ClientException;

    /**
     * Requests the remote to drain previously granted credit for this {@link Listener} link.
     *
     * @return a {@link Future} that will be completed when the remote drains this {@link Listener} link.
     *
     * @throws ClientException if an error occurs while attempting to drain the link credit.
     */
    Future<Listener> drain() throws ClientException;

    /**
     * Returns the number of Deliveries that are currently held in the {@link Listener} delivery
     * queue.  This number is likely to change immediately following the call as more deliveries
     * arrive but can be used to determine if any pending {@link Delivery} work is ready.
     *
     * @return the number of deliveries that are currently buffered locally.
     *
     * @throws ClientException if an error occurs while attempting to fetch the queue count.
     */
    long queuedDeliveries() throws ClientException;

    /**
     * Configures the listener that will be invoked on each inbound delivery from the remote.
     *
     * @param handler
     * 		The handler that is invoked for each inbound delivery.
     *
     * @return this {@link Listener} instance.
     *
     * @throws ClientException if an error occurs while attempting to add new {@link Listener} link credit.
     */
    Listener deliveryHandler(BiConsumer<Listener, AsyncDelivery> handler) throws ClientException;

}
