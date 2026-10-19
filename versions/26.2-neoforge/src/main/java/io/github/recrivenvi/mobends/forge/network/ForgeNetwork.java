// ? if target_1_20_1_forge {
/*package io.github.recrivenvi.mobends.forge.network;

import io.github.recrivenvi.mobends.core.network.NetworkConfiguration;
import io.github.recrivenvi.mobends.core.network.ServerNetworkConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

// Forge transport for the original server-controlled Mo' Bends policy.
public final class ForgeNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation("mobends", "network_config"),
                    () -> PROTOCOL,
                    NetworkRegistry.acceptMissingOr(PROTOCOL::equals),
                    NetworkRegistry.acceptMissingOr(PROTOCOL::equals));
    private static final ServerNetworkConfig SERVER_CONFIG =
            ServerNetworkConfig.load(FMLPaths.CONFIGDIR.get());
    private static boolean registered;

    private ForgeNetwork() {}

    public static synchronized void register() {
        if (registered) {
            return;
        }
        CHANNEL.messageBuilder(NetworkConfigMessage.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(NetworkConfigMessage::encode)
                .decoder(NetworkConfigMessage::decode)
                .consumerMainThread(
                        (message, context) ->
                                NetworkConfiguration.INSTANCE.applyServerValues(
                                        message.modelScalingAllowed(),
                                        message.bendsPacksAllowed(),
                                        message.movementLimited()))
                .add();
        registered = true;
    }

    public static void sendTo(ServerPlayer player) {
        if (CHANNEL.isRemotePresent(player.connection.connection)) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload());
        }
    }

    private static NetworkConfigMessage payload() {
        return new NetworkConfigMessage(
                SERVER_CONFIG.isModelScalingAllowed(),
                SERVER_CONFIG.areBendsPacksAllowed(),
                SERVER_CONFIG.isMovementLimited());
    }
}*/
// ?}
