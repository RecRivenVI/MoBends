package goblinbob.mobends.core.kumo.state.template.keyframe;

import goblinbob.mobends.core.kumo.state.template.IKumoValidationContext;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import java.util.List;

public class KeyframeNodeTemplate {

    private String type = "core:standard";

    public List<ConnectionTemplate> connections;

    public String getType() {
        return type;
    }

    public void validate(IKumoValidationContext context) throws MalformedKumoTemplateException {
        if (connections == null) {
            return;
        }
        for (ConnectionTemplate connectionTemplate : connections) {
            if (connectionTemplate == null) {
                throw new MalformedKumoTemplateException(
                        "A keyframe node contains a null connection.");
            }
            connectionTemplate.validate(context);
        }
    }
}
