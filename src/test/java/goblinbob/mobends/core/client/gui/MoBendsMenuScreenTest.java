package goblinbob.mobends.core.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MoBendsMenuScreenTest {
    @Test
    void exposesTheFlattenedConfigurationSectionsInDisplayOrder() {
        assertEquals(6, MoBendsMenuScreen.sectionTranslationKeys().size());
        assertEquals(
                "mobends.gui.section.player", MoBendsMenuScreen.sectionTranslationKeys().get(0));
        assertEquals(
                "mobends.gui.section.entities", MoBendsMenuScreen.sectionTranslationKeys().get(1));
        assertEquals(
                "mobends.gui.section.trails", MoBendsMenuScreen.sectionTranslationKeys().get(2));
        assertEquals(
                "mobends.gui.section.item_rules",
                MoBendsMenuScreen.sectionTranslationKeys().get(3));
        assertEquals(
                "mobends.gui.section.vanilla_rules",
                MoBendsMenuScreen.sectionTranslationKeys().get(4));
        assertEquals(
                "mobends.gui.section.tools", MoBendsMenuScreen.sectionTranslationKeys().get(5));
    }

    @Test
    void followsTheCreativeSpawnEggOrderForSupportedEntities() {
        assertEquals(
                java.util.List.of(
                        "minecraft:wolf",
                        "minecraft:squid",
                        "minecraft:skeleton",
                        "minecraft:zombie",
                        "minecraft:spider",
                        "minecraft:zombified_piglin"),
                MoBendsMenuScreen.entitySpawnEggOrder());
    }

    @Test
    void mapsRegisteredBenderKeysToVanillaEntityTranslations() {
        assertEquals(
                "entity.minecraft.skeleton",
                MoBendsMenuScreen.entityTranslationKey("mobends-minecraft:skeleton"));
        assertEquals(
                "entity.minecraft.player",
                MoBendsMenuScreen.entityTranslationKey("mobends-player"));
    }
}
