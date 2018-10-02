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

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.user.UserService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.search.SearchContextFactory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;

/**
 * @author Kabir Khan
 */
@Named("overbaardJiraInjectables")
public class JiraInjectables {
    @ComponentImport
    private final ActiveObjects activeObjects;

    @ComponentImport
    private final ApplicationProperties applicationProperties;

    @ComponentImport
    private final AvatarService avatarService;

    @ComponentImport
    private final IssueService issueService;

    @ComponentImport
    private final IssueLinkManager issueLinkManager;

    @ComponentImport
    private final OptionsManager optionsManager;

    @ComponentImport
    private final ProjectManager projectManager;

    @ComponentImport
    private final SearchContextFactory searchContextFactory;

    @ComponentImport
    private final SearchService searchService;

    @ComponentImport
    private final UserService userService;

    @ComponentImport
    private final VersionManager versionManager;


    //Jira does not like injecting this one, so use the ComponentAccessor
    private volatile UserManager jiraUserManager;


    @Inject
    public JiraInjectables(final ActiveObjects activeObjects, final ApplicationProperties applicationProperties,
                           final AvatarService avatarService,
                           final IssueService issueService,
                           final IssueLinkManager issueLinkManager,
                           final OptionsManager optionsManager,
                           final ProjectManager projectManager,
                           final SearchContextFactory searchContextFactory,
                           final SearchService searchService,
                           final UserService userService, final VersionManager versionManager) {
        this.activeObjects = activeObjects;
        this.applicationProperties = applicationProperties;
        this.avatarService = avatarService;
        this.issueService = issueService;
        this.issueLinkManager = issueLinkManager;
        this.optionsManager = optionsManager;
        this.projectManager = projectManager;
        this.searchContextFactory = searchContextFactory;
        this.searchService = searchService;
        this.userService = userService;
        this.versionManager = versionManager;
    }

    public ActiveObjects getActiveObjects() {
        return activeObjects;
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public AvatarService getAvatarService() {
        return avatarService;
    }

    public IssueService getIssueService() {
        return issueService;
    }

    public IssueLinkManager getIssueLinkManager() {
        return issueLinkManager;
    }


    public OptionsManager getOptionsManager() {
        return optionsManager;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public SearchContextFactory getSearchContextFactory() {
        return searchContextFactory;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public UserManager getJiraUserManager() {
        if (jiraUserManager == null) {
            jiraUserManager = ComponentAccessor.getUserManager();
        }
        return jiraUserManager;
    }

    public UserService getUserService() {
        return userService;
    }

    public VersionManager getVersionManager() {
        return versionManager;
    }
}
