package io.github.recrivenvi.mobends.core.kumo.state.template.keyframe;

import io.github.recrivenvi.mobends.core.kumo.state.template.IKumoValidationContext;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;

public class MovementKeyframeNodeTemplate extends KeyframeNodeTemplate {

    public String animationKey;

    public int startFrame = 0;

    public float playbackSpeed = 1;

    @Override
    public void validate(IKumoValidationContext context) throws MalformedKumoTemplateException {
        super.validate(context);

        if (animationKey == null || animationKey.isBlank()) {
            throw new MalformedKumoTemplateException(
                    "A movement keyframe node must specify an animation.");
        }
        if (!context.doesAnimationExist(animationKey)) {
            throw new MalformedKumoTemplateException(
                    String.format("Trying to use a missing animation: \"%s\".", animationKey));
        }
    }
}
