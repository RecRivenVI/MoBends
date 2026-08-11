package goblinbob.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import goblinbob.mobends.core.client.model.BendsCube;
import goblinbob.mobends.core.client.model.BendsModelPart;
import goblinbob.mobends.core.client.model.BendsPlane;
import goblinbob.mobends.core.client.model.BoxSide;
import goblinbob.mobends.core.client.model.FaceRotation;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.data.WolfData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.animal.Wolf;

public class WolfMutator extends Mutator<WolfData, Wolf, WolfModel<Wolf>> {
    private static final byte ALL_FACES = 0b111111;
    private static final byte WITHOUT_TOP = (byte) (ALL_FACES & ~(1 << BendsCube.TOP));
    private static final byte WITHOUT_BOTTOM = (byte) (ALL_FACES & ~(1 << BendsCube.BOTTOM));
    private static final BendsPlane MOUTH_BOTTOM =
            BendsPlane.xz(-1.5F, 0.0F, -4.0F, 3.0F, 4.0F, true, 0, 0, 3, 4, 8, 8);
    private static final BendsPlane MOUTH_TOP =
            BendsPlane.xz(-1.5F, 1.0F, -4.0F, 3.0F, 4.0F, false, 0, 0, 3, 4, 8, 8);
    private static final BendsPlane MOUTH_INSIDE =
            BendsPlane.xz(-1.5F, -4.1F, -3.0F, 3.0F, 1.0F, true, 0, 0, 3, 4, 8, 8);
    private static final BendsPlane TONGUE =
            BendsPlane.xz(-1.5F, 0.0F, -4.0F, 3.0F, 6.0F, true, 3, 0, 6, 6, 8, 8);

    private boolean baby;

    public BendsModelPart wolfHeadMain;
    public BendsModelPart wolfBody;
    public BendsModelPart wolfLeg1;
    public BendsModelPart wolfLeg2;
    public BendsModelPart wolfLeg3;
    public BendsModelPart wolfLeg4;
    public BendsModelPart wolfTail;
    public BendsModelPart wolfMane;
    public BendsModelPart nose;
    public BendsModelPart mouth;
    public BendsModelPart tongue;
    public BendsModelPart leftEar;
    public BendsModelPart rightEar;
    public BendsModelPart foreLeg1;
    public BendsModelPart foreLeg2;
    public BendsModelPart foreLeg3;
    public BendsModelPart foreLeg4;

    public WolfMutator(IEntityDataFactory<Wolf> dataFactory) {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(WolfModel<Wolf> model) {
        this.vanillaModel = model;
    }

    @Override
    public void applyVanillaModel(WolfModel<Wolf> model) {}

    @Override
    public void swapLayer(
            LivingEntityRenderer<Wolf, WolfModel<Wolf>> renderer,
            int index,
            boolean isModelVanilla) {}

    @Override
    public void deswapLayer(LivingEntityRenderer<Wolf, WolfModel<Wolf>> renderer, int index) {}

    @Override
    public boolean createParts(WolfModel<Wolf> original, float scaleFactor) {
        wolfBody = part(18, 14).setPosition(0.0F, 14.0F, 8.0F);
        wolfBody.addDevelopedCube(-3.0F, -3.0F, -8.0F, 6, 6, 9, scaleFactor)
                .offsetTextureQuad(BoxSide.TOP.ordinal(), 9.0F, 6.0F)
                .rotateTextureQuad(BoxSide.TOP.ordinal(), FaceRotation.HALF_TURN)
                .offsetTextureQuad(BoxSide.BACK.ordinal(), -12.0F, -9.0F)
                .rotateTextureQuad(BoxSide.BOTTOM.ordinal(), FaceRotation.HALF_TURN)
                .offsetTextureQuad(BoxSide.BOTTOM.ordinal(), -8.0F, 6.0F)
                .rotateTextureQuad(BoxSide.LEFT.ordinal(), FaceRotation.CLOCKWISE)
                .offsetTextureQuad(BoxSide.LEFT.ordinal(), -3.0F, -3.0F)
                .rotateTextureQuad(BoxSide.RIGHT.ordinal(), FaceRotation.COUNTER_CLOCKWISE)
                .offsetTextureQuad(BoxSide.RIGHT.ordinal(), 0.0F, -3.0F);

        wolfHeadMain = part(0, 0).setPosition(0.0F, -0.5F, -13.0F);
        wolfHeadMain.addCube(-3.0F, -3.0F, -4.0F, 6, 6, 4, scaleFactor);
        wolfBody.addChild(wolfHeadMain);

        wolfMane = part(21, 0).setPosition(0.0F, -0.5F, -12.0F);
        wolfMane.addDevelopedCube(-4.0F, -3.5F, -2.0F, 8, 7, 6, scaleFactor)
                .offsetTextureQuad(BoxSide.TOP.ordinal(), 1.0F, 7.0F)
                .rotateTextureQuad(BoxSide.TOP.ordinal(), FaceRotation.HALF_TURN)
                .offsetTextureQuad(BoxSide.BACK.ordinal(), -5.0F, -6.0F)
                .offsetTextureQuad(BoxSide.BOTTOM.ordinal(), 8.0F, 7.0F)
                .rotateTextureQuad(BoxSide.BOTTOM.ordinal(), FaceRotation.HALF_TURN)
                .rotateTextureQuad(BoxSide.LEFT.ordinal(), FaceRotation.CLOCKWISE)
                .offsetTextureQuad(BoxSide.LEFT.ordinal(), -14.0F, 1.0F)
                .rotateTextureQuad(BoxSide.RIGHT.ordinal(), FaceRotation.COUNTER_CLOCKWISE)
                .offsetTextureQuad(BoxSide.RIGHT.ordinal(), 15.0F, 1.0F)
                .offsetTextureQuad(BoxSide.FRONT.ordinal(), 1.0F, -6.0F);
        wolfBody.addChild(wolfMane);

        wolfLeg1 = createUpperLeg(-2.0F, 3.0F, -1.0F, scaleFactor, true);
        wolfLeg2 = createUpperLeg(2.0F, 3.0F, -1.0F, scaleFactor, false);
        wolfLeg3 = createUpperLeg(-2.0F, 3.0F, -12.0F, scaleFactor, true);
        wolfLeg4 = createUpperLeg(2.0F, 3.0F, -12.0F, scaleFactor, false);
        wolfBody.addChild(wolfLeg1);
        wolfBody.addChild(wolfLeg2);
        wolfBody.addChild(wolfLeg3);
        wolfBody.addChild(wolfLeg4);

        foreLeg1 = createLowerLeg(-1.0F, scaleFactor, true);
        foreLeg2 = createLowerLeg(-1.0F, scaleFactor, false);
        foreLeg3 = createLowerLeg(1.0F, scaleFactor, true);
        foreLeg4 = createLowerLeg(1.0F, scaleFactor, false);
        wolfLeg1.addChild(foreLeg1);
        wolfLeg2.addChild(foreLeg2);
        wolfLeg3.addChild(foreLeg3);
        wolfLeg4.addChild(foreLeg4);

        wolfTail = part(9, 18).setPosition(0.0F, -3.0F, 0.0F);
        wolfTail.addCube(-1.0F, 0.0F, -2.0F, 2, 8, 2, scaleFactor);
        wolfBody.addChild(wolfTail);

        nose = part(0, 10).setPosition(0.0F, 1.0F, -3.0F);
        nose.addCube(-1.5F, -1.0F, -4.0F, 3, 2, 4, scaleFactor, WITHOUT_BOTTOM);
        wolfHeadMain.addChild(nose);

        mouth = part(0, 12).setPosition(0.0F, 2.0F, -3.0F);
        mouth.addCube(-1.5F, 0.0F, -4.0F, 3, 1, 4, scaleFactor, WITHOUT_TOP);
        wolfHeadMain.addChild(mouth);

        tongue = part(0, 0).setPosition(0.0F, 2.0F, -3.0F);
        wolfHeadMain.addChild(tongue);

        leftEar = part(16, 14).setPosition(-2.0F, -3.0F, -1.0F);
        leftEar.addCube(-1.0F, -2.0F, -1.0F, 2, 2, 1, scaleFactor);
        wolfHeadMain.addChild(leftEar);

        rightEar = part(16, 14).setPosition(2.0F, -3.0F, -1.0F);
        rightEar.addCube(-1.0F, -2.0F, -1.0F, 2, 2, 1, scaleFactor);
        wolfHeadMain.addChild(rightEar);

        return true;
    }

    private static BendsModelPart part(int u, int v) {
        return new BendsModelPart(u, v).setTextureSize(64, 32);
    }

    private static BendsModelPart createUpperLeg(
            float x, float y, float z, float scaleFactor, boolean mirror) {
        BendsModelPart part = part(0, 18).setPosition(x, y, z);
        part.addCube(-1.0F, 0.0F, -1.0F, 2, 4, 2, scaleFactor);
        return part;
    }

    private static BendsModelPart createLowerLeg(float z, float scaleFactor, boolean mirror) {
        BendsModelPart part = part(0, 18).setPosition(0.0F, 4.0F, z);
        part.addCube(-1.0F, 0.0F, z < 0.0F ? 0.0F : -2.0F, 2, 4, 2, scaleFactor);
        return part;
    }

    @Override
    public void syncUpWithData(WolfData data) {
        baby = data.getEntity().isBaby();
        wolfHeadMain.syncUp(data.head);
        wolfBody.syncUp(data.body);
        wolfLeg1.syncUp(data.leg1);
        wolfLeg2.syncUp(data.leg2);
        wolfLeg3.syncUp(data.leg3);
        wolfLeg4.syncUp(data.leg4);
        wolfTail.syncUp(data.tail);
        wolfMane.syncUp(data.mane);
        nose.syncUp(data.nose);
        mouth.syncUp(data.mouth);
        tongue.syncUp(data.tongue);
        leftEar.syncUp(data.leftEar);
        rightEar.syncUp(data.rightEar);
        foreLeg1.syncUp(data.foreLeg1);
        foreLeg2.syncUp(data.foreLeg2);
        foreLeg3.syncUp(data.foreLeg3);
        foreLeg4.syncUp(data.foreLeg4);
    }

    @Override
    public boolean isModelVanilla(WolfModel<Wolf> model) {
        return wolfBody == null;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof WolfModel);
    }

    @Override
    public boolean shouldRenderCustom() {
        return wolfBody != null;
    }

    @Override
    public void renderMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color) {
        if (wolfBody == null) {
            return;
        }

        if (!baby) {
            wolfBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            return;
        }

        // 1.20.1/1.21.1 use one adult wolf mesh and AgeableListModel renders
        // the head and body with different child transforms. The mixin replaces
        // that method, so reproduce its two passes around the Mo' Bends hierarchy.
        boolean headVisible = wolfHeadMain.visible;
        wolfHeadMain.visible = false;
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0.0F, 24.0F / 16.0F, 0.0F);
        wolfBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();
        wolfHeadMain.visible = headVisible;

        if (headVisible) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 5.0F / 16.0F, 2.0F / 16.0F);
            wolfHeadMain.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            poseStack.popPose();
        }
    }

    public void renderMisc(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color) {
        if (wolfBody == null) {
            return;
        }

        poseStack.pushPose();
        if (baby) {
            // Matches the original Mo' Bends child correction used by its
            // wolf mouth/tongue layer.
            poseStack.translate(0.0F, 10.0F / 16.0F, 0.0F);
            wolfBody.applyLocalTransformPoseStack(poseStack, 1.0F / 32.0F);
            wolfHeadMain.applyLocalTransformPoseStack(poseStack);
        } else {
            wolfHeadMain.applyCharacterTransformPoseStack(poseStack);
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        MOUTH_INSIDE.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.pushPose();
        nose.applyLocalTransformPoseStack(poseStack);
        MOUTH_TOP.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.pushPose();
        mouth.applyLocalTransformPoseStack(poseStack);
        MOUTH_BOTTOM.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.pushPose();
        tongue.applyLocalTransformPoseStack(poseStack);
        TONGUE.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.popPose();
    }
}
