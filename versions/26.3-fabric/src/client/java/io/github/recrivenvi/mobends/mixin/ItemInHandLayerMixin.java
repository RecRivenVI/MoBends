package io.github.recrivenvi.mobends.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.client.RenderStateEntityContext;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.mutators.BipedMutator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Makes held items follow the lower-arm joint and Mo' Bends item rotation.
@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(
            method = "submitArmWithItem",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                            shift = At.Shift.AFTER))
    private void mobends$applyForearmTransform(
            ArmedEntityRenderState state,
            ItemStackRenderState item,
            ItemStack itemStack,
            HumanoidArm arm,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            CallbackInfo callbackInfo) {
        if (!(RenderStateEntityContext.get(state) instanceof LivingEntity entity)) {
            return;
        }

        EntityBender bender = EntityBenderRegistry.instance.getForEntity(entity);
        LivingEntityRenderer renderer =
                (LivingEntityRenderer)
                        Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        if (bender == null || renderer == null) {
            return;
        }

        Object rawMutator = bender.getMutator(renderer);
        if (rawMutator instanceof BipedMutator bipedMutator
                && rawMutator instanceof Mutator mutator
                && mutator.getData(entity) instanceof BipedEntityData data) {
            bipedMutator.applyHeldItemTransform(arm, data, poseStack);
        }
    }
}
