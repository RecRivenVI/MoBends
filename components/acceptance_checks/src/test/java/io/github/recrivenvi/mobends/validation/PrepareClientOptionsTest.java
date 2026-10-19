package io.github.recrivenvi.mobends.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PrepareClientOptionsTest {
    @Test
    void automationIsMutedAndFullHd() {
        var result = PrepareClientOptions.update(List.of(), false);
        assertTrue(result.contains("soundCategory_master:0.0"));
        assertTrue(result.contains("overrideWidth:1920"));
        assertTrue(result.contains("overrideHeight:1080"));
    }

    @Test
    void handoffHasUserSoundAndResolution() {
        var result = PrepareClientOptions.update(List.of(), true);
        assertTrue(result.contains("soundCategory_master:0.5"));
        assertTrue(result.contains("soundCategory_music:0.0"));
        assertTrue(result.contains("overrideWidth:2560"));
        assertTrue(result.contains("overrideHeight:1440"));
    }

    @Test
    void unrelatedPreferencesSurviveAndOldValuesDoNotDuplicate() {
        var result =
                PrepareClientOptions.update(
                        List.of("fov:0.7", "soundCategory_master:1.0", "soundCategory_master:0.2"),
                        true);
        assertTrue(result.contains("fov:0.7"));
        assertEquals(
                1,
                result.stream().filter(line -> line.startsWith("soundCategory_master:")).count());
    }
}
