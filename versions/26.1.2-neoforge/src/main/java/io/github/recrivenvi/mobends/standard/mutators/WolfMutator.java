package io.github.recrivenvi.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.recrivenvi.mobends.core.client.model.BendsCube;
import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.client.model.BendsPlane;
import io.github.recrivenvi.mobends.core.client.model.BoxSide;
import io.github.recrivenvi.mobends.core.client.model.FaceRotation;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import io.github.recrivenvi.mobends.lib.util.GUtil;
import io.github.recrivenvi.mobends.standard.data.WolfData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class WolfMutator extends Mutator<WolfData, Wolf, WolfModel> {
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
    private static final BendsPlane BABY_MOUTH_BOTTOM =
            BendsPlane.xz(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, true, 0, 0, 3, 4, 8, 8);
    private static final BendsPlane BABY_MOUTH_TOP =
            BendsPlane.xz(-1.5F, 1.0F, -1.0F, 3.0F, 2.0F, false, 0, 0, 3, 4, 8, 8);
    private static final BendsPlane BABY_MOUTH_INSIDE =
            BendsPlane.xz(-1.5F, -3.1F, -1.76F, 3.0F, 1.0F, true, 0, 0, 3, 4, 8, 8);
    private static final BendsPlane BABY_TONGUE =
            BendsPlane.xz(-1.5F, 0.0F, -2.0F, 3.0F, 4.0F, true, 3, 0, 6, 6, 8, 8);

    private WolfParts adultParts;
    private WolfParts babyParts;
    private boolean babyGeometry;

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
    public void storeVanillaModel(WolfModel model) {
        this.vanillaModel = model;
    }

    @Override
    public void applyVanillaModel(WolfModel model) {}

    @Override
    public void swapLayer(
            LivingEntityRenderer<?, ?, ?> renderer, int index, boolean isModelVanilla) {}

    @Override
    public void deswapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index) {}

    @Override
    public boolean createParts(WolfModel original, float scaleFactor) {
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

        adultParts = captureParts();
        createBabyParts(scaleFactor);
        babyParts = captureParts();
        selectParts(false);
        return true;
    }

    private void createBabyParts(float scaleFactor) {
        wolfBody = babyPart(0, 0).setPosition(0.0F, 19.0F, 0.0F);
        wolfBody.addCube(-3.0F, -2.0F, -4.0F, 6, 4, 8, scaleFactor);

        wolfHeadMain = babyPart(0, 12).setPosition(0.0F, -0.75F, -4.0F);
        wolfHeadMain.addCube(-2.99F, -3.25F, -3.0F, 6, 5, 5, scaleFactor + 0.025F);
        wolfBody.addChild(wolfHeadMain);

        // Baby wolves no longer have the adult upper-body/mane cube. Keep an
        // empty animation node so existing packs can still address "mane".
        wolfMane = babyPart(0, 0).setPosition(0.0F, 0.0F, 0.0F);
        wolfBody.addChild(wolfMane);

        wolfLeg1 = createBabyUpperLeg(-1.5F, 2.0F, 3.0F, 0, 22, scaleFactor);
        wolfLeg2 = createBabyUpperLeg(1.5F, 2.0F, 3.0F, 8, 22, scaleFactor);
        wolfLeg3 = createBabyUpperLeg(-1.5F, 2.0F, -3.0F, 0, 0, scaleFactor);
        wolfLeg4 = createBabyUpperLeg(1.5F, 2.0F, -3.0F, 20, 0, scaleFactor);
        wolfBody.addChild(wolfLeg1);
        wolfBody.addChild(wolfLeg2);
        wolfBody.addChild(wolfLeg3);
        wolfBody.addChild(wolfLeg4);

        foreLeg1 = createBabyLowerLeg(0, 22, -1.0F, scaleFactor);
        foreLeg2 = createBabyLowerLeg(8, 22, -1.0F, scaleFactor);
        foreLeg3 = createBabyLowerLeg(0, 0, 1.0F, scaleFactor);
        foreLeg4 = createBabyLowerLeg(20, 0, 1.0F, scaleFactor);
        wolfLeg1.addChild(foreLeg1);
        wolfLeg2.addChild(foreLeg2);
        wolfLeg3.addChild(foreLeg3);
        wolfLeg4.addChild(foreLeg4);

        // 26.x gives the baby tail an empty animated root and a nearly
        // inverted child mesh. Flattening both into one downward cube makes
        // the small interest-wag rotation look like a very fast full swing.
        wolfTail = babyPart(0, 0).setPosition(0.0F, 0.0F, 3.0F);
        BendsModelPart tailGeometry = babyPart(22, 16).setPosition(0.0F, -0.6F, 0.2F);
        tailGeometry.addCube(-1.0F, -5.7F, -1.0F, 2, 6, 2, scaleFactor);
        tailGeometry.rotation.orientX(-3.1F * GUtil.RAD_TO_DEG).finish();
        wolfTail.addChild(tailGeometry);
        wolfBody.addChild(wolfTail);

        // Keep the vanilla muzzle bounds, but move its transform origin to
        // the centre. The mouth and tongue can then hinge at the rear lower
        // edge without reusing the adult wolf's four-pixel muzzle depth.
        nose = babyPart(17, 12).setPosition(0.0F, 0.76F, -4.0F);
        nose.addCube(-1.5F, -1.0F, -1.0F, 3, 2, 2, scaleFactor, WITHOUT_BOTTOM);
        wolfHeadMain.addChild(nose);

        // The baby atlas has no separate jaw, so its animated surfaces use the
        // lower rear edge of the two-pixel muzzle as their hinge.
        mouth = babyPart(0, 0).setPosition(0.0F, 1.76F, -3.0F);
        tongue = babyPart(0, 0).setPosition(0.0F, 1.76F, -3.0F);
        wolfHeadMain.addChild(mouth);
        wolfHeadMain.addChild(tongue);

        leftEar = babyPart(0, 5).setPosition(-2.0F, -4.25F, -0.5F);
        leftEar.addCube(-1.0F, -1.0F, -0.5F, 2, 2, 1, scaleFactor);
        wolfHeadMain.addChild(leftEar);
        rightEar = babyPart(20, 5).setPosition(2.0F, -4.25F, -0.5F);
        rightEar.addCube(-1.0F, -1.0F, -0.5F, 2, 2, 1, scaleFactor);
        wolfHeadMain.addChild(rightEar);
    }

    private static BendsModelPart part(int u, int v) {
        return new BendsModelPart(u, v).setTextureSize(64, 32);
    }

    private static BendsModelPart babyPart(int u, int v) {
        return new BendsModelPart(u, v).setTextureSize(32, 32);
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

    private static BendsModelPart createBabyUpperLeg(
            float x, float y, float z, int u, int v, float scaleFactor) {
        BendsModelPart part = babyPart(u, v).setPosition(x, y, z);
        // Original Mo' Bends keeps the joint caps on both leg segments. The
        // dedicated 26.x baby texture still contains the native top/bottom UVs,
        // so retain the upper segment's bottom cap instead of leaving an open
        // wedge when the lower segment bends away from it.
        part.addCube(-1.0F, 0.0F, -1.0F, 2, 2, 2, scaleFactor, ALL_FACES);
        return part;
    }

    private static BendsModelPart createBabyLowerLeg(
            int u, int v, float hingeZ, float scaleFactor) {
        BendsModelPart part = babyPart(u, v + 2).setPosition(0.0F, 2.0F, hingeZ);
        BendsCube cube =
                new BendsCube(
                        u,
                        v + 2,
                        -1.0F,
                        0.0F,
                        hingeZ < 0.0F ? 0.0F : -2.0F,
                        2,
                        1,
                        2,
                        scaleFactor,
                        32.0F,
                        32.0F,
                        false,
                        ALL_FACES,
                        u,
                        v);
        // Moving the lower segment's side strip down by two texels preserves
        // the final row of the vanilla three-pixel leg. Like the original
        // adult Mo' Bends model, pivot the lower segment on the appropriate
        // front/back edge instead of the centre of its top face. This keeps
        // the boxes exactly aligned while standing without overlap or z-fighting.
        // Move only its joint cap back to the original top-face region.
        cube.offsetTextureQuad(BendsCube.TOP, 0.0F, -2.0F);
        part.addCube(cube);
        return part;
    }

    @Override
    public void syncUpWithData(WolfData data) {
        babyGeometry = data.getEntity().isBaby();
        selectParts(babyGeometry);
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

        if (babyGeometry) {
            remap(wolfBody, 0.0F, 14.0F, 8.0F, 0.0F, 19.0F, 0.0F);
            remap(wolfHeadMain, 0.0F, -0.5F, -13.0F, 0.0F, -0.75F, -4.0F);
            remap(wolfMane, 0.0F, -0.5F, -12.0F, 0.0F, 0.0F, 0.0F);
            remap(wolfLeg1, -2.0F, 3.0F, -1.0F, -1.5F, 2.0F, 3.0F);
            remap(wolfLeg2, 2.0F, 3.0F, -1.0F, 1.5F, 2.0F, 3.0F);
            remap(wolfLeg3, -2.0F, 3.0F, -12.0F, -1.5F, 2.0F, -3.0F);
            remap(wolfLeg4, 2.0F, 3.0F, -12.0F, 1.5F, 2.0F, -3.0F);
            remap(wolfTail, 0.0F, -3.0F, 0.0F, 0.0F, 0.0F, 3.0F);
            remap(nose, 0.0F, 1.0F, -3.0F, 0.0F, 0.76F, -4.0F);
            remap(mouth, 0.0F, 2.0F, -3.0F, 0.0F, 1.76F, -3.0F);
            remap(tongue, 0.0F, 2.0F, -3.0F, 0.0F, 1.76F, -3.0F);
            remap(leftEar, -2.0F, -3.0F, -1.0F, -2.0F, -4.25F, -0.5F);
            remap(rightEar, 2.0F, -3.0F, -1.0F, 2.0F, -4.25F, -0.5F);
            remap(foreLeg1, 0.0F, 4.0F, -1.0F, 0.0F, 2.0F, -1.0F);
            remap(foreLeg2, 0.0F, 4.0F, -1.0F, 0.0F, 2.0F, -1.0F);
            remap(foreLeg3, 0.0F, 4.0F, 1.0F, 0.0F, 2.0F, 1.0F);
            remap(foreLeg4, 0.0F, 4.0F, 1.0F, 0.0F, 2.0F, 1.0F);
        }
    }

    private static void remap(
            BendsModelPart part,
            float sourceX,
            float sourceY,
            float sourceZ,
            float targetX,
            float targetY,
            float targetZ) {
        part.position.set(
                targetX + (part.position.x - sourceX) * 0.5F,
                targetY + (part.position.y - sourceY) * 0.5F,
                targetZ + (part.position.z - sourceZ) * 0.5F);
        part.offset.set(part.offset.x * 0.5F, part.offset.y * 0.5F, part.offset.z * 0.5F);
        part.globalOffset.set(
                part.globalOffset.x * 0.5F, part.globalOffset.y * 0.5F, part.globalOffset.z * 0.5F);
    }

    private WolfParts captureParts() {
        return new WolfParts(
                wolfHeadMain,
                wolfBody,
                wolfLeg1,
                wolfLeg2,
                wolfLeg3,
                wolfLeg4,
                wolfTail,
                wolfMane,
                nose,
                mouth,
                tongue,
                leftEar,
                rightEar,
                foreLeg1,
                foreLeg2,
                foreLeg3,
                foreLeg4);
    }

    private void selectParts(boolean baby) {
        WolfParts selected = baby ? babyParts : adultParts;
        if (selected == null) {
            return;
        }
        wolfHeadMain = selected.head;
        wolfBody = selected.body;
        wolfLeg1 = selected.leg1;
        wolfLeg2 = selected.leg2;
        wolfLeg3 = selected.leg3;
        wolfLeg4 = selected.leg4;
        wolfTail = selected.tail;
        wolfMane = selected.mane;
        nose = selected.nose;
        mouth = selected.mouth;
        tongue = selected.tongue;
        leftEar = selected.leftEar;
        rightEar = selected.rightEar;
        foreLeg1 = selected.foreLeg1;
        foreLeg2 = selected.foreLeg2;
        foreLeg3 = selected.foreLeg3;
        foreLeg4 = selected.foreLeg4;
    }

    private record WolfParts(
            BendsModelPart head,
            BendsModelPart body,
            BendsModelPart leg1,
            BendsModelPart leg2,
            BendsModelPart leg3,
            BendsModelPart leg4,
            BendsModelPart tail,
            BendsModelPart mane,
            BendsModelPart nose,
            BendsModelPart mouth,
            BendsModelPart tongue,
            BendsModelPart leftEar,
            BendsModelPart rightEar,
            BendsModelPart foreLeg1,
            BendsModelPart foreLeg2,
            BendsModelPart foreLeg3,
            BendsModelPart foreLeg4) {}

    @Override
    public boolean isModelVanilla(WolfModel model) {
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
        if (wolfBody != null) {
            wolfBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
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
        wolfHeadMain.applyCharacterTransformPoseStack(poseStack);

        BendsPlane mouthInside = babyGeometry ? BABY_MOUTH_INSIDE : MOUTH_INSIDE;
        BendsPlane mouthTop = babyGeometry ? BABY_MOUTH_TOP : MOUTH_TOP;
        BendsPlane mouthBottom = babyGeometry ? BABY_MOUTH_BOTTOM : MOUTH_BOTTOM;
        BendsPlane tonguePlane = babyGeometry ? BABY_TONGUE : TONGUE;

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        mouthInside.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.pushPose();
        nose.applyLocalTransformPoseStack(poseStack);
        mouthTop.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.pushPose();
        mouth.applyLocalTransformPoseStack(poseStack);
        mouthBottom.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.pushPose();
        tongue.applyLocalTransformPoseStack(poseStack);
        tonguePlane.render(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();

        poseStack.popPose();
    }
}
