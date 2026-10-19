package io.github.recrivenvi.mobends.core.kumo.state;

import io.github.recrivenvi.mobends.core.kumo.state.condition.ITriggerCondition;
import io.github.recrivenvi.mobends.core.kumo.state.condition.TriggerConditionRegistry;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import io.github.recrivenvi.mobends.core.kumo.state.template.keyframe.ConnectionTemplate;
import java.util.List;

public class ConnectionState {

    public final INodeState targetNode;
    public final ITriggerCondition triggerCondition;
    public final float transitionDuration;
    public ConnectionTemplate.Easing transitionEasing;

    public ConnectionState(
            INodeState targetNode,
            ITriggerCondition triggerCondition,
            float transitionDuration,
            ConnectionTemplate.Easing transitionEasing) {
        this.targetNode = targetNode;
        this.triggerCondition = triggerCondition;
        this.transitionDuration = transitionDuration;
        this.transitionEasing = transitionEasing;
    }

    public static ConnectionState createFromTemplate(
            List<INodeState> nodes, ConnectionTemplate template)
            throws MalformedKumoTemplateException {
        INodeState node = null;

        try {
            node = nodes.get(template.targetNodeIndex);
        } catch (IndexOutOfBoundsException ex) {
            throw new MalformedKumoTemplateException(
                    String.format(
                            "A connection to node at index: %d was specified, which doesn't exist.",
                            template.targetNodeIndex));
        }

        if (template.triggerCondition == null) {
            throw new MalformedKumoTemplateException(
                    "No trigger condition was specified for a connection.");
        }

        return new ConnectionState(
                node,
                TriggerConditionRegistry.instance.createFromTemplate(template.triggerCondition),
                template.transitionDuration,
                template.transitionEasing);
    }
}
