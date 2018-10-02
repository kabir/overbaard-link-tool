package org.overbaard.jira.synctool.impl.config;

import java.util.Set;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class LinkFilter {
    private final Set<String> issueTypes;
    private final Set<String> priorities;
    private final Set<String> labels;
    private final Set<String> linkNames;
    private final boolean emptyOverride;

    public LinkFilter(Set<String> issueTypes, Set<String> priorities, Set<String> labels, Set<String> linkNames, boolean emptyOverride) {
        this.issueTypes = issueTypes;
        this.priorities = priorities;
        this.labels = labels;
        this.linkNames = linkNames;
        this.emptyOverride = emptyOverride;
    }
}
