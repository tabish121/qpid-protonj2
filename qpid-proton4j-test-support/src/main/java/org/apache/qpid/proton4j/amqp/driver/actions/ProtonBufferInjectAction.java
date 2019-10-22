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
package org.apache.qpid.proton4j.amqp.driver.actions;

import org.apache.qpid.proton4j.amqp.driver.AMQPTestDriver;
import org.apache.qpid.proton4j.amqp.driver.ScriptedAction;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;

/**
 * Scripted action that will write the contents of a given buffer out through the driver.
 */
public class ProtonBufferInjectAction implements ScriptedAction {

    private final ProtonBuffer buffer;
    private final AMQPTestDriver driver;

    public ProtonBufferInjectAction(AMQPTestDriver driver, ProtonBuffer buffer) {
        this.buffer = buffer;
        this.driver = driver;
    }

    @Override
    public ProtonBufferInjectAction perform(AMQPTestDriver driver) {
        driver.sendBytes(buffer);
        return this;
    }

    @Override
    public ProtonBufferInjectAction now() {
        perform(driver);
        return this;
    }

    @Override
    public ProtonBufferInjectAction later(int delay) {
        driver.afterDelay(delay, this);
        return this;
    }

    @Override
    public ProtonBufferInjectAction queue() {
        driver.addScriptedElement(this);
        return this;
    }
}
