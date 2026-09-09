//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.mutators;

import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.data.ZombieData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class ZombieMutator
        extends ZombieMutatorBase<
                ZombieData,
                Zombie,
                ZombieModel<net.minecraft.client.renderer.entity.state.ZombieRenderState>> {
    private BipedParts adultParts;
    private BipedParts babyParts;

    public ZombieMutator(IEntityDataFactory<Zombie> dataFactory) {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(
            ZombieModel<net.minecraft.client.renderer.entity.state.ZombieRenderState> model) {
        super.storeVanillaModel(model);
    }

    @Override
    public boolean createParts(
            ZombieModel<net.minecraft.client.renderer.entity.state.ZombieRenderState> original,
            float scaleFactor) {
        if (!super.createParts(original, scaleFactor)) {
            return false;
        }
        adultParts = captureBipedParts();
        createBabyParts(scaleFactor);
        babyParts = captureBipedParts();
        selectBipedParts(adultParts);
        return true;
    }

    private void createBabyParts(float scaleFactor) {
        body = babyPart(16, 16).setPosition(0.0F, 17.5F, 0.0F);
        body.addCube(-2.0F, -2.5F, -1.0F, 4, 5, 2, scaleFactor);

        head = babyPart(3, 3).setPosition(0.0F, -2.25F, 0.0F);
        head.addCube(-3.0F, -6.25F, -3.0F, 6, 6, 6, scaleFactor);
        body.addChild(head);
        headwear = babyPart(35, 3);
        headwear.addCube(-3.0F, -6.15F, -3.0F, 6, 6, 6, scaleFactor + 0.25F);
        head.addChild(headwear);

        rightArm = createBabyUpperArm(-3.0F, 36, 16, scaleFactor, true);
        leftArm = createBabyUpperArm(3.0F, 28, 16, scaleFactor, false);
        body.addChild(rightArm);
        body.addChild(leftArm);
        rightForeArm = createBabyLowerArm(36, 16, scaleFactor);
        leftForeArm = createBabyLowerArm(28, 16, scaleFactor);
        rightArm.addChild(rightForeArm);
        leftArm.addChild(leftForeArm);

        rightLeg = createBabyUpperLeg(-1.0F, 8, 16, scaleFactor);
        leftLeg = createBabyUpperLeg(1.0F, 0, 16, scaleFactor);
        rightForeLeg = createBabyLowerLeg(8, 16, scaleFactor);
        leftForeLeg = createBabyLowerLeg(0, 16, scaleFactor);
        rightLeg.addChild(rightForeLeg);
        leftLeg.addChild(leftForeLeg);
    }

    private static BendsModelPart babyPart(int u, int v) {
        return new BendsModelPart(u, v).setTextureSize(64, 64);
    }

    private static BendsModelPart createBabyUpperArm(
            float x, int u, int v, float scaleFactor, boolean right) {
        BendsModelPart part = babyPart(u, v).setPosition(x, -2.0F, 0.0F);
        part.addCube(
                -1.0F,
                -0.5F,
                -1.0F,
                2,
                2,
                2,
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
        part.addCube(-1.0F, -0.5F, -1.0F, 2, 3, 2, scaleFactor, LOWER_LIMB_FACES, u, v);
        return part;
    }

    private static BendsModelPart createBabyUpperLeg(float x, int u, int v, float scaleFactor) {
        BendsModelPart part = babyPart(u, v).setPosition(x, 20.0F, 0.0F);
        part.addCube(-1.0F, 0.0F, -1.0F, 2, 2, 2, scaleFactor);
        return part;
    }

    private static BendsModelPart createBabyLowerLeg(int u, int v, float scaleFactor) {
        BendsModelPart part = babyPart(u, v + 2).setPosition(0.0F, 2.0F, 0.0F);
        part.addCube(
                -1.0F,
                0.0F,
                -1.0F,
                2,
                2,
                2,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                u,
                v);
        return part;
    }

    @Override
    public void syncUpWithData(ZombieData data) {
        boolean baby = data.getEntity().isBaby();
        selectBipedParts(baby ? babyParts : adultParts);
        super.syncUpWithData(data);
        if (!baby) {
            return;
        }

        remapBabyPart(body, 0.0F, 12.0F, 0.0F, 0.0F, 17.5F, 0.0F);
        remapBabyPart(head, 0.0F, -12.0F, 0.0F, 0.0F, -2.25F, 0.0F);
        remapBabyPart(rightArm, -5.0F, -10.0F, 0.0F, -3.0F, -2.0F, 0.0F);
        remapBabyPart(leftArm, 5.0F, -10.0F, 0.0F, 3.0F, -2.0F, 0.0F);
        remapBabyPart(rightForeArm, 0.0F, 4.0F, 2.0F, 0.0F, 2.0F, 0.0F);
        remapBabyPart(leftForeArm, 0.0F, 4.0F, 2.0F, 0.0F, 2.0F, 0.0F);
        remapBabyPart(rightLeg, 0.0F, 12.0F, 0.0F, -1.0F, 20.0F, 0.0F);
        remapBabyPart(leftLeg, 0.0F, 12.0F, 0.0F, 1.0F, 20.0F, 0.0F);
        remapBabyPart(rightForeLeg, 0.0F, 6.0F, -2.0F, 0.0F, 2.0F, 0.0F);
        remapBabyPart(leftForeLeg, 0.0F, 6.0F, -2.0F, 0.0F, 2.0F, 0.0F);
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof ZombieModel);
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.mutators;

import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.data.ZombieData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieMutator extends ZombieMutatorBase<ZombieData, Zombie, ZombieModel<Zombie>> {

    public ZombieMutator(IEntityDataFactory<Zombie> dataFactory) {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(ZombieModel<Zombie> model) {
        super.storeVanillaModel(model);
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof ZombieModel);
    }
}*/
//?}
