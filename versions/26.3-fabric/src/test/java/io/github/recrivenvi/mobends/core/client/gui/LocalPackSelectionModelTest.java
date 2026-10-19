package io.github.recrivenvi.mobends.core.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LocalPackSelectionModelTest {
    @Test
    void stagesSelectionAndOrderingUntilTheScreenCommitsItsSnapshot() {
        LocalPackSelectionModel selection = new LocalPackSelectionModel(List.of("first"));
        List<String> committed = selection.selectedKeys();

        assertTrue(selection.select("second"));
        assertTrue(selection.move("second", -1));
        assertTrue(selection.unselect("first"));

        assertEquals(List.of("first"), committed);
        assertEquals(List.of("second"), selection.selectedKeys());
    }

    @Test
    void retainsPendingSelectionsThatRemainAvailableAfterAWatcherReload() {
        LocalPackSelectionModel selection =
                new LocalPackSelectionModel(List.of("removed", "still-present", "also-present"));

        selection.retainAvailable(List.of("still-present", "also-present", "new-pack"));

        assertEquals(List.of("still-present", "also-present"), selection.selectedKeys());
        assertFalse(selection.isSelected("removed"));
        assertTrue(selection.isSelected("still-present"));
    }
}
