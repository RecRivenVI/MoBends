// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.main;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

public class ModConfigTest {
    @Test
    public void pathOnlyPatternsKeepTheOriginalMinecraftNamespaceDefault() {
        Identifier sword = Identifier.parse("minecraft:diamond_sword");

        assertTrue(ModConfig.matches(sword, "diamond_sword"));
        assertTrue(ModConfig.matches(sword, "diamond_*"));
        assertTrue(ModConfig.matches(sword, "minecraft:*_sword"));
        assertFalse(ModConfig.matches(sword, "example:*"));
    }
}
*/// ?}
