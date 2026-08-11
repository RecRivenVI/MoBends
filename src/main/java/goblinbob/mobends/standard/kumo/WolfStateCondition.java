package goblinbob.mobends.standard.kumo;

import goblinbob.mobends.core.kumo.state.condition.ITriggerCondition;
import goblinbob.mobends.core.kumo.state.condition.ITriggerConditionContext;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import goblinbob.mobends.standard.data.WolfData;

public class WolfStateCondition implements ITriggerCondition {
    private final State state;

    public WolfStateCondition(Template template) throws MalformedKumoTemplateException {
        if (template.state == null) {
            throw new MalformedKumoTemplateException(
                    "No 'state' property given for trigger condition.");
        }
        this.state = template.state;
    }

    @Override
    public boolean isConditionMet(ITriggerConditionContext context)
            throws MalformedKumoTemplateException {
        if (!(context.getEntityData() instanceof WolfData wolfData)) {
            throw new MalformedKumoTemplateException(
                    "A wolf_state trigger condition was used on something other than a wolf.");
        }
        return state == State.SITTING && wolfData.isSitting();
    }

    public static class Template extends TriggerConditionTemplate {
        public State state;
    }

    public enum State {
        SITTING
    }
}
