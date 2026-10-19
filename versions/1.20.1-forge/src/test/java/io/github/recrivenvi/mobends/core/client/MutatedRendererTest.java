package io.github.recrivenvi.mobends.core.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class MutatedRendererTest {
    private static final float EPSILON = 0.0001F;

    @Test
    void entityLocalRotationTurnsTrailVelocityWithBodyYaw() {
        PoseStack poseStack = new PoseStack();
        MutatedRenderer.applyEntityLocalRotation(poseStack, 90.0F, 90.0F, 0.5F);

        Vector3f trailVelocity =
                poseStack.last().pose().transformDirection(new Vector3f(0.0F, 0.0F, -10.0F));

        assertEquals(10.0F, trailVelocity.x, EPSILON);
        assertEquals(0.0F, trailVelocity.y, EPSILON);
        assertEquals(0.0F, trailVelocity.z, EPSILON);
    }

    @Test
    void entityLocalRotationWrapsAcrossTheYawBoundary() {
        PoseStack poseStack = new PoseStack();
        MutatedRenderer.applyEntityLocalRotation(poseStack, 170.0F, -170.0F, 0.5F);

        Vector3f trailVelocity =
                poseStack.last().pose().transformDirection(new Vector3f(0.0F, 0.0F, -10.0F));

        assertEquals(0.0F, trailVelocity.x, EPSILON);
        assertEquals(0.0F, trailVelocity.y, EPSILON);
        assertEquals(10.0F, trailVelocity.z, EPSILON);
    }
}
