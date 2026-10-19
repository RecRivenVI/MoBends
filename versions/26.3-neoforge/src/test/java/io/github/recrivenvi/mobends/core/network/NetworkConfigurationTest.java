package io.github.recrivenvi.mobends.core.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NetworkConfigurationTest {
    @Test
    public void singleplayerUsesUnrestrictedLocalPolicy() {
        NetworkConfiguration.INSTANCE.onWorldJoin(true);

        assertTrue(NetworkConfiguration.INSTANCE.isModelScalingAllowed());
        assertTrue(NetworkConfiguration.INSTANCE.areBendsPacksAllowed());
        assertFalse(NetworkConfiguration.INSTANCE.isMovementLimited());
    }

    @Test
    public void multiplayerStartsRestrictedUntilServerPolicyArrives() {
        NetworkConfiguration.INSTANCE.onWorldJoin(false);

        assertFalse(NetworkConfiguration.INSTANCE.isModelScalingAllowed());
        assertTrue(NetworkConfiguration.INSTANCE.areBendsPacksAllowed());
        assertTrue(NetworkConfiguration.INSTANCE.isMovementLimited());
    }

    @Test
    public void appliesValuesReceivedThroughTheMinecraftConnection() {
        NetworkConfiguration.INSTANCE.onWorldJoin(false);
        NetworkConfiguration.INSTANCE.applyServerValues(true, false, false);

        assertTrue(NetworkConfiguration.INSTANCE.isModelScalingAllowed());
        assertFalse(NetworkConfiguration.INSTANCE.areBendsPacksAllowed());
        assertFalse(NetworkConfiguration.INSTANCE.isMovementLimited());
    }
}
