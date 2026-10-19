package io.github.recrivenvi.mobends.core.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.blaze3d.vertex.PoseStack;
import org.junit.jupiter.api.Test;

public class BendsModelPartTransformTest {
    private static final float EPSILON = 0.0001F;

    @Test
    public void characterTransformKeepsGlobalOffsetOutsideScaledParentChain() {
        BendsModelPart parent = new BendsModelPart().setPosition(10.0F, 0.0F, 0.0F);
        BendsModelPart child = new BendsModelPart().setPosition(4.0F, 0.0F, 0.0F);
        child.offsetScale = 0.5F;
        child.globalOffset.set(3.0F, 0.0F, 0.0F);
        child.setParent(parent);

        PoseStack poseStack = new PoseStack();
        child.applyCharacterTransformPoseStack(poseStack);

        // 3 entity-space pixels + 10 * 0.5 parent pixels + 4 * 0.5
        // child pixels = 10 pixels total.
        assertEquals(10.0F / 16.0F, poseStack.last().pose().m30(), EPSILON);
        assertEquals(0.0F, poseStack.last().pose().m31(), EPSILON);
        assertEquals(0.0F, poseStack.last().pose().m32(), EPSILON);
    }
}
