package goblinbob.mobends.neoforge.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import goblinbob.mobends.api.armor.ArmorModelProviderHolder;
import goblinbob.mobends.api.armor.IArmorTextureProvider;
import goblinbob.mobends.api.entity.IEntity;
import goblinbob.mobends.api.entity.IItemStack;
import goblinbob.mobends.api.entity.ILivingEntity;
import goblinbob.mobends.api.entity.IPlayer;
import goblinbob.mobends.api.platform.IPlatformServices;
import goblinbob.mobends.api.rendering.IArmorColorProvider;
import goblinbob.mobends.api.rendering.IArmorHelper;
import goblinbob.mobends.api.rendering.IArmorLayerProvider;
import goblinbob.mobends.api.rendering.IBufferSource;
import goblinbob.mobends.api.rendering.IEntityVertexHelper;
import goblinbob.mobends.api.rendering.IModelRenderHelper;
import goblinbob.mobends.api.rendering.IPoseStack;
import goblinbob.mobends.api.rendering.IRenderLayerProvider;
import goblinbob.mobends.api.rendering.ITesselator;
import goblinbob.mobends.api.resource.ILocalization;
import goblinbob.mobends.api.resource.IResourceManager;
import goblinbob.mobends.api.resource.IResourcePath;
import goblinbob.mobends.platform.McEntity;
import goblinbob.mobends.platform.McItemStack;
import goblinbob.mobends.platform.McLivingEntity;
import goblinbob.mobends.platform.McLocalization;
import goblinbob.mobends.platform.McPlayer;
import goblinbob.mobends.platform.McPoseStack;
import goblinbob.mobends.platform.McRenderLayerProvider;
import goblinbob.mobends.platform.McResourceManager;
import goblinbob.mobends.platform.McResourcePath;
import goblinbob.mobends.platform.VersionAdapter;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * NeoForge implementation of IPlatformServices. Provides platform-specific functionality for
 * NeoForge 1.21.1.
 */
public class NeoForgePlatformServices implements IPlatformServices {
    private final McResourceManager resourceManager = new McResourceManager();
    private final McLocalization localization = new McLocalization();
    private final McRenderLayerProvider renderLayerProvider = new McRenderLayerProvider();

    public NeoForgePlatformServices() {
        // Initialize the version adapter first (shared code depends on it)
        VersionAdapter.Holder.set(new NeoForgeVersionAdapter());

        // Initialize helper singletons
        IEntityVertexHelper.Holder.setHelper(new NeoForgeEntityVertexHelper());
        IModelRenderHelper.Holder.setHelper(new NeoForgeModelRenderHelper());
        IArmorHelper.Holder.setHelper(new NeoForgeArmorHelper());
        ArmorModelProviderHolder.setProvider(new NeoForgeArmorModelProvider());
        IArmorTextureProvider.Holder.setProvider(new NeoForgeArmorTextureProvider());
        IArmorLayerProvider.Holder.setProvider(new NeoForgeArmorLayerProvider());
        IArmorColorProvider.Holder.setProvider(new NeoForgeArmorColorProvider());
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public String getMinecraftVersion() {
        return net.minecraft.SharedConstants.getCurrentVersion().name();
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
            return new NeoForgeBufferSource(vertexConsumer);
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
        return new NeoForgeTesselator();
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
