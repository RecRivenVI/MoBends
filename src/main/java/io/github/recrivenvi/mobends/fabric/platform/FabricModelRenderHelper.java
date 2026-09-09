//? if target_26_2_fabric {
package io.github.recrivenvi.mobends.fabric.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IModelRenderHelper;
import net.minecraft.client.model.Model;

//
// Fabric 1.21.1 implementation of IModelRenderHelper.
//
// <p>MC 1.21.1: - renderToBuffer(PoseStack, VertexConsumer, int light, int overlay, int color)
// Minecraft 26.2 selects the render type before invoking custom geometry, so the callback's vertex
// consumer already carries the correct material and foil state.
//
public class FabricModelRenderHelper implements IModelRenderHelper {
    @Override
    public void renderModelToBuffer(
            Object model,
            Object poseStack,
            Object vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color) {
        Model m = (Model) model;
        PoseStack ps = (PoseStack) poseStack;
        VertexConsumer vc = (VertexConsumer) vertexConsumer;
        m.renderToBuffer(ps, vc, packedLight, packedOverlay, color);
    }

    @Override
    public Object getArmorFoilBuffer(Object bufferSource, Object renderType, boolean hasFoil) {
        return (VertexConsumer) bufferSource;
    }
}
//?}
