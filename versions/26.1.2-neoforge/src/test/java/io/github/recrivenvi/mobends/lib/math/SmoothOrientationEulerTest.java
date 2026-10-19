package io.github.recrivenvi.mobends.lib.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SmoothOrientationEulerTest {
    @Test
    void roundTripsEulerTargetsUsedByLegacyPacks() {
        SmoothOrientation orientation = new SmoothOrientation();
        orientation.orientEulerDegrees(-35.0F, 25.0F, 49.0F);

        float[] euler = orientation.getEndEulerDegrees();
        assertEquals(-35.0F, euler[0], 0.01F);
        assertEquals(25.0F, euler[1], 0.01F);
        assertEquals(49.0F, euler[2], 0.01F);
    }
}
