package goblinbob.mobends.fabric.platform;

import goblinbob.mobends.api.armor.IArmorTextureProvider;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric 1.21.1 implementation of IArmorTextureProvider.
 *
 * <p>Uses Fabric's IItemExtension.getArmorTexture() hook to allow mods to override the armor
 * texture location.
 */
public class FabricArmorTextureProvider implements IArmorTextureProvider {
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
        return equipmentLayer.getTextureLocation(layerType);
    }
}
