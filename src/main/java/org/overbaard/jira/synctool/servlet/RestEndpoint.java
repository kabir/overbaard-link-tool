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
package org.overbaard.jira.synctool.servlet;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.dmr.ModelNode;
import org.overbaard.jira.synctool.api.JiraFacade;
import org.overbaard.jira.synctool.impl.Constants;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;

/**
 * @author Kabir Khan
 */
@Path("/")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class RestEndpoint {

    /**
     * If we change anything in the payloads etc. we should bump this so that the client can take action.
     * The corresponding location on the client is in app.ts
     */
    private static final int API_VERSION = 2;

    private final JiraFacade jiraFacade;

    @Inject
    public RestEndpoint(JiraFacade jiraFacade) {
        this.jiraFacade = jiraFacade;
    }

    @GET
    @Path(Constants.VERSION)
    public Response getVersion() {
        ModelNode versionNode = new ModelNode();
        //Remove this later
        versionNode.get(Constants.VERSION).set(API_VERSION);
        versionNode.get(Constants.API_VERSION).set(API_VERSION);
        versionNode.get(Constants.OVERBAARD_VERSION).set(jiraFacade.getOverbaardSyncToolVersion());
        return createResponse(versionNode);
    }

    private Response createResponse(ModelNode modelNode) {
        return createResponse(modelNode.toJSONString(true));
    }

    private Response createResponse(String json) {
        return Response.ok(json).build();
    }

    private ApplicationUser getUser() {
        //Jira doesn't seem to like injection of this
        JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        return authenticationContext.getUser();
    }
}