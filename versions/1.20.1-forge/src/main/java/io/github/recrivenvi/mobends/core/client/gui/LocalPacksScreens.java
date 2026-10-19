package io.github.recrivenvi.mobends.core.client.gui;

import net.minecraft.client.gui.screens.Screen;

/** Selects the BendsPack screen implementation matching the active Minecraft GUI generation. */
final class LocalPacksScreens {
    private LocalPacksScreens() {}

    static Screen create(Screen parent) {
        // ? if >=26.1.2
        // return new LocalPacksScreen26(parent);
        // ? if >1.20.1 && <26.1.2
        /*return new LocalPacksScreen121(parent);*/
        // ? if <=1.20.1
        return new LocalPacksScreen120(parent);
    }
}
