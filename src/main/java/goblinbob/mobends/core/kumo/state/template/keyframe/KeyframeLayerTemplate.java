package goblinbob.mobends.core.kumo.state.template.keyframe;

import goblinbob.mobends.core.kumo.state.template.IKumoValidationContext;
import goblinbob.mobends.core.kumo.state.template.LayerTemplate;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.lib.animation.keyframe.ArmatureMask;
import java.util.List;

public class KeyframeLayerTemplate extends LayerTemplate {

    public int entryNode = 0;
    public List<KeyframeNodeTemplate> nodes;
    public ArmatureMask mask;

    @Override
    public void validate(IKumoValidationContext context) throws MalformedKumoTemplateException {
        super.validate(context);

        if (nodes == null || nodes.isEmpty()) {
            throw new MalformedKumoTemplateException(
                    "A keyframe layer must define at least one node.");
        }
        if (entryNode < 0 || entryNode >= nodes.size()) {
            throw new MalformedKumoTemplateException("Entry node index is out of bounds.");
        }

        for (int nodeIndex = 0; nodeIndex < nodes.size(); ++nodeIndex) {
            KeyframeNodeTemplate node = nodes.get(nodeIndex);
            if (node == null) {
                throw new MalformedKumoTemplateException(
                        String.format("Keyframe node at index %d is null.", nodeIndex));
            }
            node.validate(context);

            if (node.connections == null) {
                continue;
            }
            for (ConnectionTemplate connection : node.connections) {
                if (connection == null) {
                    throw new MalformedKumoTemplateException(
                            String.format(
                                    "Keyframe node at index %d contains a null connection.",
                                    nodeIndex));
                }
                if (connection.targetNodeIndex < 0 || connection.targetNodeIndex >= nodes.size()) {
                    throw new MalformedKumoTemplateException(
                            String.format(
                                    "A connection to node at index %d was specified, which doesn't exist.",
                                    connection.targetNodeIndex));
                }
            }
        }
    }
}
