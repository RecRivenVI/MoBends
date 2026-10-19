package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.data.EntityData;
import net.minecraft.client.player.AbstractClientPlayer;

//
// Renderer for player entities with Mo' Bends animations. Updated for 1.20.1 to use PoseStack
// instead of GlStateManager.
//
public class PlayerRenderer extends BipedRenderer<AbstractClientPlayer> {

    static final float MODERN_GROUNDED_CROUCH_COMPENSATION = 2.0F;

    @Override
    protected void transformLocally(
            AbstractClientPlayer entity,
            EntityData<?> data,
            float partialTicks,
            PoseStack poseStack) {
        if (entity.isCrouching()) {
            if (entity.getAbilities().flying) {
                poseStack.translate(0F, 4F * scale, 0F);
            } else {
                // AvatarRenderer already moves crouching players down by two model
                // pixels in 26.x. Cancel only that vanilla-model offset here and
                // leave SneakAnimationBit's original -1.3 pixel grounding offset
                // in control of the replacement Mo' Bends skeleton.
                poseStack.translate(0F, MODERN_GROUNDED_CROUCH_COMPENSATION * scale, 0F);
            }
        }
    }
}
