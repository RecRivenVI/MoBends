package goblinbob.mobends.core.kumo.state.template.keyframe;

import goblinbob.mobends.core.kumo.state.condition.TriggerConditionRegistry;
import goblinbob.mobends.core.kumo.state.template.IKumoValidationContext;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;

public class ConnectionTemplate {

    public int targetNodeIndex;
    public TriggerConditionTemplate triggerCondition;

    /** The duration of the transition in ticks. */
    public float transitionDuration = 0;

    public Easing transitionEasing = Easing.EASE_IN_OUT;

    public enum Easing {
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
    }

    public void validate(IKumoValidationContext context) throws MalformedKumoTemplateException {
        if (triggerCondition == null) {
            throw new MalformedKumoTemplateException("No trigger condition has been specified.");
        }
        if (transitionDuration < 0.0F) {
            throw new MalformedKumoTemplateException("A transition duration cannot be negative.");
        }
        if (transitionEasing == null) {
            throw new MalformedKumoTemplateException("A transition easing must be specified.");
        }

        triggerCondition.validate(context);
        TriggerConditionRegistry.instance.createFromTemplate(triggerCondition);
    }
}
