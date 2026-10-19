// ? if target_26_2_fabric {
package io.github.recrivenvi.mobends.fabric.platform;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IVertexConsumer;

//
// Fabric 1.21.1 implementation of IVertexConsumer. Uses the MC 1.21+ vertex API with addVertex()
// and no endVertex().
//
public class FabricVertexConsumer implements IVertexConsumer {
    private final VertexConsumer consumer;

    public FabricVertexConsumer(VertexConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public IVertexConsumer addVertex(float x, float y, float z) {
        consumer.addVertex(x, y, z);
        return this;
    }

    @Override
    public IVertexConsumer setColor(int red, int green, int blue, int alpha) {
        consumer.setColor(red, green, blue, alpha);
        return this;
    }

    @Override
    public IVertexConsumer setUv(float u, float v) {
        consumer.setUv(u, v);
        return this;
    }

    @Override
    public IVertexConsumer setOverlay(int u, int v) {
        // Pack u,v into single int: u | (v << 16)
        consumer.setOverlay(u | (v << 16));
        return this;
    }

    @Override
    public IVertexConsumer setOverlay(int overlay) {
        consumer.setOverlay(overlay);
        return this;
    }

    @Override
    public IVertexConsumer setLight(int u, int v) {
        // Pack u,v into single int: u | (v << 16)
        consumer.setLight(u | (v << 16));
        return this;
    }

    @Override
    public IVertexConsumer setLight(int light) {
        consumer.setLight(light);
        return this;
    }

    @Override
    public IVertexConsumer setNormal(float x, float y, float z) {
        consumer.setNormal(x, y, z);
        return this;
    }

    @Override
    public Object getNative() {
        return consumer;
    }

    public VertexConsumer getConsumer() {
        return consumer;
    }
}
// ?}
