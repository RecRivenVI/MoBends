package goblinbob.mobends.core.kumo.state.condition;

import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import java.util.LinkedList;
import java.util.List;

/** This condition is met if either of the nested conditions are met. */
public class AndCondition implements ITriggerCondition {

    private List<ITriggerCondition> conditions;

    public AndCondition(Template template) throws MalformedKumoTemplateException {
        if (template.conditions == null || template.conditions.isEmpty()) {
            throw new MalformedKumoTemplateException(
                    "An 'and' condition must contain at least one condition.");
        }
        this.conditions = new LinkedList<>();
        for (TriggerConditionTemplate conditionTemplate : template.conditions) {
            if (conditionTemplate != null) {
                this.conditions.add(
                        TriggerConditionRegistry.instance.createFromTemplate(conditionTemplate));
            }
        }
        if (this.conditions.isEmpty()) {
            throw new MalformedKumoTemplateException(
                    "An 'and' condition must contain at least one valid condition.");
        }
    }

    @Override
    public boolean isConditionMet(ITriggerConditionContext context)
            throws MalformedKumoTemplateException {
        for (ITriggerCondition condition : this.conditions) {
            if (!condition.isConditionMet(context)) {
                return false;
            }
        }
        return true;
    }

    public static class Template extends TriggerConditionTemplate {

        public List<TriggerConditionTemplate> conditions;
    }
}
