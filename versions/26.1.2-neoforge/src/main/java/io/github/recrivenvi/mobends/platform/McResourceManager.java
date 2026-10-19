package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.resource.IResourceManager;
import io.github.recrivenvi.mobends.api.resource.IResourcePath;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

//
// Shared implementation of IResourceManager. Wraps Minecraft's ResourceManager with a
// platform-agnostic interface. Uses VersionAdapter for version-specific Identifier creation.
//
public class McResourceManager implements IResourceManager {
    @Override
    public Optional<InputStream> getResource(IResourcePath location) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Identifier loc = getIdentifier(location);

        try {
            Optional<Resource> resource = manager.getResource(loc);
            if (resource.isPresent()) {
                return Optional.of(resource.get().open());
            }
        } catch (IOException e) {
            // Resource not found or couldn't be opened
        }

        return Optional.empty();
    }

    @Override
    public Collection<InputStream> getResources(IResourcePath location) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Identifier loc = getIdentifier(location);
        List<InputStream> streams = new ArrayList<>();

        try {
            for (Resource resource : manager.getResourceStack(loc)) {
                try {
                    streams.add(resource.open());
                } catch (IOException e) {
                    // Skip resources that can't be opened
                }
            }
        } catch (Exception e) {
            // No resources found
        }

        return streams;
    }

    @Override
    public Collection<IResourcePath> listResources(String path, String extension) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        List<IResourcePath> result = new ArrayList<>();

        manager.listResources(path, loc -> loc.getPath().endsWith(extension))
                .forEach((loc, resource) -> result.add(new McResourcePath(loc)));

        return result;
    }

    @Override
    public IResourcePath createPath(String namespace, String path) {
        return new McResourcePath(namespace, path);
    }

    @Override
    @Nullable
    public IResourcePath parsePath(String location) {
        try {
            Identifier loc = VersionAdapter.Holder.get().parseIdentifier(location);
            return new McResourcePath(loc);
        } catch (Exception e) {
            return null;
        }
    }

    private Identifier getIdentifier(IResourcePath path) {
        if (path instanceof McResourcePath mcPath) {
            return mcPath.getLocation();
        }
        return VersionAdapter.Holder.get().createIdentifier(path.getNamespace(), path.getPath());
    }
}
