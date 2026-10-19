package io.github.recrivenvi.mobends.fabric.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MoBendsModMenuTest {
    @Test
    void exposesAConfigScreenFactory() {
        assertNotNull(new MoBendsModMenu().getModConfigScreenFactory());
    }
}
