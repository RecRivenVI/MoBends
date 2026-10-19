package io.github.recrivenvi.mobends.standard.animation.bit.biped.item;

import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import io.github.recrivenvi.mobends.core.client.model.ModelPartTransform;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

/** Applies the current Minecraft arm-pose formulas to the segmented Mo' Bends arms. */
public final class VanillaUseAnimationBit extends AnimationBit<BipedEntityData<?>> {
    private static final float RADIANS_TO_DEGREES = 180.0F / (float) Math.PI;
    private final HumanoidArm actionHand;
    private final VanillaUsePose pose;

    public VanillaUseAnimationBit(HumanoidArm actionHand, VanillaUsePose pose) {
        this.actionHand = actionHand;
        this.pose = pose;
    }

    @Override
    public String[] getActions(BipedEntityData<?> data) {
        return new String[] {pose.actionName()};
    }

    @Override
    public void perform(BipedEntityData<?> data) {
        boolean rightHanded = actionHand == HumanoidArm.RIGHT;
        float direction = rightHanded ? 1.0F : -1.0F;
        ModelPartTransform mainArm = rightHanded ? data.rightArm : data.leftArm;
        ModelPartTransform offArm = rightHanded ? data.leftArm : data.rightArm;
        ModelPartTransform mainForeArm = rightHanded ? data.rightForeArm : data.leftForeArm;
        ModelPartTransform offForeArm = rightHanded ? data.leftForeArm : data.rightForeArm;
        float headPitch = data.headPitch.get();
        float headYaw = data.headYaw.get();
        LivingEntity entity = data.getEntity();

        switch (pose) {
            case CROSSBOW_CHARGE -> {
                float duration = crossbowChargeDuration(entity);
                float progress =
                        Mth.clamp(
                                        entity == null ? 0.0F : entity.getTicksUsingItem(),
                                        0.0F,
                                        duration)
                                / duration;
                float holdingX = -0.97079635F * RADIANS_TO_DEGREES;
                mainArm.rotation
                        .setSmoothness(1.0F)
                        .orientX(holdingX)
                        .rotateY(-0.8F * direction * RADIANS_TO_DEGREES);
                offArm.rotation
                        .setSmoothness(1.0F)
                        .orientX(Mth.lerp(progress, holdingX, -90.0F))
                        .rotateY(Mth.lerp(progress, 0.4F, 0.85F) * direction * RADIANS_TO_DEGREES);
                mainForeArm.rotation.orientZero();
                offForeArm.rotation.orientZero();
            }
            case CROSSBOW_HOLD -> {
                mainArm.rotation
                        .setSmoothness(1.0F)
                        .orientX(headPitch + (-0.5F * (float) Math.PI + 0.1F) * RADIANS_TO_DEGREES)
                        .rotateY(headYaw - 0.3F * direction * RADIANS_TO_DEGREES);
                offArm.rotation
                        .setSmoothness(1.0F)
                        .orientX(headPitch - 1.5F * RADIANS_TO_DEGREES)
                        .rotateY(headYaw + 0.6F * direction * RADIANS_TO_DEGREES);
                mainForeArm.rotation.orientZero();
                offForeArm.rotation.orientZero();
            }
            case THROW_SPEAR -> {
                float currentPitch = mainArm.rotation.getEndEulerDegrees()[0];
                mainArm.rotation.setSmoothness(0.8F).orientX(currentPitch * 0.5F - 180.0F);
                mainForeArm.rotation.orientZero();
            }
            case SPEAR -> {
                float pitch = headPitch - 90.0F + 0.8F * RADIANS_TO_DEGREES;
                if (entity != null && (entity.isFallFlying() || entity.isVisuallySwimming())) {
                    pitch -= 0.9599311F * RADIANS_TO_DEGREES;
                }
                pitch = Mth.clamp(pitch, -120.0F, 30.0F);
                float yaw =
                        Mth.clamp(headYaw - 0.1F * direction * RADIANS_TO_DEGREES, -60.0F, 60.0F);
                float roll = 0.0F;

                mainArm.rotation.setSmoothness(0.8F).orientX(pitch).rotateY(yaw);
                if (roll != 0.0F) {
                    mainArm.rotation.rotateZ(roll);
                }
                mainForeArm.rotation.orientZero();
            }
            case SPYGLASS -> {
                float crouchingOffset = entity != null && entity.isCrouching() ? 15.0F : 0.0F;
                mainArm.rotation
                        .setSmoothness(1.0F)
                        .orientX(
                                Mth.clamp(
                                        headPitch - 110.0F - crouchingOffset,
                                        -2.4F * RADIANS_TO_DEGREES,
                                        3.3F * RADIANS_TO_DEGREES))
                        .rotateY(headYaw - 15.0F * direction);
                mainForeArm.rotation.orientZero();
            }
            case TOOT_HORN -> {
                mainArm.rotation
                        .setSmoothness(1.0F)
                        .orientX(
                                Mth.clamp(
                                                headPitch,
                                                -1.2F * RADIANS_TO_DEGREES,
                                                1.2F * RADIANS_TO_DEGREES)
                                        - 85.0F)
                        .rotateY(headYaw - 30.0F * direction);
                mainForeArm.rotation.orientZero();
            }
            case BRUSH -> {
                float currentPitch = mainArm.rotation.getEndEulerDegrees()[0];
                mainArm.rotation
                        .setSmoothness(0.8F)
                        .orientX(currentPitch * 0.5F - 36.0F)
                        .rotateY(0.0F);
                mainForeArm.rotation.orientZero();
            }
        }
    }

    private static float crossbowChargeDuration(LivingEntity entity) {
        if (entity == null) {
            return 25.0F;
        }

        ItemStack itemStack = entity.getUseItem();

        return Math.max(1.0F, CrossbowItem.getChargeDuration(itemStack, entity));
    }
}
