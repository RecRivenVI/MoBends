//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.data.EntityData;
import net.minecraft.world.entity.LivingEntity;

//
// Renderer for bipedal entities with Mo' Bends animations. Updated for 1.20.1 to use PoseStack and
// RenderSystem instead of GlStateManager.
//
public class BipedRenderer<T extends LivingEntity> extends MutatedRenderer<T> {

    @Override
    protected void transformLocally(
            T entity, EntityData<?> data, float partialTicks, PoseStack poseStack) {
        if (entity.isCrouching()) {
            poseStack.translate(0F, 5F * scale, 0F);
        }
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import net.minecraft.world.entity.LivingEntity;

//
// Renderer for bipedal entities with Mo' Bends animations. Updated for 1.20.1 to use PoseStack and
// RenderSystem instead of GlStateManager.
//
public class BipedRenderer<T extends LivingEntity> extends MutatedRenderer<T> {

    @Override
    protected void renderLocalAccessories(
            T entity, EntityData<?> data, float partialTicks, PoseStack poseStack) {
        if (data instanceof BipedEntityData) {
            BipedEntityData<?> bipedData = (BipedEntityData<?>) data;
            if (ModConfig.showSwordTrail) {
                poseStack.pushPose();
                poseStack.scale(scale, scale, scale);
                bipedData.swordTrail.render(poseStack);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }
        }
    }

    @Override
    protected void transformLocally(
            T entity, EntityData<?> data, float partialTicks, PoseStack poseStack) {
        if (entity.isCrouching()) {
            poseStack.translate(0F, 5F * scale, 0F);
        }
    }
}*/
//?}
