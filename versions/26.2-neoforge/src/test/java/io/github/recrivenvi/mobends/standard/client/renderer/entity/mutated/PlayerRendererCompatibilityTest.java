// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlayerRendererCompatibilityTest {
    @Test
    void groundedCrouchCancelsOnlyTheModernVanillaRenderOffset() {
        assertEquals(2.0F, PlayerRenderer.MODERN_GROUNDED_CROUCH_COMPENSATION);
    }
}
// ?}
