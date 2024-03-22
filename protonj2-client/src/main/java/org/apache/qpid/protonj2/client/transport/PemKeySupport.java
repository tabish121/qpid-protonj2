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
package org.apache.qpid.protonj2.client.transport;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.security.KeyStore;
import java.security.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PemKeySupport {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PEM_PROVIDER_CLASS_NAME = "de.dentrassi.crypto.pem.PemKeyStoreProvider";

    public static final String PEM_STORE_PREFIX = "pem";
    public static final String PEM_STORE_TYPE = "PEM";
    public static final String PEMCFG_STORE_TYPE = "PEMCFG";

    private static final Constructor<Provider> pemProviderCtor;
    private static final Exception pemDiscoveryError;

    static {
        Constructor<Provider> discoveredProviderCtor = null;
        Exception discoveryError = null;

        try {
            final Class<?> clazz = Class.forName(PEM_PROVIDER_CLASS_NAME);

            if (clazz != null) {
                @SuppressWarnings("unchecked")
                final Constructor<Provider> ctor = (Constructor<Provider>) clazz.getDeclaredConstructor();

                if (ctor != null && ctor.newInstance() != null) {
                    discoveredProviderCtor = ctor;
                }
            }
        } catch (Exception createError) {
            LOG.debug("Unable to offer support for PEM keys due to error creating provider", createError);
            discoveryError = createError;
        } finally {
            pemProviderCtor = discoveredProviderCtor;
            pemDiscoveryError = discoveryError;
        }
    }

    /**
     * @return <code>true</code> if the PEM key library was found and a {@link KeyStore} can be created.
     */
    public static boolean isAvailable() {
        return pemProviderCtor != null;
    }

    /**
     * Creates a new {@link KeyStore} instance for use in loading a PEM key or PEMCFG properties
     * file that defines a chain of PEM keys.
     *
     * @param storeType
     * 		The type of store to create.
     *
     * @return a new {@link KeyStore} instance for loading PEM keys.
     *
     * @throws Exception if an error occurs or a PEM provider is not available.
     */
    public static KeyStore newPemKeyStore(String storeType) throws Exception {
        if (pemProviderCtor == null) {
            final Exception error = new UnsupportedOperationException(
                "Unable to offer support for PEM keys due to error creating provider");

            throw (Exception) error.initCause(pemDiscoveryError);
        }

        if (PEM_STORE_TYPE.equalsIgnoreCase(storeType)) {
            return KeyStore.getInstance(PEM_STORE_TYPE, pemProviderCtor.newInstance());
        } else if (PEMCFG_STORE_TYPE.equalsIgnoreCase(storeType)) {
            return KeyStore.getInstance(PEMCFG_STORE_TYPE, pemProviderCtor.newInstance());
        } else {
            throw new IllegalArgumentException("Store type passed [" + storeType + "] is not a PEM type or PEMCFG type.");
        }
    }
}
