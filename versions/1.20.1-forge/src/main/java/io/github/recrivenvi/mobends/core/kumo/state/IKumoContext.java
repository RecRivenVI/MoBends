package io.github.recrivenvi.mobends.core.kumo.state;

import io.github.recrivenvi.mobends.core.kumo.state.condition.ITriggerConditionContext;

/** This is a context which should provide all data necessary during the animation process. */
public interface IKumoContext extends ITriggerConditionContext {

    void setCurrentNode(INodeState node);
}
