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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.overbaard.jira.synctool.impl.Constants.CODE;
import static org.overbaard.jira.synctool.impl.Constants.NAME;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.overbaard.jira.synctool.api.SyncConfigurationManager;
import org.overbaard.jira.synctool.impl.activeobjects.SyncCfg;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.user.UserService;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.search.SearchContextFactory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionCallback;

import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.RawEntity;

/**
 * @author Kabir Khan
 */
public class SyncConfigurationManagerBuilder {

    private final ActiveObjects activeObjects = mock(ActiveObjects.class);
    private final ProjectManager projectManager = mock(ProjectManager.class);

    private Map<String, ModelNode> activeObjectEntries = new HashMap<>();

    public SyncConfigurationManagerBuilder addConfigActiveObjectsFromFile(String... resources) throws IOException {
        for (String resource : resources) {
            ModelNode entry = loadConfig(resource);
            addConfigActiveObjectsFromModel(entry);
        }
        return this;
    }

    public SyncConfigurationManagerBuilder addConfigActiveObjectsFromModel(ModelNode entry) throws IOException {
        addConfigActiveObject(entry.get(CODE).asString(), entry);
        return this;
    }

    public SyncConfigurationManagerBuilder addConfigActiveObject(String name, ModelNode activeObject) {
        activeObjectEntries.put(name, activeObject);
        return this;
    }

    public SyncConfigurationManager build() {
        when(activeObjects.executeInTransaction(any(TransactionCallback.class))).thenAnswer(invocation -> ((TransactionCallback)invocation.getArguments()[0]).doInTransaction());
        when(activeObjects.find(any(Class.class), any(Query.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Class<?> clazz = (Class<?>)args[0];
            if (clazz == SyncCfg.class) {
                Query query = (Query) args[1];
                if (query.getWhereClause().equals("code = ?") && query.getWhereParams().length == 1) {
                    ModelNode entry = activeObjectEntries.get(query.getWhereParams()[0]);
                    if (entry != null) {
                        return new SyncCfg[]{new MockSyncCfg("kabir", entry).syncCfg};
                    }
                }
                return new SyncCfg[0];
            } else {
                Assert.fail("Unknown");
            }
            return null;
        });

        //These should not be needed by this code path
        final ApplicationProperties applicationProperties = null;
        final AvatarService avatarService = null;
        final IssueLinkManager issueLinkManager = null;
        final IssueService issueService = null;
        final OptionsManager optionsManager = null;
        final SearchContextFactory searchContextFactory = null;
        final SearchService searchService = null;
        final UserService userService = null;
        final VersionManager versionManager = null;

        JiraInjectables jiraInjectables = new JiraInjectables(
                activeObjects,
                applicationProperties,
                avatarService,
                issueService,
                issueLinkManager,
                optionsManager,
                projectManager,
                searchContextFactory,
                searchService,
                userService,
                versionManager);

        return new SyncConfigurationManagerImpl(jiraInjectables);
    }

    public static ModelNode  loadConfig(String resource) throws IOException {
        InputStream in = SyncConfigurationManagerBuilder.class.getClassLoader().getResourceAsStream(resource);
        Assert.assertNotNull(resource, in);
        try (InputStream bin = new BufferedInputStream(in)){
            return ModelNode.fromJSONStream(bin);
        }
    }

    private static class MockRawEntity implements RawEntity<Integer> {
        @Override
        public void init() {

        }

        @Override
        public void save() {

        }

        @Override
        public EntityManager getEntityManager() {
            return null;
        }

        @Override
        public <X extends RawEntity<Integer>> Class<X> getEntityType() {
            return null;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {

        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {

        }
    }

    private static class MockSyncCfg extends MockRawEntity {
        private final SyncCfg syncCfg = mock(SyncCfg.class);
        private String owningUserKey;
        private ModelNode modelNode;

        public MockSyncCfg(String owningUserKey, ModelNode modelNode) {
            this.owningUserKey = owningUserKey;
            this.modelNode = modelNode;

            when(syncCfg.getName()).thenReturn(modelNode.get(CODE).asString());
            when(syncCfg.getCode()).thenReturn(modelNode.get(NAME).asString());
            when(syncCfg.getConfigJson()).thenReturn(modelNode.toJSONString(true));
            when(syncCfg.getOwningUser()).thenReturn(owningUserKey);
            doAnswer(invocation -> this.modelNode = ModelNode.fromJSONString((String)invocation.getArguments()[0]))
                    .when(syncCfg).setConfigJson(anyString());
            doAnswer(invocation -> this.owningUserKey = (String)invocation.getArguments()[0])
                    .when(syncCfg).setOwningUserKey(anyString());
        }
    }
}
