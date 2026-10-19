// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

//
// 将独立的半透明轨迹延迟到半透明地形之后绘制。
//
// <p>常规自定义半透明几何会早于水、玻璃等半透明地形执行，后绘制的 地形会错误覆盖剑轨和箭轨。轨迹仍沿用带深度测试且不写深度的管线，
// 因此延迟提交只改变透明内容之间的优先级，不会让轨迹穿过不透明方块。
//
public final class TrailSubmitter {
    private static final TrailPhaseBridge PHASE_BRIDGE = new TrailPhaseBridge();

    private TrailSubmitter() {}

    public static void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            RenderType renderType,
            SubmitNodeCollector.CustomGeometryRenderer renderer) {
        PHASE_BRIDGE.submit(poseStack, submitNodeCollector, renderType, renderer);
    }
}
// ?}
