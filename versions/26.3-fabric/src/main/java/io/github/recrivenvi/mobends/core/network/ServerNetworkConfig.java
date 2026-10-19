package io.github.recrivenvi.mobends.core.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;

/** Local server policy persisted under config/mobends-server.json. */
public final class ServerNetworkConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ServerNetworkConfig DEFAULTS = new ServerNetworkConfig(false, true, true);

    private boolean modelScalingAllowed;
    private boolean bendsPacksAllowed;
    private boolean movementLimited;

    private ServerNetworkConfig() {
        this(false, true, true);
    }

    public ServerNetworkConfig(
            boolean modelScalingAllowed, boolean bendsPacksAllowed, boolean movementLimited) {
        this.modelScalingAllowed = modelScalingAllowed;
        this.bendsPacksAllowed = bendsPacksAllowed;
        this.movementLimited = movementLimited;
    }

    public static ServerNetworkConfig load(Path configDirectory) {
        Path file = configDirectory.resolve("mobends-server.json");
        try {
            Files.createDirectories(configDirectory);
            if (Files.isRegularFile(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    ServerNetworkConfig loaded = GSON.fromJson(reader, ServerNetworkConfig.class);
                    if (loaded != null) {
                        return loaded;
                    }
                }
            }

            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(DEFAULTS, writer);
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to load Mo' Bends server policy; using safe defaults", exception);
        }
        return DEFAULTS.copy();
    }

    private ServerNetworkConfig copy() {
        return new ServerNetworkConfig(modelScalingAllowed, bendsPacksAllowed, movementLimited);
    }

    public boolean isModelScalingAllowed() {
        return modelScalingAllowed;
    }

    public boolean areBendsPacksAllowed() {
        return bendsPacksAllowed;
    }

    public boolean isMovementLimited() {
        return movementLimited;
    }
}
