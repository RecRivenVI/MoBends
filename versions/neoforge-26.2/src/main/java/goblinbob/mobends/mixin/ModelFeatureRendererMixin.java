package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.neoforge.client.render.MoBendsRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Restores the mutated model when Minecraft 26.2 submits its prepared feature. */
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
