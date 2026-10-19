package io.github.recrivenvi.mobends.standard.client.model.armor.tier1;

import io.github.recrivenvi.mobends.core.client.model.IModelPart;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.mutators.BipedMutator;

// Resolves animated armor bones from the active 26.x mutator or entity data fallback.
final class Tier1PoseResolver {
    private Tier1PoseResolver() {}

    static IModelPart body(BipedEntityData<?> data, BipedMutator<?, ?, ?> mutator) {
        return mutator != null ? mutator.getBody() : data.body;
    }

    static IModelPart head(BipedEntityData<?> data, BipedMutator<?, ?, ?> mutator) {
        return mutator != null ? mutator.getHead() : data.head;
    }

    static IModelPart arm(BipedEntityData<?> data, BipedMutator<?, ?, ?> mutator, boolean left) {
        if (mutator != null) {
            return left ? mutator.getLeftArm() : mutator.getRightArm();
        }
        return left ? data.leftArm : data.rightArm;
    }

    static IModelPart foreArm(
            BipedEntityData<?> data, BipedMutator<?, ?, ?> mutator, boolean left) {
        if (mutator != null) {
            return left ? mutator.getLeftForeArm() : mutator.getRightForeArm();
        }
        return left ? data.leftForeArm : data.rightForeArm;
    }

    static IModelPart leg(BipedEntityData<?> data, BipedMutator<?, ?, ?> mutator, boolean left) {
        if (mutator != null) {
            return left ? mutator.getLeftLeg() : mutator.getRightLeg();
        }
        return left ? data.leftLeg : data.rightLeg;
    }

    static IModelPart foreLeg(
            BipedEntityData<?> data, BipedMutator<?, ?, ?> mutator, boolean left) {
        if (mutator != null) {
            return left ? mutator.getLeftForeLeg() : mutator.getRightForeLeg();
        }
        return left ? data.leftForeLeg : data.rightForeLeg;
    }
}
