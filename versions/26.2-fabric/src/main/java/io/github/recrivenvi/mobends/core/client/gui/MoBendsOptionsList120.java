// ? if target_1_20_1_forge {
/*package io.github.recrivenvi.mobends.core.client.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

// Minecraft 1.20.1 options-style scrolling list for arbitrary Mo' Bends widgets.
final class MoBendsOptionsList120
        extends ContainerObjectSelectionList<MoBendsOptionsList120.Entry> {
    MoBendsOptionsList120(Minecraft minecraft, int width, int height) {
        super(
                minecraft,
                width,
                height,
                ConfigScreenLayout.SUBPAGE_LIST_TOP,
                ConfigScreenLayout.subpageListBottom(height),
                ConfigScreenLayout.SUBPAGE_ROW_HEIGHT);
        centerListVertically = false;
    }

    void addRow(AbstractWidget first, @Nullable AbstractWidget second, boolean wide) {
        if (wide) {
            first.setWidth(ConfigScreenLayout.SUBPAGE_GRID_WIDTH);
        }
        addEntry(new Entry(first, second, wide));
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 32;
    }

    final class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final AbstractWidget first;
        private final AbstractWidget second;
        private final boolean wide;
        private final List<AbstractWidget> children;

        private Entry(AbstractWidget first, @Nullable AbstractWidget second, boolean wide) {
            this.first = first;
            this.second = second;
            this.wide = wide;
            this.children = new ArrayList<>(2);
            children.add(first);
            if (second != null) {
                children.add(second);
            }
        }

        @Override
        public void render(
                GuiGraphics graphics,
                int index,
                int top,
                int left,
                int rowWidth,
                int rowHeight,
                int mouseX,
                int mouseY,
                boolean hovered,
                float partialTick) {
            int firstX =
                    wide
                            ? (MoBendsOptionsList120.this.getWidth()
                                            - ConfigScreenLayout.SUBPAGE_GRID_WIDTH)
                                    / 2
                            : ConfigScreenLayout.subpageOptionX(
                                    MoBendsOptionsList120.this.getWidth(), 0);
            first.setPosition(firstX, top);
            first.render(graphics, mouseX, mouseY, partialTick);
            if (second != null) {
                second.setPosition(
                        ConfigScreenLayout.subpageOptionX(MoBendsOptionsList120.this.getWidth(), 1),
                        top);
                second.render(graphics, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return children;
        }
    }
}*/
// ?}
