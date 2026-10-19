package io.github.recrivenvi.mobends.fabric.network;

import io.github.recrivenvi.mobends.core.network.ServerNetworkConfig;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

/** Optional server policy transport; no client classes are resolved on a dedicated server. */
public final class FabricNetworkCommon {
    private static ServerNetworkConfig serverConfig;

    private FabricNetworkCommon() {}

    public static void registerCommon() {
        serverConfig = ServerNetworkConfig.load(FabricLoader.getInstance().getConfigDir());
        PayloadTypeRegistry.clientboundPlay()
                .register(NetworkConfigPayload.TYPE, NetworkConfigPayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> {
                    if (ServerPlayNetworking.canSend(handler, NetworkConfigPayload.TYPE)) {
                        ServerPlayNetworking.send(handler.getPlayer(), payload());
                    }
                });
    }

    private static NetworkConfigPayload payload() {
        return new NetworkConfigPayload(
                serverConfig.isModelScalingAllowed(),
                serverConfig.areBendsPacksAllowed(),
                serverConfig.isMovementLimited());
    }
}
