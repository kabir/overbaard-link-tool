/*
 * Copyright 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overbaard.jira.synctool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kabir Khan
 */
public class SyncLogger {
    private final Logger logger;

    public static final SyncLogger LOGGER = new SyncLogger("org.overbaard.jira");

    /**
     * Logger to use when debugging code/new features on a remote server
     */
    public static final SyncLogger PROTOTYPE = new SyncLogger("org.overbaard.jira.prototype");

    private SyncLogger(String category) {
        logger = LoggerFactory.getLogger(category);
    }

    public void trace(String msg, Object...params) {
        logger.trace(msg, params);
    }

    public void debug(String msg, Object...params) {
        logger.debug(msg, params);
    }

    public void info(String msg, Object...params) {
        logger.info(msg, params);
    }

    public void warn(String msg, Object...params) {
        logger.warn(msg, params);
    }

    public void error(String msg, Object... params) {
        logger.error(msg, params);
    }
}
