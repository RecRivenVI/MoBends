//? if target_1_20_1_forge {
/*package io.github.recrivenvi.mobends.forge;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.api.platform.PlatformServices;
import io.github.recrivenvi.mobends.api.player.IPlayerSkinProvider;
import io.github.recrivenvi.mobends.core.Core;
import io.github.recrivenvi.mobends.core.addon.AddonHelper;
import io.github.recrivenvi.mobends.core.client.ClientRuntime;
import io.github.recrivenvi.mobends.core.client.gui.MoBendsMenuScreen;
import io.github.recrivenvi.mobends.core.network.NetworkConfiguration;
import io.github.recrivenvi.mobends.forge.client.event.RenderingEventHandler;
import io.github.recrivenvi.mobends.forge.network.ForgeNetwork;
import io.github.recrivenvi.mobends.forge.platform.ForgePlatformServices;
import io.github.recrivenvi.mobends.forge.player.ForgePlayerSkinProvider;
import io.github.recrivenvi.mobends.standard.DefaultAddon;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

// Mo' Bends Forge entry point for Minecraft 1.20.1
@Mod(MoBendsForge.MOD_ID)
public class MoBendsForge {
    public static final String MOD_ID = "mobends";

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Logger LOG = LOGGER;
    public static MoBendsForge instance;

    public MoBendsForge() {
        instance = this;

        FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = loadingContext.getModEventBus();
        ForgeNetwork.register();

        // Register lifecycle event listeners
        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::clientSetup);
            loadingContext
                    .getContainer()
                    .registerExtensionPoint(
                            ConfigScreenHandler.ConfigScreenFactory.class,
                            () ->
                                    new ConfigScreenHandler.ConfigScreenFactory(
                                            MoBendsMenuScreen::new));
        }

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Mo' Bends {} initializing...", ModStatics.VERSION);
    }

    // Common setup - runs on both client and server.
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Mo' Bends common setup");
    }

    // Client-specific setup.
    private void clientSetup(final FMLClientSetupEvent event) {
        // Initialize platform services FIRST (before Core)
        PlatformServices.set(new ForgePlatformServices());
        LOGGER.info(
                "Mo' Bends platform services initialized: {}",
                PlatformServices.get().getPlatformName());

        // Register player skin provider for slim arm detection
        IPlayerSkinProvider.Holder.setProvider(new ForgePlayerSkinProvider());

        // Initialize the Core for client
        ForgeCore.createAsClient();

        // Perform client setup on the Core (registers event handlers)
        Core.getInstance().onClientSetup();
        io.github.recrivenvi.mobends.standard.main.ModConfig.syncFromConfiguration();

        // Register the default addon (standard animations) - this registers entity benders
        AddonHelper.registerAddon(ModStatics.MODID, new DefaultAddon());

        // Apply configuration AFTER entity benders are registered
        Core.getInstance().applyConfigurationToEntityBenders();

        // Register Forge event handlers
        MinecraftForge.EVENT_BUS.register(new RenderingEventHandler());
        LOGGER.info("Mo' Bends rendering event handlers registered");

        LOGGER.info("Mo' Bends client setup complete");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Mo' Bends: Server starting");
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ForgeNetwork.sendTo(player);
        }
    }

    @SubscribeEvent
    public void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        NetworkConfiguration.INSTANCE.onWorldJoin(event.getConnection().isMemoryConnection());
    }

    // Used to refresh all systems, clear caches. Usually performed when configuration changes.
    public static void refreshSystems() {
        ClientRuntime.refreshSystems();
    }
}*/
//?}
