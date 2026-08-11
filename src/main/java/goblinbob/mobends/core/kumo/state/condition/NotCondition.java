package goblinbob.mobends.core.kumo.state.condition;

import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;

/** This condition is met if the nested condition is not met. */
public class NotCondition implements ITriggerCondition {

    private ITriggerCondition condition;

    public NotCondition(Template template) throws MalformedKumoTemplateException {
        if (template.condition == null) {
            throw new MalformedKumoTemplateException("A 'not' condition must contain a condition.");
        }
        this.condition = TriggerConditionRegistry.instance.createFromTemplate(template.condition);
    }

    @Override
    public boolean isConditionMet(ITriggerConditionContext context)
            throws MalformedKumoTemplateException {
        return !this.condition.isConditionMet(context);
    }

    public static class Template extends TriggerConditionTemplate {

        public TriggerConditionTemplate condition;
    }
}
