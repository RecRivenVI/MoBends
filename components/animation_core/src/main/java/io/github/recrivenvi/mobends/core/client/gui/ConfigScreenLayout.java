package io.github.recrivenvi.mobends.core.client.gui;

/** Minecraft 原版选项网格的尺寸契约。 */
final class ConfigScreenLayout {
    static final int SUBPAGE_OPTION_WIDTH = 150;
    static final int SUBPAGE_OPTION_GAP = 10;
    static final int SUBPAGE_GRID_WIDTH = SUBPAGE_OPTION_WIDTH * 2 + SUBPAGE_OPTION_GAP;
    static final int SUBPAGE_LIST_TOP = 32;
    static final int SUBPAGE_ROW_HEIGHT = 25;

    private ConfigScreenLayout() {}

    static int subpageOptionX(int screenWidth, int column) {
        return (screenWidth - SUBPAGE_GRID_WIDTH) / 2
                + column * (SUBPAGE_OPTION_WIDTH + SUBPAGE_OPTION_GAP);
    }

    static int subpageFooterY(int screenHeight) {
        return screenHeight - 27;
    }

    static int subpageListBottom(int screenHeight) {
        return screenHeight - 32;
    }
}
