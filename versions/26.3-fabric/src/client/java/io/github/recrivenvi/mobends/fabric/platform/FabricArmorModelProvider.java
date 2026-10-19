package io.github.recrivenvi.mobends.fabric.platform;

import io.github.recrivenvi.mobends.api.armor.IArmorModelProvider;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

//
// Fabric 1.21.1 implementation of IArmorModelProvider.
//
// <p>Returns the default model. Custom model handling in Fabric 1.21.1 is done through the
// data-driven armor system.
//
public class FabricArmorModelProvider implements IArmorModelProvider {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Model<?> getCustomArmorModel(
            LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, Model<?> defaultModel) {
        // Fabric 的盔甲模型由数据驱动装备渲染器选择；传入模型已经是最终模型。
        return defaultModel;
    }
}
