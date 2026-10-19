package io.github.recrivenvi.mobends.standard.animation.bit.skeleton;

import io.github.recrivenvi.mobends.standard.data.SkeletonData;
import net.minecraft.util.Mth;

public class WalkAnimationBit
        extends io.github.recrivenvi.mobends.standard.animation.bit.biped.WalkAnimationBit<
                SkeletonData> {
    @Override
    public void perform(SkeletonData data) {
        super.perform(data);

        if (data.isStrafing()) {
            final float PI = (float) Math.PI;
            float limbSwing = data.limbSwing.get() * 0.6662F;

            float legSwingAmount = 0.7F * data.limbSwingAmount.get() / PI * 180F;
            data.rightLeg
                    .rotation
                    .setSmoothness(1.0F)
                    .orientZ(-5F + Mth.cos(limbSwing) * legSwingAmount);
            data.leftLeg
                    .rotation
                    .setSmoothness(1.0F)
                    .orientZ(-5F + Mth.cos(limbSwing + PI) * legSwingAmount);
        }
    }
}
