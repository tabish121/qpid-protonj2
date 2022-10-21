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
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Random;

import org.apache.qpid.protonj2.test.driver.AMQPTestDriver;
import org.apache.qpid.protonj2.test.driver.SessionTracker;
import org.apache.qpid.protonj2.test.driver.codec.ListDescribedType;
import org.apache.qpid.protonj2.test.driver.codec.messaging.Accepted;
import org.apache.qpid.protonj2.test.driver.codec.messaging.Modified;
import org.apache.qpid.protonj2.test.driver.codec.messaging.Rejected;
import org.apache.qpid.protonj2.test.driver.codec.messaging.Released;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Binary;
import org.apache.qpid.protonj2.test.driver.codec.primitives.Symbol;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedInteger;
import org.apache.qpid.protonj2.test.driver.codec.primitives.UnsignedShort;
import org.apache.qpid.protonj2.test.driver.codec.transactions.Declared;
import org.apache.qpid.protonj2.test.driver.codec.transport.DeliveryState;
import org.apache.qpid.protonj2.test.driver.codec.transport.Disposition;
import org.apache.qpid.protonj2.test.driver.codec.transport.ErrorCondition;
import org.apache.qpid.protonj2.test.driver.codec.transport.Role;
import org.apache.qpid.protonj2.test.driver.matchers.transactions.TransactionalStateMatcher;
import org.apache.qpid.protonj2.test.driver.matchers.transport.DispositionMatcher;
import org.hamcrest.Matcher;

import io.netty5.buffer.Buffer;

/**
 * Scripted expectation for the AMQP Disposition performative
 */
public class DispositionExpectation extends AbstractExpectation<Disposition> {

    private final DispositionMatcher matcher = new DispositionMatcher();
    private final DeliveryStateBuilder stateBuilder = new DeliveryStateBuilder();

    public DispositionExpectation(AMQPTestDriver driver) {
        super(driver);

        // Default mandatory field validation
        withRole(notNullValue());
        withFirst(notNullValue());
    }

    @Override
    public DispositionExpectation onChannel(int channel) {
        super.onChannel(channel);
        return this;
    }

    //----- Handle the incoming Disposition validation and update local side if able

    @Override
    public void handleDisposition(int frameSize, Disposition disposition, Buffer payload, int channel, AMQPTestDriver context) {
        super.handleDisposition(frameSize, disposition, payload, channel, context);

        final UnsignedShort remoteChannel = UnsignedShort.valueOf(channel);
        final SessionTracker session = driver.sessions().getSessionFromRemoteChannel(remoteChannel);

        if (session == null) {
            throw new AssertionError(String.format(
                "Received Disposition on channel [%s] that has no matching Session for that remote channel. ", remoteChannel));
        }

        session.handleDisposition(disposition);
    }

    //----- Type specific with methods that perform simple equals checks

    public DispositionExpectation withRole(boolean role) {
        withRole(equalTo(role));
        return this;
    }

    public DispositionExpectation withRole(Boolean role) {
        withRole(equalTo(role));
        return this;
    }

    public DispositionExpectation withRole(Role role) {
        withRole(equalTo(role.getValue()));
        return this;
    }

    public DispositionExpectation withFirst(int first) {
        return withFirst(equalTo(UnsignedInteger.valueOf(first)));
    }

    public DispositionExpectation withFirst(long first) {
        return withFirst(equalTo(UnsignedInteger.valueOf(first)));
    }

    public DispositionExpectation withFirst(UnsignedInteger first) {
        return withFirst(equalTo(first));
    }

    public DispositionExpectation withLast(int last) {
        return withLast(equalTo(UnsignedInteger.valueOf(last)));
    }

    public DispositionExpectation withLast(long last) {
        return withLast(equalTo(UnsignedInteger.valueOf(last)));
    }

    public DispositionExpectation withLast(UnsignedInteger last) {
        return withLast(equalTo(last));
    }

    public DispositionExpectation withSettled(boolean settled) {
        return withSettled(equalTo(settled));
    }

    public DispositionExpectation withState(DeliveryState state) {
        return withState(equalTo(state));
    }

    public DeliveryStateBuilder withState() {
        return stateBuilder;
    }

    public DispositionExpectation withBatchable(boolean batchable) {
        return withBatchable(equalTo(batchable));
    }

    //----- Matcher based with methods for more complex validation

    public DispositionExpectation withRole(Matcher<?> m) {
        matcher.addFieldMatcher(Disposition.Field.ROLE, m);
        return this;
    }

    public DispositionExpectation withFirst(Matcher<?> m) {
        matcher.addFieldMatcher(Disposition.Field.FIRST, m);
        return this;
    }

    public DispositionExpectation withLast(Matcher<?> m) {
        matcher.addFieldMatcher(Disposition.Field.LAST, m);
        return this;
    }

    public DispositionExpectation withSettled(Matcher<?> m) {
        matcher.addFieldMatcher(Disposition.Field.SETTLED, m);
        return this;
    }

    public DispositionExpectation withState(Matcher<?> m) {
        matcher.addFieldMatcher(Disposition.Field.STATE, m);
        return this;
    }

    public DispositionExpectation withBatchable(Matcher<?> m) {
        matcher.addFieldMatcher(Disposition.Field.BATCHABLE, m);
        return this;
    }

    @Override
    protected Matcher<ListDescribedType> getExpectationMatcher() {
        return matcher;
    }

    @Override
    protected Class<Disposition> getExpectedTypeClass() {
        return Disposition.class;
    }

    public final class DeliveryStateBuilder {

        public DispositionExpectation accepted() {
            withState(Accepted.getInstance());
            return DispositionExpectation.this;
        }

        public DispositionExpectation released() {
            withState(Released.getInstance());
            return DispositionExpectation.this;
        }

        public DispositionExpectation rejected() {
            withState(new Rejected());
            return DispositionExpectation.this;
        }

        public DispositionExpectation rejected(String condition, String description) {
            withState(new Rejected().setError(new ErrorCondition(Symbol.valueOf(condition), description)));
            return DispositionExpectation.this;
        }

        public DispositionExpectation modified() {
            withState(new Modified());
            return DispositionExpectation.this;
        }

        public DispositionExpectation modified(boolean failed) {
            withState(new Modified());
            return DispositionExpectation.this;
        }

        public DispositionExpectation modified(boolean failed, boolean undeliverableHere) {
            withState(new Modified());
            return DispositionExpectation.this;
        }

        public DispositionExpectation declared() {
            final byte[] txnId = new byte[4];

            Random rand = new Random();
            rand.setSeed(System.nanoTime());
            rand.nextBytes(txnId);

            withState(new Declared().setTxnId(txnId));
            return DispositionExpectation.this;
        }

        public DispositionExpectation declared(byte[] txnId) {
            withState(new Declared().setTxnId(txnId));
            return DispositionExpectation.this;
        }

        public DispositionTransactionalStateMatcher transactional() {
            DispositionTransactionalStateMatcher matcher = new DispositionTransactionalStateMatcher(DispositionExpectation.this);
            withState(matcher);
            return matcher;
        }
    }

    //----- Extend the TransactionalStateMatcher type to have an API suitable for Transfer expectation setup

    public static class DispositionTransactionalStateMatcher extends TransactionalStateMatcher {

        private final DispositionExpectation expectation;

        public DispositionTransactionalStateMatcher(DispositionExpectation expectation) {
            this.expectation = expectation;
        }

        public DispositionExpectation also() {
            return expectation;
        }

        public DispositionExpectation and() {
            return expectation;
        }

        @Override
        public DispositionTransactionalStateMatcher withTxnId(byte[] txnId) {
            super.withTxnId(equalTo(new Binary(txnId)));
            return this;
        }

        @Override
        public DispositionTransactionalStateMatcher withTxnId(Binary txnId) {
            super.withTxnId(equalTo(txnId));
            return this;
        }

        @Override
        public DispositionTransactionalStateMatcher withOutcome(DeliveryState outcome) {
            super.withOutcome(equalTo(outcome));
            return this;
        }

        //----- Matcher based with methods for more complex validation

        @Override
        public DispositionTransactionalStateMatcher withTxnId(Matcher<?> m) {
            super.withOutcome(m);
            return this;
        }

        @Override
        public DispositionTransactionalStateMatcher withOutcome(Matcher<?> m) {
            super.withOutcome(m);
            return this;
        }

        // ----- Add a layer to allow configuring the outcome without specific type dependencies

        public DispositionTransactionalStateMatcher withAccepted() {
            super.withOutcome(Accepted.getInstance());
            return this;
        }

        public DispositionTransactionalStateMatcher withReleased() {
            super.withOutcome(Released.getInstance());
            return this;
        }

        public DispositionTransactionalStateMatcher withRejected() {
            super.withOutcome(new Rejected());
            return this;
        }

        public DispositionTransactionalStateMatcher withRejected(String condition, String description) {
            super.withOutcome(new Rejected().setError(new ErrorCondition(Symbol.valueOf(condition), description)));
            return this;
        }

        public DispositionTransactionalStateMatcher withModified() {
            super.withOutcome(new Modified());
            return this;
        }

        public DispositionTransactionalStateMatcher withModified(boolean failed) {
            super.withOutcome(new Modified().setDeliveryFailed(failed));
            return this;
        }

        public DispositionTransactionalStateMatcher withModified(boolean failed, boolean undeliverableHere) {
            super.withOutcome(new Modified().setDeliveryFailed(failed).setUndeliverableHere(undeliverableHere));
            return this;
        }
    }
}
