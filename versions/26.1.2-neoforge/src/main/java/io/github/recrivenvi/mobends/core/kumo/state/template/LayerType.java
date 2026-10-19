package io.github.recrivenvi.mobends.core.kumo.state.template;

import io.github.recrivenvi.mobends.core.kumo.state.template.keyframe.KeyframeLayerTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.procedural.ProceduralLayerTemplate;
import java.lang.reflect.Type;

public enum LayerType {
    KEYFRAME(KeyframeLayerTemplate.class),
    PROCEDURAL(ProceduralLayerTemplate.class);

    private Type templateType;

    LayerType(Type templateType) {
        this.templateType = templateType;
    }

    public Type getTemplateType() {
        return templateType;
    }
}
