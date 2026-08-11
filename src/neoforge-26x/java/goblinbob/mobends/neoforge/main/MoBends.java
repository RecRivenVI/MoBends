package goblinbob.mobends.neoforge.main;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.api.platform.PlatformServices;
import goblinbob.mobends.api.player.IPlayerSkinProvider;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.addon.AddonHelper;
import goblinbob.mobends.core.client.ClientRuntime;
import goblinbob.mobends.core.client.gui.MoBendsMenuScreen;
import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.neoforge.CoreClient;
import goblinbob.mobends.neoforge.client.event.RenderingEventHandler;
import goblinbob.mobends.neoforge.network.NeoForgeNetwork;
import goblinbob.mobends.neoforge.platform.NeoForgePlatformServices;
import goblinbob.mobends.neoforge.player.NeoForgePlayerSkinProvider;
import goblinbob.mobends.standard.DefaultAddon;
import goblinbob.mobends.standard.main.ModStatics;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/** Main entry point for the Mo' Bends mod. Ported to Minecraft 1.20.1 with Forge. */
@Mod(MoBends.MODID)
public class MoBends {
    public static final String MODID = "mobends";

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Logger LOG = LOGGER;
    public static MoBends instance;

    public MoBends(IEventBus modEventBus, ModContainer container) {
        instance = this;

        // Client-side setup
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(RenderingEventHandler::registerRenderStateModifiers);
            modEventBus.addListener(this::addClientReloadListeners);
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

    /** Common setup - runs on both client and server. */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Mo' Bends common setup complete");
    }

    /** Client-specific setup. */
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

        // Register the default addon (standard animations) - this registers entity benders
        AddonHelper.registerAddon(ModStatics.MODID, new DefaultAddon());

        // Apply configuration AFTER entity benders are registered
        Core.getInstance().applyConfigurationToEntityBenders();

        // Register NeoForge event handlers
        NeoForge.EVENT_BUS.register(new RenderingEventHandler());
        LOGGER.info("Mo' Bends rendering event handlers registered");

        LOGGER.info("Mo' Bends client setup complete");
    }

    private void addClientReloadListeners(final AddClientReloadListenersEvent event) {
        event.addListener(
                ModStatics.getResource("animation_resources"),
                (ResourceManagerReloadListener) resourceManager -> ClientRuntime.refreshSystems());
    }

    /** Used to refresh all systems, clear caches. Usually performed when configuration changes. */
}
