//? if target_26_2_fabric {
package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.fabric.client.event.RenderingEventHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fabric API 26.2 暂无手部提交事件，因此在原版提交双手前同步第一人称姿态。
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void mobends$prepareFirstPersonPose(
            float partialTick,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            LocalPlayer player,
            int packedLight,
            CallbackInfo callbackInfo) {
        RenderingEventHandler.beforeHandRender();
    }
}
//?}
