// ? if target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.rendering.IArmorColorProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

//
// NeoForge 1.21.1 implementation of IArmorColorProvider.
//
// <p>In MC 1.21.1 armor dye colors are stored via the DataComponents.DYED_COLOR component, and
// dyeable items are identified by the {@code minecraft:dyeable} item tag.
//
public class NeoForgeArmorColorProvider implements IArmorColorProvider {
    @Override
    public int getDyedColor(Object itemStack) {
        if (!(itemStack instanceof ItemStack stack)) {
            return -1;
        }

        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        if (dyed == null) {
            return -1;
        }

        return dyed.rgb() & 0xFFFFFF;
    }

    @Override
    public boolean hasDyedColor(Object itemStack) {
        return itemStack instanceof ItemStack stack && stack.has(DataComponents.DYED_COLOR);
    }

    @Override
    public boolean isDyeable(Object itemStack) {
        return itemStack instanceof ItemStack stack && stack.is(ItemTags.DYEABLE);
    }
}*/
// ?} else if target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.rendering.IArmorColorProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

//
// NeoForge 1.21.1 implementation of IArmorColorProvider.
//
// <p>In MC 1.21.1 armor dye colors are stored via the DataComponents.DYED_COLOR component, and
// dyeable items are identified by the {@code minecraft:dyeable} item tag.
//
public class NeoForgeArmorColorProvider implements IArmorColorProvider {
    @Override
    public int getDyedColor(Object itemStack) {
        if (!(itemStack instanceof ItemStack stack)) {
            return -1;
        }

        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        if (dyed == null) {
            return -1;
        }

        return dyed.rgb() & 0xFFFFFF;
    }

    @Override
    public boolean hasDyedColor(Object itemStack) {
        return itemStack instanceof ItemStack stack && stack.has(DataComponents.DYED_COLOR);
    }

    @Override
    public boolean isDyeable(Object itemStack) {
        return itemStack instanceof ItemStack stack && stack.has(DataComponents.DYED_COLOR);
    }
}*/
// ?}
