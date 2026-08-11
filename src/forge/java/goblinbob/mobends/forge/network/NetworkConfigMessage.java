package goblinbob.mobends.forge.network;

import net.minecraft.network.FriendlyByteBuf;

/** Server policy carried only over the active Minecraft play connection. */
public record NetworkConfigMessage(
        boolean modelScalingAllowed, boolean bendsPacksAllowed, boolean movementLimited) {
    public static void encode(NetworkConfigMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.modelScalingAllowed);
        buffer.writeBoolean(message.bendsPacksAllowed);
        buffer.writeBoolean(message.movementLimited);
    }

    public static NetworkConfigMessage decode(FriendlyByteBuf buffer) {
        return new NetworkConfigMessage(
                buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }
}
