//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.core.client.model.BendsModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import io.github.recrivenvi.mobends.standard.data.SquidData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.animal.squid.Squid;

public class SquidMutator extends Mutator<SquidData, Squid, SquidModel> {

    public BendsModelPart squidBody;
    public BendsModelPart[][] squidTentacles = new BendsModelPart[8][SquidData.TENTACLE_SECTIONS];

    public SquidMutator(IEntityDataFactory<Squid> dataFactory) {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(SquidModel model) {
        // In 1.20.1, model parts are accessed differently
        // SquidModel uses root.getChild() pattern
        this.vanillaModel = model;
    }

    @Override
    public void applyVanillaModel(SquidModel model) {
        // In 1.20.1, we can't directly set model fields
        // The vanilla model restoration would need to be handled via mixin
    }

    @Override
    public void swapLayer(
            LivingEntityRenderer<?, ?, ?> renderer, int index, boolean isModelVanilla) {
        // No behaviour
    }

    @Override
    public void deswapLayer(LivingEntityRenderer<?, ?, ?> renderer, int index) {
        // No behaviour
    }

    @Override
    public boolean createParts(SquidModel original, float scaleFactor) {
        this.squidBody =
                new BendsModelPart(0, 0).setTextureSize(64, 32).setPosition(0.0F, 8.0F, 0.0F);
        this.squidBody.addCube(-6.0F, -8.0F, -6.0F, 12, 16, 12, scaleFactor);
        this.squidTentacles =
                createTentacles(
                        48,
                        0,
                        64,
                        32,
                        5.0F,
                        15.0F,
                        SquidData.SECTION_HEIGHT,
                        SquidData.TENTACLE_SECTIONS,
                        scaleFactor);
        return true;
    }

    private static BendsModelPart[][] createTentacles(
            int textureU,
            int textureV,
            int textureWidth,
            int textureHeight,
            float radius,
            float rootY,
            int sectionHeight,
            int visibleSections,
            float scaleFactor) {
        BendsModelPart[][] result = new BendsModelPart[8][SquidData.TENTACLE_SECTIONS];

        for (int i = 0; i < result.length; ++i) {
            double angle = (double) i * Math.PI * 2.0D / (double) result.length;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            result[i][0] =
                    new BendsModelPart(textureU, textureV)
                            .setTextureSize(textureWidth, textureHeight)
                            .setPosition(x, rootY, z);

            for (int j = 0; j < SquidData.TENTACLE_SECTIONS; ++j) {
                if (j > 0) {
                    result[i][j] =
                            new BendsModelPart(textureU, textureV)
                                    .setTextureSize(textureWidth, textureHeight)
                                    .setPosition(0.0F, sectionHeight, 0.0F);
                    result[i][j - 1].addChild(result[i][j]);
                }

                if (j < visibleSections) {
                    float cubeZ = j == 0 ? 0.0F : -2.0F;
                    result[i][j].addCube(-1.0F, 0.0F, cubeZ, 2, sectionHeight, 2, scaleFactor);
                }
            }
        }
        return result;
    }

    @Override
    public void syncUpWithData(SquidData data) {
        this.squidBody.syncUp(data.squidBody);
        for (int i = 0; i < this.squidTentacles.length; ++i) {
            for (int j = 0; j < SquidData.TENTACLE_SECTIONS; ++j) {
                this.squidTentacles[i][j].syncUp(data.squidTentacles[i][j]);
                if (j == 0) {
                    this.squidTentacles[i][j].position.x =
                            data.squidTentacles[i][j].position.x * 1.25F;
                    this.squidTentacles[i][j].position.y =
                            15.0F + data.squidTentacles[i][j].position.y - 16.0F;
                    this.squidTentacles[i][j].position.z =
                            data.squidTentacles[i][j].position.z * 1.25F;
                }
            }
        }
    }

    @Override
    public boolean isModelVanilla(SquidModel model) {
        // In 1.20.1, check if we've applied mutations
        return this.squidBody == null;
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof SquidModel);
    }

    @Override
    public boolean shouldRenderCustom() {
        return this.squidBody != null;
    }

    @Override
    public void renderMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color) {
        if (squidBody != null) {
            squidBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
        for (BendsModelPart[] tentacle : squidTentacles) {
            if (tentacle[0] != null) {
                tentacle[0].render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            }
        }
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.mutators;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.core.client.model.IModelPart;
import io.github.recrivenvi.mobends.core.client.model.ModelPart;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import io.github.recrivenvi.mobends.standard.data.SquidData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.animal.Squid;

public class SquidMutator extends Mutator<SquidData, Squid, SquidModel<Squid>> {

    public ModelPart squidBody;
    public ModelPart[][] squidTentacles = new ModelPart[8][SquidData.TENTACLE_SECTIONS];

    public SquidMutator(IEntityDataFactory<Squid> dataFactory) {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(SquidModel<Squid> model) {
        // In 1.20.1, model parts are accessed differently
        // SquidModel uses root.getChild() pattern
        this.vanillaModel = model;
    }

    @Override
    public void applyVanillaModel(SquidModel<Squid> model) {
        // In 1.20.1, we can't directly set model fields
        // The vanilla model restoration would need to be handled via mixin
    }

    @Override
    public void swapLayer(
            LivingEntityRenderer<Squid, SquidModel<Squid>> renderer,
            int index,
            boolean isModelVanilla) {
        // No behaviour
    }

    @Override
    public void deswapLayer(LivingEntityRenderer<Squid, SquidModel<Squid>> renderer, int index) {
        // No behaviour
    }

    @Override
    public boolean createParts(SquidModel<Squid> original, float scaleFactor) {
        // In 1.20.1, SquidModel uses a completely different structure
        // The model is baked from LayerDefinition and parts are accessed via root
        // For now, create the custom parts without modifying the original model

        this.squidBody = new ModelPart(0, 0);
        this.squidBody.setPosition(0.0F, 8.0F, 0.0F);
        this.squidBody.developBox(-6.0F, -8.0F, -6.0F, 12, 16, 12, 0.0F).create();

        for (int i = 0; i < this.squidTentacles.length; ++i) {
            this.squidTentacles[i][0] = new ModelPart(48, 0);
            double d0 = (double) i * Math.PI * 2.0D / (double) this.squidTentacles.length;
            float f = (float) Math.cos(d0) * 4.0F;
            float f1 = (float) Math.sin(d0) * 4.0F;
            d0 = (double) i * -360.0D / (double) this.squidTentacles.length + 90.0D;
            this.squidTentacles[i][0].setPosition(f, 16.0F, f1);
            this.squidTentacles[i][0]
                    .developBox(-1.0F, 0.0F, 0.0F, 2, SquidData.SECTION_HEIGHT, 2, 0.0F)
                    .create();
            this.squidTentacles[i][0].rotation.rotateY((float) d0);

            for (int j = 1; j < SquidData.TENTACLE_SECTIONS; ++j) {
                this.squidTentacles[i][j] = new ModelPart(48, 0);
                this.squidTentacles[i][j].setPosition(0, SquidData.SECTION_HEIGHT, 0);
                this.squidTentacles[i][j]
                        .developBox(-1.0F, 0.0F, -2.0F, 2, SquidData.SECTION_HEIGHT, 2, 0.0F)
                        .create();
                this.squidTentacles[i][j - 1].addChild(this.squidTentacles[i][j]);
            }
        }

        return true;
    }

    @Override
    public void syncUpWithData(SquidData data) {
        this.squidBody.syncUp(data.squidBody);
        for (int i = 0; i < this.squidTentacles.length; ++i) {
            for (int j = 0; j < SquidData.TENTACLE_SECTIONS; ++j) {
                this.squidTentacles[i][j].syncUp(data.squidTentacles[i][j]);
            }
        }
    }

    @Override
    public boolean isModelVanilla(SquidModel<Squid> model) {
        // In 1.20.1, check if we've applied mutations
        return !(model instanceof IModelPart);
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof SquidModel);
    }

    @Override
    public boolean shouldRenderCustom() {
        // Squid doesn't use custom rendering yet
        return false;
    }

    @Override
    public void renderMutated(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            int color) {
        // Not implemented yet
    }
}*/
//?}
