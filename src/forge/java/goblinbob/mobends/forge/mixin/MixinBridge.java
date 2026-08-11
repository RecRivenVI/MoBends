package goblinbob.mobends.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.client.MoBendsRenderContext;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.main.ModStatics;
import goblinbob.mobends.standard.mutators.BipedMutator;
import goblinbob.mobends.standard.mutators.PlayerMutator;
import goblinbob.mobends.standard.mutators.SpiderMutator;
import goblinbob.mobends.standard.mutators.SquidMutator;
import goblinbob.mobends.standard.mutators.WolfMutator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Bridge class for mixins to access common-mc classes. This allows mixins in the forge module to
 * interact with the shared code without causing module conflicts during dev runs.
 */
public final class MixinBridge {

    private MixinBridge() {}

    /**
     * Check if a biped mutator is active and should render custom. CRITICAL: Only returns true if
     * we're in the main entity model render phase. During layer rendering (armor, elytra, etc.),
     * returns false so vanilla renders normally.
     *
     * @return true if custom rendering should be used
     */
    public static boolean shouldRenderBipedCustom() {
        // Only intercept if we're in the main model render phase
        if (!MoBendsRenderContext.isInMainModelRender()) {
            return false;
        }
        BipedMutator<?, ?, ?> mutator = MoBendsRenderContext.getCurrentBipedMutator();
        return mutator != null && mutator.shouldRenderCustom();
    }

    /**
     * Render the biped using the current mutator. Ends the main model render phase so layers
     * (armor, elytra) render normally.
     *
     * <p>MC 1.20.1: Uses float RGBA instead of packed int color
     */
    public static void renderBipedMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        BipedMutator<?, ?, ?> mutator = MoBendsRenderContext.getCurrentBipedMutator();
        if (mutator != null) {
            net.minecraft.client.model.HumanoidModel<?> vanillaModel =
                    MoBendsRenderContext.getCurrentVanillaModel();
            if (mutator instanceof PlayerMutator playerMutator
                    && vanillaModel
                            instanceof net.minecraft.client.model.PlayerModel<?> playerModel) {
                playerMutator.syncPlayerVisibility(playerModel);
            }
            // Convert float RGBA to packed ARGB int
            int color =
                    ((int) (alpha * 255.0F) << 24)
                            | ((int) (red * 255.0F) << 16)
                            | ((int) (green * 255.0F) << 8)
                            | (int) (blue * 255.0F);
            mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            // Re-sync poses to vanilla model AFTER custom render (setupAnim overwrote the Pre-event
            // sync).
            // This ensures overlay layers (Drowned outer layer, etc.) get animated poses when they
            // copy
            // from the parent model.
            if (vanillaModel != null) {
                mutator.syncPosesToVanillaModel(vanillaModel);
            }
            // End main model render phase so layers (armor, elytra) render normally
            MoBendsRenderContext.endMainModelRender();
        }
    }

    /**
     * Check if a spider mutator is active and should render custom. CRITICAL: Only returns true if
     * we're in the main entity model render phase.
     *
     * @return true if custom rendering should be used
     */
    public static boolean shouldRenderSpiderCustom() {
        // Only intercept if we're in the main model render phase
        if (!MoBendsRenderContext.isInMainModelRender()) {
            return false;
        }
        SpiderMutator mutator = MoBendsRenderContext.getCurrentSpiderMutator();
        return mutator != null && mutator.shouldRenderCustom();
    }

    /**
     * Render the spider using the current mutator. Ends the main model render phase so layers
     * render normally.
     */
    public static void renderSpiderMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        SpiderMutator mutator = MoBendsRenderContext.getCurrentSpiderMutator();
        if (mutator != null) {
            int color =
                    ((int) (alpha * 255.0F) << 24)
                            | ((int) (red * 255.0F) << 16)
                            | ((int) (green * 255.0F) << 8)
                            | (int) (blue * 255.0F);
            mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            // End main model render phase so layers render normally
            MoBendsRenderContext.endMainModelRender();
        }
    }

    public static boolean shouldRenderSquidCustom() {
        SquidMutator mutator = MoBendsRenderContext.getCurrentSquidMutator();
        return MoBendsRenderContext.isInMainModelRender()
                && mutator != null
                && mutator.shouldRenderCustom();
    }

    public static void renderSquidMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        SquidMutator mutator = MoBendsRenderContext.getCurrentSquidMutator();
        if (mutator != null) {
            mutator.renderMutated(
                    poseStack,
                    vertexConsumer,
                    packedLight,
                    packedOverlay,
                    packColor(red, green, blue, alpha));
            MoBendsRenderContext.endMainModelRender();
        }
    }

    public static boolean shouldRenderWolfCustom(Object model) {
        WolfMutator mutator = MoBendsRenderContext.getCurrentWolfMutator();
        return (MoBendsRenderContext.isInMainModelRender()
                        || model == MoBendsRenderContext.getCurrentPrimaryModel())
                && mutator != null
                && mutator.shouldRenderCustom();
    }

    public static void renderWolfMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        WolfMutator mutator = MoBendsRenderContext.getCurrentWolfMutator();
        if (mutator != null) {
            boolean mainModelRender = MoBendsRenderContext.isInMainModelRender();
            mutator.renderMutated(
                    poseStack,
                    vertexConsumer,
                    packedLight,
                    packedOverlay,
                    packColor(red, green, blue, alpha));
            if (mainModelRender) {
                MultiBufferSource bufferSource = MoBendsRenderContext.getCurrentBufferSource();
                if (bufferSource != null) {
                    VertexConsumer miscConsumer =
                            bufferSource.getBuffer(
                                    RenderType.entityCutout(
                                            ModStatics.getResource(
                                                    "textures/entity/wolf_misc.png")));
                    // The original LayerWolfMisc runs before LivingEntityRenderer restores its
                    // model transform. Rendering here preserves that same wolf-local coordinate
                    // system for the mouth planes and tongue.
                    mutator.renderMisc(
                            poseStack, miscConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
                }
            }
            MoBendsRenderContext.endMainModelRender();
        }
    }

    private static int packColor(float red, float green, float blue, float alpha) {
        return ((int) (alpha * 255.0F) << 24)
                | ((int) (red * 255.0F) << 16)
                | ((int) (green * 255.0F) << 8)
                | (int) (blue * 255.0F);
    }

    /**
     * Check if an entity has Mo'Bends animation data.
     *
     * @param entity The entity to check
     * @return true if the entity has biped animation data
     */
    public static boolean hasAnimationData(LivingEntity entity) {
        Object data = EntityDatabase.instance.get(entity);
        return data instanceof BipedEntityData<?>;
    }

    /**
     * Get Mo'Bends entity data for an entity.
     *
     * @param entity The entity
     * @return The biped entity data, or null if not available
     */
    public static BipedEntityData<?> getEntityData(LivingEntity entity) {
        Object data = EntityDatabase.instance.get(entity);
        if (data instanceof BipedEntityData<?>) {
            return (BipedEntityData<?>) data;
        }
        return null;
    }
}
