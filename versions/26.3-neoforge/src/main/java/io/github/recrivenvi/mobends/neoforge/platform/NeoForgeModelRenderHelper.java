package io.github.recrivenvi.mobends.neoforge.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IModelRenderHelper;
import net.minecraft.client.model.Model;

//
// NeoForge 1.21.1 implementation of IModelRenderHelper.
//
// <p>Minecraft 26.x selects material and foil state before invoking custom geometry.
//
public class NeoForgeModelRenderHelper implements IModelRenderHelper {
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
