//? if target_26_2_fabric {
package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.fabric.client.render.MoBendsRenderState;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.ArrowTrailManager;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowRenderer.class)
public abstract class ArrowRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void mobends$captureArrow(
            AbstractArrow arrow,
            ArrowRenderState state,
            float partialTick,
            CallbackInfo callbackInfo) {
        ((net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState) state)
                .setData(
                        MoBendsRenderState.ARROW,
                        new MoBendsRenderState.ArrowContext(arrow, partialTick));
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void mobends$submitArrowTrail(
            ArrowRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo callbackInfo) {
        MoBendsRenderState.ArrowContext context =
                ((net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState) state)
                        .getData(MoBendsRenderState.ARROW);
        if (context != null && ModConfig.showArrowTrails) {
            ArrowTrailManager.submitTrail(
                    context.arrow(), context.partialTick(), poseStack, submitNodeCollector);
        }
    }
}
//?} else if target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.neoforge.client.render.MoBendsRenderState;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.ArrowTrailManager;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowRenderer.class)
public abstract class ArrowRendererMixin {
    @Inject(method = "submit", at = @At("HEAD"))
    private void mobends$submitArrowTrail(
            ArrowRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo callbackInfo) {
        AbstractArrow arrow = state.getRenderData(MoBendsRenderState.ARROW);
        if (arrow != null && ModConfig.showArrowTrails) {
            ArrowTrailManager.submitTrail(arrow, state.partialTick, poseStack, submitNodeCollector);
        }
    }
}*/
//?}
