package io.github.recrivenvi.mobends.core.client.gui;

import net.minecraft.client.gui.screens.Screen;

/** Selects the BendsPack screen implementation matching the active Minecraft GUI generation. */
final class LocalPacksScreens {
    private LocalPacksScreens() {}

    static Screen create(Screen parent) {

        return new LocalPacksScreen26(parent);
    }
}
