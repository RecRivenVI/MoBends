package io.github.recrivenvi.mobends.core.pack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LocalBendsPackTest {
    @Test
    void exposesMetadataUsedByTheVanillaStylePackList() {
        BendsPackMeta metadata = new BendsPackMeta();
        metadata.key = "example";
        metadata.displayName = "Example Pack";
        metadata.author = "Example Author";
        metadata.description = "Example description";

        LocalBendsPack pack = new LocalBendsPack(metadata);

        assertEquals("example", pack.getKey());
        assertEquals("Example Pack", pack.getDisplayName());
        assertEquals("Example Author", pack.getAuthor());
        assertEquals("Example description", pack.getDescription());
    }

    @Test
    void normalizesMissingOptionalMetadataForSearchAndRendering() {
        BendsPackMeta metadata = new BendsPackMeta();
        metadata.key = "minimal";
        metadata.displayName = "Minimal Pack";

        LocalBendsPack pack = new LocalBendsPack(metadata);

        assertEquals("", pack.getAuthor());
        assertEquals("", pack.getDescription());
    }
}
