// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.animation.controller;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.core.animation.controller.IAnimationController;
import io.github.recrivenvi.mobends.core.animation.keyframe.AnimationLoader;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.core.kumo.state.KumoAnimatorState;
import io.github.recrivenvi.mobends.core.kumo.state.template.AnimatorTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import io.github.recrivenvi.mobends.core.util.GsonResources;
import io.github.recrivenvi.mobends.lib.util.GUtil;
import io.github.recrivenvi.mobends.standard.data.WolfData;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.slf4j.Logger;

public class WolfController implements IAnimationController<WolfData> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier WOLF_ANIMATOR =
            ModStatics.getResource("bends/animators/wolf.json");

    private KumoAnimatorState<WolfData> kumoAnimatorState;

    public WolfController() {
        try {
            AnimatorTemplate animatorTemplate =
                    GsonResources.get(WOLF_ANIMATOR, AnimatorTemplate.class);
            kumoAnimatorState =
                    new KumoAnimatorState<>(
                            animatorTemplate,
                            key -> {
                                try {
                                    return AnimationLoader.loadFromPath(key);
                                } catch (IOException exception) {
                                    LOGGER.error(
                                            "Unable to load wolf animation {}", key, exception);
                                    return null;
                                }
                            });
        } catch (IOException | MalformedKumoTemplateException exception) {
            LOGGER.error("Unable to initialize wolf animator", exception);
        }
    }

    @Override
    public Collection<String> perform(WolfData data) {
        Wolf wolf = data.getEntity();
        float partialTick = DataUpdateHandler.partialTicks;
        float ticks = wolf.tickCount + partialTick;

        if (kumoAnimatorState != null) {
            try {
                kumoAnimatorState.update(data, DataUpdateHandler.ticksPerFrame);
            } catch (MalformedKumoTemplateException exception) {
                LOGGER.error("Unable to update wolf animator", exception);
            }
        }

        // 26.x has a dedicated baby mesh. The old per-head child scaling was
        // part of the legacy adult-model scaling path and would now offset the
        // baby head twice.
        data.head.offsetScale = 1.0F;
        data.head.globalOffset.set(0.0F, 0.0F, 0.0F);
        data.head.position.set(0.0F, -0.5F, -13.0F);

        data.head.rotation.localRotateY(data.headYaw.get()).finish();
        data.head.rotation.localRotateX(data.headPitch.get()).finish();

        float interested = wolf.getHeadRollAngle(partialTick);
        data.head
                .rotation
                .localRotateZ(
                        (interested + getBodyRollAngle(wolf, partialTick, 0.0F)) * GUtil.RAD_TO_DEG)
                .finish();
        data.mane
                .rotation
                .localRotateZ(getBodyRollAngle(wolf, partialTick, -0.08F) * GUtil.RAD_TO_DEG)
                .finish();
        data.tail
                .rotation
                .localRotateZ(getBodyRollAngle(wolf, partialTick, -0.2F) * GUtil.RAD_TO_DEG)
                .finish();
        // This interested-wag is an original Mo' Bends animation and still
        // uses the same tick clock. The 26.x baby tail keeps its native fixed
        // inversion on a geometry child, so the animated root must retain the
        // same -90 degree Mo' Bends pitch basis as the adult tail.
        data.tail.rotation.localRotateZ(interested * Mth.sin(ticks) * 20.0F).finish();
        data.tail.rotation.localRotateX(toMoBendsTailPitch(wolf.getTailAngle())).finish();
        data.head.offset.set(0.0F, 0.0F, 0.0F);
        return null;
    }

    static float toMoBendsTailPitch(float vanillaTailPitchRadians) {
        return vanillaTailPitchRadians * GUtil.RAD_TO_DEG - 90.0F;
    }

    private static float getBodyRollAngle(Wolf wolf, float partialTick, float offset) {
        float progress = Mth.clamp((wolf.getShakeAnim(partialTick) + offset) / 1.8F, 0.0F, 1.0F);
        return Mth.sin(progress * GUtil.PI)
                * Mth.sin(progress * GUtil.PI * 11.0F)
                * 0.15F
                * GUtil.PI;
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard.animation.controller;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.core.animation.controller.IAnimationController;
import io.github.recrivenvi.mobends.core.animation.keyframe.AnimationLoader;
import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.core.kumo.state.KumoAnimatorState;
import io.github.recrivenvi.mobends.core.kumo.state.template.AnimatorTemplate;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import io.github.recrivenvi.mobends.core.util.GsonResources;
import io.github.recrivenvi.mobends.lib.util.GUtil;
import io.github.recrivenvi.mobends.standard.data.WolfData;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Wolf;
import org.slf4j.Logger;

public class WolfController implements IAnimationController<WolfData> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation WOLF_ANIMATOR =
            ModStatics.getResource("bends/animators/wolf.json");

    private KumoAnimatorState<WolfData> kumoAnimatorState;

    public WolfController() {
        try {
            AnimatorTemplate animatorTemplate =
                    GsonResources.get(WOLF_ANIMATOR, AnimatorTemplate.class);
            kumoAnimatorState =
                    new KumoAnimatorState<>(
                            animatorTemplate,
                            key -> {
                                try {
                                    return AnimationLoader.loadFromPath(key);
                                } catch (IOException exception) {
                                    LOGGER.error(
                                            "Unable to load wolf animation {}", key, exception);
                                    return null;
                                }
                            });
        } catch (IOException | MalformedKumoTemplateException exception) {
            LOGGER.error("Unable to initialize wolf animator", exception);
        }
    }

    @Override
    public Collection<String> perform(WolfData data) {
        Wolf wolf = data.getEntity();
        float partialTick = DataUpdateHandler.partialTicks;
        float ticks = wolf.tickCount + partialTick;

        if (kumoAnimatorState != null) {
            try {
                kumoAnimatorState.update(data, DataUpdateHandler.ticksPerFrame);
            } catch (MalformedKumoTemplateException exception) {
                LOGGER.error("Unable to update wolf animator", exception);
            }
        }

        if (wolf.isBaby()) {
            data.head.offsetScale = 0.5F;
            data.head.globalOffset.set(0.0F, 5.0F, -2.0F);
        } else {
            data.head.offsetScale = 1.0F;
            data.head.globalOffset.set(0.0F, 0.0F, 0.0F);
        }
        data.head.position.set(0.0F, -0.5F, -13.0F);

        data.head.rotation.localRotateY(data.headYaw.get()).finish();
        data.head.rotation.localRotateX(data.headPitch.get()).finish();

        float interested = wolf.getHeadRollAngle(partialTick);
        data.head
                .rotation
                .localRotateZ(
                        (interested + getBodyRollAngle(wolf, partialTick, 0.0F)) * GUtil.RAD_TO_DEG)
                .finish();
        data.mane
                .rotation
                .localRotateZ(getBodyRollAngle(wolf, partialTick, -0.08F) * GUtil.RAD_TO_DEG)
                .finish();
        data.tail
                .rotation
                .localRotateZ(getBodyRollAngle(wolf, partialTick, -0.2F) * GUtil.RAD_TO_DEG)
                .finish();
        data.tail.rotation.localRotateZ(interested * Mth.sin(ticks) * 20.0F).finish();
        data.tail.rotation.localRotateX(wolf.getTailAngle() * GUtil.RAD_TO_DEG - 90.0F).finish();
        data.head.offset.set(0.0F, 0.0F, 0.0F);
        return null;
    }

    private static float getBodyRollAngle(Wolf wolf, float partialTick, float offset) {
        return wolf.getBodyRollAngle(partialTick, offset);
    }
}
// ?}
