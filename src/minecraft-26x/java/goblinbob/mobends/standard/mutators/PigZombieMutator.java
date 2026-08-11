package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.model.BendsModelPart;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.lib.util.GUtil;
import goblinbob.mobends.standard.data.PigZombieData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.monster.piglin.ZombifiedPiglinModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

public class PigZombieMutator
        extends BipedMutator<PigZombieData, ZombifiedPiglin, ZombifiedPiglinModel> {

    // Should the height of the texture be 64 or 32(half)?
    protected boolean halfTexture = false;
    private BendsModelPart leftEar;
    private BendsModelPart rightEar;
    private BendsModelPart adultLeftEar;
    private BendsModelPart adultRightEar;
    private BendsModelPart babyLeftEar;
    private BendsModelPart babyRightEar;
    private BipedParts adultParts;
    private BipedParts babyParts;

    public PigZombieMutator(IEntityDataFactory<ZombifiedPiglin> dataFactory) {
        super(dataFactory);
    }

    @Override
    public void fetchFields(LivingEntityRenderer<?, ?, ?> renderer) {
        super.fetchFields(renderer);

        // In 1.20.1, the texture height is not easily accessible from the model
        // Default to full texture
        this.halfTexture = false;
    }

    @Override
    public void storeVanillaModel(ZombifiedPiglinModel model) {
        // In 1.20.1, models are created differently - using baked model definitions
        // For now, store a reference to indicate this is vanilla
        this.vanillaModel = model;

        // Calling the super method here, since it
        // requires the vanillaModel property to be
        // set.
        super.storeVanillaModel(model);
    }

    @Override
    public boolean createParts(ZombifiedPiglinModel original, float scaleFactor) {
        if (!super.createParts(original, scaleFactor)) {
            return false;
        }
        useIndependentLeftLimbUvs(32, 48, 16, 48, scaleFactor);

        // Zombified piglins use the modern 10-pixel piglin head and animated ears.
        body.getChildren().remove(head);
        head = new BendsModelPart(0, 0).setTextureSize(64, 64).setPosition(0.0F, -12.0F, 0.0F);
        head.addCube(-5.0F, -8.0F, -4.0F, 10, 8, 8, scaleFactor);
        body.addChild(head);

        BendsModelPart snout = new BendsModelPart(31, 1).setTextureSize(64, 64);
        snout.addCube(-2.0F, -4.0F, -5.0F, 4, 4, 1, scaleFactor);
        head.addChild(snout);
        BendsModelPart leftNostril = new BendsModelPart(2, 4).setTextureSize(64, 64);
        leftNostril.addCube(2.0F, -2.0F, -5.0F, 1, 2, 1, scaleFactor);
        head.addChild(leftNostril);
        BendsModelPart rightNostril = new BendsModelPart(2, 0).setTextureSize(64, 64);
        rightNostril.addCube(-3.0F, -2.0F, -5.0F, 1, 2, 1, scaleFactor);
        head.addChild(rightNostril);

        leftEar = new BendsModelPart(51, 6).setTextureSize(64, 64).setPosition(4.5F, -6.0F, 0.0F);
        leftEar.addCube(0.0F, 0.0F, -2.0F, 1, 5, 4, scaleFactor);
        head.addChild(leftEar);
        rightEar = new BendsModelPart(39, 6).setTextureSize(64, 64).setPosition(-4.5F, -6.0F, 0.0F);
        rightEar.addCube(-1.0F, 0.0F, -2.0F, 1, 5, 4, scaleFactor);
        head.addChild(rightEar);
        headwear = null;

        adultLeftEar = leftEar;
        adultRightEar = rightEar;
        adultParts = captureBipedParts();
        createBabyParts(scaleFactor);
        babyLeftEar = leftEar;
        babyRightEar = rightEar;
        babyParts = captureBipedParts();
        selectGeometry(false);
        return true;
    }

    private void createBabyParts(float scaleFactor) {
        body = babyPart(0, 13).setPosition(0.0F, 18.0F, -0.5F);
        body.addCube(-3.0F, -3.0F, -1.0F, 6, 5, 3, scaleFactor);

        head = babyPart(0, 0).setPosition(0.0F, -3.0F, 0.5F);
        head.addCube(-4.5F, -6.0F, -3.5F, 9, 6, 7, scaleFactor);
        BendsModelPart snout = babyPart(21, 30);
        snout.addCube(-1.5F, -3.0F, -4.5F, 3, 3, 1, scaleFactor);
        head.addChild(snout);
        body.addChild(head);
        headwear = null;

        leftEar = babyPart(0, 0).setPosition(4.2F, -4.0F, 0.0F);
        BendsModelPart leftEarGeometry = babyPart(0, 21).setPosition(1.0F, 1.75F, 0.0F);
        leftEarGeometry.addCube(-0.5F, -3.0F, -2.0F, 1, 6, 4, scaleFactor);
        leftEarGeometry.rotation.orientZ(-35.0F).finish();
        leftEar.addChild(leftEarGeometry);
        head.addChild(leftEar);
        rightEar = babyPart(0, 0).setPosition(-4.2F, -4.0F, 0.0F);
        BendsModelPart rightEarGeometry = babyPart(18, 13).setPosition(-1.0F, 1.75F, 0.0F);
        rightEarGeometry.addCube(-0.5F, -3.0F, -2.0F, 1, 6, 4, scaleFactor);
        rightEarGeometry.rotation.orientZ(35.0F).finish();
        rightEar.addChild(rightEarGeometry);
        head.addChild(rightEar);

        leftArm = createBabyUpperArm(4.0F, 28, 13, scaleFactor, false);
        rightArm = createBabyUpperArm(-4.0F, 10, 30, scaleFactor, true);
        body.addChild(leftArm);
        body.addChild(rightArm);
        leftForeArm = createBabyLowerArm(28, 13, scaleFactor);
        rightForeArm = createBabyLowerArm(10, 30, scaleFactor);
        leftArm.addChild(leftForeArm);
        rightArm.addChild(rightForeArm);

        leftLeg = createBabyUpperLeg(1.5F, 10, 23, scaleFactor);
        rightLeg = createBabyUpperLeg(-1.5F, 22, 23, scaleFactor);
        leftForeLeg = createBabyLowerLeg(10, 23, scaleFactor);
        rightForeLeg = createBabyLowerLeg(22, 23, scaleFactor);
        leftLeg.addChild(leftForeLeg);
        rightLeg.addChild(rightForeLeg);
    }

    private static BendsModelPart babyPart(int u, int v) {
        return new BendsModelPart(u, v).setTextureSize(64, 64);
    }

    private static BendsModelPart createBabyUpperArm(
            float x, int u, int v, float scaleFactor, boolean right) {
        BendsModelPart part = babyPart(u, v).setPosition(x, -3.0F, 0.5F);
        part.addCube(
                -1.0F,
                0.0F,
                -1.5F,
                2,
                2,
                3,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + (right ? 0.1F : 0.01F),
                UPPER_LIMB_FACES,
                u,
                v);
        return part;
    }

    private static BendsModelPart createBabyLowerArm(int u, int v, float scaleFactor) {
        BendsModelPart part = babyPart(u, v + 2).setPosition(0.0F, 2.0F, 0.0F);
        part.addCube(-1.0F, 0.0F, -1.5F, 2, 3, 3, scaleFactor, LOWER_LIMB_FACES, u, v);
        return part;
    }

    private static BendsModelPart createBabyUpperLeg(float x, int u, int v, float scaleFactor) {
        BendsModelPart part = babyPart(u, v).setPosition(x, 20.0F, 0.0F);
        part.addCube(-1.5F, 0.0F, -1.5F, 3, 2, 3, scaleFactor);
        return part;
    }

    private static BendsModelPart createBabyLowerLeg(int u, int v, float scaleFactor) {
        BendsModelPart part = babyPart(u, v + 2).setPosition(0.0F, 2.0F, 0.0F);
        part.addCube(
                -1.5F,
                0.0F,
                -1.5F,
                3,
                2,
                3,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                u,
                v);
        return part;
    }

    @Override
    public void syncUpWithData(PigZombieData data) {
        boolean baby = data.getEntity().isBaby();
        selectGeometry(baby);
        super.syncUpWithData(data);
        if (baby) {
            remapBabyPart(body, 0.0F, 12.0F, 0.0F, 0.0F, 18.0F, -0.5F);
            remapBabyPart(head, 0.0F, -12.0F, 0.0F, 0.0F, -3.0F, 0.5F);
            remapBabyPart(leftArm, 5.0F, -10.0F, 0.0F, 4.0F, -3.0F, 0.5F);
            remapBabyPart(rightArm, -5.0F, -10.0F, 0.0F, -4.0F, -3.0F, 0.5F);
            remapBabyPart(leftForeArm, 0.0F, 4.0F, 2.0F, 0.0F, 2.0F, 0.0F);
            remapBabyPart(rightForeArm, 0.0F, 4.0F, 2.0F, 0.0F, 2.0F, 0.0F);
            remapBabyPart(leftLeg, 0.0F, 12.0F, 0.0F, 1.5F, 20.0F, 0.0F);
            remapBabyPart(rightLeg, 0.0F, 12.0F, 0.0F, -1.5F, 20.0F, 0.0F);
            remapBabyPart(leftForeLeg, 0.0F, 6.0F, -2.0F, 0.0F, 2.0F, 0.0F);
            remapBabyPart(rightForeLeg, 0.0F, 6.0F, -2.0F, 0.0F, 2.0F, 0.0F);
        }
        ZombifiedPiglin piglin = data.getEntity();
        float partialTick = DataUpdateHandler.partialTicks;
        float animationPos = piglin.walkAnimation.position(partialTick);
        float animationSpeed = piglin.walkAnimation.speed(partialTick);
        float frequency = (piglin.tickCount + partialTick) * 0.1F + animationPos * 0.5F;
        float amplitude = 0.08F + animationSpeed * 0.4F;
        float defaultAngle = piglin.isBaby() ? 5.0F : 30.0F;
        leftEar.rotation
                .orientZ(
                        -defaultAngle
                                - (float) Math.cos(frequency * 1.2F) * amplitude * GUtil.RAD_TO_DEG)
                .finish();
        rightEar.rotation
                .orientZ(defaultAngle + (float) Math.cos(frequency) * amplitude * GUtil.RAD_TO_DEG)
                .finish();
    }

    private void selectGeometry(boolean baby) {
        selectBipedParts(baby ? babyParts : adultParts);
        leftEar = baby ? babyLeftEar : adultLeftEar;
        rightEar = baby ? babyRightEar : adultRightEar;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof ZombifiedPiglinModel);
    }
}
