package io.github.recrivenvi.mobends.core.network;

/**
 * Server-provided gameplay policy. This traffic stays inside the active Minecraft connection and
 * never contacts a public remote service.
 */
public final class NetworkConfiguration {
    public static final NetworkConfiguration INSTANCE = new NetworkConfiguration();

    private boolean modelScalingAllowed;
    private boolean bendsPacksAllowed = true;
    private boolean movementLimited = true;

    private NetworkConfiguration() {}

    public synchronized void onWorldJoin(boolean singleplayer) {
        modelScalingAllowed = singleplayer;
        bendsPacksAllowed = true;
        movementLimited = !singleplayer;
    }

    public synchronized void applyServerValues(
            boolean modelScalingAllowed, boolean bendsPacksAllowed, boolean movementLimited) {
        this.modelScalingAllowed = modelScalingAllowed;
        this.bendsPacksAllowed = bendsPacksAllowed;
        this.movementLimited = movementLimited;
    }

    public synchronized boolean isModelScalingAllowed() {
        return modelScalingAllowed;
    }

    public synchronized boolean areBendsPacksAllowed() {
        return bendsPacksAllowed;
    }

    public synchronized boolean isMovementLimited() {
        return movementLimited;
    }
}
