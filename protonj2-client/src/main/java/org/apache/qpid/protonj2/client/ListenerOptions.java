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

/**
 * Configuration options that can be supplied when creating a new {@link Listener}
 * instance which controls aspects of the behavior of the newly created {@link Listener}
 */
public class ListenerOptions extends LinkOptions<ListenerOptions> {

    private long drainTimeout = ConnectionOptions.DEFAULT_DRAIN_TIMEOUT;
    private boolean autoAccept = true;
    private int creditWindow = 10;

    /**
     * Create a new ListenerOptions instance with defaults set for all options.
     */
    public ListenerOptions() {
    }

    /**
     * Create a new ListenerOptions instance that copies the configuration from the specified source options.
     *
     * @param options
     * 		The ListenerOptions instance whose settings are to be copied into this one.
     */
    public ListenerOptions(ListenerOptions options) {
        if (options != null) {
            options.copyInto(this);
        }
    }

    /**
     * Controls if the created Listener will automatically accept the deliveries that have
     * been received by the application (default is <code>true</code>).
     *
     * @param autoAccept
     *      The value to assign for auto delivery acceptance.
     *
     * @return this {@link ListenerOptions} instance.
     */
    public ListenerOptions autoAccept(boolean autoAccept) {
        this.autoAccept = autoAccept;
        return this;
    }

    /**
     * @return the current value of the {@link Listener} auto accept setting.
     */
    public boolean autoAccept() {
        return autoAccept;
    }

    /**
     * @return the credit window configuration that will be applied to created {@link Listener} instances.
     */
    public int creditWindow() {
        return creditWindow;
    }

    /**
     * A credit window value that will be used to maintain an window of credit for Receiver instances
     * that are created.  The {@link Listener} will allow up to the credit window amount of incoming
     * deliveries to be queued and as they are read from the {@link Listener} the window will be extended
     * to maintain a consistent backlog of deliveries.  The default is to configure a credit window of 10.
     * <p>
     * To disable credit windowing and allow the client application to control the credit on the {@link Receiver}
     * link the credit window value should be set to zero.
     *
     * @param creditWindow
     *      The assigned credit window value to use.
     *
     * @return this {@link ListenerOptions} instance.
     */
    public ListenerOptions creditWindow(int creditWindow) {
        this.creditWindow = creditWindow;
        return this;
    }

    /**
     * @return the configured drain timeout value that will use to fail a pending drain request.
     */
    public long drainTimeout() {
        return drainTimeout;
    }

    /**
     * Sets the drain timeout (in milliseconds) after which a {@link Listener} request to drain
     * link credit is considered failed and the request will be marked as such.
     *
     * @param drainTimeout
     *      the drainTimeout to use for receiver links.
     *
     * @return this {@link ListenerOptions} instance.
     */
    public ListenerOptions drainTimeout(long drainTimeout) {
        return drainTimeout(drainTimeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the drain timeout value after which a {@link Listener} request to drain
     * link credit is considered failed and the request will be marked as such.
     *
     * @param timeout
     *      Timeout value to wait for a remote response.
     * @param units
     * 		The {@link TimeUnit} that defines the timeout span.
     *
     * @return this {@link ListenerOptions} instance.
     */
    public ListenerOptions drainTimeout(long timeout, TimeUnit units) {
        this.drainTimeout = units.toMillis(timeout);
        return this;
    }

    @Override
    public ListenerOptions clone() {
        return copyInto(new ListenerOptions());
    }

    /**
     * Copy all options from this {@link ListenerOptions} instance into the instance
     * provided.
     *
     * @param other
     *      the target of this copy operation.
     *
     * @return this options class for chaining.
     */
    protected ListenerOptions copyInto(ListenerOptions other) {
        super.copyInto(other);

        other.autoAccept(autoAccept);
        other.creditWindow(creditWindow);
        other.drainTimeout(drainTimeout);

        return this;
    }

    @Override
    protected ListenerOptions self() {
        return this;
    }
}
