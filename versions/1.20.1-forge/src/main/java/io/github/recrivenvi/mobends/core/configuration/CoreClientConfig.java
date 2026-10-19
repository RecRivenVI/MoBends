package io.github.recrivenvi.mobends.core.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

/** Client-side configuration with JSON persistence. */
public class CoreClientConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE_NAME = "mobends-client.json";

    private static CoreClientConfig instance;

    private File configFile;
    private ConfigData data = new ConfigData();
    private boolean initialized = false;

    public static CoreClientConfig getInstance() {
        if (instance == null) {
            instance = new CoreClientConfig();
        }
        return instance;
    }

    /**
     * Initialize config file path and load from disk. Must be called after Minecraft is available.
     */
    public void initialize() {
        if (initialized) return;

        try {
            File gameDir = Minecraft.getInstance().gameDirectory;
            File configDir = new File(gameDir, "config");
            configDir.mkdirs();
            this.configFile = new File(configDir, CONFIG_FILE_NAME);
            load();
            initialized = true;
        } catch (Exception e) {
            LOGGER.error("Failed to initialize MoBends config", e);
        }
    }

    private void load() {
        if (configFile == null || !configFile.exists()) {
            data = new ConfigData();
            LOGGER.info("MoBends config file not found, creating defaults");
            save();
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
            if (loaded != null) {
                this.data = loaded.normalized();
                LOGGER.info(
                        "Loaded MoBends config: {} entity overrides, {} applied packs",
                        data.enabledEntities.size(),
                        data.appliedPacks.size());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load MoBends config", e);
        }
    }

    /** Reloads manually edited settings without recreating the client core. */
    public void reload() {
        if (configFile != null) {
            load();
        }
    }

    private void save() {
        if (configFile == null) {
            LOGGER.warn("Config file not initialized, cannot save");
            return;
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save MoBends config", e);
        }
    }

    public List<String> getAppliedPacks() {
        return new ArrayList<>(data.appliedPacks);
    }

    public void setAppliedPacks(List<String> packs) {
        data.appliedPacks = new ArrayList<>(packs);
        save();
    }

    public boolean isEntityEnabled(String entity) {
        return data.enabledEntities.getOrDefault(entity, true);
    }

    public void setEntityEnabled(String entity, boolean enabled) {
        data.enabledEntities.put(entity, enabled);
        save();
    }

    public void setShowSwordTrail(boolean enabled) {
        data.showSwordTrail = enabled;
        save();
    }

    public void setShowArrowTrails(boolean enabled) {
        data.showArrowTrails = enabled;
        save();
    }

    public void setPerformSpinAttack(boolean enabled) {
        data.performSpinAttack = enabled;
        save();
    }

    public void setAdvancedPatterns(
            List<String> itemUseClassifications,
            List<String> itemAttackClassifications,
            List<String> keepArmorAsVanilla,
            List<String> keepEntityAsVanilla) {
        data.itemUseClassifications = normalizedList(itemUseClassifications);
        data.itemAttackClassifications = normalizedList(itemAttackClassifications);
        data.keepArmorAsVanilla = normalizedList(keepArmorAsVanilla);
        data.keepEntityAsVanilla = normalizedList(keepEntityAsVanilla);
        save();
    }

    public void ensureEntityDefaults(Collection<String> entityKeys) {
        boolean changed = false;
        List<String> sortedKeys = new ArrayList<>(entityKeys);
        Collections.sort(sortedKeys);
        for (String entityKey : sortedKeys) {
            if (!data.enabledEntities.containsKey(entityKey)) {
                data.enabledEntities.put(entityKey, true);
                changed = true;
            }
        }
        if (changed) {
            save();
        }
    }

    public boolean isEntityAnimated(String entity) {
        return isEntityEnabled(entity);
    }

    public List<String> getItemUseClassifications() {
        return copyOf(data.itemUseClassifications);
    }

    public List<String> getItemAttackClassifications() {
        return copyOf(data.itemAttackClassifications);
    }

    public List<String> getKeepArmorAsVanilla() {
        return copyOf(data.keepArmorAsVanilla);
    }

    public List<String> getKeepEntityAsVanilla() {
        return copyOf(data.keepEntityAsVanilla);
    }

    public boolean isShowSwordTrail() {
        return data.showSwordTrail;
    }

    public boolean isShowArrowTrails() {
        return data.showArrowTrails;
    }

    public boolean isPerformSpinAttack() {
        return data.performSpinAttack;
    }

    private static List<String> copyOf(List<String> values) {
        return values == null ? Collections.emptyList() : new ArrayList<>(values);
    }

    private static List<String> normalizedList(List<String> values) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    result.add(value.trim());
                }
            }
        }
        return new ArrayList<>(result);
    }

    private static class ConfigData {
        List<String> appliedPacks = new ArrayList<>();
        Map<String, Boolean> enabledEntities = new HashMap<>();
        List<String> itemUseClassifications = new ArrayList<>();
        List<String> itemAttackClassifications = new ArrayList<>();
        List<String> keepArmorAsVanilla = new ArrayList<>();
        List<String> keepEntityAsVanilla = new ArrayList<>();
        boolean showSwordTrail = true;
        boolean showArrowTrails = true;
        boolean performSpinAttack = true;

        ConfigData normalized() {
            if (appliedPacks == null) appliedPacks = new ArrayList<>();
            if (enabledEntities == null) enabledEntities = new HashMap<>();
            if (itemUseClassifications == null) itemUseClassifications = new ArrayList<>();
            if (itemAttackClassifications == null) itemAttackClassifications = new ArrayList<>();
            if (keepArmorAsVanilla == null) keepArmorAsVanilla = new ArrayList<>();
            if (keepEntityAsVanilla == null) keepEntityAsVanilla = new ArrayList<>();
            return this;
        }
    }
}
