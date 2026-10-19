// ? if target_26_2_fabric {
package io.github.recrivenvi.mobends.fabric.network;

import io.github.recrivenvi.mobends.core.network.NetworkConfiguration;
import io.github.recrivenvi.mobends.core.network.ServerNetworkConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

// Loader transport for the original server-controlled Mo' Bends policy.
public final class FabricNetwork {
    private static ServerNetworkConfig serverConfig;

    private FabricNetwork() {}

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

    public static void registerClient() {
        ClientPlayConnectionEvents.JOIN.register(
                (handler, sender, client) ->
                        NetworkConfiguration.INSTANCE.onWorldJoin(
                                handler.getConnection().isMemoryConnection()));
        ClientPlayNetworking.registerGlobalReceiver(
                NetworkConfigPayload.TYPE,
                (payload, context) ->
                        NetworkConfiguration.INSTANCE.applyServerValues(
                                payload.modelScalingAllowed(),
                                payload.bendsPacksAllowed(),
                                payload.movementLimited()));
    }

    private static NetworkConfigPayload payload() {
        return new NetworkConfigPayload(
                serverConfig.isModelScalingAllowed(),
                serverConfig.areBendsPacksAllowed(),
                serverConfig.isMovementLimited());
    }
}
// ?}
