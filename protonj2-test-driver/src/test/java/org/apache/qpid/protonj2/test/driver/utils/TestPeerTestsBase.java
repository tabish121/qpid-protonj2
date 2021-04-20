/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.qpid.protonj2.test.driver.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestPeerTestsBase {

    public static final boolean IS_WINDOWS = System.getProperty("os.name", "unknown").toLowerCase().contains("windows");

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private String testName;

    public String getTestName() {
        return testName;
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) throws Exception {
        LOG.info("========== tearDown " + testInfo.getDisplayName() + " ==========");
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) throws Exception {
        LOG.info("========== start " + testInfo.getDisplayName() + " ==========");
        testName = testInfo.getDisplayName();
    }
}
