//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.main;

import io.github.recrivenvi.mobends.core.util.IdentifierFactory;
import io.github.recrivenvi.mobends.lib.MoBendsCore;
import net.minecraft.resources.Identifier;

// Static constants for the mod. Platform-specific initialization code sets the MOD_ID.
public class ModStatics {
    public static String MOD_ID = "mobends";

    // Alias for MOD_ID for compatibility.
    public static String MODID = MOD_ID;

    public static String MOD_NAME = "Mo' Bends";
    public static final String VERSION = MoBendsCore.VERSION;

    public static Identifier getResource(String path) {
        return IdentifierFactory.create(MOD_ID, path);
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.main;

import io.github.recrivenvi.mobends.core.util.ResourceLocationFactory;
import io.github.recrivenvi.mobends.lib.MoBendsCore;
import net.minecraft.resources.ResourceLocation;

// Static constants for the mod. Platform-specific initialization code sets the MOD_ID.
public class ModStatics {
    public static String MOD_ID = "mobends";

    // Alias for MOD_ID for compatibility.
    public static String MODID = MOD_ID;

    public static String MOD_NAME = "Mo' Bends";
    public static final String VERSION = MoBendsCore.VERSION;

    public static ResourceLocation getResource(String path) {
        return ResourceLocationFactory.create(MOD_ID, path);
    }
}*/
//?}
