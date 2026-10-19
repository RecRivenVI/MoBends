// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.core.util;

import io.github.recrivenvi.mobends.core.kumo.KumoSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public class GsonResources {

    private static Map<Identifier, Object> cache = new HashMap<>();

    public static void clearCache() {
        cache.clear();
    }

    public static <T> T get(Identifier location, Class<T> classOfT) throws IOException {
        if (cache.containsKey(location)) {
            //noinspection unchecked
            return (T) cache.get(location);
        }

        Optional<Resource> resourceOpt =
                Minecraft.getInstance().getResourceManager().getResource(location);
        if (resourceOpt.isEmpty()) {
            throw new IOException("Resource not found: " + location);
        }

        try (InputStream stream = resourceOpt.get().open()) {
            T resource =
                    KumoSerializer.INSTANCE.gson.fromJson(new InputStreamReader(stream), classOfT);
            cache.put(location, resource);
            return resource;
        }
    }
}
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.core.util;

import io.github.recrivenvi.mobends.core.kumo.KumoSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class GsonResources {

    private static Map<ResourceLocation, Object> cache = new HashMap<>();

    public static void clearCache() {
        cache.clear();
    }

    public static <T> T get(ResourceLocation location, Class<T> classOfT) throws IOException {
        if (cache.containsKey(location)) {
            //noinspection unchecked
            return (T) cache.get(location);
        }

        Optional<Resource> resourceOpt =
                Minecraft.getInstance().getResourceManager().getResource(location);
        if (resourceOpt.isEmpty()) {
            throw new IOException("Resource not found: " + location);
        }

        try (InputStream stream = resourceOpt.get().open()) {
            T resource =
                    KumoSerializer.INSTANCE.gson.fromJson(new InputStreamReader(stream), classOfT);
            cache.put(location, resource);
            return resource;
        }
    }
}*/
// ?}
