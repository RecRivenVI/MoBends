package io.github.recrivenvi.mobends.core.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Mo' Bends 二级页面共用的原版选项页框架。 */
abstract class MoBendsOptionsScreen extends Screen {
    private final Screen parent;
    private final List<OptionRow> legacyRows = new ArrayList<>();

    private MoBendsOptionsList120 legacyList;

    protected MoBendsOptionsScreen(Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }

    protected abstract void addMoBendsOptions();

    @Override
    protected final void init() {
        legacyRows.clear();
        addMoBendsOptions();

        legacyList = new MoBendsOptionsList120(minecraft, width, height);
        for (OptionRow row : legacyRows) {
            legacyList.addRow(row.first, row.second, row.section);
        }
        addRenderableWidget(legacyList);

        addRenderableWidget(
                Button.builder(Component.translatable("gui.done"), button -> onClose())
                        .bounds(width / 2 - 100, ConfigScreenLayout.subpageFooterY(height), 200, 20)
                        .build());
    }

    @Override
    public void render(
            net.minecraft.client.gui.GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick) {
        renderBackground(graphics);
        graphics.fill(0, 32, width, 33, 0xFF404040);
        graphics.fill(0, height - 33, width, height - 32, 0xFF404040);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
    }

    protected final void addSection(Component title) {

        Button heading = informationButton(title);
        legacyRows.add(new OptionRow(heading, null, true));
    }

    protected final void addOptionRow(AbstractWidget first, AbstractWidget second) {

        legacyRows.add(new OptionRow(first, second, false));
    }

    protected final void addWideOption(AbstractWidget widget) {

        legacyRows.add(new OptionRow(widget, null, true));
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
