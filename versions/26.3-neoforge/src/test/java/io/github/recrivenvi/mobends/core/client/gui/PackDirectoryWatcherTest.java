package io.github.recrivenvi.mobends.core.client.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PackDirectoryWatcherTest {
    @TempDir Path packDirectory;

    @Test
    void detectsChangesInThePackDirectory() throws Exception {
        try (PackDirectoryWatcher watcher = new PackDirectoryWatcher(packDirectory)) {
            Files.writeString(packDirectory.resolve("example.bendsmeta"), "{}");
            assertTrue(awaitChange(watcher));
        }
    }

    @Test
    void detectsChangesInAnExistingDirectChildDirectory() throws Exception {
        Path childDirectory = Files.createDirectory(packDirectory.resolve("example"));
        try (PackDirectoryWatcher watcher = new PackDirectoryWatcher(packDirectory)) {
            Files.writeString(childDirectory.resolve("example.bends"), "{}");
            assertTrue(awaitChange(watcher));
        }
    }

    @Test
    void startsWatchingANewDirectChildDirectory() throws Exception {
        try (PackDirectoryWatcher watcher = new PackDirectoryWatcher(packDirectory)) {
            Path childDirectory = Files.createDirectory(packDirectory.resolve("example"));
            assertTrue(awaitChange(watcher));

            Files.writeString(childDirectory.resolve("example.bends"), "{}");
            assertTrue(awaitChange(watcher));
        }
    }

    private static boolean awaitChange(PackDirectoryWatcher watcher)
            throws IOException, InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(2));
        do {
            if (watcher.pollForChanges()) {
                return true;
            }
            Thread.sleep(10L);
        } while (Instant.now().isBefore(deadline));
        return false;
    }
}
