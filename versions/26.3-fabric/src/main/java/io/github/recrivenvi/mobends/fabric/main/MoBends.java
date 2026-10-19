package io.github.recrivenvi.mobends.fabric.main;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.fabric.network.FabricNetworkCommon;
import io.github.recrivenvi.mobends.standard.main.ModStatics;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

// Fabric 服务端与集成服务器共用入口。
public final class MoBends implements ModInitializer {
    public static final String MODID = "mobends";
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        FabricNetworkCommon.registerCommon();
        LOG.info("Mo' Bends {} common setup complete on Fabric", ModStatics.VERSION);
    }
}
