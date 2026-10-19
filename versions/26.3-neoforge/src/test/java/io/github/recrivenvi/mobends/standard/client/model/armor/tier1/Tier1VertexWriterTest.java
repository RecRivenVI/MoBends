package io.github.recrivenvi.mobends.standard.client.model.armor.tier1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.recrivenvi.mobends.standard.client.model.armor.CapturedVertex;
import org.junit.jupiter.api.Test;

class Tier1VertexWriterTest {
    @Test
    void tintColorMultipliesCapturedAndArmorChannels() {
        CapturedVertex vertex =
                new CapturedVertex(
                        0.0F, 0.0F, 0.0F, 0.5F, 0.25F, 1.0F, 0.5F, 0.0F, 0.0F, 0, 0, 0.0F, 1.0F,
                        0.0F);

        assertEquals(0x3F3F3FFF, Tier1VertexWriter.tintColor(vertex, 0x7F7FFFFF));
    }
}
