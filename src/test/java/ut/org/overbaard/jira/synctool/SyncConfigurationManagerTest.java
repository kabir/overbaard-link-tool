package ut.org.overbaard.jira.synctool;

import static org.overbaard.jira.synctool.impl.Constants.CODE;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.overbaard.jira.synctool.api.SyncConfigurationManager;
import org.overbaard.jira.synctool.impl.SyncConfigurationManagerBuilder;
import org.overbaard.jira.synctool.impl.config.SyncSetup;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SyncConfigurationManagerTest {

    @Test
    public void testLoadAndSaveValidConfig() throws Exception {
        String config = "config/initial-config.json";
        SyncConfigurationManagerBuilder cfgManagerBuilder = new SyncConfigurationManagerBuilder()
                .addConfigActiveObjectsFromFile(config);
        SyncConfigurationManager cfgManager = cfgManagerBuilder.build();
        ModelNode original = SyncConfigurationManagerBuilder.loadConfig(config);
        original.protect();
        String code = original.get(CODE).asString();

        SyncSetup syncSetup = cfgManager.getSyncSetupForDisplay(null, code);
        Assert.assertNotNull(syncSetup);
        ModelNode serialized = syncSetup.serializeModelNodeForConfig();
        Assert.assertEquals(original, serialized);


    }

    private ModelNode loadConfig(String resource) throws Exception {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource);
        Assert.assertNotNull(resource, in);
        try (InputStream bin = new BufferedInputStream(in)){
            return ModelNode.fromJSONStream(bin);
        }
    }
}
