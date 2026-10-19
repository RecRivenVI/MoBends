package io.github.recrivenvi.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

// 26.1.2 尚未提供 26.2 的 afterTerrain 自定义几何阶段。
final class TrailPhaseBridge {
    void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            RenderType renderType,
            SubmitNodeCollector.CustomGeometryRenderer renderer) {
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, renderer);
    }
}
