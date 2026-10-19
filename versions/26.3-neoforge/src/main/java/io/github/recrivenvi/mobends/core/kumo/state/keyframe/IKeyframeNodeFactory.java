package io.github.recrivenvi.mobends.core.kumo.state.keyframe;

import io.github.recrivenvi.mobends.core.kumo.state.INodeState;
import io.github.recrivenvi.mobends.core.kumo.state.template.IKumoInstancingContext;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import io.github.recrivenvi.mobends.core.kumo.state.template.keyframe.KeyframeNodeTemplate;

@FunctionalInterface
public interface IKeyframeNodeFactory<N extends INodeState, T extends KeyframeNodeTemplate> {

    N createKeyframeNode(IKumoInstancingContext context, T template)
            throws MalformedKumoTemplateException;
}
