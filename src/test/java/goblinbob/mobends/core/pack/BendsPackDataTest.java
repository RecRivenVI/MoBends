package goblinbob.mobends.core.pack;

import static org.junit.jupiter.api.Assertions.assertSame;

import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.lib.animation.keyframe.KeyframeAnimation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class BendsPackDataTest {
    @AfterEach
    void clearAnimationLoaderCache() {
        AnimationLoader.clearCache();
    }

    @Test
    void resolvesAnimationFromLoaderWhenPackDoesNotEmbedIt() throws Exception {
        KeyframeAnimation bundledAnimation = new KeyframeAnimation();
        internalAnimations().put("mobends-test-animation", bundledAnimation);

        BendsPackData data = new BendsPackData();
        data.keyframeAnimations = new HashMap<>();

        assertSame(bundledAnimation, data.getAnimation("mobends-test-animation"));
    }

    @Test
    void embeddedAnimationTakesPrecedenceOverLoaderAnimation() throws Exception {
        KeyframeAnimation bundledAnimation = new KeyframeAnimation();
        KeyframeAnimation embeddedAnimation = new KeyframeAnimation();
        internalAnimations().put("mobends-test-animation", bundledAnimation);

        BendsPackData data = new BendsPackData();
        data.keyframeAnimations = new HashMap<>();
        data.keyframeAnimations.put("mobends-test-animation", embeddedAnimation);

        assertSame(embeddedAnimation, data.getAnimation("mobends-test-animation"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, KeyframeAnimation> internalAnimations() throws Exception {
        Field field = AnimationLoader.class.getDeclaredField("internalRegistry");
        field.setAccessible(true);
        return (Map<String, KeyframeAnimation>) field.get(null);
    }
}
