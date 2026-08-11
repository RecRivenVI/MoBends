package goblinbob.mobends.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import goblinbob.mobends.core.client.gui.MoBendsMenuScreen;

/** 通过 Mod Menu 暴露 Mo' Bends 的通用配置入口。 */
public final class MoBendsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MoBendsMenuScreen::new;
    }
}
