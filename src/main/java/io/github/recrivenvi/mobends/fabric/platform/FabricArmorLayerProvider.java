//? if target_26_2_fabric {
package io.github.recrivenvi.mobends.fabric.platform;

import io.github.recrivenvi.mobends.api.rendering.IArmorLayerProvider;
import java.util.function.Consumer;
import net.minecraft.client.resources.model.EquipmentClientInfo;

//
// Fabric 1.21.1 implementation of IArmorLayerProvider.
//
// <p>MC 1.21.1 armor materials have multiple layers (for trims, dyes, etc). This provider iterates
// through ArmorMaterial.layers().
//
public class FabricArmorLayerProvider implements IArmorLayerProvider {
    @Override
    public void forEachLayer(Object armorItem, Consumer<Object> layerConsumer) {
        if (armorItem instanceof EquipmentClientInfo info) {
            for (EquipmentClientInfo.LayerType type : EquipmentClientInfo.LayerType.values()) {
                info.getLayers(type).forEach(layerConsumer);
            }
        }
    }

    @Override
    public String getLayerTexture(Object armorMaterial, Object layer, boolean isInnerModel) {
        if (layer instanceof EquipmentClientInfo.Layer equipmentLayer) {
            EquipmentClientInfo.LayerType type =
                    isInnerModel
                            ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
                            : EquipmentClientInfo.LayerType.HUMANOID;
            return equipmentLayer.getTextureLocation(type).toString();
        }
        return "";
    }

    @Override
    public boolean layerHasDyeColor(Object layer) {
        if (layer instanceof EquipmentClientInfo.Layer equipmentLayer) {
            return equipmentLayer.dyeable().isPresent();
        }
        return false;
    }
}
//?}
