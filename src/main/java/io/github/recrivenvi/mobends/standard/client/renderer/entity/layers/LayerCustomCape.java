//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.recrivenvi.mobends.core.client.RenderStateEntityContext;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.data.EntityDatabase;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.BendsCapeRenderer;
import io.github.recrivenvi.mobends.standard.data.PlayerData;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

// 为弯曲身体提交分段披风几何体。
public class LayerCustomCape extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final float MODEL_SCALE = 0.0625F;

    public LayerCustomCape(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            AvatarRenderState state,
            float yRot,
            float xRot) {
        if (state.isInvisible
                || !state.showCape
                || state.skin.cape() == null
                || state.chestEquipment.is(Items.ELYTRA)) {
            return;
        }

        if (!(RenderStateEntityContext.get(state) instanceof AbstractClientPlayer player)) {
            return;
        }
        EntityData<?> entityData = EntityDatabase.instance.get(player);
        if (!(entityData instanceof PlayerData data)) {
            return;
        }

        Identifier texture = state.skin.cape().texturePath();
        poseStack.pushPose();
        if (player.isCrouching()) {
            poseStack.translate(0.0F, data.getCapeCrouchingTranslationY() * MODEL_SCALE, 0.0F);
        }
        data.body.applyLocalTransform(poseStack, MODEL_SCALE);
        poseStack.translate(0.0F, -12.0F * MODEL_SCALE, 2.2F * MODEL_SCALE);
        data.cape.applyLocalTransform(poseStack, MODEL_SCALE);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        BendsCapeRenderer capeRenderer = new BendsCapeRenderer();
        capeRenderer.applyAnimation(data);
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entitySolid(texture),
                (pose, vertexConsumer) -> {
                    PoseStack renderingPose = new PoseStack();
                    renderingPose.last().set(pose);
                    capeRenderer.render(renderingPose, vertexConsumer, lightCoords, 0);
                });
        poseStack.popPose();
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.client.renderer.entity.layers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.recrivenvi.mobends.api.player.IPlayerSkinProvider;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.data.EntityDatabase;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.BendsCapeRenderer;
import io.github.recrivenvi.mobends.standard.data.PlayerData;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

//
// Custom cape layer for Mo' Bends animated cape rendering. Updated for 1.20.1 to use PoseStack and
// RenderSystem instead of GlStateManager.
//
public class LayerCustomCape
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final BendsCapeRenderer capeRenderer;

    public LayerCustomCape(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
        this.capeRenderer = new BendsCapeRenderer();
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        final EntityData<?> entityData = EntityDatabase.instance.get(player);
        if (!(entityData instanceof PlayerData)) return;

        final PlayerData data = (PlayerData) entityData;
        final float scale = 0.0625F;

        IPlayerSkinProvider skinProvider = IPlayerSkinProvider.Holder.getProvider();
        ResourceLocation capeTexture =
                skinProvider != null
                        ? (ResourceLocation) skinProvider.getCapeTexture(player)
                        : null;
        if (capeTexture != null
                && !player.isInvisible()
                && player.isModelPartShown(PlayerModelPart.CAPE)) {
            final ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);

            if (!itemstack.is(Items.ELYTRA)) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.pushPose();

                data.body.applyLocalTransform(poseStack, 0.0625F);
                poseStack.translate(0.0F, -12.0F * scale, 2.2F * scale);
                data.cape.applyLocalTransform(poseStack, 0.0625F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

                capeRenderer.applyAnimation(data);
                capeRenderer.render(poseStack, bufferSource, packedLight, player, 0.0625F);

                poseStack.popPose();
            }
        }
    }
}*/
//?}
