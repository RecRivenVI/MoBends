//? if target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.rendering.IArmorLayerProvider;
import java.util.function.Consumer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

//
// NeoForge 1.21.1 implementation of IArmorLayerProvider.
//
// <p>MC 1.21.1 armor materials have multiple layers (for trims, dyes, etc). This provider iterates
// through ArmorMaterial.layers().
//
public class NeoForgeArmorLayerProvider implements IArmorLayerProvider {
    @Override
    public void forEachLayer(Object armorItem, Consumer<Object> layerConsumer) {
        if (armorItem instanceof ArmorItem item) {
            ArmorMaterial material = item.getMaterial().value();
            for (ArmorMaterial.Layer layer : material.layers()) {
                layerConsumer.accept(layer);
            }
        }
    }

    @Override
    public String getLayerTexture(Object armorMaterial, Object layer, boolean isInnerModel) {
        if (layer instanceof ArmorMaterial.Layer materialLayer) {
            return materialLayer.texture(isInnerModel).toString();
        }
        return "";
    }

    @Override
    public boolean layerHasDyeColor(Object layer) {
        if (layer instanceof ArmorMaterial.Layer materialLayer) {
            return materialLayer.dyeable();
        }
        return false;
    }
}*/
//?} else if target_26_1_2_neoforge || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import io.github.recrivenvi.mobends.api.rendering.IArmorLayerProvider;
import java.util.function.Consumer;
import net.minecraft.client.resources.model.EquipmentClientInfo;

//
// NeoForge 1.21.1 implementation of IArmorLayerProvider.
//
// <p>MC 1.21.1 armor materials have multiple layers (for trims, dyes, etc). This provider iterates
// through ArmorMaterial.layers().
//
public class NeoForgeArmorLayerProvider implements IArmorLayerProvider {
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
}*/
//?}
