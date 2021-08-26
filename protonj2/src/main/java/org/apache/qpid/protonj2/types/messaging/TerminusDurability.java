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
package org.apache.qpid.protonj2.types.messaging;

import org.apache.qpid.protonj2.types.UnsignedInteger;

public enum TerminusDurability {

    NONE, CONFIGURATION, UNSETTLED_STATE;

    public UnsignedInteger getValue() {
        return UnsignedInteger.valueOf(ordinal());
    }

    public static TerminusDurability valueOf(UnsignedInteger value) {
        return TerminusDurability.valueOf(value.intValue());
    }

    public static TerminusDurability valueOf(long value) {
        if (value == 0) {
            return NONE;
        } else if (value == 1) {
            return CONFIGURATION;
        } else if (value == 2) {
            return UNSETTLED_STATE;
        }

        throw new IllegalArgumentException("Unknown TerminusDurability: " + value);
    }
}
