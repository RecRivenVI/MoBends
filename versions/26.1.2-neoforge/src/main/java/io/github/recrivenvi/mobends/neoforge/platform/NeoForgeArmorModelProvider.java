package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.armor.IArmorModelProvider;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

//
// NeoForge 1.21.1 implementation of IArmorModelProvider.
//
// <p>Returns the default model. Custom model handling in NeoForge 1.21.1 is done through the
// data-driven armor system.
//
public class NeoForgeArmorModelProvider implements IArmorModelProvider {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Model<?> getCustomArmorModel(
            LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, Model<?> defaultModel) {
        EquipmentClientInfo.LayerType layerType =
                slot == EquipmentSlot.LEGS
                        ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
                        : EquipmentClientInfo.LayerType.HUMANOID;
        return IClientItemExtensions.of(itemStack)
                .getGenericArmorModel(itemStack, layerType, (Model) defaultModel);
    }
}
