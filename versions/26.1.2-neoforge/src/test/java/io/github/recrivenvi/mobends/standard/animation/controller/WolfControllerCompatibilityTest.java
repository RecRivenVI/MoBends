package io.github.recrivenvi.mobends.standard.animation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WolfControllerCompatibilityTest {
    private static final float EPSILON = 0.0001F;

    @Test
    void vanillaTailPitchKeepsOriginalMoBendsBasis() {
        assertEquals(-54.0F, WolfController.toMoBendsTailPitch((float) (Math.PI / 5.0)), EPSILON);
        assertEquals(9.0F, WolfController.toMoBendsTailPitch(0.55F * (float) Math.PI), EPSILON);
    }
}
