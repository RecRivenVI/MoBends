// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.player.IPlayerSkinProvider;
import io.github.recrivenvi.mobends.core.client.model.BendsCube;
import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.layers.LayerCustomCape;
import io.github.recrivenvi.mobends.standard.data.PlayerData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Instantiated once per AvatarRenderer
public class PlayerMutator extends BipedMutator<PlayerData, AbstractClientPlayer, PlayerModel> {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerMutator.class);
    private static final byte UPPER_LIMB_FACES = (byte) (0b111111 & ~(1 << BendsCube.BOTTOM));
    private static final byte LOWER_LIMB_FACES = (byte) (0b111111 & ~(1 << BendsCube.TOP));

    protected BendsModelPart bodywear;
    protected BendsModelPart leftArmwear;
    protected BendsModelPart rightArmwear;
    protected BendsModelPart leftForeArmwear;
    protected BendsModelPart rightForeArmwear;
    protected BendsModelPart leftLegwear;
    protected BendsModelPart rightLegwear;
    protected BendsModelPart leftForeLegwear;
    protected BendsModelPart rightForeLegwear;

    protected boolean smallArms;

    protected LayerCustomCape layerCape;
    protected CapeLayer layerCapeVanilla;

    public PlayerMutator(IEntityDataFactory<AbstractClientPlayer> dataFactory) {
        super(dataFactory);
    }

    public boolean hasSmallArms() {
        return this.smallArms;
    }

    @Override
    public boolean mutate(LivingEntityRenderer<?, ?, ?> renderer) {
        return super.mutate(renderer);
    }

    @Override
    public void demutate(LivingEntityRenderer<?, ?, ?> renderer) {
        super.demutate(renderer);
    }

    @Override
    public void fetchFields(LivingEntityRenderer<?, ?, ?> renderer) {
        super.fetchFields(renderer);
        // 26.x 的渲染器不再携带固定的 slim 标志；实际皮肤类型会在
        // updateModel 中按当前玩家读取并在需要时重建手臂几何。
        this.smallArms = false;
    }

    //
    // Update the smallArms field based on the player's model name. This should be called when we
    // have access to the player entity.
    //
    public void updateSmallArms(AbstractClientPlayer player) {
        if (player != null) {
            IPlayerSkinProvider skinProvider = IPlayerSkinProvider.Holder.getProvider();
            this.smallArms = skinProvider != null && skinProvider.isSlimModel(player);
        }
    }

    @Override
    public void updateModel(
            AbstractClientPlayer entity,
            LivingEntityRenderer<?, ?, ?> renderer,
            float partialTicks) {
        if (entity != null && this.body != null) {
            IPlayerSkinProvider skinProvider = IPlayerSkinProvider.Holder.getProvider();
            boolean playerIsSlim = skinProvider != null && skinProvider.isSlimModel(entity);
            if (playerIsSlim != this.smallArms) {
                this.smallArms = playerIsSlim;
                createParts((PlayerModel) renderer.getModel(), 0F);
            }
        }

        super.updateModel(entity, renderer, partialTicks);
    }

    @Override
    public void storeVanillaModel(PlayerModel model) {
        super.storeVanillaModel(model);
    }

    @Override
    public void applyVanillaModel(PlayerModel model) {
        super.applyVanillaModel(model);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void swapLayer(
            LivingEntityRenderer<?, ?, ?> renderer, int index, boolean isModelVanilla) {
        super.swapLayer(renderer, index, isModelVanilla);

        final RenderLayer<?, ?> layer = layerRenderers.get(index);
        if (layer instanceof CapeLayer) {
            this.layerCape = new LayerCustomCape((AvatarRenderer) renderer);
            if (isModelVanilla) this.layerCapeVanilla = (CapeLayer) layer;
            layerRenderers.set(index, this.layerCape);
        }

        // Note: ElytraLayer is NOT swapped - vanilla elytra layer uses the synced
        // rotations from syncPosesToVanillaModel() and renders correctly
    }

    @Override
    public void deswapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index) {
        super.deswapLayer(renderer, index);

        final RenderLayer<?, ?> layer = layerRenderers.get(index);
        if (layer instanceof LayerCustomCape) {
            layerRenderers.set(index, this.layerCapeVanilla);
        }
    }

    @Override
    public boolean createParts(PlayerModel original, float scaleFactor) {
        // Arms
        int armWidth = this.smallArms ? 3 : 4;
        float armY = this.smallArms ? -9.5F : -10F;

        // Create custom bendable parts using BendsModelPart
        // Body - root of upper body hierarchy
        body = new BendsModelPart(16, 16).setTextureSize(64, 64).setPosition(0.0F, 12.0F, 0.0F);
        body.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor);

        // Head - child of body
        head = new BendsModelPart(0, 0).setTextureSize(64, 64).setPosition(0.0F, -12.0F, 0.0F);
        head.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor);
        body.addChild(head);

        // Headwear - child of head
        headwear = new BendsModelPart(32, 0).setTextureSize(64, 64);
        headwear.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor + 0.5F);
        head.addChild(headwear);

        // Left Arm (texture at 32, 48 for player) - child of body
        leftArm = new BendsModelPart(32, 48).setTextureSize(64, 64).setPosition(5.0F, armY, 0.0F);
        leftArm.addCube(
                -1.0F,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                32,
                48);
        body.addChild(leftArm);

        // Right Arm (texture at 40, 16 for player) - child of body
        rightArm = new BendsModelPart(40, 16).setTextureSize(64, 64).setPosition(-5.0F, armY, 0.0F);
        rightArm.addCube(
                -armWidth + 1,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                40,
                16);
        body.addChild(rightArm);

        // Left Forearm - child of leftArm
        leftForeArm =
                new BendsModelPart(32, 48 + 6).setTextureSize(64, 64).setPosition(0.0F, 4.0F, 2.0F);
        leftForeArm.addCube(
                -1.0F, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor, LOWER_LIMB_FACES, 32, 48);
        leftArm.addChild(leftForeArm);

        // Right Forearm - child of rightArm
        rightForeArm =
                new BendsModelPart(40, 16 + 6).setTextureSize(64, 64).setPosition(0.0F, 4.0F, 2.0F);
        rightForeArm.addCube(
                -armWidth + 1, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor, LOWER_LIMB_FACES, 40, 16);
        rightArm.addChild(rightForeArm);

        // Legs (texture at 16, 48 for left leg, 0, 16 for right leg in player model)
        // Legs are independent roots (not children of body)
        leftLeg = new BendsModelPart(16, 48).setTextureSize(64, 64).setPosition(1.9F, 12.0F, 0.0F);
        leftLeg.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        rightLeg = new BendsModelPart(0, 16).setTextureSize(64, 64).setPosition(-1.9F, 12.0F, 0.0F);
        rightLeg.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        // Left Foreleg - child of leftLeg
        leftForeLeg =
                new BendsModelPart(16, 48 + 6)
                        .setTextureSize(64, 64)
                        .setPosition(0.0F, 6.0F, -2.0F);
        leftForeLeg.addCube(
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                16,
                48);
        leftLeg.addChild(leftForeLeg);

        // Right Foreleg - child of rightLeg
        rightForeLeg =
                new BendsModelPart(0, 16 + 6).setTextureSize(64, 64).setPosition(0.0F, 6.0F, -2.0F);
        rightForeLeg.addCube(
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                0,
                16);
        rightLeg.addChild(rightForeLeg);

        // Wear layers (second skin layer)
        float wearOffset = 0.25F;
        float limbWearHeight = (6.0F + 2.0F * scaleFactor + 0.5F) - 0.25F;

        // Bodywear - child of body
        bodywear = new BendsModelPart(16, 32).setTextureSize(64, 64);
        bodywear.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor + wearOffset);
        body.addChild(bodywear);

        // Left arm wear - child of leftArm
        leftArmwear = new BendsModelPart(48, 48).setTextureSize(64, 64);
        addResizedWearCube(
                leftArmwear,
                -1.0F,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0025F,
                0.0F,
                UPPER_LIMB_FACES,
                48,
                48);
        leftArm.addChild(leftArmwear);

        // Right arm wear - child of rightArm
        rightArmwear = new BendsModelPart(40, 32).setTextureSize(64, 64);
        addResizedWearCube(
                rightArmwear,
                -armWidth + 1,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0025F,
                0.0F,
                UPPER_LIMB_FACES,
                40,
                32);
        rightArm.addChild(rightArmwear);

        // Left forearm wear - child of leftForeArm
        leftForeArmwear = new BendsModelPart(48, 48 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                leftForeArmwear,
                -1.0F,
                0.0F,
                -4.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                48,
                48);
        leftForeArm.addChild(leftForeArmwear);

        // Right forearm wear - child of rightForeArm
        rightForeArmwear = new BendsModelPart(40, 32 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                rightForeArmwear,
                -armWidth + 1,
                0.0F,
                -4.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                40,
                32);
        rightForeArm.addChild(rightForeArmwear);

        // Left leg wear - child of leftLeg
        leftLegwear = new BendsModelPart(0, 48).setTextureSize(64, 64);
        addResizedWearCube(
                leftLegwear,
                -2.0F,
                0.0F,
                -2.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0F,
                0.0F,
                UPPER_LIMB_FACES,
                0,
                48);
        leftLeg.addChild(leftLegwear);

        // Right leg wear - child of rightLeg
        rightLegwear = new BendsModelPart(0, 32).setTextureSize(64, 64);
        addResizedWearCube(
                rightLegwear,
                -2.0F,
                0.0F,
                -2.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0F,
                0.0F,
                UPPER_LIMB_FACES,
                0,
                32);
        rightLeg.addChild(rightLegwear);

        // Left foreleg wear - child of leftForeLeg
        leftForeLegwear = new BendsModelPart(0, 48 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                leftForeLegwear,
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                0,
                48);
        leftForeLeg.addChild(leftForeLegwear);

        // Right foreleg wear - child of rightForeLeg
        rightForeLegwear = new BendsModelPart(0, 32 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                rightForeLegwear,
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                0,
                32);
        rightForeLeg.addChild(rightForeLegwear);

        return true;
    }

    private static void addResizedWearCube(
            BendsModelPart part,
            float x,
            float y,
            float z,
            int width,
            int height,
            int depth,
            float inflation,
            float resizedHeight,
            float seamInflation,
            float yOffset,
            byte faceVisibilityFlag,
            int bottomTexOffsetX,
            int bottomTexOffsetY) {
        float minX = x - inflation - seamInflation;
        float minY = y - inflation + yOffset;
        float minZ = z - inflation - seamInflation;
        float maxX = x + width + inflation + seamInflation;
        float maxY = y - inflation + resizedHeight + yOffset;
        float maxZ = z + depth + inflation + seamInflation;
        part.addCubeBounds(
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ,
                width,
                height,
                depth,
                faceVisibilityFlag,
                bottomTexOffsetX,
                bottomTexOffsetY);
    }

    // 同步当前玩家渲染状态中的模型层开关。
    public void syncPlayerVisibility(
            boolean spectator,
            boolean showHat,
            boolean showJacket,
            boolean showLeftSleeve,
            boolean showRightSleeve,
            boolean showLeftPants,
            boolean showRightPants) {
        boolean showBody = !spectator;

        body.setVisible(showBody);
        head.setVisible(true);
        headwear.setVisible(showHat);
        leftArm.setVisible(showBody);
        rightArm.setVisible(showBody);
        leftLeg.setVisible(showBody);
        rightLeg.setVisible(showBody);

        bodywear.setVisible(showJacket);
        leftArmwear.setVisible(showLeftSleeve);
        leftForeArmwear.setVisible(showLeftSleeve);
        rightArmwear.setVisible(showRightSleeve);
        rightForeArmwear.setVisible(showRightSleeve);
        leftLegwear.setVisible(showLeftPants);
        leftForeLegwear.setVisible(showLeftPants);
        rightLegwear.setVisible(showRightPants);
        rightForeLegwear.setVisible(showRightPants);
    }

    @Override
    public void syncUpWithData(PlayerData data) {
        super.syncUpWithData(data);
        // Sync wear parts with their base parts - they share the same transforms
    }

    @Override
    public void performAnimations(
            PlayerData data,
            String animatedEntityKey,
            LivingEntityRenderer<?, ?, ?> renderer,
            float partialTicks) {
        // Sync wear visibility with base parts
        if (leftForeArmwear != null && leftArmwear != null)
            leftForeArmwear.setVisible(leftArmwear.isShowing());
        if (rightForeArmwear != null && rightArmwear != null)
            rightForeArmwear.setVisible(rightArmwear.isShowing());
        if (leftForeLegwear != null && leftLegwear != null)
            leftForeLegwear.setVisible(leftLegwear.isShowing());
        if (rightForeLegwear != null && rightLegwear != null)
            rightForeLegwear.setVisible(rightLegwear.isShowing());

        super.performAnimations(data, animatedEntityKey, renderer, partialTicks);
    }

    @Override
    public void postRefresh() {
        // 26.1 的装备层使用延迟渲染状态，不再持有可主动刷新的盔甲层实例。
    }

    // Called before the first person hand is rendered, so the mutator can pose it in any way.
    public void poseForFirstPersonView() {
        if (this.body != null) this.body.getRotation().identity();
        if (this.rightArm != null) this.rightArm.getRotation().identity();
        if (this.rightForeArm != null) this.rightForeArm.getRotation().identity();
        if (this.leftArm != null) this.leftArm.getRotation().identity();
        if (this.leftForeArm != null) this.leftForeArm.getRotation().identity();
    }

    public void renderFirstPersonArm(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color,
            net.minecraft.world.entity.HumanoidArm arm,
            boolean showSleeve) {
        BendsModelPart upperArm =
                arm == net.minecraft.world.entity.HumanoidArm.LEFT ? leftArm : rightArm;
        BendsModelPart armWear =
                arm == net.minecraft.world.entity.HumanoidArm.LEFT ? leftArmwear : rightArmwear;
        BendsModelPart foreArmWear =
                arm == net.minecraft.world.entity.HumanoidArm.LEFT
                        ? leftForeArmwear
                        : rightForeArmwear;
        if (upperArm == null) {
            return;
        }

        if (armWear != null) armWear.visible = showSleeve;
        if (foreArmWear != null) foreArmWear.visible = showSleeve;
        poseStack.pushPose();
        upperArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    @Override
    public boolean isModelVanilla(PlayerModel model) {
        // Check if we've already created custom parts
        return this.body == null;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof PlayerModel);
    }

    @Override
    public PlayerData getData(AbstractClientPlayer entity) {
        // 渲染状态可能批处理不同皮肤类型的玩家，始终按实体再次同步。
        if (entity != null) {
            IPlayerSkinProvider skinProvider = IPlayerSkinProvider.Holder.getProvider();
            boolean playerIsSlim = skinProvider != null && skinProvider.isSlimModel(entity);
            if (playerIsSlim != this.smallArms) {
                LOG.debug(
                        "Slim arm mismatch detected for {}: mutator={}, player={}. Updating.",
                        entity.getName().getString(),
                        this.smallArms,
                        playerIsSlim);
                this.smallArms = playerIsSlim;
                // Note: Parts are already created with the old dimension.
                // They will be corrected on next mutation cycle.
            }
        }
        return super.getData(entity);
    }

    @Override
    public void renderMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int packedColor) {
        // Render body and all attached parts (head, arms)
        if (body != null) {
            if (body.isShowing()) {
                body.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
            } else if (head != null && head.isShowing()) {
                // 旁观者只显示头部；头部仍通过父级链应用身体变换。
                head.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
            }
        }

        // Render legs (not attached to body)
        if (leftLeg != null) {
            leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }
        if (rightLeg != null) {
            rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }
    }

    // Getters for wear parts
    public BendsModelPart getBodywear() {
        return bodywear;
    }

    public BendsModelPart getLeftArmwear() {
        return leftArmwear;
    }

    public BendsModelPart getRightArmwear() {
        return rightArmwear;
    }

    public BendsModelPart getLeftForeArmwear() {
        return leftForeArmwear;
    }

    public BendsModelPart getRightForeArmwear() {
        return rightForeArmwear;
    }

    public BendsModelPart getLeftLegwear() {
        return leftLegwear;
    }

    public BendsModelPart getRightLegwear() {
        return rightLegwear;
    }

    public BendsModelPart getLeftForeLegwear() {
        return leftForeLegwear;
    }

    public BendsModelPart getRightForeLegwear() {
        return rightForeLegwear;
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.player.IPlayerSkinProvider;
import io.github.recrivenvi.mobends.core.client.model.BendsCube;
import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.layers.LayerCustomCape;
import io.github.recrivenvi.mobends.standard.data.PlayerData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Instantiated once per PlayerRenderer
public class PlayerMutator
        extends BipedMutator<PlayerData, AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerMutator.class);
    private static final byte ALL_FACES = 0b111111;
    private static final byte UPPER_LIMB_FACES = (byte) (ALL_FACES & ~(1 << BendsCube.BOTTOM));
    private static final byte LOWER_LIMB_FACES = (byte) (ALL_FACES & ~(1 << BendsCube.TOP));

    protected BendsModelPart bodywear;
    protected BendsModelPart leftArmwear;
    protected BendsModelPart rightArmwear;
    protected BendsModelPart leftForeArmwear;
    protected BendsModelPart rightForeArmwear;
    protected BendsModelPart leftLegwear;
    protected BendsModelPart rightLegwear;
    protected BendsModelPart leftForeLegwear;
    protected BendsModelPart rightForeLegwear;

    protected boolean smallArms;

    protected LayerCustomCape layerCape;
    protected CapeLayer layerCapeVanilla;

    public PlayerMutator(IEntityDataFactory<AbstractClientPlayer> dataFactory) {
        super(dataFactory);
    }

    public boolean hasSmallArms() {
        return this.smallArms;
    }

    @Override
    public boolean mutate(
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
                    renderer) {
        return super.mutate(renderer);
    }

    @Override
    public void demutate(
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
                    renderer) {
        super.demutate(renderer);
    }

    @Override
    public void fetchFields(
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
                    renderer) {
        super.fetchFields(renderer);

        // Does the renderer have Small Arms?
        // In 1.20.1, slim is a field in PlayerRenderer that determines arm width.
        // The access transformer makes f_117788_ (slim) public.
        if (renderer instanceof PlayerRenderer playerRenderer) {
            this.smallArms = detectSlimArms(playerRenderer);
        }
    }

    //
    // Detect slim arms from the PlayerRenderer. Uses reflection on the 'slim' field (made
    // accessible via access widener), with model cube width analysis as fallback.
    //
    private boolean detectSlimArms(PlayerRenderer playerRenderer) {
        // Approach 1: Reflection on 'slim' field (Mojang name + SRG fallback)
        // Access widener makes setAccessible succeed reliably at runtime
        String[] fieldNames = {"slim", "f_117788_"};
        for (String fieldName : fieldNames) {
            try {
                java.lang.reflect.Field slimField =
                        PlayerRenderer.class.getDeclaredField(fieldName);
                slimField.setAccessible(true);
                boolean result = slimField.getBoolean(playerRenderer);
                LOG.debug("Detected slim arms via field '{}': {}", fieldName, result);
                return result;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        // Approach 2: Check model arm cube width (slim = 3px, default = 4px)
        try {
            PlayerModel<?> model = playerRenderer.getModel();
            if (model != null && model.leftArm != null) {
                // 'cubes' field also made accessible via access widener
                String[] cubeFieldNames = {"cubes", "f_104222_"};
                java.util.List<?> cubes = null;
                for (String fieldName : cubeFieldNames) {
                    try {
                        java.lang.reflect.Field cubesField =
                                net.minecraft.client.model.geom.ModelPart.class.getDeclaredField(
                                        fieldName);
                        cubesField.setAccessible(true);
                        cubes = (java.util.List<?>) cubesField.get(model.leftArm);
                        break;
                    } catch (NoSuchFieldException ignored) {
                    }
                }
                if (cubes != null) {
                    for (Object obj : cubes) {
                        net.minecraft.client.model.geom.ModelPart.Cube cube =
                                (net.minecraft.client.model.geom.ModelPart.Cube) obj;
                        float width = cube.maxX - cube.minX;
                        if (Math.abs(width - 3.0f) < 0.1f) {
                            LOG.debug("Detected slim arms via cube width: true");
                            return true;
                        }
                    }
                    LOG.debug("Detected slim arms via cube width: false");
                    return false;
                }
            }
        } catch (Exception e) {
            LOG.warn("Failed to detect slim arms via model cube width: {}", e.getMessage());
        }

        LOG.warn("Could not detect slim arms, defaulting to standard arms");
        return false;
    }

    //
    // Update the smallArms field based on the player's model name. This should be called when we
    // have access to the player entity.
    //
    public void updateSmallArms(AbstractClientPlayer player) {
        if (player != null) {
            IPlayerSkinProvider skinProvider = IPlayerSkinProvider.Holder.getProvider();
            this.smallArms = skinProvider != null && skinProvider.isSlimModel(player);
        }
    }

    @Override
    public void updateModel(
            AbstractClientPlayer entity,
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            float partialTicks) {
        if (entity != null && this.body != null) {
            IPlayerSkinProvider skinProvider = IPlayerSkinProvider.Holder.getProvider();
            boolean playerIsSlim = skinProvider != null && skinProvider.isSlimModel(entity);
            if (playerIsSlim != this.smallArms) {
                this.smallArms = playerIsSlim;
                createParts(renderer.getModel(), 0F);
            }
        }

        super.updateModel(entity, renderer, partialTicks);
    }

    @Override
    public void storeVanillaModel(PlayerModel<AbstractClientPlayer> model) {
        super.storeVanillaModel(model);
    }

    @Override
    public void applyVanillaModel(PlayerModel<AbstractClientPlayer> model) {
        super.applyVanillaModel(model);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void swapLayer(
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            int index,
            boolean isModelVanilla) {
        super.swapLayer(renderer, index, isModelVanilla);

        final RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer =
                layerRenderers.get(index);
        if (layer instanceof CapeLayer) {
            this.layerCape = new LayerCustomCape((PlayerRenderer) renderer);
            if (isModelVanilla) this.layerCapeVanilla = (CapeLayer) layer;
            layerRenderers.set(index, this.layerCape);
        }

        // Note: ElytraLayer is NOT swapped - vanilla elytra layer uses the synced
        // rotations from syncPosesToVanillaModel() and renders correctly
    }

    @Override
    public void deswapLayer(
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            int index) {
        super.deswapLayer(renderer, index);

        final RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer =
                layerRenderers.get(index);
        if (layer instanceof LayerCustomCape) {
            layerRenderers.set(index, this.layerCapeVanilla);
        }
    }

    @Override
    public boolean createParts(PlayerModel<AbstractClientPlayer> original, float scaleFactor) {
        // Arms
        int armWidth = this.smallArms ? 3 : 4;
        float armY = this.smallArms ? -9.5F : -10F;

        // Create custom bendable parts using BendsModelPart
        // Body - root of upper body hierarchy
        body = new BendsModelPart(16, 16).setTextureSize(64, 64).setPosition(0.0F, 12.0F, 0.0F);
        body.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor);

        // Head - child of body
        head = new BendsModelPart(0, 0).setTextureSize(64, 64).setPosition(0.0F, -12.0F, 0.0F);
        head.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor);
        body.addChild(head);

        // Headwear - child of head
        headwear = new BendsModelPart(32, 0).setTextureSize(64, 64);
        headwear.addCube(-4.0F, -8.0F, -4.0F, 8, 8, 8, scaleFactor + 0.5F);
        head.addChild(headwear);

        // Left Arm (texture at 32, 48 for player) - child of body
        leftArm = new BendsModelPart(32, 48).setTextureSize(64, 64).setPosition(5.0F, armY, 0.0F);
        leftArm.addCube(
                -1.0F,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                32,
                48);
        body.addChild(leftArm);

        // Right Arm (texture at 40, 16 for player) - child of body
        rightArm = new BendsModelPart(40, 16).setTextureSize(64, 64).setPosition(-5.0F, armY, 0.0F);
        rightArm.addCube(
                -armWidth + 1,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                UPPER_LIMB_FACES,
                40,
                16);
        body.addChild(rightArm);

        // Left Forearm - child of leftArm
        leftForeArm =
                new BendsModelPart(32, 48 + 6).setTextureSize(64, 64).setPosition(0.0F, 4.0F, 2.0F);
        leftForeArm.addCube(
                -1.0F, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor, LOWER_LIMB_FACES, 32, 48);
        leftArm.addChild(leftForeArm);

        // Right Forearm - child of rightArm
        rightForeArm =
                new BendsModelPart(40, 16 + 6).setTextureSize(64, 64).setPosition(0.0F, 4.0F, 2.0F);
        rightForeArm.addCube(
                -armWidth + 1, 0.0F, -4.0F, armWidth, 6, 4, scaleFactor, LOWER_LIMB_FACES, 40, 16);
        rightArm.addChild(rightForeArm);

        // Legs (texture at 16, 48 for left leg, 0, 16 for right leg in player model)
        // Legs are independent roots (not children of body)
        leftLeg = new BendsModelPart(16, 48).setTextureSize(64, 64).setPosition(1.9F, 12.0F, 0.0F);
        leftLeg.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        rightLeg = new BendsModelPart(0, 16).setTextureSize(64, 64).setPosition(-1.9F, 12.0F, 0.0F);
        rightLeg.addCube(-2.0F, 0.0F, -2.0F, 4, 6, 4, scaleFactor);

        // Left Foreleg - child of leftLeg
        leftForeLeg =
                new BendsModelPart(16, 48 + 6)
                        .setTextureSize(64, 64)
                        .setPosition(0.0F, 6.0F, -2.0F);
        leftForeLeg.addCube(
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                16,
                48);
        leftLeg.addChild(leftForeLeg);

        // Right Foreleg - child of rightLeg
        rightForeLeg =
                new BendsModelPart(0, 16 + 6).setTextureSize(64, 64).setPosition(0.0F, 6.0F, -2.0F);
        rightForeLeg.addCube(
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + 0.01F,
                scaleFactor,
                scaleFactor + 0.01F,
                ALL_FACES,
                0,
                16);
        rightLeg.addChild(rightForeLeg);

        // Wear layers (second skin layer)
        float wearOffset = 0.25F;
        float limbWearHeight = (6.0F + 2.0F * scaleFactor + 0.5F) - 0.25F;

        // Bodywear - child of body
        bodywear = new BendsModelPart(16, 32).setTextureSize(64, 64);
        bodywear.addCube(-4.0F, -12.0F, -2.0F, 8, 12, 4, scaleFactor + wearOffset);
        body.addChild(bodywear);

        // Left arm wear - child of leftArm
        leftArmwear = new BendsModelPart(48, 48).setTextureSize(64, 64);
        addResizedWearCube(
                leftArmwear,
                -1.0F,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0025F,
                0.0F,
                UPPER_LIMB_FACES,
                48,
                48);
        leftArm.addChild(leftArmwear);

        // Right arm wear - child of rightArm
        rightArmwear = new BendsModelPart(40, 32).setTextureSize(64, 64);
        addResizedWearCube(
                rightArmwear,
                -armWidth + 1,
                -2.0F,
                -2.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0025F,
                0.0F,
                UPPER_LIMB_FACES,
                40,
                32);
        rightArm.addChild(rightArmwear);

        // Left forearm wear - child of leftForeArm
        leftForeArmwear = new BendsModelPart(48, 48 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                leftForeArmwear,
                -1.0F,
                0.0F,
                -4.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                48,
                48);
        leftForeArm.addChild(leftForeArmwear);

        // Right forearm wear - child of rightForeArm
        rightForeArmwear = new BendsModelPart(40, 32 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                rightForeArmwear,
                -armWidth + 1,
                0.0F,
                -4.0F,
                armWidth,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                40,
                32);
        rightForeArm.addChild(rightForeArmwear);

        // Left leg wear - child of leftLeg
        leftLegwear = new BendsModelPart(0, 48).setTextureSize(64, 64);
        addResizedWearCube(
                leftLegwear,
                -2.0F,
                0.0F,
                -2.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0F,
                0.0F,
                UPPER_LIMB_FACES,
                0,
                48);
        leftLeg.addChild(leftLegwear);

        // Right leg wear - child of rightLeg
        rightLegwear = new BendsModelPart(0, 32).setTextureSize(64, 64);
        addResizedWearCube(
                rightLegwear,
                -2.0F,
                0.0F,
                -2.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.0F,
                0.0F,
                UPPER_LIMB_FACES,
                0,
                32);
        rightLeg.addChild(rightLegwear);

        // Left foreleg wear - child of leftForeLeg
        leftForeLegwear = new BendsModelPart(0, 48 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                leftForeLegwear,
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                0,
                48);
        leftForeLeg.addChild(leftForeLegwear);

        // Right foreleg wear - child of rightForeLeg
        rightForeLegwear = new BendsModelPart(0, 32 + 6).setTextureSize(64, 64);
        addResizedWearCube(
                rightForeLegwear,
                -2.0F,
                0.0F,
                0.0F,
                4,
                6,
                4,
                scaleFactor + wearOffset,
                limbWearHeight,
                0.005F,
                0.25F,
                LOWER_LIMB_FACES,
                0,
                32);
        rightForeLeg.addChild(rightForeLegwear);

        return true;
    }

    private static void addResizedWearCube(
            BendsModelPart part,
            float x,
            float y,
            float z,
            int width,
            int height,
            int depth,
            float inflation,
            float resizedHeight,
            float seamInflation,
            float yOffset,
            byte faceVisibilityFlag,
            int bottomTexOffsetX,
            int bottomTexOffsetY) {
        float minX = x - inflation - seamInflation;
        float minY = y - inflation + yOffset;
        float minZ = z - inflation - seamInflation;
        float maxX = x + width + inflation + seamInflation;
        float maxY = y - inflation + resizedHeight + yOffset;
        float maxZ = z + depth + inflation + seamInflation;
        part.addCubeBounds(
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ,
                width,
                height,
                depth,
                faceVisibilityFlag,
                bottomTexOffsetX,
                bottomTexOffsetY);
    }

    public void syncPlayerVisibility(PlayerModel<?> model) {
        syncPlayerVisibility(
                !model.body.visible && model.head.visible,
                model.hat.visible,
                model.jacket.visible,
                model.leftSleeve.visible,
                model.rightSleeve.visible,
                model.leftPants.visible,
                model.rightPants.visible);

        // Non-spectator render hooks and other mods may hide individual base
        // parts directly, so preserve those flags as well.
        body.setVisible(model.body.visible);
        head.setVisible(model.head.visible);
        leftArm.setVisible(model.leftArm.visible);
        rightArm.setVisible(model.rightArm.visible);
        leftLeg.setVisible(model.leftLeg.visible);
        rightLeg.setVisible(model.rightLeg.visible);
    }

    public void syncPlayerVisibility(
            boolean spectator,
            boolean showHat,
            boolean showJacket,
            boolean showLeftSleeve,
            boolean showRightSleeve,
            boolean showLeftPants,
            boolean showRightPants) {
        boolean showBody = !spectator;

        body.setVisible(showBody);
        head.setVisible(true);
        headwear.setVisible(showHat);
        leftArm.setVisible(showBody);
        rightArm.setVisible(showBody);
        leftLeg.setVisible(showBody);
        rightLeg.setVisible(showBody);

        bodywear.setVisible(showJacket);
        leftArmwear.setVisible(showLeftSleeve);
        leftForeArmwear.setVisible(showLeftSleeve);
        rightArmwear.setVisible(showRightSleeve);
        rightForeArmwear.setVisible(showRightSleeve);
        leftLegwear.setVisible(showLeftPants);
        leftForeLegwear.setVisible(showLeftPants);
        rightLegwear.setVisible(showRightPants);
        rightForeLegwear.setVisible(showRightPants);
    }

    @Override
    public void syncUpWithData(PlayerData data) {
        super.syncUpWithData(data);
        // Sync wear parts with their base parts - they share the same transforms
    }

    @Override
    public void performAnimations(
            PlayerData data,
            String animatedEntityKey,
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            float partialTicks) {
        // Sync wear visibility with base parts
        if (leftForeArmwear != null && leftArmwear != null)
            leftForeArmwear.setVisible(leftArmwear.isShowing());
        if (rightForeArmwear != null && rightArmwear != null)
            rightForeArmwear.setVisible(rightArmwear.isShowing());
        if (leftForeLegwear != null && leftLegwear != null)
            leftForeLegwear.setVisible(leftLegwear.isShowing());
        if (rightForeLegwear != null && rightLegwear != null)
            rightForeLegwear.setVisible(rightLegwear.isShowing());

        super.performAnimations(data, animatedEntityKey, renderer, partialTicks);
    }

    @Override
    public void postRefresh() {
        if (this.layerArmor != null) this.layerArmor.initArmor();
    }

    // Called before the first person hand is rendered, so the mutator can pose it in any way.
    public void poseForFirstPersonView() {
        if (this.body != null) this.body.getRotation().identity();
        if (this.rightArm != null) this.rightArm.getRotation().identity();
        if (this.rightForeArm != null) this.rightForeArm.getRotation().identity();
        if (this.leftArm != null) this.leftArm.getRotation().identity();
        if (this.leftForeArm != null) this.leftForeArm.getRotation().identity();
    }

    @Override
    public boolean isModelVanilla(PlayerModel<AbstractClientPlayer> model) {
        // Check if we've already created custom parts
        return this.body == null;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof PlayerModel);
    }

    @Override
    public PlayerData getData(AbstractClientPlayer entity) {
        // Update slim arms based on the actual player's skin model
        // This ensures correct detection even if initial reflection failed
        if (entity != null) {
            IPlayerSkinProvider skinProvider = IPlayerSkinProvider.Holder.getProvider();
            boolean playerIsSlim = skinProvider != null && skinProvider.isSlimModel(entity);
            if (playerIsSlim != this.smallArms) {
                LOG.debug(
                        "Slim arm mismatch detected for {}: mutator={}, player={}. Updating.",
                        entity.getName().getString(),
                        this.smallArms,
                        playerIsSlim);
                this.smallArms = playerIsSlim;
                // Note: Parts are already created with the old dimension.
                // They will be corrected on next mutation cycle.
            }
        }
        return super.getData(entity);
    }

    @Override
    public PlayerData getOrMakeData(AbstractClientPlayer entity) {
        return super.getOrMakeData(entity);
    }

    @Override
    public void renderMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int packedColor) {
        // Render body and all attached parts (head, arms)
        if (body != null) {
            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }

        // Render legs (not attached to body)
        if (leftLeg != null) {
            leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }
        if (rightLeg != null) {
            rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
        }
    }

    // Getters for wear parts
    public BendsModelPart getBodywear() {
        return bodywear;
    }

    public BendsModelPart getLeftArmwear() {
        return leftArmwear;
    }

    public BendsModelPart getRightArmwear() {
        return rightArmwear;
    }

    public BendsModelPart getLeftForeArmwear() {
        return leftForeArmwear;
    }

    public BendsModelPart getRightForeArmwear() {
        return rightForeArmwear;
    }

    public BendsModelPart getLeftLegwear() {
        return leftLegwear;
    }

    public BendsModelPart getRightLegwear() {
        return rightLegwear;
    }

    public BendsModelPart getLeftForeLegwear() {
        return leftForeLegwear;
    }

    public BendsModelPart getRightForeLegwear() {
        return rightForeLegwear;
    }
}
// ?}
