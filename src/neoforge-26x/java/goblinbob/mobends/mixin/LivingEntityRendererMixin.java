package goblinbob.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.neoforge.client.render.MoBendsRenderState;
import goblinbob.mobends.standard.main.ModStatics;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 原版在提交各渲染层前会再次运行 setupAnim；在该点恢复弯曲姿态， 让手持物、头部物品及其他依赖父模型的层捕获正确的矩阵。 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(
            method = "submit",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
                            ordinal = 1,
                            shift = At.Shift.AFTER))
    private void mobends$submitWolfMisc(
            LivingEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo callbackInfo) {
        MoBendsRenderState.Context context = state.getRenderData(MoBendsRenderState.CONTEXT);
        if (context == null || state.isInvisible) {
            return;
        }

        LivingEntity entity = context.entity();
        LivingEntityRenderer renderer = context.renderer();
        EntityBender bender = context.bender();
        if (!(bender.getMutator(renderer) instanceof WolfMutator)) {
            return;
        }

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityCutout(ModStatics.getResource("textures/entity/wolf_misc.png")),
                (pose, vertexConsumer) -> {
                    if (!bender.restoreMutation(renderer, entity)
                            || !(bender.getMutator(renderer) instanceof WolfMutator wolfMutator)) {
                        return;
                    }
                    PoseStack renderingPose = new PoseStack();
                    renderingPose.last().set(pose);
                    wolfMutator.renderMisc(
                            renderingPose,
                            vertexConsumer,
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            -1);
                });
    }

    @Inject(
            method = "submit",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/model/EntityModel;setupAnim(Ljava/lang/Object;)V",
                            shift = At.Shift.AFTER))
    private void mobends$syncModelBeforeLayers(
            LivingEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo callbackInfo) {
        MoBendsRenderState.syncPrimaryModelForLayers(state);
    }
}
