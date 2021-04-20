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
package org.apache.qpid.protonj2.test.driver.codec.transactions;

import java.util.List;

import org.apache.qpid.protonj2.test.driver.codec.ListDescribedType;
import org.apache.qpid.protonj2.test.driver.codec.messaging.Outcome;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Binary;
import org.apache.qpid.protonj2.test.driver.codec.primitives.DescribedType;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Symbol;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedLong;
import org.apache.qpid.protonj2.test.driver.codec.transport.DeliveryState;

public class Declared extends ListDescribedType implements DeliveryState, Outcome {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:declared:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000033L);

    /**
     * Enumeration which maps to fields in the Declared Performative
     */
    public enum Field {
        TXN_ID
    }

    public Declared() {
        super(Field.values().length);
    }

    @SuppressWarnings("unchecked")
    public Declared(Object described) {
        super(Field.values().length, (List<Object>) described);
    }

    public Declared(List<Object> described) {
        super(Field.values().length, described);
    }

    @Override
    public Symbol getDescriptor() {
        return DESCRIPTOR_SYMBOL;
    }

    public Declared setTxnId(Binary o) {
        getList().set(Field.TXN_ID.ordinal(), o);
        return this;
    }

    public Binary getTxnId() {
        return (Binary) getList().get(Field.TXN_ID.ordinal());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DescribedType)) {
            return false;
        }

        DescribedType d = (DescribedType) obj;
        if (!(DESCRIPTOR_CODE.equals(d.getDescriptor()) || DESCRIPTOR_SYMBOL.equals(d.getDescriptor()))) {
            return false;
        }

        Object described = getDescribed();
        Object described2 = d.getDescribed();
        if (described == null) {
            return described2 == null;
        } else {
            return described.equals(described2);
        }
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public DeliveryStateType getType() {
        return DeliveryStateType.Declared;
    }
}
