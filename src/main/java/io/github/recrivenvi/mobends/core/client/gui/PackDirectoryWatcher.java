package io.github.recrivenvi.mobends.core.client.gui;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/** 与原版资源包选择器一致的包目录监听器。 */
final class PackDirectoryWatcher implements AutoCloseable {
    private final WatchService watcher;
    private final Path packDirectory;

    PackDirectoryWatcher(Path packDirectory) throws IOException {
        this.packDirectory = packDirectory;
        this.watcher = packDirectory.getFileSystem().newWatchService();

        try {
            watchDirectory(packDirectory);
            try (DirectoryStream<Path> paths = Files.newDirectoryStream(packDirectory)) {
                for (Path path : paths) {
                    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                        watchDirectory(path);
                    }
                }
            }
        } catch (Exception exception) {
            watcher.close();
            throw exception;
        }
    }

    private void watchDirectory(Path directory) throws IOException {
        directory.register(
                watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    boolean pollForChanges() throws IOException {
        boolean changed = false;
        WatchKey key;
        while ((key = watcher.poll()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                changed = true;
                if (packDirectory.equals(key.watchable())
                        && event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path newPath = packDirectory.resolve((Path) event.context());
                    if (Files.isDirectory(newPath, LinkOption.NOFOLLOW_LINKS)) {
                        watchDirectory(newPath);
                    }
                }
            }
            key.reset();
        }
        return changed;
    }

    @Override
    public void close() throws IOException {
        watcher.close();
    }
}
