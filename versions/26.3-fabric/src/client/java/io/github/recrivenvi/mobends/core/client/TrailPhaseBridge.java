package io.github.recrivenvi.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.mixin.RenderTypeAccessor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

// 26.3 的输出附件由 RenderPass 管理；保留 afterTerrain 阶段与轨迹混合顺序。
final class TrailPhaseBridge {
    private final RenderType trailRenderType;

    TrailPhaseBridge() {
        this(
                RenderTypeAccessor.mobends$create(
                        "mobends_trail",
                        RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
                                .sortOnUpload()
                                .createRenderSetup()));
    }

    TrailPhaseBridge(RenderType trailRenderType) {
        this.trailRenderType = trailRenderType;
    }

    void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            RenderType renderType,
            SubmitNodeCollector.CustomGeometryRenderer renderer) {
        if (submitNodeCollector instanceof SubmitNodeStorage storage) {
            SubmitNodeCollection collection = storage.order(0);
            collection.afterTerrain.submit(
                    new CustomFeatureRenderer.Submit(
                            poseStack.last().copy(), this.trailRenderType, renderer));
            return;
        }

        // 保留非世界渲染调用方的兼容路径。
        submitNodeCollector.submitCustomGeometry(poseStack, renderType, renderer);
    }
}
