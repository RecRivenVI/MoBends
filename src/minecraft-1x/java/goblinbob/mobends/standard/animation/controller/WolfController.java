package goblinbob.mobends.standard.animation.controller;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.kumo.state.KumoAnimatorState;
import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.util.GsonResources;
import goblinbob.mobends.lib.util.GUtil;
import goblinbob.mobends.standard.data.WolfData;
import goblinbob.mobends.standard.main.ModStatics;
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
