package io.github.recrivenvi.mobends.core.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
// ? if >1.20.1
// import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

/** Mo' Bends 二级页面共用的原版选项页框架。 */
abstract class MoBendsOptionsScreen
        // ? if >1.20.1
        // extends OptionsSubScreen
        // ? if <=1.20.1
        extends Screen {
    private final Screen parent;
    private final List<OptionRow> legacyRows = new ArrayList<>();

    // ? if <=1.20.1
    private MoBendsOptionsList120 legacyList;

    protected MoBendsOptionsScreen(Screen parent, Component title) {
        // ? if >1.20.1
        // super(parent, Minecraft.getInstance().options, title);
        // ? if <=1.20.1
        super(title);
        this.parent = parent;
    }

    protected abstract void addMoBendsOptions();

    // ? if >1.20.1 {
    /*@Override
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

    */
    // ?} else {
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

    // ?}

    protected final void addSection(Component title) {
        // ? if >1.21.1 {
        /*if (list != null) {
            list.addHeader(title);
        }
        */
        // ?} else if >1.20.1 {
        /*Button heading = informationButton(title);
        addOptionRow(heading, null);*/
        // ?} else {
        Button heading = informationButton(title);
        legacyRows.add(new OptionRow(heading, null, true));
        // ?}
    }

    protected final void addOptionRow(AbstractWidget first, AbstractWidget second) {
        // ? if >1.20.1 {
        /*if (list != null) {
            list.addSmall(first, second);
        }
        */
        // ?} else {
        legacyRows.add(new OptionRow(first, second, false));
        // ?}
    }

    protected final void addWideOption(AbstractWidget widget) {
        // ? if >=26.2 {
        /*if (list != null) {
            list.addBig(widget);
        }
        */
        // ?} else if >1.20.1 {
        /*addOptionRow(widget, null);*/
        // ?} else {
        legacyRows.add(new OptionRow(widget, null, true));
        // ?}
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
