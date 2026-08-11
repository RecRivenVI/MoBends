package goblinbob.mobends.core.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ServerNetworkConfigTest {
    @TempDir Path tempDirectory;

    @Test
    public void createsAndLoadsOriginalSafeDefaults() {
        ServerNetworkConfig config = ServerNetworkConfig.load(tempDirectory);

        assertFalse(config.isModelScalingAllowed());
        assertTrue(config.areBendsPacksAllowed());
        assertTrue(config.isMovementLimited());
        assertTrue(Files.isRegularFile(tempDirectory.resolve("mobends-server.json")));
    }

    @Test
    public void loadsLocalServerPolicyWithoutAnyRemoteService() throws IOException {
        Files.writeString(
                tempDirectory.resolve("mobends-server.json"),
                """
                {
                  "modelScalingAllowed": true,
                  "bendsPacksAllowed": false,
                  "movementLimited": false
                }
                """);

        ServerNetworkConfig config = ServerNetworkConfig.load(tempDirectory);

        assertTrue(config.isModelScalingAllowed());
        assertFalse(config.areBendsPacksAllowed());
        assertFalse(config.isMovementLimited());
    }

    @Test
    public void keepsSafeDefaultsForFieldsMissingFromAnOlderConfig() throws IOException {
        Files.writeString(
                tempDirectory.resolve("mobends-server.json"),
                """
                {
                  "modelScalingAllowed": true
                }
                """);

        ServerNetworkConfig config = ServerNetworkConfig.load(tempDirectory);

        assertTrue(config.isModelScalingAllowed());
        assertTrue(config.areBendsPacksAllowed());
        assertTrue(config.isMovementLimited());
    }
}
