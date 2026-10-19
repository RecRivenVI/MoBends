package io.github.recrivenvi.mobends.standard.animation.bit.biped.item;

import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import net.minecraft.world.entity.HumanoidArm;

@FunctionalInterface
public interface ItemActionFactory<T extends AnimationBit<?>> {
    T create(HumanoidArm actionHand);
}
