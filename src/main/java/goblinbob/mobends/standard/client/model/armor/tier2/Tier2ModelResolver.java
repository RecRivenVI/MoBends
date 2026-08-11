package goblinbob.mobends.standard.client.model.armor.tier2;

import javax.annotation.Nullable;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

/** Resolves model roots and conventional limb names for Tier 2 armor renderers. */
final class Tier2ModelResolver {
    private static final String[] ROOT_FIELDS = {"root", "body", "main"};

    private Tier2ModelResolver() {}

    @Nullable static ModelPart findRoot(Model model) {
        try {
            for (String fieldName : ROOT_FIELDS) {
                try {
                    java.lang.reflect.Field field = model.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(model);
                    if (value instanceof ModelPart) {
                        return (ModelPart) value;
                    }
                } catch (NoSuchFieldException ignored) {
                    // Try the next conventional root name.
                }
            }

            for (java.lang.reflect.Field field : model.getClass().getDeclaredFields()) {
                if (ModelPart.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return (ModelPart) field.get(model);
                }
            }
        } catch (ReflectiveOperationException | SecurityException ignored) {
            // A custom model may not expose a readable root; the caller retains vanilla fallback.
        }
        return null;
    }

    @Nullable static ModelPart findPart(ModelPart root, String... names) {
        for (String name : names) {
            try {
                ModelPart child = root.getChild(name);
                if (child != null) {
                    return child;
                }
            } catch (RuntimeException ignored) {
                // A missing child is normal for non-humanoid custom models.
            }
        }
        return null;
    }
}
