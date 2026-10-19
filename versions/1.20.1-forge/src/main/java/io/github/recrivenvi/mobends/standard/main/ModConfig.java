// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.main;

import io.github.recrivenvi.mobends.core.configuration.CoreClientConfig;
import io.github.recrivenvi.mobends.core.util.WildcardPattern;
import io.github.recrivenvi.mobends.standard.AttackActionType;
import io.github.recrivenvi.mobends.standard.UseActionType;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//
// Mod configuration abstraction. Platform-specific implementations provide the actual config
// values.
//
public class ModConfig {
    private static ModConfig instance;

    // Whether to show sword trails (static field for compatibility).
    public static boolean showSwordTrail = true;

    // Whether to show arrow trails (static field for compatibility).
    public static boolean showArrowTrails = true;

    // Whether to perform spin attack animation.
    public static boolean performSpinAttack = true;

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }

    public static void syncFromConfiguration() {
        CoreClientConfig config = CoreClientConfig.getInstance();
        showSwordTrail = config.isShowSwordTrail();
        showArrowTrails = config.isShowArrowTrails();
        performSpinAttack = config.isPerformSpinAttack();
    }

    //
    // @param entityKey The entity key (e.g., "player", "zombie")
    // @return Whether the entity animation is enabled
    //
    public boolean isEntityEnabled(String entityKey) {
        return CoreClientConfig.getInstance().isEntityEnabled(entityKey);
    }

    //
    // Checks if the armor should render with vanilla rendering instead of Mo' Bends.
    //
    // @param armorStack The armor stack to check
    // @return true if the armor should use vanilla rendering
    //
    public static boolean shouldKeepArmorAsVanilla(ItemStack armorStack) {
        if (armorStack == null || armorStack.isEmpty()) {
            return false;
        }
        return matchesAny(
                BuiltInRegistries.ITEM.getKey(armorStack.getItem()),
                CoreClientConfig.getInstance().getKeepArmorAsVanilla());
    }

    //
    // Checks if an entity should use vanilla rendering instead of Mo' Bends.
    //
    // @param entity The entity to check
    // @return true if the entity should use vanilla rendering
    //
    public static boolean shouldKeepEntityAsVanilla(LivingEntity entity) {
        return entity != null
                && !(entity instanceof Player)
                && matchesAny(
                        BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()),
                        CoreClientConfig.getInstance().getKeepEntityAsVanilla());
    }

    //
    // Gets the use action type for an item.
    //
    // @param item The item
    // @return The use action type, or null to use built-in logic
    //
    public static UseActionType getItemUseAction(Item item) {
        return classify(
                item,
                CoreClientConfig.getInstance().getItemUseClassifications(),
                UseActionType::valueOf);
    }

    //
    // Gets the attack action type for an item.
    //
    // @param item The item
    // @return The attack action type, or null to use built-in logic
    //
    public static AttackActionType getItemAttackAction(Item item) {
        return classify(
                item,
                CoreClientConfig.getInstance().getItemAttackClassifications(),
                AttackActionType::valueOf);
    }

    private static <T> T classify(Item item, List<String> entries, Function<String, T> parser) {
        if (item == null) {
            return null;
        }

        Identifier key = BuiltInRegistries.ITEM.getKey(item);
        for (String encoded : entries) {
            int equals = encoded.indexOf('=');
            if (equals <= 0 || equals == encoded.length() - 1) {
                continue;
            }

            if (matches(key, encoded.substring(0, equals).trim())) {
                try {
                    return parser.apply(
                            encoded.substring(equals + 1).trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {
                    // Invalid manually-edited entries are ignored so the built-in classifier can
                    // take over.
                }
            }
        }
        return null;
    }

    private static boolean matchesAny(Identifier key, List<String> patterns) {
        for (String pattern : patterns) {
            if (matches(key, pattern)) {
                return true;
            }
        }
        return false;
    }

    static boolean matches(Identifier key, String encodedPattern) {
        if (key == null || encodedPattern == null) {
            return false;
        }

        String pattern = encodedPattern.trim();
        String namespacePattern = "minecraft";
        String pathPattern = pattern;
        int colon = pattern.indexOf(':');
        if (colon >= 0) {
            if (colon == 0 || colon == pattern.length() - 1) {
                return false;
            }
            namespacePattern = pattern.substring(0, colon);
            pathPattern = pattern.substring(colon + 1);
        }

        WildcardPattern namespace = new WildcardPattern(namespacePattern);
        WildcardPattern path = new WildcardPattern(pathPattern);
        return namespace.matches(key.getNamespace()) && path.matches(key.getPath());
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard.main;

import io.github.recrivenvi.mobends.core.configuration.CoreClientConfig;
import io.github.recrivenvi.mobends.core.util.WildcardPattern;
import io.github.recrivenvi.mobends.standard.AttackActionType;
import io.github.recrivenvi.mobends.standard.UseActionType;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//
// Mod configuration abstraction. Platform-specific implementations provide the actual config
// values.
//
public class ModConfig {
    private static ModConfig instance;

    // Whether to show sword trails (static field for compatibility).
    public static boolean showSwordTrail = true;

    // Whether to show arrow trails (static field for compatibility).
    public static boolean showArrowTrails = true;

    // Whether to perform spin attack animation.
    public static boolean performSpinAttack = true;

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }

    public static void syncFromConfiguration() {
        CoreClientConfig config = CoreClientConfig.getInstance();
        showSwordTrail = config.isShowSwordTrail();
        showArrowTrails = config.isShowArrowTrails();
        performSpinAttack = config.isPerformSpinAttack();
    }

    //
    // @param entityKey The entity key (e.g., "player", "zombie")
    // @return Whether the entity animation is enabled
    //
    public boolean isEntityEnabled(String entityKey) {
        return CoreClientConfig.getInstance().isEntityEnabled(entityKey);
    }

    //
    // Checks if the armor should render with vanilla rendering instead of Mo' Bends.
    //
    // @param armorStack The armor stack to check
    // @return true if the armor should use vanilla rendering
    //
    public static boolean shouldKeepArmorAsVanilla(ItemStack armorStack) {
        if (armorStack == null || armorStack.isEmpty()) {
            return false;
        }
        return matchesAny(
                BuiltInRegistries.ITEM.getKey(armorStack.getItem()),
                CoreClientConfig.getInstance().getKeepArmorAsVanilla());
    }

    //
    // Checks if an entity should use vanilla rendering instead of Mo' Bends.
    //
    // @param entity The entity to check
    // @return true if the entity should use vanilla rendering
    //
    public static boolean shouldKeepEntityAsVanilla(LivingEntity entity) {
        return entity != null
                && !(entity instanceof Player)
                && matchesAny(
                        BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()),
                        CoreClientConfig.getInstance().getKeepEntityAsVanilla());
    }

    //
    // Gets the use action type for an item.
    //
    // @param item The item
    // @return The use action type, or null to use built-in logic
    //
    public static UseActionType getItemUseAction(Item item) {
        return classify(
                item,
                CoreClientConfig.getInstance().getItemUseClassifications(),
                UseActionType::valueOf);
    }

    //
    // Gets the attack action type for an item.
    //
    // @param item The item
    // @return The attack action type, or null to use built-in logic
    //
    public static AttackActionType getItemAttackAction(Item item) {
        return classify(
                item,
                CoreClientConfig.getInstance().getItemAttackClassifications(),
                AttackActionType::valueOf);
    }

    private static <T> T classify(Item item, List<String> entries, Function<String, T> parser) {
        if (item == null) {
            return null;
        }

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        for (String encoded : entries) {
            int equals = encoded.indexOf('=');
            if (equals <= 0 || equals == encoded.length() - 1) {
                continue;
            }

            if (matches(key, encoded.substring(0, equals).trim())) {
                try {
                    return parser.apply(
                            encoded.substring(equals + 1).trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {
                    // Invalid manually-edited entries are ignored so the built-in classifier can
                    // take over.
                }
            }
        }
        return null;
    }

    private static boolean matchesAny(ResourceLocation key, List<String> patterns) {
        for (String pattern : patterns) {
            if (matches(key, pattern)) {
                return true;
            }
        }
        return false;
    }

    static boolean matches(ResourceLocation key, String encodedPattern) {
        if (key == null || encodedPattern == null) {
            return false;
        }

        String pattern = encodedPattern.trim();
        String namespacePattern = "minecraft";
        String pathPattern = pattern;
        int colon = pattern.indexOf(':');
        if (colon >= 0) {
            if (colon == 0 || colon == pattern.length() - 1) {
                return false;
            }
            namespacePattern = pattern.substring(0, colon);
            pathPattern = pattern.substring(colon + 1);
        }

        WildcardPattern namespace = new WildcardPattern(namespacePattern);
        WildcardPattern path = new WildcardPattern(pathPattern);
        return namespace.matches(key.getNamespace()) && path.matches(key.getPath());
    }
}
// ?}
