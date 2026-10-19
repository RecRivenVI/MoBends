package io.github.recrivenvi.mobends.standard.animation.bit.biped.item;

import javax.annotation.Nullable;

/** Modern vanilla item-use poses that do not map to an original Mo' Bends action. */
public enum VanillaUsePose {
    CROSSBOW_CHARGE("crossbow"),
    CROSSBOW_HOLD("crossbow"),
    THROW_SPEAR("spear"),
    SPEAR("spear"),
    SPYGLASS("spyglass"),
    TOOT_HORN("toot_horn"),
    BRUSH("brush");

    private final String actionName;

    VanillaUsePose(String actionName) {
        this.actionName = actionName;
    }

    public String actionName() {
        return actionName;
    }

    @Nullable public static VanillaUsePose fromArmPoseName(String name) {
        if (name == null) {
            return null;
        }
        return switch (name) {
            case "CROSSBOW_CHARGE" -> CROSSBOW_CHARGE;
            case "CROSSBOW_HOLD" -> CROSSBOW_HOLD;
            case "THROW_SPEAR", "THROW_TRIDENT" -> THROW_SPEAR;
            case "SPEAR" -> SPEAR;
            case "SPYGLASS" -> SPYGLASS;
            case "TOOT_HORN" -> TOOT_HORN;
            case "BRUSH" -> BRUSH;
            default -> null;
        };
    }
}
