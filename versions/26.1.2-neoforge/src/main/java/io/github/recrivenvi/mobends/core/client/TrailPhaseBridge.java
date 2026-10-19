// ? if target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.mixin.RenderTypeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

// 26.2 将独立的半透明轨迹放入半透明地形之后的特效阶段。
final class TrailPhaseBridge {
    private static final OutputTarget TRANSLUCENT_TARGET =
            new OutputTarget(
                    "mobends_trail",
                    () -> Minecraft.getInstance().levelRenderer.translucentTarget());

    private final RenderType trailRenderType;

    TrailPhaseBridge() {
        this(
                RenderTypeAccessor.mobends$create(
                        "mobends_trail",
                        RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
                                .setOutputTarget(TRANSLUCENT_TARGET)
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
*/
// ?} else if target_26_1_2_neoforge {
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
// ?}
