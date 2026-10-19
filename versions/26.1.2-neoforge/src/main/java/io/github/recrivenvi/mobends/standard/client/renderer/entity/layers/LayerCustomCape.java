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
