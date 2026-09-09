package io.github.recrivenvi.mobends.core.addon;

/** This class can be freely used by Addon developers. */
public class AddonHelper {

    public static void registerAddon(String modId, IAddon addon) {
        Addons.registerAddon(modId, addon);
    }
}
