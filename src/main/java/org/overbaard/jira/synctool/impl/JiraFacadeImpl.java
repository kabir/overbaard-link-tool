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

import java.io.InputStream;
import java.util.jar.Manifest;

import javax.inject.Inject;
import javax.inject.Named;

import org.overbaard.jira.synctool.api.JiraFacade;
import org.overbaard.jira.synctool.api.SyncConfigurationManager;
import org.overbaard.jira.synctool.api.SyncManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Named ("overbaardSyncToolJiraFacade")
public class JiraFacadeImpl implements JiraFacade, InitializingBean, DisposableBean {
    private final SyncConfigurationManager syncConfigurationManager;

    private final SyncManager syncManager;

    private static final String overbaardSyncToolVersion;

    static {
        String version;
        try (InputStream stream = JiraFacadeImpl.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")) {
            Manifest manifest = null;
            if (stream != null) {
                manifest = new Manifest(stream);
            }
            version = manifest.getMainAttributes().getValue("Bundle-Version");
        } catch (Exception e) {
            // ignored
            version = "Error";
        }
        overbaardSyncToolVersion = version;
    }

    @Inject
    public JiraFacadeImpl(final SyncConfigurationManager syncConfigurationManager,
                          final SyncManager syncManager) {
        this.syncConfigurationManager = syncConfigurationManager;
        this.syncManager = syncManager;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

    public String getOverbaardSyncToolVersion() {
        return overbaardSyncToolVersion;
    }
}