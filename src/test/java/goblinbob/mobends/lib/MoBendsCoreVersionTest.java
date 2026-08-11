package goblinbob.mobends.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MoBendsCoreVersionTest {
    @Test
    void exposesTheExpandedGradleVersion() {
        assertEquals(System.getProperty("mobends.expectedVersion"), MoBendsCore.VERSION);
    }
}
