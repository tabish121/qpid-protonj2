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
package org.apache.qpid.protonj2.types.transactions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.qpid.protonj2.types.Binary;
import org.junit.jupiter.api.Test;

public class DischargeTest {

    @Test
    public void testToStringOnEmptyObject() {
        assertNotNull(new Discharge().toString());
    }

    @Test
    public void testTxnId() {
        Binary txnId = new Binary(new byte[] { 1 });
        Discharge discharge = new Discharge();

        assertNull(discharge.getTxnId());
        discharge.setTxnId(txnId);
        assertNotNull(discharge.getTxnId());

        try {
            discharge.setTxnId(null);
            fail("The TXN field is mandatory and cannot be set to null");
        } catch (NullPointerException npe) {}
    }
}
