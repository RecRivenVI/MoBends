package goblinbob.mobends.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Mo' Bends Core - Pure Java library with no Minecraft dependencies. Contains: math utilities, flux
 * reactive system, animation core, kumo state machine.
 */
public final class MoBendsCore {
    public static final String VERSION = loadVersion();

    private MoBendsCore() {
        // Utility class
    }

    private static String loadVersion() {
        try (InputStream stream =
                MoBendsCore.class.getResourceAsStream("/mobends_version.properties")) {
            if (stream == null) {
                return "unknown";
            }

            Properties properties = new Properties();
            properties.load(stream);
            return properties.getProperty("version", "unknown");
        } catch (IOException exception) {
            return "unknown";
        }
    }
}
