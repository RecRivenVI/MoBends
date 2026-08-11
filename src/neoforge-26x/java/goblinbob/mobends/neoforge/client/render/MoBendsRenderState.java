package goblinbob.mobends.neoforge.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.mutators.Mutator;
import goblinbob.mobends.standard.client.model.armor.tier1.Tier1Renderer;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.main.ModConfig;
import goblinbob.mobends.standard.main.ModStatics;
import goblinbob.mobends.standard.mutators.BipedMutator;
import goblinbob.mobends.standard.mutators.PlayerMutator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;

/** 把实体对象与 Mo' Bends 上下文绑定到 26.1 的延迟渲染状态。 */
public final class MoBendsRenderState {
    private static final Tier1Renderer DEFERRED_ARMOR_RENDERER = new Tier1Renderer();
    public static final ContextKey<LivingEntity> ENTITY =
            new ContextKey<>(ModStatics.getResource("render_entity"));
    public static final ContextKey<Context> CONTEXT =
            new ContextKey<>(ModStatics.getResource("render_context"));
    public static final ContextKey<AbstractArrow> ARROW =
            new ContextKey<>(ModStatics.getResource("render_arrow"));

    private MoBendsRenderState() {}

    public record Context(
            LivingEntity entity,
            LivingEntityRenderer<?, ?, ?> renderer,
            EntityBender<?> bender,
            EntityModel<?> primaryModel,
            float partialTick) {}

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean renderMutatedModel(
            EntityModel<?> submittedModel,
            LivingEntityRenderState state,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color) {
        Context context = state.getRenderData(CONTEXT);
        if (context == null) {
            return false;
        }

        EntityBender bender = context.bender();
        LivingEntityRenderer renderer = context.renderer();
        LivingEntity entity = context.entity();
        if (!bender.restoreMutation(renderer, entity)) {
            return false;
        }

        Object rawMutator = bender.getMutator(renderer);
        if (!(rawMutator instanceof Mutator mutator) || !mutator.shouldRenderCustom()) {
            return false;
        }

        if (submittedModel != context.primaryModel()) {
            if (rawMutator instanceof BipedMutator<?, ?, ?> bipedMutator
                    && submittedModel
                            instanceof net.minecraft.client.model.object.equipment.ElytraModel) {
                poseStack.pushPose();
                bipedMutator.applyBodyAttachmentTransform(poseStack);
                submittedModel.renderToBuffer(
                        poseStack, vertexConsumer, packedLight, packedOverlay, color);
                poseStack.popPose();
                return true;
            }
            if (rawMutator instanceof BipedMutator<?, ?, ?> bipedMutator
                    && submittedModel instanceof HumanoidModel<?> armorModel
                    && mutator.getData(entity) instanceof BipedEntityData<?> bipedData) {
                EquipmentSlot slot = inferArmorSlot(armorModel);
                ItemStack armorStack = getEquipment(state, slot);
                if (slot != null
                        && !armorStack.isEmpty()
                        && !ModConfig.shouldKeepArmorAsVanilla(armorStack)) {
                    boolean slimArms =
                            entity
                                            instanceof
                                            net.minecraft.client.player.AbstractClientPlayer player
                                    && goblinbob.mobends.api.player.IPlayerSkinProvider.Holder
                                                    .getProvider()
                                            != null
                                    && goblinbob.mobends.api.player.IPlayerSkinProvider.Holder
                                            .getProvider()
                                            .isSlimModel(player);
                    DEFERRED_ARMOR_RENDERER.renderDeferred(
                            poseStack,
                            vertexConsumer,
                            armorModel,
                            bipedData,
                            slot,
                            packedLight,
                            packedOverlay,
                            color,
                            slimArms,
                            bipedMutator);
                    return true;
                }
            }
            return false;
        }

        if (rawMutator instanceof PlayerMutator playerMutator
                && state instanceof AvatarRenderState avatarState) {
            playerMutator.syncPlayerVisibility(
                    avatarState.isSpectator,
                    avatarState.showHat,
                    avatarState.showJacket,
                    avatarState.showLeftSleeve,
                    avatarState.showRightSleeve,
                    avatarState.showLeftPants,
                    avatarState.showRightPants);
        }

        if (rawMutator instanceof BipedMutator<?, ?, ?> bipedMutator
                && submittedModel instanceof HumanoidModel<?> humanoidModel) {
            bipedMutator.syncPosesToVanillaModel(humanoidModel);
        }

        mutator.renderMutated(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void syncPrimaryModelForLayers(LivingEntityRenderState state) {
        Context context = state.getRenderData(CONTEXT);
        if (context == null || !(context.primaryModel() instanceof HumanoidModel<?> model)) {
            return;
        }

        Object rawMutator = context.bender().getMutator((LivingEntityRenderer) context.renderer());
        if (rawMutator instanceof BipedMutator<?, ?, ?> bipedMutator) {
            bipedMutator.syncPosesToVanillaModel(model);
        }
    }

    private static EquipmentSlot inferArmorSlot(HumanoidModel<?> model) {
        boolean head = !model.head.isEmpty();
        boolean body = !model.body.isEmpty();
        boolean arms = !model.leftArm.isEmpty() || !model.rightArm.isEmpty();
        boolean legs = !model.leftLeg.isEmpty() || !model.rightLeg.isEmpty();

        if (head && !body && !arms && !legs) {
            return EquipmentSlot.HEAD;
        }
        if (!head && body && arms && !legs) {
            return EquipmentSlot.CHEST;
        }
        if (!head && body && !arms && legs) {
            return EquipmentSlot.LEGS;
        }
        if (!head && !body && !arms && legs) {
            return EquipmentSlot.FEET;
        }
        return null;
    }

    private static ItemStack getEquipment(LivingEntityRenderState state, EquipmentSlot slot) {
        if (!(state
                        instanceof
                        net.minecraft.client.renderer.entity.state.HumanoidRenderState
                                humanoidState)
                || slot == null) {
            return ItemStack.EMPTY;
        }
        return switch (slot) {
            case HEAD -> humanoidState.headEquipment;
            case CHEST -> humanoidState.chestEquipment;
            case LEGS -> humanoidState.legsEquipment;
            case FEET -> humanoidState.feetEquipment;
            default -> ItemStack.EMPTY;
        };
    }
}
