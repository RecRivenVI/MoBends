package goblinbob.mobends.standard.client.model.armor.tier1;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.standard.client.model.armor.ArmorPoseHelper;

/** Restores the model-space anchor removed while capturing a torso part at the origin. */
final class Tier1BodyAnchor {
    private Tier1BodyAnchor() {}

    static void apply(PoseStack poseStack, Tier1PartCapture.State state) {
        apply(poseStack, state.x(), state.y(), state.z());
    }

    static void apply(PoseStack poseStack, float x, float y, float z) {
        poseStack.translate(
                x * ArmorPoseHelper.SCALE, y * ArmorPoseHelper.SCALE, z * ArmorPoseHelper.SCALE);
    }
}
