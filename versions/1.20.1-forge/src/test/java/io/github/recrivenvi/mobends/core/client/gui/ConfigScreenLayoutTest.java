package io.github.recrivenvi.mobends.core.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConfigScreenLayoutTest {
    @Test
    void keepsSubpageRowsOnTheVanillaSmallOptionsGrid() {
        assertEquals(150, ConfigScreenLayout.SUBPAGE_OPTION_WIDTH);
        assertEquals(310, ConfigScreenLayout.SUBPAGE_GRID_WIDTH);
        assertEquals(345, ConfigScreenLayout.subpageOptionX(1000, 0));
        assertEquals(505, ConfigScreenLayout.subpageOptionX(1000, 1));
        assertEquals(693, ConfigScreenLayout.subpageFooterY(720));
        assertEquals(688, ConfigScreenLayout.subpageListBottom(720));
    }
}
