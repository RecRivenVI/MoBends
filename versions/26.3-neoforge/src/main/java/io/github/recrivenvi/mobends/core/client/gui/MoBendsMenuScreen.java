package io.github.recrivenvi.mobends.core.client.gui;

import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.client.ClientRuntime;
import io.github.recrivenvi.mobends.core.configuration.CoreClientConfig;
import io.github.recrivenvi.mobends.core.network.NetworkConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Mo' Bends 的统一原版选项式配置页。 */
public final class MoBendsMenuScreen extends MoBendsOptionsScreen {
    private static final List<String> SECTION_TRANSLATION_KEYS =
            List.of(
                    "mobends.gui.section.player",
                    "mobends.gui.section.entities",
                    "mobends.gui.section.trails",
                    "mobends.gui.section.item_rules",
                    "mobends.gui.section.vanilla_rules",
                    "mobends.gui.section.tools");
    private static final List<String> ENTITY_SPAWN_EGG_ORDER =
            List.of(
                    "minecraft:wolf",
                    "minecraft:squid",
                    "minecraft:skeleton",
                    "minecraft:zombie",
                    "minecraft:spider",
                    "minecraft:zombified_piglin");

    private EditBox useActions;
    private EditBox attackActions;
    private EditBox vanillaArmor;
    private EditBox vanillaEntities;

    public MoBendsMenuScreen(Screen parent) {
        super(parent, Component.translatable("mobends.gui.title"));
    }

    @Override
    protected void addMoBendsOptions() {
        CoreClientConfig config = CoreClientConfig.getInstance();

        EntityBender<?> playerBender = null;
        List<EntityBender<?>> entities = new ArrayList<>();
        for (EntityBender<?> bender : EntityBenderRegistry.instance.getRegistered()) {
            if (normalizedBenderKey(bender.getKey()).equals("player")) {
                playerBender = bender;
            } else {
                entities.add(bender);
            }
        }
        entities.sort(
                java.util.Comparator.comparingInt(
                        bender -> entitySpawnEggOrderIndex(bender.getKey())));

        addSection(sectionTitle(0));
        Button spinAttack =
                booleanButton(
                        config.isPerformSpinAttack(),
                        "mobends.config.spin_attack",
                        config::setPerformSpinAttack);
        addOptionRow(
                playerBender == null ? spinAttack : benderButton(playerBender),
                playerBender == null ? null : spinAttack);

        addSection(sectionTitle(1));
        addBenderRows(entities);

        addSection(sectionTitle(2));
        addOptionRow(
                booleanButton(
                        config.isShowSwordTrail(),
                        "mobends.config.sword_trails",
                        config::setShowSwordTrail),
                booleanButton(
                        config.isShowArrowTrails(),
                        "mobends.config.arrow_trails",
                        config::setShowArrowTrails));

        addSection(sectionTitle(3));
        useActions = addField("mobends.config.item_use", config.getItemUseClassifications());
        attackActions =
                addField("mobends.config.item_attack", config.getItemAttackClassifications());

        addSection(sectionTitle(4));
        vanillaArmor = addField("mobends.config.vanilla_armor", config.getKeepArmorAsVanilla());
        vanillaEntities =
                addField("mobends.config.vanilla_entities", config.getKeepEntityAsVanilla());

        addSection(sectionTitle(5));
        Button packs =
                optionButton(
                        Component.translatable("mobends.gui.open_local_packs"),
                        button -> {
                            saveAdvancedPatterns();
                            ScreenNavigator.show(LocalPacksScreens.create(this));
                        });
        packs.active = NetworkConfiguration.INSTANCE.areBendsPacksAllowed();
        addWideOption(packs);
    }

    private Button booleanButton(
            boolean initialValue,
            String translationKey,
            java.util.function.Consumer<Boolean> setter) {
        final boolean[] value = {initialValue};
        Component label = Component.translatable(translationKey);
        return optionButton(
                toggleLabel(label, initialValue),
                button -> {
                    value[0] = !value[0];
                    setter.accept(value[0]);
                    ClientRuntime.applyClientConfiguration();
                    button.setMessage(toggleLabel(label, value[0]));
                });
    }

    private void addBenderRows(List<EntityBender<?>> benders) {
        for (int index = 0; index < benders.size(); index += 2) {
            Button first = benderButton(benders.get(index));
            Button second =
                    index + 1 < benders.size() ? benderButton(benders.get(index + 1)) : null;
            addOptionRow(first, second);
        }
    }

    private Button benderButton(EntityBender<?> bender) {
        CoreClientConfig config = CoreClientConfig.getInstance();
        boolean enabled = config.isEntityEnabled(bender.getKey());
        Component entityName = entityName(bender.getKey());
        return optionButton(
                toggleLabel(entityName, enabled),
                button -> {
                    boolean newValue = !config.isEntityEnabled(bender.getKey());
                    config.setEntityEnabled(bender.getKey(), newValue);
                    bender.setAnimate(newValue);
                    EntityBenderRegistry.instance.clearCache();
                    button.setMessage(toggleLabel(entityName(bender.getKey()), newValue));
                });
    }

    private EditBox addField(String translationKey, List<String> values) {
        Component label = Component.translatable(translationKey);
        EditBox field = new EditBox(font, 0, 0, ConfigScreenLayout.SUBPAGE_OPTION_WIDTH, 20, label);
        field.setMaxLength(4096);
        field.setValue(String.join("; ", values));
        Component tooltip = Component.translatable(translationKey + ".tooltip");
        field.setTooltip(Tooltip.create(tooltip));
        addOptionRow(informationButton(label), field);
        return field;
    }

    @Override
    protected void beforeClose() {
        saveAdvancedPatterns();
    }

    private void saveAdvancedPatterns() {
        if (useActions == null
                || attackActions == null
                || vanillaArmor == null
                || vanillaEntities == null) {
            return;
        }

        CoreClientConfig.getInstance()
                .setAdvancedPatterns(
                        parse(useActions),
                        parse(attackActions),
                        parse(vanillaArmor),
                        parse(vanillaEntities));
        ClientRuntime.applyClientConfiguration();
    }

    private static List<String> parse(EditBox field) {
        return Arrays.stream(field.getValue().split("[;\\r\\n]+"))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    private static int entitySpawnEggOrderIndex(String key) {
        int index = ENTITY_SPAWN_EGG_ORDER.indexOf(normalizedBenderKey(key));
        return index < 0 ? Integer.MAX_VALUE : index;
    }

    private static Component toggleLabel(Component label, boolean enabled) {
        return Component.translatable(
                "mobends.gui.toggle",
                label,
                Component.translatable(enabled ? "options.on" : "options.off"));
    }

    private static Component entityName(String key) {
        String normalizedKey = normalizedBenderKey(key);
        String translationKey = entityTranslationKey(key);
        if (translationKey != null) {
            return Component.translatable(translationKey);
        }

        int separator = Math.max(normalizedKey.lastIndexOf(':'), normalizedKey.lastIndexOf('-'));
        String name = separator >= 0 ? normalizedKey.substring(separator + 1) : normalizedKey;
        String[] words = name.replace('_', ' ').split(" ");
        StringBuilder display = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (!display.isEmpty()) display.append(' ');
                display.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
                display.append(word.substring(1));
            }
        }
        return Component.literal(display.toString());
    }

    static String entityTranslationKey(String key) {
        String normalizedKey = normalizedBenderKey(key);
        int namespaceSeparator = normalizedKey.lastIndexOf(':');
        if (namespaceSeparator > 0 && namespaceSeparator + 1 < normalizedKey.length()) {
            return "entity."
                    + normalizedKey.substring(0, namespaceSeparator)
                    + "."
                    + normalizedKey.substring(namespaceSeparator + 1);
        }
        if (normalizedKey.equals("player")) {
            return "entity.minecraft.player";
        }
        return null;
    }

    private static String normalizedBenderKey(String key) {
        return key.startsWith("mobends-") ? key.substring("mobends-".length()) : key;
    }

    static List<String> sectionTranslationKeys() {
        return SECTION_TRANSLATION_KEYS;
    }

    static List<String> entitySpawnEggOrder() {
        return ENTITY_SPAWN_EGG_ORDER;
    }

    private static Component sectionTitle(int index) {
        return Component.translatable(SECTION_TRANSLATION_KEYS.get(index));
    }
}
