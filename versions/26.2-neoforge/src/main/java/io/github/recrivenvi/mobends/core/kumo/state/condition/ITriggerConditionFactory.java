package io.github.recrivenvi.mobends.core.kumo.state.condition;

import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import io.github.recrivenvi.mobends.core.kumo.state.template.TriggerConditionTemplate;

@FunctionalInterface
public interface ITriggerConditionFactory<
        C extends ITriggerCondition, T extends TriggerConditionTemplate> {

    C createTriggerCondition(T template) throws MalformedKumoTemplateException;
}
