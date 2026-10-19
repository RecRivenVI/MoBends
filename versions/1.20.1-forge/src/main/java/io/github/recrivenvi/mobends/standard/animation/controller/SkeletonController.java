// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.animation.controller;

import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import io.github.recrivenvi.mobends.core.animation.controller.IAnimationController;
import io.github.recrivenvi.mobends.core.animation.layer.HardAnimationLayer;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.JumpAnimationBit;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.item.BipedActionController;
import io.github.recrivenvi.mobends.standard.animation.bit.skeleton.StandAnimationBit;
import io.github.recrivenvi.mobends.standard.animation.bit.skeleton.WalkAnimationBit;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.data.SkeletonData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//
// This is an animation controller for a skeleton instance. It's a part of the EntityData structure.
//
public class SkeletonController implements IAnimationController<SkeletonData> {
    protected HardAnimationLayer<BipedEntityData<Skeleton>> layerBase;
    protected AnimationBit<? extends BipedEntityData<Skeleton>> bitStand, bitWalk, bitJump;

    protected final BipedActionController actionController = new BipedActionController();

    public SkeletonController() {
        this.layerBase = new HardAnimationLayer<>();

        this.bitStand = new StandAnimationBit();
        this.bitWalk = new WalkAnimationBit();
        this.bitJump = new JumpAnimationBit<>();
    }

    public void performActionAnimations(
            SkeletonData data, Skeleton skeleton, Collection<String> actions) {
        final HumanoidArm primaryHand = skeleton.getMainArm();
        final ItemStack heldItemMainhand = skeleton.getMainHandItem();
        final ItemStack heldItemOffhand = skeleton.getOffhandItem();
        final Item activeItem = skeleton.getUseItem().getItem();

        actionController.perform(
                data, primaryHand, heldItemMainhand, heldItemOffhand, activeItem, actions);
    }

    @Override
    public Collection<String> perform(SkeletonData skeletonData) {
        Skeleton skeleton = skeletonData.getEntity();

        if (!skeletonData.isOnGround() || skeletonData.getTicksAfterTouchdown() < 1) {
            this.layerBase.playOrContinueBit(bitJump, skeletonData);
        } else {
            if (skeletonData.isStillHorizontally()) {
                this.layerBase.playOrContinueBit(bitStand, skeletonData);
            } else {
                this.layerBase.playOrContinueBit(bitWalk, skeletonData);
            }
        }

        List<String> actions = new ArrayList<>();
        this.layerBase.perform(skeletonData, actions);
        this.performActionAnimations(skeletonData, skeleton, actions);
        return actions;
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard.animation.controller;

import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import io.github.recrivenvi.mobends.core.animation.controller.IAnimationController;
import io.github.recrivenvi.mobends.core.animation.layer.HardAnimationLayer;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.JumpAnimationBit;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.item.BipedActionController;
import io.github.recrivenvi.mobends.standard.animation.bit.skeleton.StandAnimationBit;
import io.github.recrivenvi.mobends.standard.animation.bit.skeleton.WalkAnimationBit;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.data.SkeletonData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//
// This is an animation controller for a skeleton instance. It's a part of the EntityData structure.
//
public class SkeletonController implements IAnimationController<SkeletonData> {
    protected HardAnimationLayer<BipedEntityData<Skeleton>> layerBase;
    protected AnimationBit<? extends BipedEntityData<Skeleton>> bitStand, bitWalk, bitJump;

    protected final BipedActionController actionController = new BipedActionController();

    public SkeletonController() {
        this.layerBase = new HardAnimationLayer<>();

        this.bitStand = new StandAnimationBit();
        this.bitWalk = new WalkAnimationBit();
        this.bitJump = new JumpAnimationBit<>();
    }

    public void performActionAnimations(
            SkeletonData data, Skeleton skeleton, Collection<String> actions) {
        final HumanoidArm primaryHand = skeleton.getMainArm();
        final ItemStack heldItemMainhand = skeleton.getMainHandItem();
        final ItemStack heldItemOffhand = skeleton.getOffhandItem();
        final Item activeItem = skeleton.getUseItem().getItem();

        actionController.perform(
                data, primaryHand, heldItemMainhand, heldItemOffhand, activeItem, actions);
    }

    @Override
    public Collection<String> perform(SkeletonData skeletonData) {
        Skeleton skeleton = skeletonData.getEntity();

        if (!skeletonData.isOnGround() || skeletonData.getTicksAfterTouchdown() < 1) {
            this.layerBase.playOrContinueBit(bitJump, skeletonData);
        } else {
            if (skeletonData.isStillHorizontally()) {
                this.layerBase.playOrContinueBit(bitStand, skeletonData);
            } else {
                this.layerBase.playOrContinueBit(bitWalk, skeletonData);
            }
        }

        List<String> actions = new ArrayList<>();
        this.layerBase.perform(skeletonData, actions);
        this.performActionAnimations(skeletonData, skeleton, actions);
        return actions;
    }
}
// ?}
