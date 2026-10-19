package io.github.recrivenvi.mobends.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.lib.math.Quaternion;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;

public class LegacyGlHelperTest {
    private static final float EPSILON = 0.000001F;

    @Test
    public void zeroQuaternionRestPoseKeepsThePoseMatrixIntact() {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(2.0F, 3.0F, 4.0F);
        Matrix4f before = new Matrix4f(poseStack.last().pose());

        GlHelper.rotate(poseStack, new Quaternion(0.0F, 0.0F, 0.0F, 0.0F));

        Matrix4f after = poseStack.last().pose();
        for (int column = 0; column < 4; ++column) {
            for (int row = 0; row < 4; ++row) {
                assertEquals(before.get(column, row), after.get(column, row), EPSILON);
            }
        }
    }
}
