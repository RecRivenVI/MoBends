// ? if target_26_2_fabric {
/*package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.fabric.client.render.MoBendsRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 在模型真正绘制时恢复对应实体的动画。保留原版模型提交阶段建立的 描边、纹理图集包装与破坏覆盖顶点消费者。
@Mixin(ModelFeatureRenderer.class)
public abstract class ModelFeatureRendererMixin {
    @Redirect(
            method = "prepareModel",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private <S> void mobends$renderMutatedModel(
            Model<? super S> model,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color,
            ModelFeatureRenderer.Submit<S> submit) {
        if (model instanceof net.minecraft.client.model.EntityModel<?> entityModel
                && submit.state() instanceof LivingEntityRenderState livingState
                && MoBendsRenderState.renderMutatedModel(
                        entityModel,
                        livingState,
                        poseStack,
                        vertexConsumer,
                        packedLight,
                        packedOverlay,
                        color)) {
            return;
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
*/
// ?} else if target_26_1_2_neoforge {
package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.neoforge.client.render.MoBendsRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 在模型真正绘制时恢复对应实体的动画，避免延迟提交导致多个实体共用最后一个姿态。
@Mixin(ModelFeatureRenderer.class)
public abstract class ModelFeatureRendererMixin {
    @Redirect(
            method = "renderModel",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private <S> void mobends$renderMutatedModel(
            Model<? super S> model,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color,
            SubmitNodeStorage.ModelSubmit<S> submit,
            RenderType renderType,
            VertexConsumer originalBuffer,
            OutlineBufferSource outlineBufferSource,
            MultiBufferSource.BufferSource crumblingBufferSource) {
        if (model instanceof net.minecraft.client.model.EntityModel<?> entityModel
                && submit.state() instanceof LivingEntityRenderState livingState
                && MoBendsRenderState.renderMutatedModel(
                        entityModel,
                        livingState,
                        poseStack,
                        vertexConsumer,
                        packedLight,
                        packedOverlay,
                        color)) {
            return;
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
// ?} else if target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.neoforge.client.render.MoBendsRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Restores the mutated model when Minecraft 26.2 submits its prepared feature.
@Mixin(ModelFeatureRenderer.class)
public abstract class ModelFeatureRendererMixin {
    @Redirect(
            method = "prepareModel",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/model/Model;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
    private <S> void mobends$renderMutatedModel(
            Model<? super S> model,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color,
            ModelFeatureRenderer.Submit<S> submit) {
        if (model instanceof net.minecraft.client.model.EntityModel<?> entityModel
                && submit.state() instanceof LivingEntityRenderState livingState
                && MoBendsRenderState.renderMutatedModel(
                        entityModel,
                        livingState,
                        poseStack,
                        vertexConsumer,
                        packedLight,
                        packedOverlay,
                        color)) {
            return;
        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}*/
// ?}
