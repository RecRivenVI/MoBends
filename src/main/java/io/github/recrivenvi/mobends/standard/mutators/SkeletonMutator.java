//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.mutators;

import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.data.SkeletonData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.monster.skeleton.Skeleton;

// Instantiated once per SkeletonRenderer
public class SkeletonMutator
        extends BipedMutator<
                SkeletonData,
                Skeleton,
                SkeletonModel<net.minecraft.client.renderer.entity.state.SkeletonRenderState>> {

    protected boolean boneLimbs = false;

    public SkeletonMutator(IEntityDataFactory<Skeleton> dataCreationFunction) {
        super(dataCreationFunction);
    }

    @Override
    public void fetchFields(LivingEntityRenderer<?, ?, ?> renderer) {
        super.fetchFields(renderer);

        // In 1.20.1, skeleton limbs are always bone-style (thin)
        this.boneLimbs = true;
    }

    @Override
    public void storeVanillaModel(
            SkeletonModel<net.minecraft.client.renderer.entity.state.SkeletonRenderState> model) {
        super.storeVanillaModel(model);
    }

    @Override
    public boolean createParts(
            SkeletonModel<net.minecraft.client.renderer.entity.state.SkeletonRenderState> original,
            float scaleFactor) {
        // Create custom bendable parts using BendsModelPart
        // Body - root of upper body hierarchy
        body = new BendsModelPart(16, 16).setTextureSize(64, 32).setPosition(0.0F, 12.0F, 0.0F);
        body.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor);

        // Head - child of body
        head = new BendsModelPart(0, 0).setTextureSize(64, 32).setPosition(0.0F, -12.0F, 0.0F);
        head.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor);
        body.addChild(head);

        // Headwear - child of head
        headwear = new BendsModelPart(32, 0).setTextureSize(64, 32);
        headwear.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor + 0.5F);
        head.addChild(headwear);

        // Skeleton uses thin bone limbs (2 pixels wide)
        // Right Arm - child of body
        rightArm =
                new BendsModelPart(40, 16).setTextureSize(64, 32).setPosition(-5.0F, -10.0F, 0.0F);
        rightArm.addCube(
                -1.0F,
                -2.0F,
                -1.0F,
                2,
                6,
                2,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                40,
                16);
        body.addChild(rightArm);

        // Left Arm - child of body
        leftArm =
                new BendsModelPart(40, 16)
                        .setTextureSize(64, 32)
                        .setPosition(5.0F, -10.0F, 0.0F)
                        .setMirror(true);
        leftArm.addCube(
                -1.0F,
                -2.0F,
                -1.0F,
                2,
                6,
                2,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                40,
                16);
        body.addChild(leftArm);

        // Right Forearm - child of rightArm
        rightForeArm =
                new BendsModelPart(40, 16 + 6).setTextureSize(64, 32).setPosition(0.0F, 4.0F, 1.0F);
        rightForeArm.addCube(-1.0F, 0.0F, -2.0F, 2, 6, 2, scaleFactor, LOWER_LIMB_FACES, 40, 16);
        rightArm.addChild(rightForeArm);

        // Left Forearm - child of leftArm
        leftForeArm =
                new BendsModelPart(40, 16 + 6)
                        .setTextureSize(64, 32)
                        .setPosition(0.0F, 4.0F, 1.0F)
                        .setMirror(true);
        leftForeArm.addCube(-1.0F, 0.0F, -2.0F, 2, 6, 2, scaleFactor, LOWER_LIMB_FACES, 40, 16);
        leftArm.addChild(leftForeArm);

        // Legs (also thin) - independent roots
        rightLeg = new BendsModelPart(0, 16).setTextureSize(64, 32).setPosition(-2.0F, 12.0F, 0.0F);
        rightLeg.addCube(-1.0F, 0.0F, -1.0F, 2, 6, 2, scaleFactor);

        leftLeg =
                new BendsModelPart(0, 16)
                        .setTextureSize(64, 32)
                        .setPosition(2.0F, 12.0F, 0.0F)
                        .setMirror(true);
        leftLeg.addCube(-1.0F, 0.0F, -1.0F, 2, 6, 2, scaleFactor);

        // Right Foreleg - child of rightLeg
        rightForeLeg =
                new BendsModelPart(0, 16 + 6).setTextureSize(64, 32).setPosition(0.0F, 6.0F, -2.0F);
        rightForeLeg.addCube(
                -1.0F,
                0.0F,
                0.0F,
                2,
                6,
                2,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                0,
                16);
        rightLeg.addChild(rightForeLeg);

        // Left Foreleg - child of leftLeg
        leftForeLeg =
                new BendsModelPart(0, 16 + 6)
                        .setTextureSize(64, 32)
                        .setPosition(0.0F, 6.0F, -2.0F)
                        .setMirror(true);
        leftForeLeg.addCube(
                -1.0F,
                0.0F,
                0.0F,
                2,
                6,
                2,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                0,
                16);
        leftLeg.addChild(leftForeLeg);

        return true;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof SkeletonModel);
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.mutators;

import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.data.SkeletonData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.monster.Skeleton;

// Instantiated once per SkeletonRenderer
public class SkeletonMutator extends BipedMutator<SkeletonData, Skeleton, SkeletonModel<Skeleton>> {

    protected boolean boneLimbs = false;

    public SkeletonMutator(IEntityDataFactory<Skeleton> dataCreationFunction) {
        super(dataCreationFunction);
    }

    @Override
    public void fetchFields(LivingEntityRenderer<Skeleton, SkeletonModel<Skeleton>> renderer) {
        super.fetchFields(renderer);

        // In 1.20.1, skeleton limbs are always bone-style (thin)
        this.boneLimbs = true;
    }

    @Override
    public void storeVanillaModel(SkeletonModel<Skeleton> model) {
        super.storeVanillaModel(model);
    }

    @Override
    public boolean createParts(SkeletonModel<Skeleton> original, float scaleFactor) {
        // Create custom bendable parts using BendsModelPart
        // Body - root of upper body hierarchy
        body = new BendsModelPart(16, 16).setTextureSize(64, 32).setPosition(0.0F, 12.0F, 0.0F);
        body.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor);

        // Head - child of body
        head = new BendsModelPart(0, 0).setTextureSize(64, 32).setPosition(0.0F, -12.0F, 0.0F);
        head.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor);
        body.addChild(head);

        // Headwear - child of head
        headwear = new BendsModelPart(32, 0).setTextureSize(64, 32);
        headwear.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor + 0.5F);
        head.addChild(headwear);

        // Skeleton uses thin bone limbs (2 pixels wide)
        // Right Arm - child of body
        rightArm =
                new BendsModelPart(40, 16).setTextureSize(64, 32).setPosition(-5.0F, -10.0F, 0.0F);
        rightArm.addCube(-1.0F, -2.0F, -1.0F, 2, 6, 2, scaleFactor);
        body.addChild(rightArm);

        // Left Arm - child of body
        leftArm =
                new BendsModelPart(40, 16)
                        .setTextureSize(64, 32)
                        .setPosition(5.0F, -10.0F, 0.0F)
                        .setMirror(true);
        leftArm.addCube(-1.0F, -2.0F, -1.0F, 2, 6, 2, scaleFactor);
        body.addChild(leftArm);

        // Right Forearm - child of rightArm
        rightForeArm =
                new BendsModelPart(40, 16 + 6).setTextureSize(64, 32).setPosition(0.0F, 4.0F, 1.0F);
        rightForeArm.addCube(-1.0F, 0.0F, -2.0F, 2, 6, 2, scaleFactor);
        rightArm.addChild(rightForeArm);

        // Left Forearm - child of leftArm
        leftForeArm =
                new BendsModelPart(40, 16 + 6)
                        .setTextureSize(64, 32)
                        .setPosition(0.0F, 4.0F, 1.0F)
                        .setMirror(true);
        leftForeArm.addCube(-1.0F, 0.0F, -2.0F, 2, 6, 2, scaleFactor);
        leftArm.addChild(leftForeArm);

        // Legs (also thin) - independent roots
        rightLeg = new BendsModelPart(0, 16).setTextureSize(64, 32).setPosition(-2.0F, 12.0F, 0.0F);
        rightLeg.addCube(-1.0F, 0.0F, -1.0F, 2, 6, 2, scaleFactor);

        leftLeg =
                new BendsModelPart(0, 16)
                        .setTextureSize(64, 32)
                        .setPosition(2.0F, 12.0F, 0.0F)
                        .setMirror(true);
        leftLeg.addCube(-1.0F, 0.0F, -1.0F, 2, 6, 2, scaleFactor);

        // Right Foreleg - child of rightLeg
        rightForeLeg =
                new BendsModelPart(0, 16 + 6).setTextureSize(64, 32).setPosition(0.0F, 6.0F, -2.0F);
        rightForeLeg.addCube(-1.0F, 0.0F, 0.0F, 2, 6, 2, scaleFactor);
        rightLeg.addChild(rightForeLeg);

        // Left Foreleg - child of leftLeg
        leftForeLeg =
                new BendsModelPart(0, 16 + 6)
                        .setTextureSize(64, 32)
                        .setPosition(0.0F, 6.0F, -2.0F)
                        .setMirror(true);
        leftForeLeg.addCube(-1.0F, 0.0F, 0.0F, 2, 6, 2, scaleFactor);
        leftLeg.addChild(leftForeLeg);

        return true;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof SkeletonModel);
    }
}*/
//?}
