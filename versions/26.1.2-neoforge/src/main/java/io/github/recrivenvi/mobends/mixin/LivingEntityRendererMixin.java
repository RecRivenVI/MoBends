// ? if target_26_2_fabric {
/*package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.client.RenderStateEntityContext;
import io.github.recrivenvi.mobends.core.client.TrailSubmitter;
import io.github.recrivenvi.mobends.core.data.LivingEntityData;
import io.github.recrivenvi.mobends.fabric.client.render.MoBendsRenderState;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import io.github.recrivenvi.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.model.EntityModel;
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

// 原版在提交各渲染层前会再次运行 setupAnim；在该点恢复弯曲姿态， 让手持物、头部物品及其他依赖父模型的层捕获正确的矩阵。
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void mobends$captureEntity(
            LivingEntity entity,
            LivingEntityRenderState state,
            float partialTick,
            CallbackInfo callbackInfo) {
        MoBendsRenderState.set(state, MoBendsRenderState.ENTITY, entity);
        RenderStateEntityContext.put(state, entity);
        MoBendsRenderState.set(state, MoBendsRenderState.PARTIAL_TICK, partialTick);
        MoBendsRenderState.set(state, MoBendsRenderState.CONTEXT, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "submit", at = @At("HEAD"))
    private void mobends$beforeLivingSubmit(
            LivingEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo callbackInfo) {
        LivingEntity entity = MoBendsRenderState.get(state, MoBendsRenderState.ENTITY);
        if (entity == null) {
            return;
        }

        LivingEntityRenderer renderer = (LivingEntityRenderer) (Object) this;
        EntityBender bender = EntityBenderRegistry.instance.getForEntity(entity);
        if (bender == null) {
            return;
        }

        if (!bender.isAnimated()) {
            bender.deapplyMutation(renderer, entity);
            return;
        }

        Float capturedPartialTick = MoBendsRenderState.get(state, MoBendsRenderState.PARTIAL_TICK);
        float partialTick = capturedPartialTick != null ? capturedPartialTick : 0.0F;
        if (!bender.applyMutation(renderer, entity, partialTick)) {
            return;
        }

        EntityModel<?> primaryModel = renderer.getModel();
        MoBendsRenderState.set(
                state,
                MoBendsRenderState.CONTEXT,
                new MoBendsRenderState.Context(
                        entity, renderer, bender, primaryModel, partialTick));
        poseStack.pushPose();
        LivingEntityData entityData =
                (LivingEntityData) bender.getMutator(renderer).getData(entity);
        if (ModConfig.showSwordTrail && entityData instanceof BipedEntityData<?> bipedData) {
            poseStack.pushPose();
            MutatedRenderer.applyEntityLocalRotation(
                    poseStack, entity.yBodyRotO, entity.yBodyRot, partialTick);
            poseStack.scale(0.0625f, 0.0625f, 0.0625f);
            TrailSubmitter.submit(
                    poseStack,
                    submitNodeCollector,
                    RenderTypes.debugQuads(),
                    bipedData.swordTrail::render);
            poseStack.popPose();
        }
        bender.beforeRender(entityData, entity, partialTick, poseStack);
    }

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
        MoBendsRenderState.Context context =
                MoBendsRenderState.get(state, MoBendsRenderState.CONTEXT);
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "submit", at = @At("RETURN"))
    private void mobends$afterLivingSubmit(
            LivingEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo callbackInfo) {
        MoBendsRenderState.Context context =
                MoBendsRenderState.get(state, MoBendsRenderState.CONTEXT);
        if (context == null) {
            return;
        }

        ((EntityBender) context.bender())
                .afterRender(context.entity(), context.partialTick(), poseStack);
        poseStack.popPose();
        // 26.2 会在实体 submit 返回之后才消费 ModelFeatureRenderer 的模型节点。
        // CONTEXT 必须随渲染状态保留到实际绘制阶段；下一次 extractRenderState
        // 会先将它清空，避免复用状态时携带上一实体的数据。
    }
}
*/
// ?} else if target_26_1_2_neoforge || target_26_2_neoforge {
package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.neoforge.client.render.MoBendsRenderState;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import io.github.recrivenvi.mobends.standard.mutators.WolfMutator;
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

// 原版在提交各渲染层前会再次运行 setupAnim；在该点恢复弯曲姿态， 让手持物、头部物品及其他依赖父模型的层捕获正确的矩阵。
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
// ?}
