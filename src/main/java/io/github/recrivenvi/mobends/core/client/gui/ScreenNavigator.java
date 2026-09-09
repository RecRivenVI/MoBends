package io.github.recrivenvi.mobends.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** 使用各版本普通页面切换入口，避免强制插入额外渲染帧。 */
public final class ScreenNavigator {
    private ScreenNavigator() {}

    public static void show(Screen screen) {
        //? if >=26.2
        Minecraft.getInstance().gui.setScreen(screen);
        //? if <26.2
        /*Minecraft.getInstance().setScreen(screen);*/
    }
}
