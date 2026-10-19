package io.github.recrivenvi.mobends.standard.animation.bit.player;

import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.standard.data.PlayerData;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public class CapeAnimationBit extends AnimationBit<PlayerData> {

    @Override
    public String[] getActions(PlayerData entityData) {
        return null;
    }

    @Override
    public void perform(PlayerData data) {
        final AbstractClientPlayer player = data.getEntity();

        data.cape.rotation.orientX(0.0F);

        final double partialTicks = DataUpdateHandler.partialTicks;
        ClientAvatarState clientState = player.avatarState();
        double d0 =
                clientState.getInterpolatedCloakX((float) partialTicks)
                        - Mth.lerp(partialTicks, player.xo, player.getX());
        double d1 =
                clientState.getInterpolatedCloakY((float) partialTicks)
                        - Mth.lerp(partialTicks, player.yo, player.getY());
        double d2 =
                clientState.getInterpolatedCloakZ((float) partialTicks)
                        - Mth.lerp(partialTicks, player.zo, player.getZ());
        double f = player.yBodyRotO + (player.yBodyRot - player.yBodyRotO) * partialTicks;
        double d3 = Math.sin(f * 0.017453292);
        double d4 = -Math.cos(f * 0.017453292);
        double f1 = d1 * 10.0;
        f1 = Mth.clamp(f1, -6.0F, 32.0F);
        float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
        float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        double f4 = clientState.getInterpolatedBob((float) partialTicks);
        f1 =
                f1
                        + Math.sin(
                                        clientState.getInterpolatedWalkDistance(
                                                        (float) partialTicks)
                                                * 6.0F)
                                * 32.0F
                                * f4;

        if (player.isCrouching()) {
            f1 += data.getCapeCrouchingPitchOffset();
        }

        if (data.isFlying() && player.isSprinting()) {
            data.cape.rotation.setSmoothness(0.5F).orientX(0.0F);

            data.setCapeWaveSpeed(4.0F);
        } else {
            data.cape.rotation.setSmoothness(0.5F).orientX((float) (6.0F + f2 / 2.0F + f1));
            data.cape.rotation.rotateZ(f3 / 2.0F);
            data.cape.rotation.rotateY(-f3 / 2.0F);

            data.setCapeWaveSpeed(1.0F);
        }
    }
}
