package goblinbob.mobends.neoforge;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.event.*;
import goblinbob.mobends.core.configuration.CoreClientConfig;
import goblinbob.mobends.core.pack.PackManager;
import goblinbob.mobends.standard.main.ModConfig;
import javax.annotation.Nullable;
import org.slf4j.Logger;

/** Client-side Core implementation for Mo' Bends NeoForge. */
public class CoreClient extends Core {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static CoreClient INSTANCE;

    private CoreClientConfig configuration;

    CoreClient() {
        INSTANCE = this;
        Core.instance = this;
        this.configuration = CoreClientConfig.getInstance();
    }

    public CoreClientConfig getConfiguration() {
        return configuration;
    }

    @Override
    public void onClientSetup() {
        // Initialize configuration (requires Minecraft to be initialized)
        configuration.initialize();
        ModConfig.syncFromConfiguration();

        // Initialize pack manager
        PackManager.INSTANCE.initialize(configuration);

        LOGGER.info("Mo' Bends client core initialized");
    }

    @Override
    public void applyConfigurationToEntityBenders() {
        LOGGER.info("Applying configuration to entity benders");
        EntityBenderRegistry.instance.applyConfiguration(configuration);
    }

    @Nullable public static CoreClient getInstance() {
        return INSTANCE;
    }

    /** Creates the client core instance. */
    public static void createAsClient() {
        new CoreClient();
    }
}
