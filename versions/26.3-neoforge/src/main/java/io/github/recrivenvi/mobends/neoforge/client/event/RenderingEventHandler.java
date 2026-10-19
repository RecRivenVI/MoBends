package io.github.recrivenvi.mobends.neoforge.client.event;

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
}
