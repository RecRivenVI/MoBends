package io.github.recrivenvi.mobends.core.kumo.state;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.core.kumo.state.keyframe.KeyframeLayerState;
import io.github.recrivenvi.mobends.core.kumo.state.procedural.ProceduralLayerState;
import io.github.recrivenvi.mobends.core.kumo.state.template.IKumoInstancingContext;
import io.github.recrivenvi.mobends.core.kumo.state.template.LayerTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import io.github.recrivenvi.mobends.core.kumo.state.template.keyframe.KeyframeLayerTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.procedural.ProceduralLayerTemplate;
import org.slf4j.Logger;

/**
 * Represent the state of a KUMO animation layer. This doesn't have to be keyframe animation, this
 * can be any mutation over time.
 */
public interface ILayerState {
    Logger LOGGER = LogUtils.getLogger();

    void start(IKumoContext context);

    void update(IKumoContext context, float deltaTime) throws MalformedKumoTemplateException;

    static ILayerState createFromTemplate(IKumoInstancingContext context, LayerTemplate template)
            throws MalformedKumoTemplateException {
        switch (template.getLayerType()) {
            case KEYFRAME:
                return KeyframeLayerState.createFromTemplate(
                        context, (KeyframeLayerTemplate) template);
            case PROCEDURAL:
                return ProceduralLayerState.createFromTemplate(
                        context, (ProceduralLayerTemplate) template);
            default:
                LOGGER.warn(
                        String.format(
                                "Unknown layer type was specified in state template: %d",
                                template.getLayerType().ordinal()));
        }

        return null;
    }
}
