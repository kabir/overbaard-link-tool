package org.overbaard.jira.synctool.impl.config;

import static org.overbaard.jira.synctool.impl.Constants.CODE;
import static org.overbaard.jira.synctool.impl.Constants.DESCRIPTION;
import static org.overbaard.jira.synctool.impl.Constants.PROJECT_SETS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.overbaard.jira.synctool.SyncValidationException;

/**
 * The main class involved in defining a sync
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SyncSetup {
    private final String code;
    private final String description;
    private final List<ProjectSet> projectSets;

    private SyncSetup(
            String code,
            String description,
            List<ProjectSet> projectSets) {
        this.code = code;
        this.description = description;
        this.projectSets = projectSets;
    }

    public static SyncSetup loadAndValidate(ModelNode setupNode) {
        if (!setupNode.hasDefined(CODE)) {
            throw new SyncValidationException("The setup must have a '" + CODE + "' set to a string");
        }
        String code = setupNode.get(CODE).asString();

        if (!setupNode.hasDefined(DESCRIPTION)) {
            throw new SyncValidationException("The setup must have a '" + DESCRIPTION + "' set to a string");
        }
        String description = setupNode.get(DESCRIPTION).asString();

        List<ProjectSet> projectSets = new ArrayList<>();
        for (ModelNode ps : loadList(setupNode, PROJECT_SETS)) {
            projectSets.add(ProjectSet.load(ps));
        }

        return new SyncSetup(
                code,
                description,
                Collections.unmodifiableList(projectSets));
    }

    private static List<ModelNode> loadList(ModelNode setupNode, String key) {
        if (setupNode.hasDefined(key)) {
            ModelNode child = setupNode.get(key);
            if (child.getType() == ModelType.LIST) {
                List<ModelNode> list = child.asList();
                if (list.size() > 0) {
                    return list;
                }
            }
        }
        throw new SyncValidationException("The setup must have a '" + key + "' child, of type array, with at least one entry");
    }

    public ModelNode serializeModelNodeForConfig() {
        ModelNode result = new ModelNode();
        result.get(CODE).set(code);
        result.get(DESCRIPTION).set(description);
        result.get(PROJECT_SETS).setEmptyList();
        for (ProjectSet set : projectSets) {
            result.get(PROJECT_SETS).add(set.serializeModelNodeForConfig());
        }
        return result;
    }
}
