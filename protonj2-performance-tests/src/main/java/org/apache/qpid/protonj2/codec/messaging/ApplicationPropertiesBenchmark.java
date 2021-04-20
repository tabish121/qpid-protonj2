/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.protonj2.codec.messaging;

import java.io.IOException;
import java.util.HashMap;

import org.apache.qpid.protonj2.buffer.ProtonBuffer;
import org.apache.qpid.protonj2.codec.CodecBenchmarkBase;
import org.apache.qpid.protonj2.types.UnsignedByte;
import org.apache.qpid.protonj2.types.UnsignedInteger;
import org.apache.qpid.protonj2.types.UnsignedShort;
import org.apache.qpid.protonj2.types.messaging.ApplicationProperties;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

public class ApplicationPropertiesBenchmark extends CodecBenchmarkBase {

    private Blackhole blackhole;
    private ApplicationProperties properties;

    @Setup
    public void init(Blackhole blackhole) {
        super.init();
        this.blackhole = blackhole;
        initApplicationProperties();
        encode();
    }

    private void initApplicationProperties() {
        properties = new ApplicationProperties(new HashMap<String, Object>());
        properties.getValue().put("test1", UnsignedByte.valueOf((byte) 128));
        properties.getValue().put("test2", UnsignedShort.valueOf((short) 128));
        properties.getValue().put("test3", UnsignedInteger.valueOf((byte) 128));
    }

    @Benchmark
    public ProtonBuffer encode() {
        buffer.clear();
        encoder.writeObject(buffer, encoderState, properties);
        return buffer;
    }

    @Benchmark
    public ProtonBuffer decode() throws IOException {
        buffer.setReadIndex(0);
        blackhole.consume(decoder.readObject(buffer, decoderState));
        return buffer;
    }

    public static void main(String[] args) throws RunnerException {
        runBenchmark(ApplicationPropertiesBenchmark.class);
    }
}