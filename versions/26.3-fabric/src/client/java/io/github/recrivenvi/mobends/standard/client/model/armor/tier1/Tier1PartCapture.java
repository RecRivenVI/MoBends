package io.github.recrivenvi.mobends.standard.client.model.armor.tier1;

import net.minecraft.client.model.geom.ModelPart;

/** Captures and restores a model part while its geometry is rendered at the origin. */
final class Tier1PartCapture {
    private Tier1PartCapture() {}

    static State moveToOrigin(ModelPart part) {
        State state = new State(part.x, part.y, part.z, part.xRot, part.yRot, part.zRot);
        part.x = 0.0F;
        part.y = 0.0F;
        part.z = 0.0F;
        part.xRot = 0.0F;
        part.yRot = 0.0F;
        part.zRot = 0.0F;
        return state;
    }

    static void restore(ModelPart part, State state) {
        part.x = state.x();
        part.y = state.y();
        part.z = state.z();
        part.xRot = state.xRot();
        part.yRot = state.yRot();
        part.zRot = state.zRot();
    }

    record State(float x, float y, float z, float xRot, float yRot, float zRot) {}
}
