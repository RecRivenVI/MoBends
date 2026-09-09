//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.util.GlHelper;
import net.minecraft.world.entity.LivingEntity;

public abstract class MutatedRenderer<T extends LivingEntity> {

    protected final float scale = 0.0625F;

    // Called right before the entity is rendered
    public void beforeRender(
            EntityData<T> data, T entity, float partialTicks, PoseStack poseStack) {
        // NOTE: The poseStack is already translated to the entity's position by
        // EntityRenderDispatcher. Do NOT add an extra entity-to-view translation here,
        // as that creates a double-offset that causes remote players to visually teleport
        // during rotational animations (attack spins, etc.).

        // Remove body rotation so we can apply animations in entity-local space
        applyEntityLocalRotation(poseStack, entity.yBodyRotO, entity.yBodyRot, partialTicks);

        this.renderLocalAccessories(entity, data, partialTicks, poseStack);

        float childScale = entity.isBaby() ? getChildScale() : 1.0F;
        float childModelScale = entity.isBaby() ? getChildModelScale() : 1.0F;

        if (childModelScale != 1.0F) {
            poseStack.scale(childModelScale, childModelScale, childModelScale);
        }

        // Original Mo' Bends animations are authored against adult dimensions.
        // Keep their world-space offsets at childScale even when a modern entity,
        // such as the 26.x wolf, already supplies dedicated baby geometry and does
        // not need the model itself scaled down.
        float childOffsetCompensation = childScale / childModelScale;
        poseStack.translate(
                data.globalOffset.getX() * scale * childOffsetCompensation,
                data.globalOffset.getY() * scale * childOffsetCompensation,
                data.globalOffset.getZ() * scale * childOffsetCompensation);
        float centerY = entity.getBbHeight() / (2.0F * childModelScale);
        poseStack.translate(0, centerY, 0);
        GlHelper.rotate(poseStack, data.centerRotation.getSmooth());
        poseStack.translate(0, -centerY, 0);
        GlHelper.rotate(poseStack, data.renderRotation.getSmooth());

        poseStack.translate(
                data.localOffset.getX() * scale * childOffsetCompensation,
                data.localOffset.getY() * scale * childOffsetCompensation,
                data.localOffset.getZ() * scale * childOffsetCompensation);

        this.transformLocally(entity, data, partialTicks, poseStack);

        // Re-add body rotation
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        interpolateRotation(entity.yBodyRotO, entity.yBodyRot, partialTicks)));
    }

    // Called right after the entity is rendered.
    public void afterRender(T entity, float partialTicks, PoseStack poseStack) {
        // No default behaviour
    }

    //
    // Used to render accessories for that entity, e.g. Sword trails. Also used to transform the
    // entity, like offset or rotate it.
    //
    protected void renderLocalAccessories(
            T entity, EntityData<?> data, float partialTicks, PoseStack poseStack) {
        // No default behaviour
    }

    protected void transformLocally(
            T entity, EntityData<?> data, float partialTicks, PoseStack poseStack) {
        // No default behaviour
    }

    protected static float interpolateRotation(
            float prevYawOffset, float yawOffset, float partialTicks) {
        float f;
        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F)
            ;

        while (f >= 180.0F) f -= 360.0F;

        return prevYawOffset + partialTicks * f;
    }

    //
    // Applies the body-yaw transform used before rendering entity-local accessories. Sword-trail
    // geometry is submitted separately on deferred renderers, so it must copy this exact transform
    // before its pose is captured.
    //
    public static void applyEntityLocalRotation(
            PoseStack poseStack, float previousBodyYaw, float bodyYaw, float partialTicks) {
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        -interpolateRotation(previousBodyYaw, bodyYaw, partialTicks)));
    }

    protected float getChildScale() {
        return 0.5F;
    }

    //
    // Model-space child scaling used by ports that replace vanilla child rendering. Dedicated baby
    // meshes override this without changing the scale of animation offsets authored for the
    // original adult model.
    //
    protected float getChildModelScale() {
        return getChildScale();
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.core.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.util.GlHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.entity.LivingEntity;

public abstract class MutatedRenderer<T extends LivingEntity> {

    protected final float scale = 0.0625F;
    protected final TextureManager textureManager;

    public MutatedRenderer() {
        textureManager = Minecraft.getInstance().getTextureManager();
    }

    // Called right before the entity is rendered
    public void beforeRender(
            EntityData<T> data, T entity, float partialTicks, PoseStack poseStack) {
        // NOTE: The poseStack is already translated to the entity's position by
        // EntityRenderDispatcher. Do NOT add an extra entity-to-view translation here,
        // as that creates a double-offset that causes remote players to visually teleport
        // during rotational animations (attack spins, etc.).

        // Remove body rotation so we can apply animations in entity-local space
        applyEntityLocalRotation(poseStack, entity.yBodyRotO, entity.yBodyRot, partialTicks);

        this.renderLocalAccessories(entity, data, partialTicks, poseStack);

        float childScale = entity.isBaby() ? getChildScale() : 1.0F;
        float childModelScale = entity.isBaby() ? getChildModelScale() : 1.0F;

        if (childModelScale != 1.0F) {
            poseStack.scale(childModelScale, childModelScale, childModelScale);
        }

        // The original renderer scales animation offsets for children, not the whole
        // model. Compensate for the model-space scale so the resulting world-space
        // offset remains childScale on renderers that still use global child scaling.
        float childOffsetCompensation = childScale / childModelScale;
        poseStack.translate(
                data.globalOffset.getX() * scale * childOffsetCompensation,
                data.globalOffset.getY() * scale * childOffsetCompensation,
                data.globalOffset.getZ() * scale * childOffsetCompensation);
        float centerY = entity.getBbHeight() / (2.0F * childModelScale);
        poseStack.translate(0, centerY, 0);
        GlHelper.rotate(poseStack, data.centerRotation.getSmooth());
        poseStack.translate(0, -centerY, 0);
        GlHelper.rotate(poseStack, data.renderRotation.getSmooth());

        poseStack.translate(
                data.localOffset.getX() * scale * childOffsetCompensation,
                data.localOffset.getY() * scale * childOffsetCompensation,
                data.localOffset.getZ() * scale * childOffsetCompensation);

        this.transformLocally(entity, data, partialTicks, poseStack);

        // Re-add body rotation
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        interpolateRotation(entity.yBodyRotO, entity.yBodyRot, partialTicks)));
    }

    // Called right after the entity is rendered.
    public void afterRender(T entity, float partialTicks, PoseStack poseStack) {
        // No default behaviour
    }

    //
    // Used to render accessories for that entity, e.g. Sword trails. Also used to transform the
    // entity, like offset or rotate it.
    //
    protected void renderLocalAccessories(
            T entity, EntityData<?> data, float partialTicks, PoseStack poseStack) {
        // No default behaviour
    }

    protected void transformLocally(
            T entity, EntityData<?> data, float partialTicks, PoseStack poseStack) {
        // No default behaviour
    }

    protected static float interpolateRotation(
            float prevYawOffset, float yawOffset, float partialTicks) {
        float f;
        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F)
            ;

        while (f >= 180.0F) f -= 360.0F;

        return prevYawOffset + partialTicks * f;
    }

    //
    // Applies the body-yaw transform used before rendering entity-local accessories. Sword-trail
    // geometry is submitted separately on deferred renderers, so it must copy this exact transform
    // before its pose is captured.
    //
    public static void applyEntityLocalRotation(
            PoseStack poseStack, float previousBodyYaw, float bodyYaw, float partialTicks) {
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        -interpolateRotation(previousBodyYaw, bodyYaw, partialTicks)));
    }

    protected float getChildScale() {
        return 0.5F;
    }

    //
    // Model-space child scaling used by ports that replace vanilla child rendering. Renderers such
    // as the wolf reproduce vanilla's separate head/body passes and therefore opt out to avoid
    // applying the child scale twice.
    //
    protected float getChildModelScale() {
        return getChildScale();
    }
}*/
//?}
