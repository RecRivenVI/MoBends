// ? if target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.client.event;

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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

public class RenderingEventHandler {
    // Track which entities we pushed pose for (to avoid imbalanced stack)
    private static final Set<Integer> entitiesWithPushedPose = new HashSet<>();

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.isPaused()) return;

        // Update entity data each game tick (state updates, not rendering)
        // This is client-side only, so motion calculations will be correct
        EntityDatabase.instance.updateClient();
        Addons.onClientTick();
    }

    @SubscribeEvent
    public void onRenderTick(RenderFrameEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Reevaluate dirty computed values (FluxHandler functionality)
        ComputedDependencyHelper.reevaluateDirty();

        float renderTickTime = event.getPartialTick().getGameTimeDeltaPartialTick(false);

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
}*/
// ?} else if target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.client.event;

import com.google.common.reflect.TypeToken;
import io.github.recrivenvi.mobends.core.addon.Addons;
import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.client.RenderStateEntityContext;
import io.github.recrivenvi.mobends.core.client.TrailSubmitter;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.core.data.EntityDatabase;
import io.github.recrivenvi.mobends.core.data.LivingEntityData;
import io.github.recrivenvi.mobends.core.util.BenderHelper;
import io.github.recrivenvi.mobends.lib.flux.ComputedDependencyHelper;
import io.github.recrivenvi.mobends.neoforge.client.render.MoBendsRenderState;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import io.github.recrivenvi.mobends.standard.mutators.PlayerMutator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

// 26.1 客户端动画时钟及延迟渲染桥接。
public class RenderingEventHandler {
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (entity, state) -> {
                    state.setRenderData(MoBendsRenderState.ENTITY, entity);
                    state.setRenderData(MoBendsRenderState.CONTEXT, null);
                    RenderStateEntityContext.put(state, entity);
                });
        event.registerEntityModifier(
                new TypeToken<ArrowRenderer<AbstractArrow, ArrowRenderState>>() {},
                (entity, state) -> state.setRenderData(MoBendsRenderState.ARROW, entity));
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.isPaused()) {
            return;
        }

        EntityDatabase.instance.updateClient();
        Addons.onClientTick();
    }

    @SubscribeEvent
    public void onRenderTick(RenderFrameEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        ComputedDependencyHelper.reevaluateDirty();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        if (!minecraft.isPaused()) {
            DataUpdateHandler.partialTicks = partialTick;
        }

        float ticks = minecraft.player.tickCount + partialTick;
        if (DataUpdateHandler.checkTicksRestart(ticks)) {
            EntityDatabase.instance.onTicksRestart();
        }

        if (!(minecraft.level.isClientSide() && minecraft.isPaused())) {
            DataUpdateHandler.update(partialTick, ticks);
            EntityDatabase.instance.updateRender(partialTick);
            Addons.onRenderTick(partialTick);
        } else {
            DataUpdateHandler.onPaused();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent
    public void beforeHandRender(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity viewEntity = minecraft.getCameraEntity();
        if (!(viewEntity instanceof AbstractClientPlayer player)
                || !BenderHelper.isEntityAnimated(player)) {
            return;
        }

        AvatarRenderer renderer =
                (AvatarRenderer) minecraft.getEntityRenderDispatcher().getRenderer(player);
        EntityBender bender = EntityBenderRegistry.instance.getForEntity(player);
        if (bender == null
                || !bender.applyMutation(renderer, player, DataUpdateHandler.partialTicks)) {
            return;
        }
        PlayerMutator mutator =
                (PlayerMutator)
                        BenderHelper.getMutatorForRenderer(AbstractClientPlayer.class, renderer);
        if (mutator != null) {
            mutator.poseForFirstPersonView();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent
    public void beforeLivingRender(RenderLivingEvent.Pre<?, ?, ?> event) {
        LivingEntityRenderState state = event.getRenderState();
        LivingEntity entity = state.getRenderData(MoBendsRenderState.ENTITY);
        if (entity == null) {
            return;
        }

        LivingEntityRenderer renderer = event.getRenderer();
        EntityBender bender = EntityBenderRegistry.instance.getForEntity(entity);
        if (bender == null) {
            return;
        }

        if (!bender.isAnimated()) {
            bender.deapplyMutation(renderer, entity);
            return;
        }

        if (!bender.applyMutation(renderer, entity, event.getPartialTick())) {
            return;
        }

        EntityModel<?> primaryModel = renderer.getModel();
        state.setRenderData(
                MoBendsRenderState.CONTEXT,
                new MoBendsRenderState.Context(
                        entity, renderer, bender, primaryModel, event.getPartialTick()));

        event.getPoseStack().pushPose();
        LivingEntityData entityData =
                (LivingEntityData) bender.getMutator(renderer).getData(entity);
        if (ModConfig.showSwordTrail && entityData instanceof BipedEntityData<?> bipedData) {
            event.getPoseStack().pushPose();
            MutatedRenderer.applyEntityLocalRotation(
                    event.getPoseStack(),
                    entity.yBodyRotO,
                    entity.yBodyRot,
                    event.getPartialTick());
            event.getPoseStack().scale(0.0625f, 0.0625f, 0.0625f);
            TrailSubmitter.submit(
                    event.getPoseStack(),
                    event.getSubmitNodeCollector(),
                    RenderTypes.debugQuads(),
                    bipedData.swordTrail::render);
            event.getPoseStack().popPose();
        }
        bender.beforeRender(entityData, entity, event.getPartialTick(), event.getPoseStack());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent
    public void afterLivingRender(RenderLivingEvent.Post<?, ?, ?> event) {
        LivingEntityRenderState state = event.getRenderState();
        MoBendsRenderState.Context context = state.getRenderData(MoBendsRenderState.CONTEXT);
        if (context == null) {
            return;
        }

        ((EntityBender) context.bender())
                .afterRender(context.entity(), context.partialTick(), event.getPoseStack());
        event.getPoseStack().popPose();
        // 26.1 的模型节点同样在 RenderLivingEvent.Post 之后才真正绘制。
        // 上下文由下一次 render-state extraction 清空，不能在提交结束时提前删除。
    }
}*/
// ?}
