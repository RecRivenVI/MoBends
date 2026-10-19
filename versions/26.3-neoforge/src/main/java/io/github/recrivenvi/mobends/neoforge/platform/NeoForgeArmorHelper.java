package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.rendering.IArmorHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

//
// NeoForge 1.21.1 implementation of IArmorHelper.
//
// <p>MC 1.21.1: ArmorItem.getMaterial() returns Holder<ArmorMaterial> with unwrapKey()
//
public class NeoForgeArmorHelper implements IArmorHelper {
    @Override
    public String getArmorMaterialName(Object armorItem) {
        if (!(armorItem instanceof ItemStack stack)) {
            return "unknown";
        }
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable == null
                ? "unknown"
                : equippable
                        .assetId()
                        .map(ResourceKey::identifier)
                        .map(loc -> loc.getPath())
                        .orElse("unknown");
    }
}
