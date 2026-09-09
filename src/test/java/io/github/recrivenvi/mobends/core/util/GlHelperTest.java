//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.lib.math.Quaternion;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

class GlHelperTest {
    @Test
    void zeroQuaternionKeepsLegacyIdentityTransform() {
        PoseStack poseStack = new PoseStack();
        Matrix4f poseBefore = new Matrix4f(poseStack.last().pose());
        Matrix3f normalBefore = new Matrix3f(poseStack.last().normal());

        GlHelper.rotate(poseStack, new Quaternion(0.0F, 0.0F, 0.0F, 0.0F));

        assertEquals(poseBefore, poseStack.last().pose());
        assertEquals(normalBefore, poseStack.last().normal());
    }
}
//?}
