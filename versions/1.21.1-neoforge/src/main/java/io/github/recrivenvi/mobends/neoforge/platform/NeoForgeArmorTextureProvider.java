// ? if target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.armor.IArmorTextureProvider;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

//
// NeoForge 1.21.1 implementation of IArmorTextureProvider.
//
// <p>Uses NeoForge's IItemExtension.getArmorTexture() hook to allow mods to override the armor
// texture location.
//
public class NeoForgeArmorTextureProvider implements IArmorTextureProvider {
    @Override
    @Nullable public <E extends LivingEntity> ResourceLocation getArmorTexture(
            ArmorItem armorItem,
            ItemStack itemStack,
            E entity,
            EquipmentSlot slot,
            @Nullable Object layer,
            boolean isInnerModel) {
        // NeoForge 1.21.1: ArmorItem.getArmorTexture(ItemStack, Entity, EquipmentSlot,
        // ArmorMaterial.Layer, boolean)
        ArmorMaterial.Layer materialLayer =
                (layer instanceof ArmorMaterial.Layer) ? (ArmorMaterial.Layer) layer : null;
        return armorItem.getArmorTexture(itemStack, entity, slot, materialLayer, isInnerModel);
    }
}
// ?} else if target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.armor.IArmorTextureProvider;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;

//
// NeoForge 1.21.1 implementation of IArmorTextureProvider.
//
// <p>Uses NeoForge's IItemExtension.getArmorTexture() hook to allow mods to override the armor
// texture location.
//
public class NeoForgeArmorTextureProvider implements IArmorTextureProvider {
    @Override
    @Nullable public <E extends LivingEntity> Identifier getArmorTexture(
            ItemStack itemStack,
            E entity,
            EquipmentSlot slot,
            @Nullable Object layer,
            boolean isInnerModel) {
        if (!(layer instanceof EquipmentClientInfo.Layer equipmentLayer)) {
            return null;
        }
        EquipmentClientInfo.LayerType layerType =
                isInnerModel
                        ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
                        : EquipmentClientInfo.LayerType.HUMANOID;
        Identifier defaultTexture = equipmentLayer.getTextureLocation(layerType);
        return ClientHooks.getArmorTexture(itemStack, layerType, equipmentLayer, defaultTexture);
    }
}*/
// ?}
