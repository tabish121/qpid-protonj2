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

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import org.apache.qpid.protonj2.client.AsyncDelivery;
import org.apache.qpid.protonj2.client.Listener;
import org.apache.qpid.protonj2.client.ListenerOptions;
import org.apache.qpid.protonj2.client.exceptions.ClientException;
import org.apache.qpid.protonj2.client.exceptions.ClientIllegalStateException;
import org.apache.qpid.protonj2.client.exceptions.ClientOperationTimedOutException;
import org.apache.qpid.protonj2.client.futures.ClientFuture;
import org.apache.qpid.protonj2.client.util.FifoDeliveryQueue;
import org.apache.qpid.protonj2.engine.IncomingDelivery;
import org.apache.qpid.protonj2.engine.Receiver;
import org.apache.qpid.protonj2.types.messaging.Released;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client asynchronous listener implementation.
 */
public class ClientListener extends ClientReceiverLinkType<Listener> implements Listener {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ListenerOptions options;
    private final FifoDeliveryQueue messageQueue;
    private final ReentrantLock dispatchLock = new ReentrantLock();

    private BiConsumer<Listener, AsyncDelivery> deliveryHandler;
    private org.apache.qpid.protonj2.engine.Receiver protonReceiver;

    ClientListener(ClientSession session, ListenerOptions options, String listenerId, Receiver protonReceiver) {
        super(session, listenerId, options, protonReceiver);

        this.options = options;

        if (options.creditWindow() > 0) {
            protonReceiver.addCredit(options.creditWindow());
        }

        messageQueue = new FifoDeliveryQueue(options.creditWindow());
        messageQueue.start();
    }

    @Override
    public Listener addCredit(int credits) throws ClientException {
        checkClosedOrFailed();
        ClientFuture<Listener> creditAdded = session.getFutureFactory().createFuture();

        executor.execute(() -> {
            if (notClosedOrFailed(creditAdded)) {
                if (options.creditWindow() != 0) {
                    creditAdded.failed(new ClientIllegalStateException(
                        "Cannot add credit when a credit window has been configured"));
                } else if (protonReceiver.isDraining()) {
                    creditAdded.failed(new ClientIllegalStateException("Cannot add credit while a drain is pending"));
                } else {
                    try {
                        protonReceiver.addCredit(credits);
                        creditAdded.complete(this);
                    } catch (Exception ex) {
                        creditAdded.failed(ClientExceptionSupport.createNonFatalOrPassthrough(ex));
                    }
                }
            }
        });

        return session.request(this, creditAdded);
    }

    @Override
    public Future<Listener> drain() throws ClientException {
        checkClosedOrFailed();
        final ClientFuture<Listener> drainComplete = session.getFutureFactory().createFuture();

        executor.execute(() -> {
            if (notClosedOrFailed(drainComplete)) {
                if (protonReceiver.isDraining()) {
                    drainComplete.failed(new ClientIllegalStateException("Receiver is already draining"));
                    return;
                }

                try {
                    if (protonReceiver.drain()) {
                        drainingFuture = drainComplete;
                        drainingTimeout = session.scheduleRequestTimeout(drainingFuture, options.drainTimeout(),
                            () -> new ClientOperationTimedOutException("Timed out waiting for remote to respond to drain request"));
                    } else {
                        drainComplete.complete(this);
                    }
                } catch (Exception ex) {
                    drainComplete.failed(ClientExceptionSupport.createNonFatalOrPassthrough(ex));
                }
            }
        });

        return drainComplete;
    }

    @Override
    public long queuedDeliveries() throws ClientException {
        return messageQueue.size();
    }

    @Override
    public Listener deliveryHandler(BiConsumer<Listener, AsyncDelivery> handler) throws ClientException {
        checkClosedOrFailed();

        dispatchLock.lock();
        try {
            deliveryHandler = handler;
        } finally {
            dispatchLock.unlock();
        }

        return this;
    }

    @Override
    protected void replenishCreditIfNeeded() {
        int creditWindow = options.creditWindow();
        if (creditWindow > 0) {
            int currentCredit = protonReceiver.getCredit();
            if (currentCredit <= creditWindow * 0.5) {
                int potentialPrefetch = currentCredit + messageQueue.size();

                if (potentialPrefetch <= creditWindow * 0.7) {
                    int additionalCredit = creditWindow - potentialPrefetch;

                    LOG.trace("Consumer granting additional credit: {}", additionalCredit);
                    try {
                        protonReceiver.addCredit(additionalCredit);
                    } catch (Exception ex) {
                        LOG.debug("Error caught during credit top-up", ex);
                    }
                }
            }
        }
    }

    @Override
    protected void handleDeliveryRead(IncomingDelivery delivery) {
        LOG.trace("Delivery data was received: {}", delivery);

        if (delivery.getDefaultDeliveryState() == null) {
            delivery.setDefaultDeliveryState(Released.getInstance());
        }

        if (!delivery.isPartial()) {
            LOG.trace("{} has incoming Message(s).", this);
            //TODO messageQueue.enqueue(new ClientDelivery(this, delivery));
        } else {
            delivery.claimAvailableBytes();
        }
    }

    //----- ClientLinkType APIs implemented to complete the listener

    @Override
    protected Listener self() {
        return this;
    }

    @Override
    protected void linkSpecificLocalCloseHandler() {
        messageQueue.stop();  // Ensure blocked receivers are all unblocked.
    }

    @Override
    protected void recreateLinkForReconnect() {
        int previousCredit = protonReceiver.getCredit() + messageQueue.size();

        messageQueue.clear();  // Prefetched messages should be discarded.

        if (drainingFuture != null) {
            drainingFuture.complete(this);
            if (drainingTimeout != null) {
                drainingTimeout.cancel(false);
                drainingTimeout = null;
            }
        }

        protonReceiver.localCloseHandler(null);
        protonReceiver.localDetachHandler(null);
        protonReceiver.close();
        protonReceiver = ClientReceiverBuilder.recreateReceiver(session, protonReceiver, options);
        protonReceiver.setLinkedResource(this);
        protonReceiver.addCredit(previousCredit);
    }
}
