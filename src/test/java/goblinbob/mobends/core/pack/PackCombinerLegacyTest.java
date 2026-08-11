package goblinbob.mobends.core.pack;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;
import goblinbob.mobends.lib.animation.keyframe.KeyframeAnimation;
import java.util.List;
import org.junit.jupiter.api.Test;

class PackCombinerLegacyTest {
    @Test
    void firstPackClaimsTargetAcrossJsonAndLegacyFormats() {
        BendsPackData json = new BendsPackData();
        AnimatorTemplate playerAnimator = new AnimatorTemplate();
        json.targets.put("mobends-player", playerAnimator);

        BendsPackData legacy = new BendsPackData();
        legacy.legacyTargets.put("mobends-player", new java.util.LinkedHashMap<>());

        BendsPackData combined = PackCombiner.combineData(List.of(json, legacy));

        assertSame(playerAnimator, combined.targets.get("mobends-player"));
        assertFalse(combined.legacyTargets.containsKey("mobends-player"));
    }

    @Test
    void legacyTargetAlsoWinsWhenItAppearsBeforeJsonTarget() {
        BendsPackData legacy = new BendsPackData();
        java.util.LinkedHashMap<String, java.util.List<LegacyBendsPackDefinition.Action>> actions =
                new java.util.LinkedHashMap<>();
        legacy.legacyTargets.put("mobends-player", actions);

        BendsPackData json = new BendsPackData();
        json.targets.put("mobends-player", new AnimatorTemplate());

        BendsPackData combined = PackCombiner.combineData(List.of(legacy, json));

        assertSame(actions, combined.legacyTargets.get("mobends-player"));
        assertFalse(combined.targets.containsKey("mobends-player"));
    }

    @Test
    void firstPackAlsoWinsEmbeddedAnimationNameCollisions() {
        KeyframeAnimation firstAnimation = new KeyframeAnimation();
        KeyframeAnimation secondAnimation = new KeyframeAnimation();
        BendsPackData first = new BendsPackData();
        BendsPackData second = new BendsPackData();
        first.keyframeAnimations.put("shared", firstAnimation);
        second.keyframeAnimations.put("shared", secondAnimation);
        second.keyframeAnimations.put("second-only", secondAnimation);

        BendsPackData combined = PackCombiner.combineData(List.of(first, second));

        assertSame(firstAnimation, combined.keyframeAnimations.get("shared"));
        assertSame(secondAnimation, combined.keyframeAnimations.get("second-only"));
        assertTrue(combined.targets.isEmpty());
    }
}
