package io.github.recrivenvi.mobends.core.client;

import io.github.recrivenvi.mobends.core.Core;
import io.github.recrivenvi.mobends.core.addon.Addons;
import io.github.recrivenvi.mobends.core.animation.keyframe.AnimationLoader;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.configuration.CoreClientConfig;
import io.github.recrivenvi.mobends.core.data.EntityDatabase;
import io.github.recrivenvi.mobends.core.pack.PackDataProvider;
import io.github.recrivenvi.mobends.core.pack.PackManager;
import io.github.recrivenvi.mobends.core.util.GsonResources;
import io.github.recrivenvi.mobends.standard.main.ModConfig;

/** Loader-neutral client refresh operations used by resource reloads and screens. */
public final class ClientRuntime {
    private ClientRuntime() {}

    public static void refreshSystems() {
        CoreClientConfig.getInstance().reload();
        ModConfig.syncFromConfiguration();
        AnimationLoader.clearCache();
        GsonResources.clearCache();
        PackDataProvider.INSTANCE.clearCache();
        PackManager.INSTANCE.reload();
        EntityDatabase.instance.refresh();
        EntityBenderRegistry.instance.refreshMutators();
        if (Core.getInstance() != null) {
            Core.getInstance().applyConfigurationToEntityBenders();
        }
        Addons.onRefresh();
    }

    public static void applyClientConfiguration() {
        ModConfig.syncFromConfiguration();
        if (Core.getInstance() != null) {
            Core.getInstance().applyConfigurationToEntityBenders();
        }
        EntityBenderRegistry.instance.clearCache();
    }
}
