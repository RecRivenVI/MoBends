package goblinbob.mobends.neoforge.platform;

import goblinbob.mobends.api.armor.IArmorTextureProvider;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;

/**
 * NeoForge 1.21.1 implementation of IArmorTextureProvider.
 *
 * <p>Uses NeoForge's IItemExtension.getArmorTexture() hook to allow mods to override the armor
 * texture location.
 */
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
}
