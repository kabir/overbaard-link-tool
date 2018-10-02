package org.overbaard.jira.synctool.impl.config;

import static org.overbaard.jira.synctool.impl.Constants.CODE;
import static org.overbaard.jira.synctool.impl.Constants.QUERY_FILTER;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.overbaard.jira.synctool.SyncValidationException;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ProjectConfig {
    private final String code;
    private final String queryFilter;

    public ProjectConfig(String code, String queryFilter) {
        this.code = code;
        this.queryFilter = queryFilter;
    }

    static ProjectConfig load(String side, ModelNode modelNode) {
        if (modelNode.getType() != ModelType.OBJECT) {
            throw new SyncValidationException("All '" + side + "' entries must be objects");
        }
        if (!modelNode.hasDefined(CODE)) {
            throw new SyncValidationException("All '" + side + "' entries must have a '" + CODE + "' string");
        }
        final String queryFilter = modelNode.hasDefined(QUERY_FILTER) ?
                modelNode.get(QUERY_FILTER).asString() : null;

        return new ProjectConfig(
                modelNode.get(CODE).asString(),
                queryFilter);
    }

    public ModelNode serializeModelNodeForConfig() {
        ModelNode result = new ModelNode();
        result.get(CODE).set(code);
        if (queryFilter != null) {
            result.get(QUERY_FILTER).set(queryFilter);
        }
        return result;
    }
}
