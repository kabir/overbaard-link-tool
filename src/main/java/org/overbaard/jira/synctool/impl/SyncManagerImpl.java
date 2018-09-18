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
package org.overbaard.jira.synctool.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.overbaard.jira.synctool.api.SyncConfigurationManager;
import org.overbaard.jira.synctool.api.SyncManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * The interface to the loaded boards
 *
 * @author Kabir Khan
 */
@Named("overbaardBoardManager")
public class SyncManagerImpl implements SyncManager, InitializingBean, DisposableBean {

    private static final int REFRESH_TIMEOUT_SECONDS = 5 * 60;

    private final JiraInjectables jiraInjectables;

    private final SyncConfigurationManager syncConfigurationManager;

    @Inject
    public SyncManagerImpl(JiraInjectables jiraInjectables,
                           SyncConfigurationManager syncConfigurationManager) {
        this.jiraInjectables = jiraInjectables;
        this.syncConfigurationManager = syncConfigurationManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void destroy() throws Exception {
    }
}
