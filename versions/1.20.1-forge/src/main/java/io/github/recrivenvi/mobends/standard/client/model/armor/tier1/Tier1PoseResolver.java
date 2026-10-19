package io.github.recrivenvi.mobends.standard.client.model.armor.tier1;

import io.github.recrivenvi.mobends.core.client.model.ModelPartTransform;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;

// Resolves animated armor bones from the 1.x entity data layout.
final class Tier1PoseResolver {
    private Tier1PoseResolver() {}

    static ModelPartTransform body(BipedEntityData<?> data) {
        return data.body;
    }

    static ModelPartTransform head(BipedEntityData<?> data) {
        return data.head;
    }

    static ModelPartTransform arm(BipedEntityData<?> data, boolean left) {
        return left ? data.leftArm : data.rightArm;
    }

    static ModelPartTransform foreArm(BipedEntityData<?> data, boolean left) {
        return left ? data.leftForeArm : data.rightForeArm;
    }

    static ModelPartTransform leg(BipedEntityData<?> data, boolean left) {
        return left ? data.leftLeg : data.rightLeg;
    }

    static ModelPartTransform foreLeg(BipedEntityData<?> data, boolean left) {
        return left ? data.leftForeLeg : data.rightForeLeg;
    }
}
