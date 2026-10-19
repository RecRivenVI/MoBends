package io.github.recrivenvi.mobends.validation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

/** Checks immutable migration inputs without opening or parsing user worlds. */
public final class PreservationCheck {
    private PreservationCheck() {}

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments[0]).toAbsolutePath().normalize();
        JsonArray entries;
        try (var reader = Files.newBufferedReader(root.resolve(arguments[1]))) {
            entries = JsonParser.parseReader(reader).getAsJsonArray();
        }
        long bytes = 0;
        boolean structureOnly = arguments.length > 3 && arguments[3].equals("structure");
        for (var element : entries) {
            var entry = element.getAsJsonObject();
            Path path = root.resolve(entry.get("destination").getAsString()).normalize();
            if (!path.startsWith(root))
                throw new IOException("Preservation path escaped repository");
            if (!entry.get("sha256").getAsString().matches("[0-9a-fA-F]{64}")) {
                throw new IOException("Invalid preservation hash");
            }
            if (structureOnly) continue;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (var stream = new DigestInputStream(Files.newInputStream(path), digest)) {
                stream.transferTo(java.io.OutputStream.nullOutputStream());
            }
            if (Files.size(path) != entry.get("bytes").getAsLong()
                    || !HexFormat.of()
                            .formatHex(digest.digest())
                            .equalsIgnoreCase(entry.get("sha256").getAsString())) {
                throw new IOException("Preservation mismatch: " + entry.get("destination"));
            }
            bytes += Files.size(path);
        }
        JsonObject result = new JsonObject();
        result.addProperty("createdAt", Instant.now().toString());
        result.addProperty("files", entries.size());
        result.addProperty("bytes", bytes);
        result.addProperty("hashMismatches", 0);
        result.addProperty("status", "PASS");
        if (!structureOnly) {
            Path destination = root.resolve(arguments[2]);
            Files.createDirectories(destination.getParent());
            Files.writeString(destination, new Gson().toJson(result) + "\n");
        }
        if (structureOnly) {
            System.out.println(
                    "Preservation manifest structure: " + entries.size() + " entries: PASS");
        } else {
            System.out.println("Preserved " + entries.size() + " files, " + bytes + " bytes: PASS");
        }
    }
}
