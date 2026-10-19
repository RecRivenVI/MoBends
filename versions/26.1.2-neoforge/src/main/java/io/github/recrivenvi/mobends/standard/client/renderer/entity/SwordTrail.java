package io.github.recrivenvi.mobends.standard.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.core.client.model.ModelPartTransform;
import io.github.recrivenvi.mobends.core.util.IColorRead;
import io.github.recrivenvi.mobends.lib.math.Quaternion;
import io.github.recrivenvi.mobends.lib.math.vector.Vec3f;
import io.github.recrivenvi.mobends.lib.util.GUtil;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class SwordTrail {
    protected final Supplier<IColorRead> baseColor;
    protected LinkedList<TrailPart> trailPartList = new LinkedList<>();

    public SwordTrail(Supplier<IColorRead> baseColor) {
        this.baseColor = baseColor;
    }

    public void reset() {
        trailPartList.clear();
    }

    protected static class TrailPart {
        protected HumanoidArm primaryHand;
        protected IColorRead baseColor;

        protected ModelPartTransform body;
        protected ModelPartTransform arm;
        protected ModelPartTransform foreArm;

        protected Quaternion renderRotation = new Quaternion();
        protected Vec3f renderOffset = new Vec3f();
        protected Quaternion itemRotation = new Quaternion();
        protected Vec3f position = new Vec3f();

        protected float velocityX, velocityY, velocityZ;
        protected float ticksExisted = 0F;

        public TrailPart(
                HumanoidArm primaryHand,
                IColorRead baseColor,
                float velocityX,
                float velocityY,
                float velocityZ) {
            this.body = new ModelPartTransform();
            this.arm = new ModelPartTransform();
            this.foreArm = new ModelPartTransform();
            this.primaryHand = primaryHand;
            this.baseColor = baseColor;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
        }

        public void update(float ticksPerFrame) {
            this.ticksExisted += ticksPerFrame;
            this.position.x += this.velocityX * ticksPerFrame;
            this.position.y += this.velocityY * ticksPerFrame;
            this.position.z += this.velocityZ * ticksPerFrame;
        }

        public Vec3f[] getPoints() {
            float alpha = ticksExisted / 5F;
            alpha = Math.min(alpha, 1F);
            alpha = 1F - alpha;

            final Vec3f[] points =
                    new Vec3f[] {new Vec3f(0, 0, -8 + 8 * alpha), new Vec3f(0, 0, -8 - 8 * alpha)};

            GUtil.translate(points, 0, 0, 16);
            GUtil.rotate(points, itemRotation);
            GUtil.translate(points, primaryHand == HumanoidArm.LEFT ? 1 : -1, -6, 0);
            GUtil.rotate(points, foreArm.rotation.getSmooth());
            GUtil.translate(points, 0, -6 + 2, 0);
            GUtil.rotate(points, arm.rotation.getSmooth());
            GUtil.translate(points, arm.position.x, 10, 0);
            GUtil.rotate(points, body.rotation.getSmooth());
            GUtil.translate(points, 0, 12, 0);
            GUtil.rotate(points, renderRotation);
            GUtil.translate(points, renderOffset.x, renderOffset.y, renderOffset.z);

            for (final Vec3f point : points) {
                point.add(position);
            }

            return points;
        }

        public float getAlpha() {
            float alpha = ticksExisted / 5F;
            alpha = Math.min(alpha, 1F);
            return 1F - alpha;
        }
    }

    public void render(PoseStack.Pose pose, VertexConsumer vertexConsumer) {
        if (trailPartList.isEmpty()) {
            return;
        }

        if (trailPartList.size() < 2) {
            return;
        }

        Iterator<TrailPart> it = trailPartList.iterator();
        TrailPart prevPart = null;
        Vec3f[] prevTransformedPoints = null;
        float prevAlpha = 0;

        while (it.hasNext()) {
            final TrailPart part = it.next();
            final Vec3f[] points = part.getPoints();
            final float alpha = part.getAlpha();
            final IColorRead color = part.baseColor;

            if (prevPart != null && prevTransformedPoints != null) {
                // Draw quad connecting previous part to current
                // Convert RGBA float to packed ARGB int
                int prevColor =
                        ((int) (prevAlpha * 255.0F) << 24)
                                | ((int) (color.getR() * 255.0F) << 16)
                                | ((int) (color.getG() * 255.0F) << 8)
                                | (int) (color.getB() * 255.0F);
                int currColor =
                        ((int) (alpha * 255.0F) << 24)
                                | ((int) (color.getR() * 255.0F) << 16)
                                | ((int) (color.getG() * 255.0F) << 8)
                                | (int) (color.getB() * 255.0F);

                vertexConsumer
                        .addVertex(
                                pose,
                                prevTransformedPoints[0].x,
                                prevTransformedPoints[0].y,
                                prevTransformedPoints[0].z)
                        .setColor(prevColor);
                vertexConsumer
                        .addVertex(
                                pose,
                                prevTransformedPoints[1].x,
                                prevTransformedPoints[1].y,
                                prevTransformedPoints[1].z)
                        .setColor(prevColor);
                vertexConsumer
                        .addVertex(pose, points[1].x, points[1].y, points[1].z)
                        .setColor(currColor);
                vertexConsumer
                        .addVertex(pose, points[0].x, points[0].y, points[0].z)
                        .setColor(currColor);
            }

            prevPart = part;
            prevTransformedPoints = points;
            prevAlpha = alpha;
        }
    }

    public void add(
            BipedEntityData<?> entityData, float velocityX, float velocityY, float velocityZ) {
        final LivingEntity entity = entityData.getEntity();
        final HumanoidArm primaryHand = entity.getMainArm();
        final TrailPart newPart =
                new TrailPart(primaryHand, this.baseColor.get(), velocityX, velocityY, velocityZ);

        newPart.body.syncUp(entityData.body);

        if (primaryHand == HumanoidArm.RIGHT) {
            newPart.arm.syncUp(entityData.rightArm);
            newPart.foreArm.syncUp(entityData.rightForeArm);
            newPart.itemRotation.set(entityData.renderRightItemRotation.getSmooth());
        } else {
            newPart.arm.syncUp(entityData.leftArm);
            newPart.foreArm.syncUp(entityData.leftForeArm);
            newPart.itemRotation.set(entityData.renderLeftItemRotation.getSmooth());
        }

        newPart.renderOffset.set(
                entityData.globalOffset.getX(),
                entityData.globalOffset.getY(),
                entityData.globalOffset.getZ());
        newPart.renderRotation.set(entityData.renderRotation.getSmooth());
        newPart.renderRotation.negate();

        trailPartList.add(newPart);
    }

    public void add(BipedEntityData<?> entityData) {
        add(entityData, 0, 0, 0);
    }

    public void update(float ticksPerFrame) {
        final Iterator<TrailPart> it = trailPartList.iterator();
        while (it.hasNext()) {
            final TrailPart trailPart = it.next();
            trailPart.update(ticksPerFrame);

            if (trailPart.ticksExisted > 20) {
                it.remove();
            }
        }
    }
}
