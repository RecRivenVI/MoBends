package goblinbob.mobends.core.client;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.addon.Addons;
import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.configuration.CoreClientConfig;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.pack.PackDataProvider;
import goblinbob.mobends.core.pack.PackManager;
import goblinbob.mobends.core.util.GsonResources;
import goblinbob.mobends.standard.main.ModConfig;

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
