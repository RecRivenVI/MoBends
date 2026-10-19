package io.github.recrivenvi.mobends.neoforge.platform;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IEntityVertexHelper;

//
// NeoForge 1.21.1 implementation of IEntityVertexHelper.
//
// <p>Use the combined vertex method, matching vanilla ModelPart.Cube. Deferred entity buffers may
// return a delegate from individual setters, so chaining them can leave a vertex incomplete.
//
public class NeoForgeEntityVertexHelper implements IEntityVertexHelper {
    @Override
    public void emitVertex(
            Object vertexConsumer,
            float x,
            float y,
            float z,
            int color,
            float u,
            float v,
            int overlay,
            int light,
            float normalX,
            float normalY,
            float normalZ) {
        VertexConsumer consumer = (VertexConsumer) vertexConsumer;
        consumer.addVertex(x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ);
    }
}
