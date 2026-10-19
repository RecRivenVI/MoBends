package io.github.recrivenvi.mobends.core.kumo.state.condition;

import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.kumo.state.ILayerState;
import io.github.recrivenvi.mobends.core.kumo.state.INodeState;

public interface ITriggerConditionContext {

    /** Returns data for the entity that's being animated. */
    EntityData<?> getEntityData();

    /** Returns the layer this condition has to be met on. */
    ILayerState getLayerState();

    /** Returns the current node. */
    INodeState getCurrentNode();
}
