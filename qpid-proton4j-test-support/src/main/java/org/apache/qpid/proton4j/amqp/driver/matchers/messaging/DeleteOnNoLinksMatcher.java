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
package org.apache.qpid.proton4j.amqp.driver.matchers.messaging;

import org.apache.qpid.proton4j.amqp.driver.codec.messaging.DeleteOnNoLinks;
import org.apache.qpid.proton4j.amqp.driver.matchers.ListDescribedTypeMatcher;

public class DeleteOnNoLinksMatcher extends ListDescribedTypeMatcher {

    public DeleteOnNoLinksMatcher() {
        super(0, DeleteOnNoLinks.DESCRIPTOR_CODE, DeleteOnNoLinks.DESCRIPTOR_SYMBOL);
    }

    @Override
    protected Class<?> getDescribedTypeClass() {
        return DeleteOnNoLinks.class;
    }
}
