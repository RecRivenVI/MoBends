package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.forge.mixin.MixinBridge;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//
// Mixin to intercept AgeableListModel rendering and redirect to MoBends custom rendering when a
// mutation is active for HumanoidModel instances.
//
// <p>MC 1.20.1: renderToBuffer uses float RGBA instead of packed int color
//
@Mixin(AgeableListModel.class)
public abstract class AgeableListModelMixin<T extends LivingEntity> {

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
        // Check for HumanoidModel (biped entities)
        if ((Object) this instanceof HumanoidModel) {
            if (MixinBridge.shouldRenderBipedCustom()) {
                MixinBridge.renderBipedMutated(
                        poseStack,
                        vertexConsumer,
                        packedLight,
                        packedOverlay,
                        red,
                        green,
                        blue,
                        alpha);
                ci.cancel();
            }
        } else if ((Object) this instanceof WolfModel && MixinBridge.shouldRenderWolfCustom(this)) {
            // ColorableAgeableListModel has already multiplied the wet-shade tint
            // before delegating here. WolfMutator reproduces the skipped child layout.
            MixinBridge.renderWolfMutated(
                    poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            ci.cancel();
        }
    }
}
