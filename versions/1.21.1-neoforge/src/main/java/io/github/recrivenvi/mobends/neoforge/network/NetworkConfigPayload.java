package io.github.recrivenvi.mobends.neoforge.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Server policy carried only over the active Minecraft play connection.
public record NetworkConfigPayload(
        boolean modelScalingAllowed, boolean bendsPacksAllowed, boolean movementLimited)
        implements CustomPacketPayload {
    public static final Type<NetworkConfigPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mobends", "network_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NetworkConfigPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    NetworkConfigPayload::modelScalingAllowed,
                    ByteBufCodecs.BOOL,
                    NetworkConfigPayload::bendsPacksAllowed,
                    ByteBufCodecs.BOOL,
                    NetworkConfigPayload::movementLimited,
                    NetworkConfigPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
