package io.github.recrivenvi.mobends.core.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

/** Mo' Bends 二级页面共用的原版选项页框架。 */
abstract class MoBendsOptionsScreen extends OptionsSubScreen

/*extends Screen*/
{
    private final Screen parent;
    private final List<OptionRow> legacyRows = new ArrayList<>();

    protected MoBendsOptionsScreen(Screen parent, Component title) {

        super(parent, Minecraft.getInstance().options, title);

        this.parent = parent;
    }

    protected abstract void addMoBendsOptions();

    @Override
    protected final void addOptions() {
        addMoBendsOptions();
    }

    @Override
    protected final void addFooter() {
        layout.addToFooter(
                Button.builder(Component.translatable("gui.done"), button -> onClose())
                        .width(200)
                        .build());
    }

    protected final void addSection(Component title) {

        if (list != null) {
            list.addHeader(title);
        }
    }

    protected final void addOptionRow(AbstractWidget first, AbstractWidget second) {

        if (list != null) {
            list.addSmall(first, second);
        }
    }

    protected final void addWideOption(AbstractWidget widget) {

        addOptionRow(widget, null);
    }

    protected final Button optionButton(Component label, Button.OnPress onPress) {
        return Button.builder(label, onPress)
                .bounds(0, 0, ConfigScreenLayout.SUBPAGE_OPTION_WIDTH, 20)
                .build();
    }

    protected final Button informationButton(Component label) {
        Button button = optionButton(label, ignored -> {});
        button.active = false;
        return button;
    }

    protected void beforeClose() {}

    @Override
    public final void onClose() {
        beforeClose();
        ScreenNavigator.show(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class OptionRow {
        private final AbstractWidget first;
        private final AbstractWidget second;
        private final boolean section;

        private OptionRow(AbstractWidget first, AbstractWidget second, boolean section) {
            this.first = first;
            this.second = second;
            this.section = section;
        }
    }
}
