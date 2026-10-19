package io.github.recrivenvi.mobends.neoforge.platform;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IBufferSource;
import io.github.recrivenvi.mobends.api.rendering.IRenderLayer;
import io.github.recrivenvi.mobends.api.rendering.IVertexConsumer;

//
// NeoForge implementation of IBufferSource. Wraps the vertex consumer selected by Minecraft's model
// submission pipeline.
//
public class NeoForgeBufferSource implements IBufferSource {
    private final VertexConsumer vertexConsumer;

    public NeoForgeBufferSource(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
    }

    @Override
    public IVertexConsumer getBuffer(IRenderLayer renderLayer) {
        return new NeoForgeVertexConsumer(vertexConsumer);
    }

    @Override
    public Object getNative() {
        return vertexConsumer;
    }

    public VertexConsumer getVertexConsumer() {
        return vertexConsumer;
    }
}
