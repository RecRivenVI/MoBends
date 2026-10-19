package io.github.recrivenvi.mobends.forge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.addon.Addons;
import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.client.MoBendsRenderContext;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.core.data.EntityDatabase;
import io.github.recrivenvi.mobends.core.data.LivingEntityData;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import io.github.recrivenvi.mobends.core.util.BenderHelper;
import io.github.recrivenvi.mobends.lib.flux.ComputedDependencyHelper;
import io.github.recrivenvi.mobends.standard.mutators.BipedMutator;
import io.github.recrivenvi.mobends.standard.mutators.PlayerMutator;
import io.github.recrivenvi.mobends.standard.mutators.SpiderMutator;
import io.github.recrivenvi.mobends.standard.mutators.SquidMutator;
import io.github.recrivenvi.mobends.standard.mutators.WolfMutator;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderingEventHandler {
    // Track which entities we pushed pose for (to avoid imbalanced stack)
    private static final Set<Integer> entitiesWithPushedPose = new HashSet<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.isPaused()) return;

        // Update entity data each game tick (state updates, not rendering)
        // This is client-side only, so motion calculations will be correct
        EntityDatabase.instance.updateClient();
        Addons.onClientTick();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Reevaluate dirty computed values (FluxHandler functionality)
        ComputedDependencyHelper.reevaluateDirty();

        float renderTickTime = event.renderTickTime;

        if (!mc.isPaused()) {
            DataUpdateHandler.partialTicks = renderTickTime;
        }

        // Use player.tickCount like the original code
        final float newTicks = mc.player.tickCount + renderTickTime;

        if (DataUpdateHandler.checkTicksRestart(newTicks)) {
            EntityDatabase.instance.onTicksRestart();
        }

        if (!(mc.level.isClientSide && mc.isPaused())) {
            DataUpdateHandler.update(renderTickTime, newTicks);
            EntityDatabase.instance.updateRender(renderTickTime);
            Addons.onRenderTick(renderTickTime);
        } else {
            DataUpdateHandler.onPaused();
        }
    }

    @SubscribeEvent
    public void beforeHandRender(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Entity viewEntity = mc.getCameraEntity();

        if (!(viewEntity instanceof AbstractClientPlayer)) return;

        AbstractClientPlayer player = (AbstractClientPlayer) viewEntity;

        if (!BenderHelper.isEntityAnimated(player)) return;

        PlayerRenderer renderPlayer =
                (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        PlayerMutator mutator =
                (PlayerMutator)
                        BenderHelper.getMutatorForRenderer(
                                AbstractClientPlayer.class, renderPlayer);
        if (mutator != null) mutator.poseForFirstPersonView();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public void beforeLivingRender(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();
        LivingEntityRenderer renderer = event.getRenderer();
        float partialTicks = event.getPartialTick();
        PoseStack poseStack = event.getPoseStack();

        EntityBender bender = EntityBenderRegistry.instance.getForEntity(entity);
        if (bender == null) return;

        if (entity.isSpectator()) {
            bender.deapplyMutation(renderer, entity);
            return;
        }

        // Push pose for proper transform isolation
        poseStack.pushPose();
        entitiesWithPushedPose.add(entity.getId());

        if (bender.isAnimated()) {
            // Apply mutation - this creates the mutator if it doesn't exist,
            // updates the model, performs animations, and syncs with data
            boolean mutationApplied = bender.applyMutation(renderer, entity, partialTicks);

            if (mutationApplied) {
                // Get the mutator that was just created/updated
                final Object rawMutator = bender.getMutator(renderer);
                if (rawMutator == null) {
                    return;
                }

                final Mutator<?, LivingEntity, ?> mutator =
                        (Mutator<?, LivingEntity, ?>) rawMutator;
                final LivingEntityData<LivingEntity> data =
                        (LivingEntityData<LivingEntity>) mutator.getData(entity);
                MoBendsRenderContext.setCurrentPrimaryModel(renderer.getModel());

                // Set up render context based on mutator type
                if (rawMutator instanceof BipedMutator<?, ?, ?> bipedMutator) {
                    MoBendsRenderContext.setCurrentBipedMutator(bipedMutator);
                    // Begin main model render phase - mixin will end it after rendering player
                    // model
                    MoBendsRenderContext.beginMainModelRender();

                    // Store vanilla model reference for post-render pose sync (overlay layers)
                    EntityModel<?> model = renderer.getModel();
                    if (model instanceof HumanoidModel<?> humanoidModel) {
                        MoBendsRenderContext.setCurrentVanillaModel(humanoidModel);
                        bipedMutator.syncPosesToVanillaModel(humanoidModel);
                    }
                } else if (rawMutator instanceof SpiderMutator spiderMutator) {
                    MoBendsRenderContext.setCurrentSpiderMutator(spiderMutator);
                    MoBendsRenderContext.beginMainModelRender();
                } else if (rawMutator instanceof SquidMutator squidMutator) {
                    MoBendsRenderContext.setCurrentSquidMutator(squidMutator);
                    MoBendsRenderContext.beginMainModelRender();
                } else if (rawMutator instanceof WolfMutator wolfMutator) {
                    MoBendsRenderContext.setCurrentWolfMutator(wolfMutator);
                    MoBendsRenderContext.setCurrentBufferSource(event.getMultiBufferSource());
                    MoBendsRenderContext.beginMainModelRender();
                }
                // Call beforeRender for any entity-specific setup
                bender.beforeRender(data, entity, partialTicks, poseStack);
            }
        } else {
            // Entity is not animated - deapply any existing mutation
            bender.deapplyMutation(renderer, entity);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public void afterLivingRender(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        EntityBender bender = EntityBenderRegistry.instance.getForEntity(entity);

        if (bender == null) {
            MoBendsRenderContext.clear();
            return;
        }

        // Always clear the render context after rendering all Mo' Bends passes.
        MoBendsRenderContext.clear();

        // Only pop if we pushed for this entity
        if (entitiesWithPushedPose.remove(entity.getId())) {
            bender.afterRender(entity, event.getPartialTick(), event.getPoseStack());
            event.getPoseStack().popPose();
        }
    }
}
