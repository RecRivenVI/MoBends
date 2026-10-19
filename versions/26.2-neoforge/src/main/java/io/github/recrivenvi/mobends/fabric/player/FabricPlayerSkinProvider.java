// ? if target_26_2_fabric {
/*package io.github.recrivenvi.mobends.fabric.player;

import io.github.recrivenvi.mobends.api.player.IPlayerSkinProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerModelType;

//
// Fabric 1.21.1 implementation of IPlayerSkinProvider. Uses MC 1.21.1 API: getSkin().model(),
// getSkin().capeTexture(), getSkin().elytraTexture().
//
public class FabricPlayerSkinProvider implements IPlayerSkinProvider {
    @Override
    public boolean isSlimModel(Object player) {
        if (player instanceof AbstractClientPlayer acp) {
            return acp.getSkin().model() == PlayerModelType.SLIM;
        }
        return false;
    }

    @Override
    public Object getCapeTexture(Object player) {
        if (player instanceof AbstractClientPlayer acp) {
            return acp.getSkin().cape() == null ? null : acp.getSkin().cape().texturePath();
        }
        return null;
    }

    @Override
    public Object getElytraTexture(Object player) {
        if (player instanceof AbstractClientPlayer acp) {
            return acp.getSkin().elytra() == null ? null : acp.getSkin().elytra().texturePath();
        }
        return null;
    }
}
*/// ?}
