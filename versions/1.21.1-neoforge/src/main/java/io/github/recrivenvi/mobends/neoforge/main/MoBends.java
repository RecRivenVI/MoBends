package io.github.recrivenvi.mobends.neoforge.main;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.api.platform.PlatformServices;
import io.github.recrivenvi.mobends.api.player.IPlayerSkinProvider;
import io.github.recrivenvi.mobends.core.Core;
import io.github.recrivenvi.mobends.core.addon.AddonHelper;
import io.github.recrivenvi.mobends.core.client.ClientRuntime;
import io.github.recrivenvi.mobends.core.client.gui.MoBendsMenuScreen;
import io.github.recrivenvi.mobends.core.network.NetworkConfiguration;
import io.github.recrivenvi.mobends.neoforge.CoreClient;
import io.github.recrivenvi.mobends.neoforge.client.event.RenderingEventHandler;
import io.github.recrivenvi.mobends.neoforge.network.NeoForgeNetwork;
import io.github.recrivenvi.mobends.neoforge.platform.NeoForgePlatformServices;
import io.github.recrivenvi.mobends.neoforge.player.NeoForgePlayerSkinProvider;
import io.github.recrivenvi.mobends.standard.DefaultAddon;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

// Main entry point for the Mo' Bends mod. Ported to Minecraft 1.20.1 with Forge.
@Mod(MoBends.MODID)
public class MoBends {
    public static final String MODID = "mobends";

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Logger LOG = LOGGER;
    public static MoBends instance;

    public MoBends(IEventBus modEventBus, ModContainer container) {
        instance = this;

        // Client-side setup
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::clientSetup);
            NeoForge.EVENT_BUS.addListener(this::onClientLoggingIn);
            container.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (IConfigScreenFactory) (ignored, parent) -> new MoBendsMenuScreen(parent));
        }

        // Register lifecycle event listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(NeoForgeNetwork::registerPayloads);
        NeoForge.EVENT_BUS.addListener(NeoForgeNetwork::onPlayerLoggedIn);

        LOGGER.info("Mo' Bends {} initializing...", ModStatics.VERSION);
    }

    private void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        NetworkConfiguration.INSTANCE.onWorldJoin(event.getConnection().isMemoryConnection());
    }

    // Common setup - runs on both client and server.
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Mo' Bends common setup complete");
    }

    // Client-specific setup.
    private void clientSetup(final FMLClientSetupEvent event) {
        // Initialize platform services FIRST (before Core)
        PlatformServices.set(new NeoForgePlatformServices());
        LOGGER.info(
                "Mo' Bends platform services initialized: {}",
                PlatformServices.get().getPlatformName());

        // Register player skin provider for slim arm detection
        IPlayerSkinProvider.Holder.setProvider(new NeoForgePlayerSkinProvider());

        // Initialize the Core for client
        CoreClient.createAsClient();

        // Perform client setup on the Core (registers event handlers)
        Core.getInstance().onClientSetup();
        io.github.recrivenvi.mobends.standard.main.ModConfig.syncFromConfiguration();

        // Register the default addon (standard animations) - this registers entity benders
        AddonHelper.registerAddon(ModStatics.MODID, new DefaultAddon());

        // Apply configuration AFTER entity benders are registered
        Core.getInstance().applyConfigurationToEntityBenders();

        // Register NeoForge event handlers
        NeoForge.EVENT_BUS.register(new RenderingEventHandler());
        LOGGER.info("Mo' Bends rendering event handlers registered");

        LOGGER.info("Mo' Bends client setup complete");
    }

    // Used to refresh all systems, clear caches. Usually performed when configuration changes.
    public static void refreshSystems() {
        ClientRuntime.refreshSystems();
    }
}
