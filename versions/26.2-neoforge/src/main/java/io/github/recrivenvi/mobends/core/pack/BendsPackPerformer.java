package io.github.recrivenvi.mobends.core.pack;

import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

public class BendsPackPerformer {

    public static final BendsPackPerformer INSTANCE = new BendsPackPerformer();

    public void performCurrentPack(
            EntityData<?> entityData,
            String animatedEntityKey,
            @Nullable Collection<String> actions) {
        final BendsPackData packData = PackDataProvider.INSTANCE.getAppliedData();
        if (packData == null) {
            return;
        }

        try {
            entityData.packAnimationState.update(
                    entityData, packData, animatedEntityKey, DataUpdateHandler.ticksPerFrame);
            Map<String, java.util.List<LegacyBendsPackDefinition.Action>> legacyTarget =
                    packData.legacyTargets.get(
                            animatedEntityKey.toLowerCase(java.util.Locale.ROOT));
            if (legacyTarget != null) {
                LegacyBendsPackDefinition definition = new LegacyBendsPackDefinition();
                definition.targets.put(
                        animatedEntityKey.toLowerCase(java.util.Locale.ROOT), legacyTarget);
                LegacyBendsPackPerformer.perform(
                        entityData, definition, animatedEntityKey, actions);
            }
        } catch (MalformedKumoTemplateException e) {
            // Resetting the applied packs due to malformed templates.
            e.printStackTrace();
            PackManager.INSTANCE.resetAppliedPacks(true);
        }
    }
}
