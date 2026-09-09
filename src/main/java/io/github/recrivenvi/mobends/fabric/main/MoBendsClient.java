//? if target_26_2_fabric {
package io.github.recrivenvi.mobends.fabric.main;

import io.github.recrivenvi.mobends.api.platform.PlatformServices;
import io.github.recrivenvi.mobends.api.player.IPlayerSkinProvider;
import io.github.recrivenvi.mobends.core.Core;
import io.github.recrivenvi.mobends.core.addon.AddonHelper;
import io.github.recrivenvi.mobends.core.client.ClientRuntime;
import io.github.recrivenvi.mobends.fabric.CoreClient;
import io.github.recrivenvi.mobends.fabric.client.event.RenderingEventHandler;
import io.github.recrivenvi.mobends.fabric.network.FabricNetwork;
import io.github.recrivenvi.mobends.fabric.platform.FabricPlatformServices;
import io.github.recrivenvi.mobends.fabric.player.FabricPlayerSkinProvider;
import io.github.recrivenvi.mobends.standard.DefaultAddon;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

// Fabric 物理客户端入口。
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
//?}
