package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.client.RenderStateEntityContext;
import io.github.recrivenvi.mobends.standard.animation.SwimmingPosePolicy;
import io.github.recrivenvi.mobends.standard.mutators.PlayerMutator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Replaces the rigid first-person arm with the segmented player arm.
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
    @Redirect(
            method =
                    "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;swimAmount:F"))
    private float mobends$selectSwimmingRootOwner(AvatarRenderState state) {
        LivingEntity entity = RenderStateEntityContext.get(state);
        EntityBender<?> bender =
                entity == null ? null : EntityBenderRegistry.instance.getForEntity(entity);
        boolean mobendsAnimated = bender != null && bender.isAnimated();
        return SwimmingPosePolicy.vanillaRootAmount(state.swimAmount, mobendsAnimated);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void mobends$renderMutatedFirstPersonArm(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            Identifier skinTexture,
            ModelPart armPart,
            boolean hasSleeve,
            CallbackInfo callbackInfo) {
        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        AvatarRenderer renderer = (AvatarRenderer) (Object) this;
        EntityBender bender = EntityBenderRegistry.instance.getForEntity(player);
        if (bender == null || !bender.isAnimated()) {
            return;
        }
        Object rawMutator = bender.getMutator((LivingEntityRenderer) renderer);
        if (!(rawMutator instanceof PlayerMutator mutator)) {
            return;
        }

        HumanoidArm arm =
                armPart == ((PlayerModel) renderer.getModel()).leftArm
                        ? HumanoidArm.LEFT
                        : HumanoidArm.RIGHT;
        mutator.poseForFirstPersonView();
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityTranslucent(skinTexture),
                (pose, vertexConsumer) -> {
                    if (!bender.restoreMutation((LivingEntityRenderer) renderer, player)) {
                        return;
                    }
                    Object activeMutator = bender.getMutator((LivingEntityRenderer) renderer);
                    if (!(activeMutator instanceof PlayerMutator currentMutator)) {
                        return;
                    }
                    currentMutator.poseForFirstPersonView();
                    PoseStack renderingPose = new PoseStack();
                    renderingPose.last().set(pose);
                    currentMutator.renderFirstPersonArm(
                            renderingPose,
                            vertexConsumer,
                            lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            -1,
                            arm,
                            hasSleeve);
                });
        callbackInfo.cancel();
    }
}
