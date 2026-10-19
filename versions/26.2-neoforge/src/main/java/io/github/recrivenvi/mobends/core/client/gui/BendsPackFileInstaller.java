package io.github.recrivenvi.mobends.core.client.gui;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;

/** Installs local BendsPack files, including the ZIP container used by newer packs. */
final class BendsPackFileInstaller {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_ZIP_ENTRIES = 32;
    private static final long MAX_UNCOMPRESSED_BYTES = 16L * 1024L * 1024L;

    private BendsPackFileInstaller() {}

    static List<Path> install(List<Path> sources, Path packDirectory) {
        List<Path> rejected = new ArrayList<>();
        Path normalizedDirectory = packDirectory.toAbsolutePath().normalize();
        for (Path source : sources) {
            if (!Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)) {
                rejected.add(source);
                continue;
            }

            String filename = source.getFileName().toString();
            String lowerFilename = filename.toLowerCase(Locale.ROOT);
            try {
                if (isPackFile(lowerFilename)) {
                    copyPackFile(source, normalizedDirectory, filename);
                } else if (lowerFilename.endsWith(".zip")) {
                    installZip(source, normalizedDirectory);
                } else {
                    rejected.add(source);
                }
            } catch (IOException exception) {
                LOGGER.warn("Failed to install local Mo' Bends pack from {}", source, exception);
                rejected.add(source);
            }
        }
        return rejected;
    }

    private static void installZip(Path source, Path packDirectory) throws IOException {
        Path temporaryDirectory = Files.createTempDirectory("mobends-pack-import-");
        List<Path> extracted = new ArrayList<>();
        Set<String> filenames = new HashSet<>();
        long totalBytes = 0L;
        int entryCount = 0;
        try {
            try (InputStream input = Files.newInputStream(source);
                    ZipInputStream zip = new ZipInputStream(input)) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    if (++entryCount > MAX_ZIP_ENTRIES) {
                        throw new IOException("BendsPack ZIP contains too many entries");
                    }

                    Path entryPath = Path.of(entry.getName()).normalize();
                    if (entryPath.isAbsolute()
                            || entryPath.startsWith("..")
                            || entryPath.getFileName() == null) {
                        throw new IOException("BendsPack ZIP contains an unsafe path");
                    }

                    String filename = entryPath.getFileName().toString();
                    if (!isPackFile(filename.toLowerCase(Locale.ROOT))) {
                        continue;
                    }
                    if (!filenames.add(filename.toLowerCase(Locale.ROOT))) {
                        throw new IOException("BendsPack ZIP contains duplicate filenames");
                    }

                    Path target = temporaryDirectory.resolve(filename);
                    extracted.add(target);
                    try (OutputStream output = Files.newOutputStream(target)) {
                        byte[] buffer = new byte[8192];
                        int read;
                        while ((read = zip.read(buffer)) >= 0) {
                            totalBytes += read;
                            if (totalBytes > MAX_UNCOMPRESSED_BYTES) {
                                throw new IOException(
                                        "BendsPack ZIP is too large after extraction");
                            }
                            output.write(buffer, 0, read);
                        }
                    }
                }
            }

            if (extracted.isEmpty()) {
                throw new IOException("BendsPack ZIP contains no supported pack files");
            }
            for (Path file : extracted) {
                copyPackFile(file, packDirectory, file.getFileName().toString());
            }
        } finally {
            for (Path file : extracted) {
                Files.deleteIfExists(file);
            }
            Files.deleteIfExists(temporaryDirectory);
        }
    }

    private static void copyPackFile(Path source, Path packDirectory, String filename)
            throws IOException {
        Path target = packDirectory.resolve(filename).normalize();
        if (!packDirectory.equals(target.getParent())) {
            throw new IOException("BendsPack filename escapes its target directory");
        }
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private static boolean isPackFile(String lowerFilename) {
        return lowerFilename.endsWith(".bends") || lowerFilename.endsWith(".bendsmeta");
    }
}
