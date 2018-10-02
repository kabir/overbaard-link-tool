package org.overbaard.jira.synctool.impl.config;

import static org.overbaard.jira.synctool.impl.Constants.LEFT;
import static org.overbaard.jira.synctool.impl.Constants.PROJECT_SETS;
import static org.overbaard.jira.synctool.impl.Constants.RIGHT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.overbaard.jira.synctool.SyncValidationException;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ProjectSet {

    private final List<ProjectConfig> leftProjects;
    private final List<ProjectConfig> rightProjects;

    private ProjectSet(
            List<ProjectConfig> leftProjects,
            List<ProjectConfig> rightProjects) {
        this.leftProjects = leftProjects;
        this.rightProjects = rightProjects;
    }

    static ProjectSet load(ModelNode modelNode) {
        List<ProjectConfig> left = loadSetSide(modelNode, LEFT);
        List<ProjectConfig> right = loadSetSide(modelNode, RIGHT);

        // 1:1, 1:* and *:1 are valid. *:* is not. loadSetSide() validates each has a size bigger than 1
        if (left.size() > 1 && right.size() > 1) {
            throw new SyncValidationException("" +
                    "You can not have more than one entry for both '" + LEFT + "' and '" + RIGHT + "'");
        }

        return new ProjectSet(
                Collections.unmodifiableList(left),
                Collections.unmodifiableList(right));
    }

    private static List<ProjectConfig> loadSetSide(ModelNode modelNode, String key) {
        if (modelNode.hasDefined(key)) {
            ModelNode sideNode = modelNode.get(key);
            if (sideNode.getType() == ModelType.LIST) {
                List<ModelNode> list = sideNode.asList();
                if (list.size() > 0) {
                    List<ProjectConfig> projectConfigs = new ArrayList<>();
                    for (ModelNode prNode : list) {
                        projectConfigs.add(ProjectConfig.load(key, prNode));
                    }
                    return projectConfigs;
                }
            }
        }
        throw new SyncValidationException("All '" + PROJECT_SETS + "' entries must contain a '" + key + "' array");
    }

    public ModelNode serializeModelNodeForConfig() {
        ModelNode result = new ModelNode();
        result.get(LEFT).set(serializeSetSideForConfig(leftProjects));
        result.get(RIGHT).set(serializeSetSideForConfig(rightProjects));
        return result;
    }

    private ModelNode serializeSetSideForConfig(List<ProjectConfig> projects) {
        ModelNode result = new ModelNode().setEmptyList();
        for (ProjectConfig projectConfig : projects) {
            result.add(projectConfig.serializeModelNodeForConfig());
        }
        return result;
    }
}
