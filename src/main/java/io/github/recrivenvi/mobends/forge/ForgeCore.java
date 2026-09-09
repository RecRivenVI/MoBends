//? if target_1_20_1_forge {
/*package io.github.recrivenvi.mobends.forge;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.core.Core;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.configuration.CoreClientConfig;
import io.github.recrivenvi.mobends.core.pack.PackManager;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

// Client-side Core implementation for Mo' Bends Forge.
@OnlyIn(Dist.CLIENT)
public class ForgeCore extends Core {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ForgeCore INSTANCE;

    private CoreClientConfig configuration;

    ForgeCore() {
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
        // Initialize pack manager
        PackManager.INSTANCE.initialize(configuration);

        LOGGER.info("Mo' Bends Forge core initialized");
    }

    @Override
    public void applyConfigurationToEntityBenders() {
        LOGGER.info("Applying configuration to entity benders");
        EntityBenderRegistry.instance.applyConfiguration(configuration);
    }

    @Nullable public static ForgeCore getInstance() {
        return INSTANCE;
    }

    // Creates the client core instance.
    public static void createAsClient() {
        new ForgeCore();
    }
}*/
//?}
