package io.github.recrivenvi.mobends.standard.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SwimmingPosePolicyTest {
    private static final float EPSILON = 0.0001F;

    @Test
    public void startsOnlyAfterVanillaVisualSwimmingPose() {
        assertFalse(SwimmingPosePolicy.shouldAnimate(false));
        assertTrue(SwimmingPosePolicy.shouldAnimate(true));
    }

    @Test
    public void animatedPlayerUsesOnlyTheMoBendsRootTransform() {
        assertEquals(0.0F, SwimmingPosePolicy.vanillaRootAmount(1.0F, true), EPSILON);
        assertEquals(0.0F, SwimmingPosePolicy.vanillaRootAmount(0.35F, true), EPSILON);
    }

    @Test
    public void vanillaPlayerKeepsTheMinecraftSwimTransition() {
        assertEquals(1.0F, SwimmingPosePolicy.vanillaRootAmount(1.0F, false), EPSILON);
        assertEquals(0.35F, SwimmingPosePolicy.vanillaRootAmount(0.35F, false), EPSILON);
    }

    @Test
    public void keepsBaseRootPitchIndependentFromVanillaViewPitch() {
        assertEquals(80.0F, SwimmingPosePolicy.mobendsBaseRootPitch(1.0F), EPSILON);
        assertEquals(40.0F, SwimmingPosePolicy.mobendsBaseRootPitch(0.5F), EPSILON);
    }

    @Test
    public void carriesVanillaUpAndDownPitchIntoBodyCenterRotation() {
        assertEquals(0.0F, SwimmingPosePolicy.mobendsViewPitch(1.0F, 0.0F), EPSILON);
        assertEquals(-45.0F, SwimmingPosePolicy.mobendsViewPitch(1.0F, -45.0F), EPSILON);
        assertEquals(45.0F, SwimmingPosePolicy.mobendsViewPitch(1.0F, 45.0F), EPSILON);
    }

    @Test
    public void clampsPitchAndTransitionToVanillaRanges() {
        assertEquals(-90.0F, SwimmingPosePolicy.mobendsViewPitch(2.0F, -120.0F), EPSILON);
        assertEquals(90.0F, SwimmingPosePolicy.mobendsViewPitch(2.0F, 120.0F), EPSILON);
        assertEquals(0.0F, SwimmingPosePolicy.mobendsViewPitch(-1.0F, 45.0F), EPSILON);
    }

    @Test
    public void headCounterRotationPreventsDoubleApplyingVanillaPitch() {
        float vanillaPitch = 45.0F;
        float rootPitch =
                SwimmingPosePolicy.mobendsBaseRootPitch(1.0F)
                        + SwimmingPosePolicy.mobendsViewPitch(1.0F, vanillaPitch);
        float localHeadPitch =
                vanillaPitch + SwimmingPosePolicy.mobendsHeadCounterRotation(1.0F, vanillaPitch);
        assertEquals(vanillaPitch, rootPitch + localHeadPitch, EPSILON);
    }

    @Test
    public void entryAndExitEndpointsDoNotLeaveResidualRootRotation() {
        assertEquals(0.0F, SwimmingPosePolicy.mobendsBaseRootPitch(0.0F), EPSILON);
        assertEquals(0.0F, SwimmingPosePolicy.mobendsViewPitch(0.0F, 45.0F), EPSILON);
        assertEquals(80.0F, SwimmingPosePolicy.mobendsBaseRootPitch(1.0F), EPSILON);
        assertEquals(45.0F, SwimmingPosePolicy.mobendsViewPitch(1.0F, 45.0F), EPSILON);
    }

    @Test
    public void headKeepsItsWorldPitchThroughoutTheTransition() {
        float vanillaPitch = -35.0F;
        float transition = 0.5F;
        float rootPitch =
                SwimmingPosePolicy.mobendsBaseRootPitch(transition)
                        + SwimmingPosePolicy.mobendsViewPitch(transition, vanillaPitch);
        float localHeadPitch =
                vanillaPitch
                        + SwimmingPosePolicy.mobendsHeadCounterRotation(transition, vanillaPitch);

        assertEquals(vanillaPitch, rootPitch + localHeadPitch, EPSILON);
    }
}
