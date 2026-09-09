//? if target_1_21_1_neoforge || target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.network;

import io.github.recrivenvi.mobends.core.network.NetworkConfiguration;
import io.github.recrivenvi.mobends.core.network.ServerNetworkConfig;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// NeoForge transport for the original server-controlled Mo' Bends policy.
public final class NeoForgeNetwork {
    private static final ServerNetworkConfig SERVER_CONFIG =
            ServerNetworkConfig.load(FMLPaths.CONFIGDIR.get());

    private NeoForgeNetwork() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .optional()
                .playToClient(
                        NetworkConfigPayload.TYPE,
                        NetworkConfigPayload.CODEC,
                        (payload, context) ->
                                NetworkConfiguration.INSTANCE.applyServerValues(
                                        payload.modelScalingAllowed(),
                                        payload.bendsPacksAllowed(),
                                        payload.movementLimited()));
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.connection.hasChannel(NetworkConfigPayload.TYPE)) {
            PacketDistributor.sendToPlayer(player, payload());
        }
    }

    private static NetworkConfigPayload payload() {
        return new NetworkConfigPayload(
                SERVER_CONFIG.isModelScalingAllowed(),
                SERVER_CONFIG.areBendsPacksAllowed(),
                SERVER_CONFIG.isMovementLimited());
    }
}*/
//?}
