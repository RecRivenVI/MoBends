// ? if target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.rendering.IArmorHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

//
// NeoForge 1.21.1 implementation of IArmorHelper.
//
// <p>MC 1.21.1: ArmorItem.getMaterial() returns Holder<ArmorMaterial> with unwrapKey()
//
public class NeoForgeArmorHelper implements IArmorHelper {
    @Override
    public String getArmorMaterialName(Object armorItem) {
        if (!(armorItem instanceof ArmorItem item)) {
            return "unknown";
        }

        net.minecraft.core.Holder<ArmorMaterial> materialHolder = item.getMaterial();
        return materialHolder
                .unwrapKey()
                .map(ResourceKey::location)
                .map(loc -> loc.getPath())
                .orElse("unknown");
    }
}*/
// ?} else if target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

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
}*/
// ?}
