package io.github.recrivenvi.mobends.core.kumo.state.condition;

import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;

@FunctionalInterface
public interface ITriggerCondition {

    default void onNodeStarted(ITriggerConditionContext context) {}

    boolean isConditionMet(ITriggerConditionContext context) throws MalformedKumoTemplateException;
}
