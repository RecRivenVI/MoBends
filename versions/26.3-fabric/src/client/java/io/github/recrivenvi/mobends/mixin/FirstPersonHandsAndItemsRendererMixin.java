package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.fabric.client.event.RenderingEventHandler;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 在原版提交双手前同步第一人称姿态；26.3 将手部渲染数据独立为 RenderState。
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonHandsAndItemsRendererMixin {
    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void mobends$prepareFirstPersonPose(
            float partialTick,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            PlayerRenderState player,
            FirstPersonHandsAndItemsRenderState hands,
            CallbackInfo callbackInfo) {
        RenderingEventHandler.beforeHandRender();
    }
}
