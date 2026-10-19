package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.resource.IResourcePath;
import java.util.Objects;
import net.minecraft.resources.Identifier;

//
// Shared implementation of IResourcePath. Wraps Minecraft's Identifier with a platform-agnostic
// interface.
//
public class McResourcePath implements IResourcePath {
    private final Identifier location;

    public McResourcePath(Identifier location) {
        this.location = location;
    }

    public McResourcePath(String namespace, String path) {
        this.location = VersionAdapter.Holder.get().createIdentifier(namespace, path);
    }

    @Override
    public String getNamespace() {
        return location.getNamespace();
    }

    @Override
    public String getPath() {
        return location.getPath();
    }

    @Override
    public IResourcePath withPath(String path) {
        return new McResourcePath(location.getNamespace(), path);
    }

    @Override
    public Object getNative() {
        return location;
    }

    public Identifier getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof IResourcePath that) {
            return Objects.equals(getNamespace(), that.getNamespace())
                    && Objects.equals(getPath(), that.getPath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return location.toString();
    }
}
