package io.github.recrivenvi.mobends.core.kumo.state.template;

import io.github.recrivenvi.mobends.lib.animation.keyframe.KeyframeAnimation;

/**
 * This is a context available during template instancing, which should provide all necessary
 * instantiation data.
 */
public interface IKumoInstancingContext {

    KeyframeAnimation getAnimation(String key);
}
