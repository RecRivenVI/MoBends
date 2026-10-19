package io.github.recrivenvi.mobends.core.pack;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.core.configuration.CoreClientConfig;
import io.github.recrivenvi.mobends.core.util.ErrorReporter;
import io.github.recrivenvi.mobends.lib.flux.ObservableMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public class PackManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final PackManager INSTANCE = new PackManager();

    private File localDirectory;

    private ObservableMap<String, LocalBendsPack> localPacks = new ObservableMap<>();

    private CoreClientConfig config;
    private final List<IBendsPack> appliedPacks;

    public PackManager() {
        appliedPacks = new LinkedList<>();
    }

    public void initialize(CoreClientConfig config) {
        localDirectory = new File(Minecraft.getInstance().gameDirectory, "bendspacks");
        localDirectory.mkdir();

        this.config = config;
        try {
            initLocalPacks();
        } catch (InvalidPackFormatException e) {
            // Some of the packs were in an invalid format.
            e.printStackTrace();
            ErrorReporter.showErrorToPlayer(e);
        }
    }

    public void initLocalPacks() throws InvalidPackFormatException {
        localPacks.clear();
        appliedPacks.clear();
        PackDataProvider.INSTANCE.clearCache();

        File[] files = localDirectory.listFiles();
        if (files == null) {
            return;
        }

        Arrays.sort(files, (left, right) -> left.getName().compareToIgnoreCase(right.getName()));
        for (File file : files) {
            if (file.getName().toLowerCase(Locale.ROOT).endsWith(".bendsmeta")) {
                try {
                    LocalBendsPack bendsPack = LocalBendsPack.readFromFile(file);
                    localPacks.put(bendsPack.getKey(), bendsPack);
                } catch (IOException ex) {
                    LOGGER.error(
                            String.format("Couldn't load local bends pack: '%s'", file.getName()));
                }
            }
        }

        for (File file : files) {
            String filename = file.getName();
            if (!filename.toLowerCase(Locale.ROOT).endsWith(".bends")) {
                continue;
            }
            String baseName = filename.substring(0, filename.length() - ".bends".length());
            if (new File(file.getParentFile(), baseName + ".bendsmeta").isFile()) {
                continue;
            }

            try {
                LocalBendsPack bendsPack = LocalBendsPack.readLegacyFromFile(file);
                localPacks.put(bendsPack.getKey(), bendsPack);
            } catch (IOException exception) {
                LOGGER.error(
                        "Couldn't load legacy local BendsPack: '{}'", file.getName(), exception);
            }
        }

        // Re-adding the applied packs.
        for (String key : config.getAppliedPacks()) {
            IBendsPack pack = localPacks.get(key);
            if (pack != null) {
                appliedPacks.add(pack);
            }
        }

        try {
            PackDataProvider.INSTANCE.createBendsPackData(appliedPacks);
        } catch (InvalidPackFormatException ex) {
            resetAppliedPacks(true);
            throw ex;
        }
    }

    /** Reloads local pack metadata and reapplies the pack keys from configuration. */
    public void reload() {
        try {
            initLocalPacks();
        } catch (InvalidPackFormatException exception) {
            LOGGER.error("Couldn't reload local Mo' Bends packs", exception);
            ErrorReporter.showErrorToPlayer(exception);
        }
    }

    /** Applies an ordered set of already-installed local packs. */
    public void setAppliedPacks(Collection<String> packKeys, boolean saveToConfig)
            throws InvalidPackFormatException {
        List<IBendsPack> newAppliedPacks = new LinkedList<>();
        for (String key : packKeys) {
            IBendsPack pack = localPacks.get(key);
            if (pack != null) {
                newAppliedPacks.add(pack);
            }
        }

        PackDataProvider.INSTANCE.createBendsPackData(newAppliedPacks);
        appliedPacks.clear();
        appliedPacks.addAll(newAppliedPacks);

        if (saveToConfig) {
            config.setAppliedPacks(new java.util.ArrayList<>(packKeys));
        }
    }

    public void resetAppliedPacks(boolean saveToConfig) {
        appliedPacks.clear();

        if (saveToConfig) {
            config.setAppliedPacks(java.util.Collections.emptyList());
        }

        try {
            PackDataProvider.INSTANCE.createBendsPackData(appliedPacks);
        } catch (InvalidPackFormatException ignored) {
            // Never should happen.
        }
    }

    public Collection<IBendsPack> getAppliedPacks() {
        return appliedPacks;
    }

    public Collection<LocalBendsPack> getLocalPacks() {
        return localPacks.values();
    }

    public File getLocalDirectory() {
        return localDirectory;
    }

    public File getMetaFileForPack(String filename) throws IOException {
        File packFile = new File(localDirectory, filename + ".bendsmeta");
        packFile.createNewFile();
        return packFile;
    }

    public File getDataFileForPack(String filename) throws IOException {
        return new File(localDirectory, filename + ".bends");
    }
}
