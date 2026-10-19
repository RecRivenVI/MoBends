package io.github.recrivenvi.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.core.client.model.BendsCube;
import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import io.github.recrivenvi.mobends.core.util.GlHelper;
import io.github.recrivenvi.mobends.lib.math.Quaternion;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public abstract class BipedMutator<
                D extends BipedEntityData<E>, E extends LivingEntity, M extends HumanoidModel<?>>
        extends Mutator<D, E, M> {
    protected static final byte ALL_FACES = 0b111111;
    protected static final byte UPPER_LIMB_FACES = (byte) (0b111111 & ~(1 << BendsCube.BOTTOM));
    protected static final byte LOWER_LIMB_FACES = (byte) (0b111111 & ~(1 << BendsCube.TOP));

    // Custom bendable parts
    protected BendsModelPart body;
    protected BendsModelPart head;
    protected BendsModelPart headwear;
    protected BendsModelPart leftArm;
    protected BendsModelPart rightArm;
    protected BendsModelPart leftForeArm;
    protected BendsModelPart rightForeArm;
    protected BendsModelPart leftLeg;
    protected BendsModelPart rightLeg;
    protected BendsModelPart leftForeLeg;
    protected BendsModelPart rightForeLeg;

    // Snapshot used by entities that have genuinely different adult and baby atlases.
    protected record BipedParts(
            BendsModelPart body,
            BendsModelPart head,
            BendsModelPart headwear,
            BendsModelPart leftArm,
            BendsModelPart rightArm,
            BendsModelPart leftForeArm,
            BendsModelPart rightForeArm,
            BendsModelPart leftLeg,
            BendsModelPart rightLeg,
            BendsModelPart leftForeLeg,
            BendsModelPart rightForeLeg) {}

    // Store vanilla model parts for demutation
    protected ModelPart vanillaBody;
    protected ModelPart vanillaHead;
    protected ModelPart vanillaHat;
    protected ModelPart vanillaLeftArm;
    protected ModelPart vanillaRightArm;
    protected ModelPart vanillaLeftLeg;
    protected ModelPart vanillaRightLeg;

    public BipedMutator(IEntityDataFactory<E> dataFactory) {
        super(dataFactory);
    }

    //
    // Used to store the model parameter as the vanilla model, so then the mutation can be reversed.
    //
    @Override
    public void storeVanillaModel(M model) {
        this.vanillaBody = model.body;
        this.vanillaHead = model.head;
        this.vanillaHat = model.hat;
        this.vanillaLeftArm = model.leftArm;
        this.vanillaRightArm = model.rightArm;
        this.vanillaLeftLeg = model.leftLeg;
        this.vanillaRightLeg = model.rightLeg;
    }

    // Sets the model parameter back to it's vanilla state. Used to demutate the model.
    @Override
    public void applyVanillaModel(M model) {
        // In 1.20.1, model parts are final and cannot be directly replaced
        // The demutation will need to handle this differently
        // For now, we just track that we need to restore vanilla rendering
    }

    //
    // Swaps out the vanilla layers for their custom counterparts, and if it's a vanilla model, it
    // stores the vanilla layers for future mutation reversal.
    //
    @Override
    public void swapLayer(
            LivingEntityRenderer<?, ?, ?> renderer, int index, boolean isModelVanilla) {
        // 26.1 的渲染层提交模型状态，姿态在实际绘制阶段由渲染桥同步。
    }

    // Swaps the custom layers back with the vanilla layers. Used to demutate the model.
    @Override
    public void deswapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index) {
        // 没有替换原版层，因此无需恢复。
    }

    //
    // Creates all the custom parts you need! It creates custom BendsModelPart instances for
    // bendable limbs.
    //
    @Override
    public boolean createParts(M original, float scaleFactor) {
        // Create custom bendable parts
        // Body - the root of the upper body hierarchy
        body = new BendsModelPart(16, 16).setTextureSize(64, 64).setPosition(0.0F, 12.0F, 0.0F);
        body.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor);

        // Head - child of body
        head = new BendsModelPart(0, 0).setTextureSize(64, 64).setPosition(0.0F, -12.0F, 0.0F);
        head.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor);
        body.addChild(head);

        // Headwear - child of head
        headwear = new BendsModelPart(32, 0).setTextureSize(64, 64);
        headwear.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor + 0.5F);
        head.addChild(headwear);

        // Arms
        int armWidth = 4;
        float armY = -10F;

        // Left arm - child of body
        leftArm =
                new BendsModelPart(40, 16)
                        .setTextureSize(64, 64)
                        .setPosition(5.0F, armY, 0.0F)
                        .setMirror(true);
        leftArm.addCube(
                -1.0F,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                40,
                16);
        body.addChild(leftArm);

        // Right arm - child of body
        rightArm = new BendsModelPart(40, 16).setTextureSize(64, 64).setPosition(-5.0F, armY, 0.0F);
        rightArm.addCube(
                -armWidth + 1,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.1F,
                UPPER_LIMB_FACES,
                40,
                16);
        body.addChild(rightArm);

        // Left forearm - child of left arm
        leftForeArm =
                new BendsModelPart(40, 22)
                        .setTextureSize(64, 64)
                        .setPosition(0.0F, 4.0F, 2.0F)
                        .setMirror(true);
        leftForeArm.addCube(
                -1.0F, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor, LOWER_LIMB_FACES, 40, 16);
        leftArm.addChild(leftForeArm);

        // Right forearm - child of right arm
        rightForeArm =
                new BendsModelPart(40, 22).setTextureSize(64, 64).setPosition(0.0F, 4.0F, 2.0F);
        rightForeArm.addCube(
                -armWidth + 1, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor, LOWER_LIMB_FACES, 40, 16);
        rightArm.addChild(rightForeArm);

        // Legs - independent roots (not children of body)
        rightLeg = new BendsModelPart(0, 16).setTextureSize(64, 64).setPosition(0.0F, 12F, 0F);
        rightLeg.addCube(-3.9F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        leftLeg =
                new BendsModelPart(0, 16)
                        .setTextureSize(64, 64)
                        .setPosition(0.0F, 12.0F, 0.0F)
                        .setMirror(true);
        leftLeg.addCube(-0.1F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        // Left foreleg - child of left leg
        leftForeLeg =
                new BendsModelPart(0, 22)
                        .setTextureSize(64, 64)
                        .setPosition(0, 6.0F, -2.0F)
                        .setMirror(true);
        leftForeLeg.addCube(
                -0.1F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                0,
                16);
        leftLeg.addChild(leftForeLeg);

        // Right foreleg - child of right leg
        rightForeLeg = new BendsModelPart(0, 22).setTextureSize(64, 64).setPosition(0, 6.0F, -2.0F);
        rightForeLeg.addCube(
                -3.9F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                0,
                16);
        rightLeg.addChild(rightForeLeg);

        return true;
    }

    //
    // Rebuilds the left limbs for current humanoid models whose 64x64 atlas has dedicated left-side
    // regions instead of the legacy mirrored right side. Adult piglin meshes use this layout, while
    // ordinary zombies deliberately keep the mirrored 64x32-compatible layout.
    //
    protected void useIndependentLeftLimbUvs(
            int armU, int armV, int legU, int legV, float scaleFactor) {
        body.getChildren().remove(leftArm);
        leftArm =
                new BendsModelPart(armU, armV)
                        .setTextureSize(64, 64)
                        .setPosition(5.0F, -10.0F, 0.0F);
        leftArm.addCube(
                -1.0F,
                -2.0F,
                -2.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                armU,
                armV);
        body.addChild(leftArm);

        leftForeArm =
                new BendsModelPart(armU, armV + 6)
                        .setTextureSize(64, 64)
                        .setPosition(0.0F, 4.0F, 2.0F);
        leftForeArm.addCube(-1.0F, 0.0F, -4.0F, 4, 6, 4, scaleFactor, LOWER_LIMB_FACES, armU, armV);
        leftArm.addChild(leftForeArm);

        leftLeg =
                new BendsModelPart(legU, legV)
                        .setTextureSize(64, 64)
                        .setPosition(0.0F, 12.0F, 0.0F);
        leftLeg.addCube(-0.1F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        leftForeLeg =
                new BendsModelPart(legU, legV + 6)
                        .setTextureSize(64, 64)
                        .setPosition(0.0F, 6.0F, -2.0F);
        leftForeLeg.addCube(
                -0.1F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                legU,
                legV);
        leftLeg.addChild(leftForeLeg);
    }

    protected BipedParts captureBipedParts() {
        return new BipedParts(
                body,
                head,
                headwear,
                leftArm,
                rightArm,
                leftForeArm,
                rightForeArm,
                leftLeg,
                rightLeg,
                leftForeLeg,
                rightForeLeg);
    }

    protected void selectBipedParts(BipedParts parts) {
        body = parts.body();
        head = parts.head();
        headwear = parts.headwear();
        leftArm = parts.leftArm();
        rightArm = parts.rightArm();
        leftForeArm = parts.leftForeArm();
        rightForeArm = parts.rightForeArm();
        leftLeg = parts.leftLeg();
        rightLeg = parts.rightLeg();
        leftForeLeg = parts.leftForeLeg();
        rightForeLeg = parts.rightForeLeg();
    }

    protected static void remapBabyPart(
            BendsModelPart part,
            float sourceX,
            float sourceY,
            float sourceZ,
            float targetX,
            float targetY,
            float targetZ) {
        if (part == null) {
            return;
        }
        part.position.set(
                targetX + (part.position.x - sourceX) * 0.5F,
                targetY + (part.position.y - sourceY) * 0.5F,
                targetZ + (part.position.z - sourceZ) * 0.5F);
        part.offset.set(part.offset.x * 0.5F, part.offset.y * 0.5F, part.offset.z * 0.5F);
        part.globalOffset.set(
                part.globalOffset.x * 0.5F, part.globalOffset.y * 0.5F, part.globalOffset.z * 0.5F);
    }

    @Override
    public void syncUpWithData(D data) {
        head.syncUp(data.head);
        body.syncUp(data.body);
        leftArm.syncUp(data.leftArm);
        rightArm.syncUp(data.rightArm);
        leftLeg.syncUp(data.leftLeg);
        rightLeg.syncUp(data.rightLeg);
        leftForeArm.syncUp(data.leftForeArm);
        rightForeArm.syncUp(data.rightForeArm);
        leftForeLeg.syncUp(data.leftForeLeg);
        rightForeLeg.syncUp(data.rightForeLeg);
    }

    // True, if this renderer wasn't mutated before.
    @Override
    public boolean isModelVanilla(M model) {
        // Check if we've already created custom parts
        return this.body == null;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof HumanoidModel);
    }

    @Override
    public boolean shouldRenderCustom() {
        return this.body != null;
    }

    @Override
    public void renderMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color) {
        // Render body and attached parts
        if (body != null) {
            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }

        // Render legs (not attached to body)
        if (leftLeg != null) {
            leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
        if (rightLeg != null) {
            rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }

    // Applies the split forearm and item-specific rotation after vanilla has positioned an arm.
    public void applyHeldItemTransform(
            HumanoidArm arm, BipedEntityData<?> data, PoseStack poseStack) {
        BendsModelPart foreArm = arm == HumanoidArm.LEFT ? leftForeArm : rightForeArm;
        if (foreArm == null || data == null) {
            return;
        }

        foreArm.applyLocalTransformPoseStack(poseStack);
        poseStack.translate(0.0F, -4.0F / 16.0F, -2.0F / 16.0F);
        poseStack.translate(0.0F, 8.0F / 16.0F, 0.0F);
        GlHelper.rotate(
                poseStack,
                (arm == HumanoidArm.LEFT
                                ? data.renderLeftItemRotation
                                : data.renderRightItemRotation)
                        .getSmooth());
        poseStack.translate(0.0F, -8.0F / 16.0F, 0.0F);
    }

    // Positions a torso-mounted model, such as elytra, in the animated body space.
    public void applyBodyAttachmentTransform(PoseStack poseStack) {
        if (body != null) {
            body.applyCharacterTransformPoseStack(poseStack);
            poseStack.translate(0.0F, -12.0F / 16.0F, 0.0F);
        }
    }

    // Getters for layers to access parts
    public BendsModelPart getBody() {
        return body;
    }

    public BendsModelPart getHead() {
        return head;
    }

    public BendsModelPart getLeftArm() {
        return leftArm;
    }

    public BendsModelPart getRightArm() {
        return rightArm;
    }

    public BendsModelPart getLeftForeArm() {
        return leftForeArm;
    }

    public BendsModelPart getRightForeArm() {
        return rightForeArm;
    }

    public BendsModelPart getLeftLeg() {
        return leftLeg;
    }

    public BendsModelPart getRightLeg() {
        return rightLeg;
    }

    public BendsModelPart getLeftForeLeg() {
        return leftForeLeg;
    }

    public BendsModelPart getRightForeLeg() {
        return rightForeLeg;
    }

    // Store the vanilla default positions so we can restore + offset
    private float[] vanillaBodyPos,
            vanillaHeadPos,
            vanillaLeftArmPos,
            vanillaRightArmPos,
            vanillaLeftLegPos,
            vanillaRightLegPos;
    private boolean vanillaPositionsStored = false;

    //
    // Sync animated poses from BendsModelParts to vanilla HumanoidModel ModelParts. This allows
    // vanilla layers (armor, held items) to use our animated poses.
    //
    public void syncPosesToVanillaModel(HumanoidModel<?> model) {
        if (model == null) return;

        // Store vanilla default positions on first call
        if (!vanillaPositionsStored) {
            vanillaBodyPos = new float[] {model.body.x, model.body.y, model.body.z};
            vanillaHeadPos = new float[] {model.head.x, model.head.y, model.head.z};
            vanillaLeftArmPos = new float[] {model.leftArm.x, model.leftArm.y, model.leftArm.z};
            vanillaRightArmPos = new float[] {model.rightArm.x, model.rightArm.y, model.rightArm.z};
            vanillaLeftLegPos = new float[] {model.leftLeg.x, model.leftLeg.y, model.leftLeg.z};
            vanillaRightLegPos = new float[] {model.rightLeg.x, model.rightLeg.y, model.rightLeg.z};
            vanillaPositionsStored = true;
        }

        Quaternion bodyRotation = body.rotation.getSmooth();
        float bodyPivotX =
                body.globalOffset.x + (body.position.x + body.offset.x) * body.offsetScale;
        float bodyPivotY =
                body.globalOffset.y + (body.position.y + body.offset.y) * body.offsetScale;
        float bodyPivotZ =
                body.globalOffset.z + (body.position.z + body.offset.z) * body.offsetScale;

        float[] bodyNeck = rotateVectorByQuaternion(bodyRotation, 0.0F, -12.0F, 0.0F);
        model.body.x = bodyPivotX + bodyNeck[0];
        model.body.y = bodyPivotY + bodyNeck[1];
        model.body.z = bodyPivotZ + bodyNeck[2];
        float[] bodyEuler = quaternionToEulerXYZ(bodyRotation);
        model.body.xRot = bodyEuler[0];
        model.body.yRot = bodyEuler[1];
        model.body.zRot = bodyEuler[2];
        model.body.visible = body.isShowing();

        syncBodyChildToModelPart(
                head, model.head, bodyPivotX, bodyPivotY, bodyPivotZ, bodyRotation);
        syncBodyChildToModelPart(
                leftArm, model.leftArm, bodyPivotX, bodyPivotY, bodyPivotZ, bodyRotation);
        syncBodyChildToModelPart(
                rightArm, model.rightArm, bodyPivotX, bodyPivotY, bodyPivotZ, bodyRotation);

        syncPartToModelPart(leftLeg, model.leftLeg, vanillaLeftLegPos);
        syncPartToModelPart(rightLeg, model.rightLeg, vanillaRightLegPos);

        // Sync hat visibility with head
        if (model.hat != null && head != null) {
            model.hat.visible = head.isShowing();
            // Sync hat position/rotation with head
            model.hat.x = model.head.x;
            model.hat.y = model.head.y;
            model.hat.z = model.head.z;
            model.hat.xRot = model.head.xRot;
            model.hat.yRot = model.head.yRot;
            model.hat.zRot = model.head.zRot;
        }
    }

    //
    // Sync a single BendsModelPart's transform to a vanilla ModelPart. Syncs rotation, visibility,
    // and animation position offsets.
    //
    private void syncPartToModelPart(
            BendsModelPart bendsPart, ModelPart modelPart, float[] vanillaPos) {
        if (bendsPart == null || modelPart == null) return;

        // Sync animation offset to vanilla model position
        // Add the animation offset to the stored vanilla base position
        if (vanillaPos != null) {
            modelPart.x = vanillaPos[0] + bendsPart.offset.x;
            modelPart.y = vanillaPos[1] + bendsPart.offset.y;
            modelPart.z = vanillaPos[2] + bendsPart.offset.z;
        }

        // Convert quaternion rotation to Euler angles (XYZ order)
        Quaternion q = bendsPart.rotation.getSmooth();
        float[] euler = quaternionToEulerXYZ(q);
        modelPart.xRot = euler[0];
        modelPart.yRot = euler[1];
        modelPart.zRot = euler[2];

        // Sync visibility
        modelPart.visible = bendsPart.isShowing();
    }

    private void syncBodyChildToModelPart(
            BendsModelPart child,
            ModelPart modelPart,
            float bodyPivotX,
            float bodyPivotY,
            float bodyPivotZ,
            Quaternion bodyRotation) {
        if (child == null || modelPart == null) return;
        float[] pivot = new float[3];
        Quaternion rotation =
                composeChildWorld(bodyPivotX, bodyPivotY, bodyPivotZ, bodyRotation, child, pivot);
        setEndModelPart(modelPart, pivot, rotation, child.isShowing());
    }

    private static Quaternion composeChildWorld(
            float parentPivotX,
            float parentPivotY,
            float parentPivotZ,
            Quaternion parentRotation,
            BendsModelPart child,
            float[] outPivot) {
        float lx = (child.position.x + child.offset.x) * child.offsetScale;
        float ly = (child.position.y + child.offset.y) * child.offsetScale;
        float lz = (child.position.z + child.offset.z) * child.offsetScale;
        float[] rotated = rotateVectorByQuaternion(parentRotation, lx, ly, lz);
        outPivot[0] = parentPivotX + rotated[0];
        outPivot[1] = parentPivotY + rotated[1];
        outPivot[2] = parentPivotZ + rotated[2];
        return Quaternion.mul(parentRotation, child.rotation.getSmooth(), new Quaternion());
    }

    private static void setEndModelPart(
            ModelPart modelPart, float[] pivot, Quaternion rotation, boolean visible) {
        modelPart.x = pivot[0];
        modelPart.y = pivot[1];
        modelPart.z = pivot[2];
        float[] euler = quaternionToEulerXYZ(rotation);
        modelPart.xRot = euler[0];
        modelPart.yRot = euler[1];
        modelPart.zRot = euler[2];
        modelPart.visible = visible;
    }

    private static float[] rotateVectorByQuaternion(Quaternion q, float x, float y, float z) {
        float tx = 2.0F * (q.y * z - q.z * y);
        float ty = 2.0F * (q.z * x - q.x * z);
        float tz = 2.0F * (q.x * y - q.y * x);
        return new float[] {
            x + q.w * tx + (q.y * tz - q.z * ty),
            y + q.w * ty + (q.z * tx - q.x * tz),
            z + q.w * tz + (q.x * ty - q.y * tx)
        };
    }

    //
    // Convert quaternion to Euler angles in XYZ rotation order (radians). Returns [xRot, yRot,
    // zRot].
    //
    private static float[] quaternionToEulerXYZ(Quaternion q) {
        float[] euler = new float[3];

        // X rotation (pitch)
        float sinX = 2.0f * (q.w * q.x + q.y * q.z);
        float cosX = 1.0f - 2.0f * (q.x * q.x + q.y * q.y);
        euler[0] = (float) Math.atan2(sinX, cosX);

        // Y rotation (yaw)
        float sinY = 2.0f * (q.w * q.y - q.z * q.x);
        if (Math.abs(sinY) >= 1.0f) {
            euler[1] = (float) Math.copySign(Math.PI / 2, sinY);
        } else {
            euler[1] = (float) Math.asin(sinY);
        }

        // Z rotation (roll)
        float sinZ = 2.0f * (q.w * q.z + q.x * q.y);
        float cosZ = 1.0f - 2.0f * (q.y * q.y + q.z * q.z);
        euler[2] = (float) Math.atan2(sinZ, cosZ);

        return euler;
    }
}
