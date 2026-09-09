//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.animation.controller;

import io.github.recrivenvi.mobends.core.animation.controller.IAnimationController;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.lib.util.GUtil;
import io.github.recrivenvi.mobends.standard.data.SquidData;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.squid.Squid;

//
// This is an animation controller for a squid instance. It's a part of the EntityData structure.
//
public class SquidController implements IAnimationController<SquidData> {

    @Override
    @Nullable public Collection<String> perform(SquidData data) {
        Squid squid = data.getEntity();

        // squidRotation/prevSquidRotation became tentacleMovement/oldTentacleMovement.
        // xBodyRot is the body's pitch in degrees and must not drive the tentacle cycle.
        float squidRotation =
                squid.oldTentacleMovement
                        + (squid.tentacleMovement - squid.oldTentacleMovement)
                                * DataUpdateHandler.partialTicks
                        + 1.1F;
        float f = squidRotation / GUtil.PI;
        f = Math.max(0.0F, f);
        float baseTentacleAngle = 0.0F;
        if (squid.oldTentacleMovement < GUtil.PI) {
            baseTentacleAngle = Mth.sin(f * f * (float) Math.PI) * 60.0f;
        }

        for (int i = 0; i < data.squidTentacles.length; ++i) {

            double d0 = (double) i * -360.0D / (double) data.squidTentacles.length + 90.0D;
            data.squidTentacles[i][0]
                    .rotation
                    .setSmoothness(0.1F)
                    .orientX(baseTentacleAngle)
                    .rotateY((float) d0);

            float f2 = squidRotation / (GUtil.PI * 2);
            f2 = Math.max(0.0F, f2);
            for (int j = 1; j < SquidData.TENTACLE_SECTIONS; ++j) {
                float tentacleAngle = 0;
                if (squid.tentacleMovement < GUtil.PI) {
                    tentacleAngle = Mth.sin(f2 * GUtil.PI * 2 + j * 0.1F) * 10.0F;
                }
                data.squidTentacles[i][j].rotation.setSmoothness(0.1F).orientX(-tentacleAngle);
            }
        }

        return null;
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.animation.controller;

import io.github.recrivenvi.mobends.core.animation.controller.IAnimationController;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.lib.util.GUtil;
import io.github.recrivenvi.mobends.standard.data.SquidData;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;

//
// This is an animation controller for a squid instance. It's a part of the EntityData structure.
//
public class SquidController implements IAnimationController<SquidData> {

    @Override
    @Nullable public Collection<String> perform(SquidData data) {
        Squid squid = data.getEntity();

        // In 1.20.1, squidRotation/prevSquidRotation were renamed to xBodyRot/xBodyRotO
        float squidRotation =
                squid.xBodyRotO
                        + (squid.xBodyRot - squid.xBodyRotO) * DataUpdateHandler.partialTicks
                        + 1.1F;
        float f = squidRotation / GUtil.PI;
        f = Math.max(0.0F, f);
        float baseTentacleAngle = 0.0F;
        if (squid.xBodyRotO < GUtil.PI) {
            baseTentacleAngle = Mth.sin(f * f * (float) Math.PI) * 60.0f;
        }

        for (int i = 0; i < data.squidTentacles.length; ++i) {

            double d0 = (double) i * -360.0D / (double) data.squidTentacles.length + 90.0D;
            data.squidTentacles[i][0]
                    .rotation
                    .setSmoothness(0.1F)
                    .orientX(baseTentacleAngle)
                    .rotateY((float) d0);

            float f2 = squidRotation / (GUtil.PI * 2);
            f2 = Math.max(0.0F, f2);
            for (int j = 1; j < SquidData.TENTACLE_SECTIONS; ++j) {
                float tentacleAngle = 0;
                if (squid.xBodyRot < GUtil.PI) {
                    tentacleAngle = Mth.sin(f2 * GUtil.PI * 2 + j * 0.1F) * 10.0F;
                }
                data.squidTentacles[i][j].rotation.setSmoothness(0.1F).orientX(-tentacleAngle);
            }
        }

        return null;
    }
}*/
//?}
