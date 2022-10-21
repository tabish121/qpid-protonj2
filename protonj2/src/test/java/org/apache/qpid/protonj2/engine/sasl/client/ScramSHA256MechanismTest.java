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
package org.apache.qpid.protonj2.engine.sasl.client;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.buffer.ProtonBufferAllocator;
import org.junit.jupiter.api.Test;

/**
 * The known good used by these tests is taken from the example in RFC 7677 section 3.
 */
public class ScramSHA256MechanismTest extends AbstractScramSHAMechanismTestBase {

    private static final String TEST_USERNAME = "user";
    private static final String TEST_PASSWORD = "pencil";

    private static final String CLIENT_NONCE = "rOprNGfwEbeRWgbNEkqO";

    private static final ProtonBuffer EXPECTED_CLIENT_INITIAL_RESPONSE = ProtonBufferAllocator.defaultAllocator().copy(
        "n,,n=user,r=rOprNGfwEbeRWgbNEkqO".getBytes(StandardCharsets.UTF_8));
    private static final ProtonBuffer SERVER_FIRST_MESSAGE = ProtonBufferAllocator.defaultAllocator().copy(
        "r=rOprNGfwEbeRWgbNEkqO%hvYDpWUa2RaTCAfuxFIlj)hNlF$k0,s=W22ZaJ0SNY7soEsUEjb6gQ==,i=4096".getBytes(StandardCharsets.UTF_8));
    private static final ProtonBuffer EXPECTED_CLIENT_FINAL_MESSAGE = ProtonBufferAllocator.defaultAllocator().copy(
        "c=biws,r=rOprNGfwEbeRWgbNEkqO%hvYDpWUa2RaTCAfuxFIlj)hNlF$k0,p=dHzbZapWIk4jUhN+Ute9ytag9zjfMHgsqmmiz7AndVQ=".getBytes(StandardCharsets.UTF_8));
    private static final ProtonBuffer SERVER_FINAL_MESSAGE = ProtonBufferAllocator.defaultAllocator().copy(
        "v=6rriTRBi23WpRR/wtup+mMhUZUn/dB5nLTJRsjl95G4=".getBytes(StandardCharsets.UTF_8));

    public ScramSHA256MechanismTest() {
        super(EXPECTED_CLIENT_INITIAL_RESPONSE,
              SERVER_FIRST_MESSAGE,
              EXPECTED_CLIENT_FINAL_MESSAGE,
              SERVER_FINAL_MESSAGE);
    }

    @Override
    protected SaslCredentialsProvider getTestCredentials() {
        return credentials(TEST_USERNAME, TEST_PASSWORD);
    }

    @Override
    protected Mechanism getMechanismForTesting() {
        return new ScramSHA256Mechanism(CLIENT_NONCE);
    }

    @Test
    public void testGetNameMatchesValueInSaslMechanismsEnum() {
        assertEquals(SaslMechanisms.SCRAM_SHA_256.getName(), getMechanismForTesting().getName());
    }

    @Test
    public void testDifferentClientNonceOnEachInstance() throws Exception {
        ScramSHA256Mechanism mech1 = new ScramSHA256Mechanism();
        ScramSHA256Mechanism mech2 = new ScramSHA256Mechanism();

        ProtonBuffer clientInitialResponse1 = mech1.getInitialResponse(getTestCredentials());
        ProtonBuffer clientInitialResponse2 = mech2.getInitialResponse(getTestCredentials());

        assertTrue(clientInitialResponse1.toString(StandardCharsets.UTF_8).startsWith("n,,n=user,r="));
        assertTrue(clientInitialResponse2.toString(StandardCharsets.UTF_8).startsWith("n,,n=user,r="));

        assertThat(clientInitialResponse1, not(equalTo(clientInitialResponse2)));
    }

    @Test
    public void testUsernameCommaEqualsCharactersEscaped() throws Exception {
        String originalUsername = "user,name=";
        String escapedUsername = "user=2Cname=3D";

        String expectedInitialResponseString = "n,,n=" + escapedUsername + ",r=" + CLIENT_NONCE;
        ProtonBuffer expectedInitialResponseBuffer = ProtonBufferAllocator.defaultAllocator().copy(
            expectedInitialResponseString.getBytes(StandardCharsets.UTF_8));

        ScramSHA256Mechanism mech = new ScramSHA256Mechanism(CLIENT_NONCE);

        ProtonBuffer clientInitialResponse = mech.getInitialResponse(credentials(originalUsername, "password"));
        assertEquals(expectedInitialResponseBuffer, clientInitialResponse);
    }

    @Test
    public void testPasswordCommaEqualsCharactersNotEscaped() throws Exception {
        Mechanism mechanism = getMechanismForTesting();
        SaslCredentialsProvider credentials = credentials(TEST_USERNAME, TEST_PASSWORD + ",=");

        ProtonBuffer clientInitialResponse = mechanism.getInitialResponse(credentials);
        assertEquals(EXPECTED_CLIENT_INITIAL_RESPONSE, clientInitialResponse);

        ProtonBuffer serverFirstMessage = ProtonBufferAllocator.defaultAllocator().copy(
            "r=rOprNGfwEbeRWgbNEkqOb291012f-b281-47d3-acbc-fefffaad60f2,s=fQwuXmWB4XES7vNK4oBlLtH9cbWAmtxO+Z+tZ9m5W54=,i=4096".getBytes(StandardCharsets.UTF_8));
        ProtonBuffer expectedClientFinalMessage = ProtonBufferAllocator.defaultAllocator().copy(
            "c=biws,r=rOprNGfwEbeRWgbNEkqOb291012f-b281-47d3-acbc-fefffaad60f2,p=PNeUNfKwyqBPjMssgF7yk4iLt8W24NS/D99HjBbXwyw=".getBytes(StandardCharsets.UTF_8));

        ProtonBuffer clientFinalMessage = mechanism.getChallengeResponse(credentials, serverFirstMessage);

        assertEquals(expectedClientFinalMessage, clientFinalMessage);

        ProtonBuffer serverFinalMessage = ProtonBufferAllocator.defaultAllocator().copy(
            "v=/N9SY26AOvz2QZkJZkyXpomWknaFWSN6zBGqg5RNG9w=".getBytes(StandardCharsets.UTF_8));
        ProtonBuffer expectedFinalChallengeResponse = ProtonBufferAllocator.defaultAllocator().copy("".getBytes());

        assertEquals(expectedFinalChallengeResponse, mechanism.getChallengeResponse(credentials, serverFinalMessage));

        mechanism.verifyCompletion();
    }
}