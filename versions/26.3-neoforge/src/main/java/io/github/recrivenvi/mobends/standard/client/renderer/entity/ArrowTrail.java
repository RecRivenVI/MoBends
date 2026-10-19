package io.github.recrivenvi.mobends.standard.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.core.client.TrailSubmitter;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.lib.math.vector.Vec3f;
import io.github.recrivenvi.mobends.lib.math.vector.VectorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.Vec3;

public class ArrowTrail {
    public static final int MAX_LENGTH = 10;
    public static final float SPAWN_INTERVAL = 1;

    private final Minecraft mc;
    private AbstractArrow trackedArrow;
    private TrailNode[] nodes;
    private float spawnCooldown = 0;

    public ArrowTrail(AbstractArrow arrow) {
        this.mc = Minecraft.getInstance();
        this.trackedArrow = arrow;
        this.spawnCooldown = SPAWN_INTERVAL;
        this.nodes = new TrailNode[MAX_LENGTH];

        resetNodes();
    }

    public void onRenderTick() {
        spawnCooldown += DataUpdateHandler.ticksPerFrame;
    }

    public void submit(
            float partialTicks, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (this.spawnCooldown > 40) {
            this.spawnCooldown = 0;
            resetNodes();
        }

        while (this.spawnCooldown >= SPAWN_INTERVAL) {
            for (int i = MAX_LENGTH - 1; i > 0; i--) {
                nodes[i].moveTo(nodes[i - 1]);
            }
            nodes[0].moveTo(trackedArrow);
            this.spawnCooldown -= SPAWN_INTERVAL;
        }

        double originX = Mth.lerp(partialTicks, trackedArrow.xo, trackedArrow.getX());
        double originY = Mth.lerp(partialTicks, trackedArrow.yo, trackedArrow.getY());
        double originZ = Mth.lerp(partialTicks, trackedArrow.zo, trackedArrow.getZ());
        TrailSubmitter.submit(
                poseStack,
                submitNodeCollector,
                RenderTypes.debugQuads(),
                (pose, vertexConsumer) ->
                        renderNodes(pose, vertexConsumer, originX, originY, originZ));
    }

    public void resetNodes() {
        for (int i = 0; i < MAX_LENGTH; i++) this.nodes[i] = new TrailNode(trackedArrow);
    }

    public void renderNodes(
            PoseStack.Pose pose,
            VertexConsumer vertexConsumer,
            double originX,
            double originY,
            double originZ) {
        for (int i = 1; i < MAX_LENGTH; i++) {
            TrailNode node0 = nodes[i - 1];
            TrailNode node1 = nodes[i];

            Vec3 pos0 = new Vec3(node0.x - originX, node0.y - originY, node0.z - originZ);
            Vec3 pos1 = new Vec3(node1.x - originX, node1.y - originY, node1.z - originZ);
            float scale0 = ((float) (MAX_LENGTH - i)) / MAX_LENGTH * .1F;
            float scale1 = ((float) MAX_LENGTH - i - 1.0f) / MAX_LENGTH * .1F;
            if (i == 1) {
                scale1 = 0;
            }
            final Vec3f up0 = node0.up;
            final Vec3f right0 = node0.right;
            final Vec3f up1 = node1.up;
            final Vec3f right1 = node1.right;

            int alpha = Math.max(0, Math.min(255, (int) (127.0F * (MAX_LENGTH - i) / MAX_LENGTH)));
            addVertex(vertexConsumer, pose, pos0, right0, -scale0, alpha);
            addVertex(vertexConsumer, pose, pos0, right0, scale0, alpha);
            addVertex(vertexConsumer, pose, pos1, right1, scale1, alpha);
            addVertex(vertexConsumer, pose, pos1, right1, -scale1, alpha);

            addVertex(vertexConsumer, pose, pos0, up0, -scale0, alpha);
            addVertex(vertexConsumer, pose, pos0, up0, scale0, alpha);
            addVertex(vertexConsumer, pose, pos1, up1, scale1, alpha);
            addVertex(vertexConsumer, pose, pos1, up1, -scale1, alpha);
        }
    }

    private static void addVertex(
            VertexConsumer vertexConsumer,
            PoseStack.Pose pose,
            Vec3 position,
            Vec3f direction,
            float scale,
            int alpha) {
        vertexConsumer
                .addVertex(
                        pose,
                        (float) (position.x + direction.x * scale),
                        (float) (position.y + direction.y * scale),
                        (float) (position.z + direction.z * scale))
                .setColor(255, 255, 255, alpha);
    }

    public boolean shouldBeRemoved() {
        return mc.level == null || trackedArrow.isRemoved();
    }

    static class TrailNode {
        public double x;
        public double y;
        public double z;

        public final Vec3f up;
        public final Vec3f right;

        TrailNode(AbstractArrow arrow) {
            this.up = new Vec3f();
            this.right = new Vec3f();

            this.moveTo(arrow);
        }

        public void moveTo(TrailNode trailNode) {
            this.x = trailNode.x;
            this.y = trailNode.y;
            this.z = trailNode.z;
            this.up.set(trailNode.up);
            this.right.set(trailNode.right);
        }

        public void moveTo(AbstractArrow arrow) {
            this.x = arrow.getX();
            this.y = arrow.getY();
            this.z = arrow.getZ();

            final Vec3 forward = arrow.getForward();

            // Calculate up vector from pitch and yaw
            float pitch = arrow.getXRot();
            float yaw = arrow.getYRot();
            float upPitch = pitch + 90F;

            float f = Mth.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
            float f1 = Mth.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
            float f2 = -Mth.cos(-upPitch * ((float) Math.PI / 180F));
            float f3 = Mth.sin(-upPitch * ((float) Math.PI / 180F));
            Vec3 up = new Vec3(f1 * f2, f3, f * f2);

            this.up.set((float) -up.x, (float) -up.y, (float) up.z);

            VectorUtils.cross(
                    (float) -forward.x,
                    (float) -forward.y,
                    (float) forward.z,
                    this.up.x,
                    this.up.y,
                    this.up.z,
                    this.right);
        }
    }
}
