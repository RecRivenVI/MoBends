package goblinbob.mobends.fabric.main;

import goblinbob.mobends.api.platform.PlatformServices;
import goblinbob.mobends.api.player.IPlayerSkinProvider;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.addon.AddonHelper;
import goblinbob.mobends.core.client.ClientRuntime;
import goblinbob.mobends.fabric.CoreClient;
import goblinbob.mobends.fabric.client.event.RenderingEventHandler;
import goblinbob.mobends.fabric.network.FabricNetwork;
import goblinbob.mobends.fabric.platform.FabricPlatformServices;
import goblinbob.mobends.fabric.player.FabricPlayerSkinProvider;
import goblinbob.mobends.standard.DefaultAddon;
import goblinbob.mobends.standard.main.ModStatics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

/** Fabric 物理客户端入口。 */
public final class MoBendsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PlatformServices.set(new FabricPlatformServices());
        IPlayerSkinProvider.Holder.setProvider(new FabricPlayerSkinProvider());

        CoreClient.createAsClient();
        Core.getInstance().onClientSetup();
        AddonHelper.registerAddon(ModStatics.MODID, new DefaultAddon());
        Core.getInstance().applyConfigurationToEntityBenders();
        FabricNetwork.registerClient();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(
                        new SimpleSynchronousResourceReloadListener() {
                            @Override
                            public Identifier getFabricId() {
                                return ModStatics.getResource("animation_resources");
                            }

                            @Override
                            public void onResourceManagerReload(ResourceManager resourceManager) {
                                ClientRuntime.refreshSystems();
                            }
                        });

        RenderingEventHandler.register();

        MoBends.LOG.info("Mo' Bends client setup complete on Fabric");
    }
}
