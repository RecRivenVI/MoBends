package goblinbob.mobends.fabric.platform;

import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.api.rendering.IBufferSource;
import goblinbob.mobends.api.rendering.IRenderLayer;
import goblinbob.mobends.api.rendering.IVertexConsumer;

/**
 * Fabric implementation of IBufferSource. Wraps the vertex consumer supplied by Minecraft 26.2's
 * custom-geometry callback.
 */
public class FabricBufferSource implements IBufferSource {
    private final VertexConsumer vertexConsumer;

    public FabricBufferSource(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
    }

    @Override
    public IVertexConsumer getBuffer(IRenderLayer renderLayer) {
        return new FabricVertexConsumer(vertexConsumer);
    }

    @Override
    public Object getNative() {
        return vertexConsumer;
    }

    public VertexConsumer getVertexConsumer() {
        return vertexConsumer;
    }
}
