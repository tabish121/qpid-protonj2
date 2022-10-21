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

package org.apache.qpid.protonj2.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A basic scheduling service API that does not provide the full {@link ScheduledExecutorService}
 * API which is generally not needed by the proton {@link Engine} and might not be provided by
 * the component provided to the engine which performs scheduled services.
 */
public interface Scheduler {

    /**
     * Runs the given {@link Runnable} command at some point in the future on
     * a the same thread of execution as all other API interactions with the
     * proton {@link Engine}. The execution can be queued or it could be run
     * immediately so long as the execution occurs on the thread that has exclusive
     * access to the engine.
     *
     * @param command the {@link Runnable} instance to execute.
     *
     * @throws NullPointerException if the given command is null.
     * @throws RejectedExecutionException if the command cannot be run for some reason.
     */
    void execute(Runnable command);

    /**
     *
     * @param command
     * @param delay
     * @param unit
     *
     * @return a {@link Future} instance that can be awaited for completion of the task.
     *
     * @throws NullPointerException if the given command is null.
     * @throws RejectedExecutionException if the command cannot be run for some reason.
     */
    public Future<?> schedule(Runnable command, long delay, TimeUnit unit);

    /**
     *
     * @param <V>
     * @param task
     * @param delay
     * @param unit
     *
     * @return a {@link Future} instance that can be awaited for completion of the task.
     */
    public <V> java.util.concurrent.Future<V> schedule(Callable<V> task, long delay, TimeUnit unit);

    /**
     *
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     *
     * @return a {@link Future} instance that can be awaited for completion of the task.
     *
     * @throws NullPointerException if the given command is null.
     * @throws RejectedExecutionException if the command cannot be run for some reason.
     */
    public Future<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    /**
     *
     * @param command
     * @param initialDelay
     * @param delay
     * @param unit
     *
     * @return a {@link Future} instance that can be awaited for completion of the task.
     *
     * @throws NullPointerException if the given command is null.
     * @throws RejectedExecutionException if the command cannot be run for some reason.
     */
    public Future<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);

}
