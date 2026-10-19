package io.github.recrivenvi.mobends.standard.client.model.armor.tier1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IEntityVertexHelper;
import io.github.recrivenvi.mobends.standard.client.model.armor.CapturedVertex;
import java.util.List;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/** Applies the current pose matrices and armor tint before submitting captured vertices. */
final class Tier1VertexWriter {
    private Tier1VertexWriter() {}

    static void emit(
            PoseStack poseStack,
            VertexConsumer consumer,
            List<CapturedVertex> vertices,
            int packedLight,
            int packedOverlay,
            int armorColor) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        for (CapturedVertex vertex : vertices) {
            float x =
                    matrix.m00() * vertex.x
                            + matrix.m10() * vertex.y
                            + matrix.m20() * vertex.z
                            + matrix.m30();
            float y =
                    matrix.m01() * vertex.x
                            + matrix.m11() * vertex.y
                            + matrix.m21() * vertex.z
                            + matrix.m31();
            float z =
                    matrix.m02() * vertex.x
                            + matrix.m12() * vertex.y
                            + matrix.m22() * vertex.z
                            + matrix.m32();
            float normalX =
                    normal.m00() * vertex.normalX
                            + normal.m10() * vertex.normalY
                            + normal.m20() * vertex.normalZ;
            float normalY =
                    normal.m01() * vertex.normalX
                            + normal.m11() * vertex.normalY
                            + normal.m21() * vertex.normalZ;
            float normalZ =
                    normal.m02() * vertex.normalX
                            + normal.m12() * vertex.normalY
                            + normal.m22() * vertex.normalZ;

            IEntityVertexHelper.Holder.getHelper()
                    .emitVertex(
                            consumer,
                            x,
                            y,
                            z,
                            tintColor(vertex, armorColor),
                            vertex.u,
                            vertex.v,
                            packedOverlay,
                            packedLight,
                            normalX,
                            normalY,
                            normalZ);
        }
    }

    static int tintColor(CapturedVertex vertex, int armorColor) {
        float tintRed = ((armorColor >> 16) & 0xFF) / 255.0F;
        float tintGreen = ((armorColor >> 8) & 0xFF) / 255.0F;
        float tintBlue = (armorColor & 0xFF) / 255.0F;
        float tintAlpha = ((armorColor >> 24) & 0xFF) / 255.0F;
        return ((int) (vertex.alpha * tintAlpha * 255.0F) << 24)
                | ((int) (vertex.red * tintRed * 255.0F) << 16)
                | ((int) (vertex.green * tintGreen * 255.0F) << 8)
                | (int) (vertex.blue * tintBlue * 255.0F);
    }
}
