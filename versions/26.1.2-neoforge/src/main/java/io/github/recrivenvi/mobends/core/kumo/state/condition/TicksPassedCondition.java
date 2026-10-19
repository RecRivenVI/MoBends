package io.github.recrivenvi.mobends.core.kumo.state.condition;

import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import io.github.recrivenvi.mobends.core.kumo.state.template.TriggerConditionTemplate;

/**
 * This condition is met once a certain amount of ticks have passed since the start of the node's
 * lifetime.
 */
public class TicksPassedCondition implements ITriggerCondition {

    private final float ticksToPass;
    private float ticksOnStart;

    public TicksPassedCondition(Template template) throws MalformedKumoTemplateException {
        if (template.ticksToPass < 0) {
            throw new MalformedKumoTemplateException(
                    "ticks_passed cannot use a negative tick count.");
        }
        this.ticksToPass = template.ticksToPass;
    }

    @Override
    public void onNodeStarted(ITriggerConditionContext context) {
        this.ticksOnStart = DataUpdateHandler.getTicks();
    }

    @Override
    public boolean isConditionMet(ITriggerConditionContext context) {
        return DataUpdateHandler.getTicks() > this.ticksOnStart + this.ticksToPass;
    }

    public static class Template extends TriggerConditionTemplate {

        public int ticksToPass;
    }
}
