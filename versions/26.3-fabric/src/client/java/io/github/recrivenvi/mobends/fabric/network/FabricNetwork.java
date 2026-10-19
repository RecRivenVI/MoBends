package io.github.recrivenvi.mobends.fabric.network;

import io.github.recrivenvi.mobends.core.network.NetworkConfiguration;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

// Loader transport for the original server-controlled Mo' Bends policy.
public final class FabricNetwork {
    private FabricNetwork() {}

    public static void registerCommon() {
        FabricNetworkCommon.registerCommon();
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
}
