//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.resource.ILocalization;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;

// Shared implementation of ILocalization. Wraps Minecraft's I18n system.
public class McLocalization implements ILocalization {
    @Override
    public String get(String key) {
        return I18n.get(key);
    }

    @Override
    public String get(String key, Object... args) {
        return I18n.get(key, args);
    }

    @Override
    public boolean has(String key) {
        return Language.getInstance().has(key);
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.resource.ILocalization;
import net.minecraft.client.resources.language.I18n;

// Shared implementation of ILocalization. Wraps Minecraft's I18n system.
public class McLocalization implements ILocalization {
    @Override
    public String get(String key) {
        return I18n.get(key);
    }

    @Override
    public String get(String key, Object... args) {
        return I18n.get(key, args);
    }

    @Override
    public boolean has(String key) {
        return I18n.exists(key);
    }
}*/
//?}
