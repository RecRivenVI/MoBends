package io.github.recrivenvi.mobends.standard.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PlayerRenderPoseCompatibilityTest {
    private static final float EPSILON = 0.0001F;

    @Test
    public void capeDoesNotRepeatTheAnimatedTorsoCrouchPitch() {
        PlayerData data = new PlayerData(null);

        assertEquals(0.0F, data.getCapeCrouchingPitchOffset(), EPSILON);
        assertEquals(0.0F, data.getCapeCrouchingTranslationY(), EPSILON);
    }
}
