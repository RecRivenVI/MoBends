package goblinbob.mobends.core.util;

import net.minecraft.resources.Identifier;

/** 为当前 Minecraft 版本集中创建资源标识符。 */
public final class IdentifierFactory {
    private IdentifierFactory() {}

    public static Identifier create(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier parse(String location) {
        return Identifier.parse(location);
    }
}
