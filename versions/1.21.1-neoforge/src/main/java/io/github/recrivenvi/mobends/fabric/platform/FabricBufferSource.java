// ? if target_26_2_fabric {
/*package io.github.recrivenvi.mobends.fabric.platform;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IBufferSource;
import io.github.recrivenvi.mobends.api.rendering.IRenderLayer;
import io.github.recrivenvi.mobends.api.rendering.IVertexConsumer;

//
// Fabric implementation of IBufferSource. Wraps the vertex consumer supplied by Minecraft 26.2's
// custom-geometry callback.
//
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
*/// ?}
