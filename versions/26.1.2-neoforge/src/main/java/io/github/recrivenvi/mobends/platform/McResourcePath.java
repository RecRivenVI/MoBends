// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
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
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.resource.IResourcePath;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

//
// Shared implementation of IResourcePath. Wraps Minecraft's ResourceLocation with a
// platform-agnostic interface.
//
public class McResourcePath implements IResourcePath {
    private final ResourceLocation location;

    public McResourcePath(ResourceLocation location) {
        this.location = location;
    }

    public McResourcePath(String namespace, String path) {
        this.location = VersionAdapter.Holder.get().createResourceLocation(namespace, path);
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

    public ResourceLocation getLocation() {
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
}*/
// ?}
