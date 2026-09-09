//? if target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.rendering.IBufferSource;
import io.github.recrivenvi.mobends.api.rendering.IRenderLayer;
import io.github.recrivenvi.mobends.api.rendering.IVertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

// NeoForge implementation of IBufferSource. Wraps Minecraft's MultiBufferSource.
public class NeoForgeBufferSource implements IBufferSource {
    private final MultiBufferSource bufferSource;

    public NeoForgeBufferSource(MultiBufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    @Override
    public IVertexConsumer getBuffer(IRenderLayer renderLayer) {
        RenderType renderType = (RenderType) renderLayer.getNative();
        return new NeoForgeVertexConsumer(bufferSource.getBuffer(renderType));
    }

    @Override
    public Object getNative() {
        return bufferSource;
    }

    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }
}*/
//?} else if target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

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
}*/
//?}
