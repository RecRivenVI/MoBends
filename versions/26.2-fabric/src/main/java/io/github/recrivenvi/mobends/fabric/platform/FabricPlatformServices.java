// ? if target_26_2_fabric {
package io.github.recrivenvi.mobends.fabric.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.recrivenvi.mobends.api.armor.ArmorModelProviderHolder;
import io.github.recrivenvi.mobends.api.armor.IArmorTextureProvider;
import io.github.recrivenvi.mobends.api.entity.IEntity;
import io.github.recrivenvi.mobends.api.entity.IItemStack;
import io.github.recrivenvi.mobends.api.entity.ILivingEntity;
import io.github.recrivenvi.mobends.api.entity.IPlayer;
import io.github.recrivenvi.mobends.api.platform.IPlatformServices;
import io.github.recrivenvi.mobends.api.rendering.IArmorColorProvider;
import io.github.recrivenvi.mobends.api.rendering.IArmorHelper;
import io.github.recrivenvi.mobends.api.rendering.IArmorLayerProvider;
import io.github.recrivenvi.mobends.api.rendering.IBufferSource;
import io.github.recrivenvi.mobends.api.rendering.IEntityVertexHelper;
import io.github.recrivenvi.mobends.api.rendering.IModelRenderHelper;
import io.github.recrivenvi.mobends.api.rendering.IPoseStack;
import io.github.recrivenvi.mobends.api.rendering.IRenderLayerProvider;
import io.github.recrivenvi.mobends.api.rendering.ITesselator;
import io.github.recrivenvi.mobends.api.resource.ILocalization;
import io.github.recrivenvi.mobends.api.resource.IResourceManager;
import io.github.recrivenvi.mobends.api.resource.IResourcePath;
import io.github.recrivenvi.mobends.platform.McEntity;
import io.github.recrivenvi.mobends.platform.McItemStack;
import io.github.recrivenvi.mobends.platform.McLivingEntity;
import io.github.recrivenvi.mobends.platform.McLocalization;
import io.github.recrivenvi.mobends.platform.McPlayer;
import io.github.recrivenvi.mobends.platform.McPoseStack;
import io.github.recrivenvi.mobends.platform.McRenderLayerProvider;
import io.github.recrivenvi.mobends.platform.McResourceManager;
import io.github.recrivenvi.mobends.platform.McResourcePath;
import io.github.recrivenvi.mobends.platform.VersionAdapter;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//
// Fabric implementation of IPlatformServices. Provides platform-specific functionality for Fabric
// 1.21.1.
//
public class FabricPlatformServices implements IPlatformServices {
    private final McResourceManager resourceManager = new McResourceManager();
    private final McLocalization localization = new McLocalization();
    private final McRenderLayerProvider renderLayerProvider = new McRenderLayerProvider();

    public FabricPlatformServices() {
        // Initialize the version adapter first (shared code depends on it)
        VersionAdapter.Holder.set(new FabricVersionAdapter());

        // Initialize helper singletons
        IEntityVertexHelper.Holder.setHelper(new FabricEntityVertexHelper());
        IModelRenderHelper.Holder.setHelper(new FabricModelRenderHelper());
        IArmorHelper.Holder.setHelper(new FabricArmorHelper());
        ArmorModelProviderHolder.setProvider(new FabricArmorModelProvider());
        IArmorTextureProvider.Holder.setProvider(new FabricArmorTextureProvider());
        IArmorLayerProvider.Holder.setProvider(new FabricArmorLayerProvider());
        IArmorColorProvider.Holder.setProvider(new FabricArmorColorProvider());
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public String getMinecraftVersion() {
        return net.minecraft.SharedConstants.getCurrentVersion().name();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public ILocalization getLocalization() {
        return localization;
    }

    @Override
    public IRenderLayerProvider getRenderLayerProvider() {
        return renderLayerProvider;
    }

    @Override
    @Nullable public IEntity wrapEntity(Object nativeEntity) {
        if (nativeEntity instanceof Entity entity) {
            return new McEntity(entity);
        }
        return null;
    }

    @Override
    @Nullable public ILivingEntity wrapLivingEntity(Object nativeEntity) {
        if (nativeEntity instanceof LivingEntity entity) {
            return new McLivingEntity(entity);
        }
        return null;
    }

    @Override
    @Nullable public IPlayer wrapPlayer(Object nativeEntity) {
        if (nativeEntity instanceof Player player) {
            return new McPlayer(player);
        }
        return null;
    }

    @Override
    public IPoseStack wrapPoseStack(Object nativePoseStack) {
        if (nativePoseStack instanceof PoseStack poseStack) {
            return new McPoseStack(poseStack);
        }
        throw new IllegalArgumentException(
                "Expected PoseStack, got: " + nativePoseStack.getClass().getName());
    }

    @Override
    public IBufferSource wrapBufferSource(Object nativeBufferSource) {
        if (nativeBufferSource instanceof VertexConsumer vertexConsumer) {
            return new FabricBufferSource(vertexConsumer);
        }
        throw new IllegalArgumentException(
                "Expected VertexConsumer, got: " + nativeBufferSource.getClass().getName());
    }

    @Override
    public IItemStack wrapItemStack(Object nativeItemStack) {
        if (nativeItemStack instanceof ItemStack itemStack) {
            return new McItemStack(itemStack);
        }
        throw new IllegalArgumentException(
                "Expected ItemStack, got: " + nativeItemStack.getClass().getName());
    }

    @Override
    public IResourcePath wrapResourceLocation(Object nativeIdentifier) {
        if (nativeIdentifier instanceof Identifier location) {
            return new McResourcePath(location);
        }
        throw new IllegalArgumentException(
                "Expected Identifier, got: " + nativeIdentifier.getClass().getName());
    }

    @Override
    public IPoseStack createPoseStack() {
        return new McPoseStack();
    }

    @Override
    public IResourcePath createResourcePath(String namespace, String path) {
        return new McResourcePath(namespace, path);
    }

    @Override
    @Nullable public IResourcePath parseResourcePath(String location) {
        try {
            Identifier loc = VersionAdapter.Holder.get().parseIdentifier(location);
            return new McResourcePath(loc);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ITesselator getTesselator() {
        return new FabricTesselator();
    }

    @Override
    public void setPositionShader() {
        // 26.1 由 RenderType 选择渲染管线。
    }

    @Override
    public void setPositionColorShader() {
        // 26.1 由 RenderType 选择渲染管线。
    }

    @Override
    public void setPositionTexShader() {
        // 26.1 由 RenderType 选择渲染管线。
    }

    @Override
    public void setPositionTexColorShader() {
        // 26.1 由 RenderType 选择渲染管线。
    }
}
// ?}
