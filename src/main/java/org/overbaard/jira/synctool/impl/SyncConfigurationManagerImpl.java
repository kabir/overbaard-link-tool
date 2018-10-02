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

import static org.overbaard.jira.synctool.impl.Constants.CODE;
import static org.overbaard.jira.synctool.impl.Constants.CONFIGS;
import static org.overbaard.jira.synctool.impl.Constants.EDIT;
import static org.overbaard.jira.synctool.impl.Constants.ID;
import static org.overbaard.jira.synctool.impl.Constants.NAME;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dmr.ModelNode;
import org.overbaard.jira.synctool.SyncPermissionException;
import org.overbaard.jira.synctool.api.SyncConfigurationManager;
import org.overbaard.jira.synctool.impl.activeobjects.SyncCfg;
import org.overbaard.jira.synctool.impl.config.SyncSetup;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import net.java.ao.Query;
import net.sf.ehcache.concurrent.Sync;

/**
 * @author Kabir Khan
 */
@Named("overbaardBoardConfigurationManager")
public class SyncConfigurationManagerImpl implements SyncConfigurationManager {

    private volatile Map<String, SyncSetup> syncSetups = new ConcurrentHashMap<>();

    private final JiraInjectables jiraInjectables;

    /**
     * The 'Rank' custom field id
     */
    private volatile long rankCustomFieldId = -1;

    @Inject
    public SyncConfigurationManagerImpl(JiraInjectables jiraInjectables) {
        this.jiraInjectables = jiraInjectables;
    }

    public String getConfigList(ApplicationUser user, boolean forConfig) {
        Set<SyncCfg> configs = loadSyncConfigs();
        ModelNode configsList = new ModelNode();
        configsList.setEmptyList();
        for (SyncCfg config : configs) {
            ModelNode configNode = new ModelNode();
            configNode.get(ID).set(config.getID());
            configNode.get(CODE).set(config.getCode());
            configNode.get(NAME).set(config.getName());
            ModelNode configJson = ModelNode.fromJSONString(config.getConfigJson());
            if (forConfig) {
                if (canEditConfig(user, configJson)) {
                    configNode.get(EDIT).set(true);
                }
                configsList.add(configNode);
            } else {
                //A guess at what is needed to view the boards
                if (canViewComparison(user, configNode)) {
                    configsList.add(configNode);
                }
            }
        }

        //Add a few more fields
        ModelNode config = new ModelNode();
        config.get(CONFIGS).set(configsList);

        return config.toJSONString(true);
    }

    @Override
    public SyncSetup getSyncSetupForDisplay(ApplicationUser user, String code) {
        SyncSetup syncSetup = getBoardConfig(code);

        if (syncSetup != null && !canViewComparison(user, syncSetup)) {
            throw new SyncPermissionException("Insufficient permissions to view sync '" + code + "'");
        }
        return syncSetup;
    }

    public SyncSetup getBoardConfig(final String code) {
        SyncSetup syncSetup = syncSetups.get(code);
        if (syncSetup == null) {
            final ActiveObjects activeObjects = jiraInjectables.getActiveObjects();
            SyncCfg[] cfgs = activeObjects.executeInTransaction(new TransactionCallback<SyncCfg[]>() {
                @Override
                public SyncCfg[] doInTransaction() {
                    return activeObjects.find(SyncCfg.class, Query.select().where("code = ?", code));
                }
            });

            if (cfgs != null && cfgs.length == 1) {
                SyncCfg cfg = cfgs[0];
                syncSetup = SyncSetup.loadAndValidate(ModelNode.fromJSONString(cfg.getConfigJson()));

                SyncSetup old = syncSetups.putIfAbsent(code, syncSetup);
                if (old != null) {
                    syncSetup = old;
                }
            }
        }
        return syncSetup;
    }

    private Set<SyncCfg> loadSyncConfigs() {
        final ActiveObjects activeObjects = jiraInjectables.getActiveObjects();

        return activeObjects.executeInTransaction(new TransactionCallback<Set<SyncCfg>>() {
            @Override
            public Set<SyncCfg> doInTransaction() {
                Set<SyncCfg> configs = new TreeSet<>((o1, o2) -> {
                    return o1.getName().compareTo(o2.getName());
                });
                for (SyncCfg boardCfg : activeObjects.find(SyncCfg.class)) {
                    configs.add(boardCfg);

                }
                return configs;
            }
        });
    }

    private boolean canEditConfig(ApplicationUser user, ModelNode config) {
        // TODO
        return true;
    }

    private boolean canViewComparison(ApplicationUser user, ModelNode config) {
        // TODO
        return true;
    }

    private boolean canViewComparison(ApplicationUser user, SyncSetup syncSetup) {
        // TODO
        return true;
    }
}