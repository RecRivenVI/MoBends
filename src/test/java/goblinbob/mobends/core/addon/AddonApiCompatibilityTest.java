package goblinbob.mobends.core.addon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

class AddonApiCompatibilityTest {
    @Test
    void exposesRegisteredAddonsAndTheirDisplayNames() {
        IAddon addon =
                new IAddon() {
                    @Override
                    public void registerContent(AddonAnimationRegistry registry) {}

                    @Override
                    public String getDisplayName() {
                        return "Compatibility Test";
                    }
                };

        AddonHelper.registerAddon("mobends_test", addon);

        assertTrue(
                StreamSupport.stream(Addons.getRegistered().spliterator(), false)
                        .anyMatch(registered -> registered == addon));
        assertEquals("Compatibility Test", addon.getDisplayName());
    }
}
