// ? if target_1_20_1_forge {
/*package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.forge.mixin.MixinBridge;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//
// Mixin to intercept HierarchicalModel rendering and redirect to MoBends custom rendering when a
// spider mutation is active.
//
// <p>MC 1.20.1: renderToBuffer uses float RGBA instead of packed int color
//
@Mixin(HierarchicalModel.class)
public abstract class SpiderModelMixin<T extends Entity> {

    @Inject(method = "renderToBuffer", at = @At("HEAD"), cancellable = true)
    private void mobends$interceptRender(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha,
            CallbackInfo ci) {
        Object model = this;
        if (model instanceof SpiderModel && MixinBridge.shouldRenderSpiderCustom()) {
            MixinBridge.renderSpiderMutated(
                    poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            ci.cancel();
        } else if (model instanceof SquidModel && MixinBridge.shouldRenderSquidCustom()) {
            MixinBridge.renderSquidMutated(
                    poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            ci.cancel();
        }
    }
}*/
// ?} else if target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.neoforge.mixin.MixinBridge;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//
// Mixin to intercept HierarchicalModel rendering and redirect to MoBends custom rendering when a
// spider mutation is active.
//
@Mixin(HierarchicalModel.class)
public abstract class SpiderModelMixin<T extends Entity> {

    @Inject(method = "renderToBuffer", at = @At("HEAD"), cancellable = true)
    private void mobends$interceptRender(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color,
            CallbackInfo ci) {
        if ((Object) this instanceof SpiderModel && MixinBridge.shouldRenderSpiderCustom()) {
            MixinBridge.renderSpiderMutated(
                    poseStack, vertexConsumer, packedLight, packedOverlay, color);
            ci.cancel();
        } else if ((Object) this instanceof SquidModel && MixinBridge.shouldRenderSquidCustom()) {
            MixinBridge.renderSquidMutated(
                    poseStack, vertexConsumer, packedLight, packedOverlay, color);
            ci.cancel();
        }
    }
}*/
// ?}
