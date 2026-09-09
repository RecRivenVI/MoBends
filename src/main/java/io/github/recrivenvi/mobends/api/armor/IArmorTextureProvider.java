//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.api.armor;

import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

//
// Platform-specific provider for custom armor textures. NeoForge and Forge have different APIs for
// getting custom armor textures.
//
public interface IArmorTextureProvider {
    //
    // Gets the custom texture for the armor item, if any.
    //
    // @param itemStack The item stack
    // @param entity The entity wearing the armor
    // @param slot The equipment slot
    // @param layer The armor material layer (Object to support both MC versions -
    //     ArmorMaterial.Layer in 1.21.1, null in 1.20.1)
    // @param isInnerModel Whether this is for the inner model (leggings)
    // @return The custom texture location, or null to use default
    //
    @Nullable <E extends LivingEntity> Identifier getArmorTexture(
            ItemStack itemStack,
            E entity,
            EquipmentSlot slot,
            @Nullable Object layer,
            boolean isInnerModel);

    // Default implementation that returns null (use default texture).
    IArmorTextureProvider DEFAULT =
            new IArmorTextureProvider() {
                @Override
                public <E extends LivingEntity> Identifier getArmorTexture(
                        ItemStack itemStack,
                        E entity,
                        EquipmentSlot slot,
                        Object layer,
                        boolean isInnerModel) {
                    return null;
                }
            };

    // Holder for the platform-specific armor texture provider.
    class Holder {
        private static IArmorTextureProvider provider = DEFAULT;

        public static void setProvider(IArmorTextureProvider provider) {
            Holder.provider = provider;
        }

        public static IArmorTextureProvider getProvider() {
            return provider;
        }
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.api.armor;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

//
// Platform-specific provider for custom armor textures. NeoForge and Forge have different APIs for
// getting custom armor textures.
//
public interface IArmorTextureProvider {
    //
    // Gets the custom texture for the armor item, if any.
    //
    // @param armorItem The armor item
    // @param itemStack The item stack
    // @param entity The entity wearing the armor
    // @param slot The equipment slot
    // @param layer The armor material layer (Object to support both MC versions -
    //     ArmorMaterial.Layer in 1.21.1, null in 1.20.1)
    // @param isInnerModel Whether this is for the inner model (leggings)
    // @return The custom texture location, or null to use default
    //
    @Nullable <E extends LivingEntity> ResourceLocation getArmorTexture(
            ArmorItem armorItem,
            ItemStack itemStack,
            E entity,
            EquipmentSlot slot,
            @Nullable Object layer,
            boolean isInnerModel);

    // Default implementation that returns null (use default texture).
    IArmorTextureProvider DEFAULT =
            new IArmorTextureProvider() {
                @Override
                public <E extends LivingEntity> ResourceLocation getArmorTexture(
                        ArmorItem armorItem,
                        ItemStack itemStack,
                        E entity,
                        EquipmentSlot slot,
                        Object layer,
                        boolean isInnerModel) {
                    return null;
                }
            };

    // Holder for the platform-specific armor texture provider.
    class Holder {
        private static IArmorTextureProvider provider = DEFAULT;

        public static void setProvider(IArmorTextureProvider provider) {
            Holder.provider = provider;
        }

        public static IArmorTextureProvider getProvider() {
            return provider;
        }
    }
}*/
//?}
