// ? if target_26_2_fabric {
package io.github.recrivenvi.mobends.fabric.client.event;

import io.github.recrivenvi.mobends.core.addon.Addons;
import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.core.data.EntityDatabase;
import io.github.recrivenvi.mobends.core.util.BenderHelper;
import io.github.recrivenvi.mobends.lib.flux.ComputedDependencyHelper;
import io.github.recrivenvi.mobends.standard.mutators.PlayerMutator;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Entity;

// Fabric 客户端动画时钟与第一人称渲染钩子。
public final class RenderingEventHandler {
    private RenderingEventHandler() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {
                    if (client.player == null || client.isPaused()) {
                        return;
                    }
                    EntityDatabase.instance.updateClient();
                    Addons.onClientTick();
                });

        LevelRenderEvents.START_MAIN.register(
                context ->
                        updateRenderTick(
                                Minecraft.getInstance()
                                        .getDeltaTracker()
                                        .getGameTimeDeltaPartialTick(false)));
    }

    private static void updateRenderTick(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        ComputedDependencyHelper.reevaluateDirty();
        if (!minecraft.isPaused()) {
            DataUpdateHandler.partialTicks = partialTick;
        }

        float ticks = minecraft.player.tickCount + partialTick;
        if (DataUpdateHandler.checkTicksRestart(ticks)) {
            EntityDatabase.instance.onTicksRestart();
        }

        if (!minecraft.isPaused()) {
            DataUpdateHandler.update(partialTick, ticks);
            EntityDatabase.instance.updateRender(partialTick);
            Addons.onRenderTick(partialTick);
        } else {
            DataUpdateHandler.onPaused();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void beforeHandRender() {
        Minecraft minecraft = Minecraft.getInstance();
        Entity viewEntity = minecraft.getCameraEntity();
        if (!(viewEntity instanceof AbstractClientPlayer player)
                || !BenderHelper.isEntityAnimated(player)) {
            return;
        }

        AvatarRenderer renderer =
                (AvatarRenderer) minecraft.getEntityRenderDispatcher().getRenderer(player);
        EntityBender bender = EntityBenderRegistry.instance.getForEntity(player);
        if (bender == null
                || !bender.applyMutation(renderer, player, DataUpdateHandler.partialTicks)) {
            return;
        }
        PlayerMutator mutator =
                (PlayerMutator)
                        BenderHelper.getMutatorForRenderer(AbstractClientPlayer.class, renderer);
        if (mutator != null) {
            mutator.poseForFirstPersonView();
        }
    }
}
// ?}
