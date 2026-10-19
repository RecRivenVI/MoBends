package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.rendering.IRenderLayer;
import io.github.recrivenvi.mobends.api.rendering.IRenderLayerProvider;
import io.github.recrivenvi.mobends.api.resource.IResourcePath;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

//
// Shared implementation of IRenderLayerProvider. Provides access to Minecraft's RenderType factory
// methods. Uses VersionAdapter for version-specific Identifier creation.
//
public class McRenderLayerProvider implements IRenderLayerProvider {
    @Override
    public IRenderLayer entitySolid(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.entitySolid(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer entityCutout(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.entityCutout(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer entityCutoutNoCull(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.entityCutout(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer entityTranslucent(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.entityTranslucent(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer entityTranslucentCull(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.entityTranslucentCull(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer armorCutoutNoCull(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.armorCutoutNoCull(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer entitySmoothCutout(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.entityCutout(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer eyes(IResourcePath texture) {
        return new McRenderLayer(RenderTypes.eyes(getIdentifier(texture)));
    }

    @Override
    public IRenderLayer lines() {
        return new McRenderLayer(RenderTypes.lines());
    }

    private Identifier getIdentifier(IResourcePath path) {
        if (path instanceof McResourcePath mcPath) {
            return mcPath.getLocation();
        }
        return VersionAdapter.Holder.get().createIdentifier(path.getNamespace(), path.getPath());
    }
}
