package io.github.recrivenvi.mobends.validation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Writes only the explicitly selected owned instance's native client preferences. */
public final class PrepareClientOptions {
    private PrepareClientOptions() {}

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments[0]).toAbsolutePath().normalize();
        Path instance = root.resolve(arguments[1]).normalize();
        if (!instance.startsWith(root)
                || !(arguments[1].startsWith("validations/smoke-")
                                && arguments[1].contains("/instance/26.3-neoforge/client")
                        || arguments[1].equals("instances/26.3-neoforge/client"))) {
            throw new IllegalArgumentException("Not an owned reference client instance");
        }
        boolean handoff = arguments[2].equals("handoff");
        Files.createDirectories(instance);
        Path options = instance.resolve("options.txt");
        List<String> existing = Files.exists(options) ? Files.readAllLines(options) : List.of();
        Files.write(options, update(existing, handoff));
        System.out.println(
                "Prepared " + instance + ": " + (handoff ? "user acceptance" : "automated"));
    }

    static List<String> update(List<String> existing, boolean handoff) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("onboardAccessibility", "false");
        values.put("soundCategory_master", handoff ? "0.5" : "0.0");
        values.put("soundCategory_music", "0.0");
        values.put("overrideWidth", handoff ? "2560" : "1920");
        values.put("overrideHeight", handoff ? "1440" : "1080");
        values.put("fullscreen", "false");
        values.put("pauseOnLostFocus", "false");
        List<String> result = new ArrayList<>();
        for (String line : existing) {
            int colon = line.indexOf(':');
            String key = colon < 0 ? line : line.substring(0, colon);
            if (!values.containsKey(key)) result.add(line);
        }
        values.forEach((key, value) -> result.add(key + ":" + value));
        return result;
    }
}
