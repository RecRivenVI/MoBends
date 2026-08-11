package goblinbob.mobends.core.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BendsPackFileInstallerTest {
    @TempDir Path temporaryDirectory;

    @Test
    void extractsPairedJsonPackFromZip() throws Exception {
        Path source = temporaryDirectory.resolve("pack.zip");
        writeZip(
                source,
                new String[][] {
                    {"folder/example.bends", "{}"},
                    {"folder/example.bendsmeta", "{\"key\":\"example\"}"},
                    {"folder/readme.txt", "ignored"}
                });
        Path destination = Files.createDirectory(temporaryDirectory.resolve("packs"));

        assertTrue(BendsPackFileInstaller.install(List.of(source), destination).isEmpty());
        assertEquals("{}", Files.readString(destination.resolve("example.bends")));
        assertTrue(Files.isRegularFile(destination.resolve("example.bendsmeta")));
        assertFalse(Files.exists(destination.resolve("readme.txt")));
    }

    @Test
    void rejectsZipTraversalWithoutWritingPackFiles() throws Exception {
        Path source = temporaryDirectory.resolve("unsafe.zip");
        writeZip(source, new String[][] {{"../escape.bends", "{}"}});
        Path destination = Files.createDirectory(temporaryDirectory.resolve("safe-packs"));

        assertEquals(List.of(source), BendsPackFileInstaller.install(List.of(source), destination));
        assertFalse(Files.exists(temporaryDirectory.resolve("escape.bends")));
        try (var files = Files.list(destination)) {
            assertEquals(0L, files.count());
        }
    }

    private static void writeZip(Path path, String[][] entries) throws Exception {
        try (OutputStream output = Files.newOutputStream(path);
                ZipOutputStream zip = new ZipOutputStream(output)) {
            for (String[] entry : entries) {
                zip.putNextEntry(new ZipEntry(entry[0]));
                zip.write(entry[1].getBytes(java.nio.charset.StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        }
    }
}
