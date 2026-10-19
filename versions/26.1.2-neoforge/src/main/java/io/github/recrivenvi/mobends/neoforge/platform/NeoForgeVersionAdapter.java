package io.github.recrivenvi.mobends.neoforge.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.platform.VersionAdapter;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import org.joml.Matrix4f;

//
// NeoForge 1.21.1 implementation of VersionAdapter. Provides MC 1.21.1 specific API
// implementations.
//
public class NeoForgeVersionAdapter implements VersionAdapter {
    @Override
    public String getPlayerModelName(AbstractClientPlayer player) {
        // MC 1.21.1 uses getSkin().model().id()
        return player.getSkin().model().getSerializedName();
    }

    @Override
    public Identifier parseIdentifier(String location) {
        // MC 1.21.1 uses Identifier.parse()
        return Identifier.parse(location);
    }

    @Override
    public Identifier createIdentifier(String namespace, String path) {
        // MC 1.21.1 uses Identifier.fromNamespaceAndPath()
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.getDist().isClient();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public String getMinecraftVersion() {
        return "26.1.2";
    }

    @Override
    public void mulPoseMatrix(PoseStack poseStack, Matrix4f matrix) {
        // MC 1.21.1 uses mulPose(Matrix4f)
        poseStack.mulPose(matrix);
    }

    @Override
    public boolean isItemEdible(ItemStack itemStack) {
        // MC 1.21.1 uses DataComponents
        return itemStack.has(DataComponents.FOOD);
    }
}
