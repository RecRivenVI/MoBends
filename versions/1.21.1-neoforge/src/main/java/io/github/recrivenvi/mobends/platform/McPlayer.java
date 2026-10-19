package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.entity.IPlayer;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Shared implementation of IPlayer. Wraps Minecraft's Player with a platform-agnostic interface.
 * Uses VersionAdapter for version-specific API differences.
 */
public class McPlayer extends McLivingEntity implements IPlayer {
    private final Player player;

    public McPlayer(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public UUID getUUID() {
        return player.getUUID();
    }

    @Override
    public String getName() {
        return player.getName().getString();
    }

    @Override
    public boolean hasSlimArms() {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            String model = VersionAdapter.Holder.get().getPlayerModelName(clientPlayer);
            return "slim".equals(model);
        }
        return false;
    }

    @Override
    public boolean isFlying() {
        return player.getAbilities().flying;
    }

    @Override
    public boolean isCreative() {
        return player.isCreative();
    }

    @Override
    public boolean isSpectator() {
        return player.isSpectator();
    }

    @Override
    public boolean isSprinting() {
        return player.isSprinting();
    }

    @Override
    public boolean isBlocking() {
        return player.isBlocking();
    }

    @Override
    public boolean isClimbing() {
        return player.onClimbable();
    }

    @Override
    public int getFoodLevel() {
        return player.getFoodData().getFoodLevel();
    }

    @Override
    public int getExperienceLevel() {
        return player.experienceLevel;
    }

    @Override
    public boolean isLocalPlayer() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getUUID().equals(player.getUUID());
    }

    @Override
    public int getCapeAnimationTick() {
        return player.tickCount;
    }

    @Override
    public double getPrevCapeX() {
        return player.xOld;
    }

    @Override
    public double getPrevCapeY() {
        return player.yOld;
    }

    @Override
    public double getPrevCapeZ() {
        return player.zOld;
    }

    @Override
    public double getCapeX() {
        return player.getX();
    }

    @Override
    public double getCapeY() {
        return player.getY();
    }

    @Override
    public double getCapeZ() {
        return player.getZ();
    }

    public Player getPlayer() {
        return player;
    }
}
