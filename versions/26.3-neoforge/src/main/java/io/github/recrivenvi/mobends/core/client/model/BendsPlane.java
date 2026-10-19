package io.github.recrivenvi.mobends.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.rendering.IEntityVertexHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** A small textured XZ plane used by geometry that cannot be represented by a cube. */
public final class BendsPlane {
    private final Vertex[] vertices;
    private final Vector3f normal;

    private BendsPlane(Vertex[] vertices, Vector3f normal) {
        this.vertices = vertices;
        this.normal = normal;
    }

    public static BendsPlane xz(
            float minX,
            float y,
            float minZ,
            float width,
            float length,
            boolean facingUp,
            int minU,
            int minV,
            int maxU,
            int maxV,
            int textureWidth,
            int textureHeight) {
        float scale = 1.0F / 16.0F;
        float x1 = minX * scale;
        float x2 = (minX + width) * scale;
        float z1 = minZ * scale;
        float z2 = (minZ + length) * scale;
        float py = y * scale;
        float u1 = (float) minU / textureWidth;
        float v1 = (float) minV / textureHeight;
        float u2 = (float) maxU / textureWidth;
        float v2 = (float) maxV / textureHeight;

        Vertex a = new Vertex(x1, py, z2, u1, v1);
        Vertex b = new Vertex(x1, py, z1, u1, v2);
        Vertex c = new Vertex(x2, py, z1, u2, v2);
        Vertex d = new Vertex(x2, py, z2, u2, v1);
        return facingUp
                ? new BendsPlane(new Vertex[] {a, b, c, d}, new Vector3f(0.0F, -1.0F, 0.0F))
                : new BendsPlane(new Vertex[] {d, c, b, a}, new Vector3f(0.0F, 1.0F, 0.0F));
    }

    public void render(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            int packedOverlay,
            int color) {
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vector3f transformedNormal = new Vector3f(normal).mul(normalMatrix);

        for (Vertex vertex : vertices) {
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
            IEntityVertexHelper.Holder.getHelper()
                    .emitVertex(
                            consumer,
                            x,
                            y,
                            z,
                            color,
                            vertex.u,
                            vertex.v,
                            packedOverlay,
                            packedLight,
                            transformedNormal.x(),
                            transformedNormal.y(),
                            transformedNormal.z());
        }
    }

    private record Vertex(float x, float y, float z, float u, float v) {}
}
