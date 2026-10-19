package io.github.recrivenvi.mobends.core.pack;

import io.github.recrivenvi.mobends.core.animation.keyframe.AnimationLoader;
import io.github.recrivenvi.mobends.core.kumo.state.template.AnimatorTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.IKumoInstancingContext;
import io.github.recrivenvi.mobends.lib.animation.keyframe.KeyframeAnimation;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BendsPackData implements IKumoInstancingContext {

    public Map<String, AnimatorTemplate> targets = new HashMap<>();
    public Map<String, KeyframeAnimation> keyframeAnimations = new HashMap<>();
    public Map<String, Map<String, List<LegacyBendsPackDefinition.Action>>> legacyTargets =
            new LinkedHashMap<>();

    @Override
    public KeyframeAnimation getAnimation(String key) {
        KeyframeAnimation animation = keyframeAnimations.get(key);
        if (animation != null) {
            return animation;
        }

        // Pack validation has always allowed nodes to reference bundled
        // animations by resource path. Resolve those same paths at runtime so
        // a valid pack does not create a node with a null animation.
        try {
            return AnimationLoader.loadFromPath(key);
        } catch (IOException ignored) {
            return null;
        }
    }
}
