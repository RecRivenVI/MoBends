// ? if target_1_20_1_forge {
/*package io.github.recrivenvi.mobends.core.client.gui;

import io.github.recrivenvi.mobends.core.pack.LocalBendsPack;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// Version-local clone of Minecraft's two-column transferable pack list.
final class BendsPackSelectionList120
        extends ObjectSelectionList<BendsPackSelectionList120.PackEntry> {
    private static final ResourceLocation DEFAULT_PACK_ICON =
            new ResourceLocation("minecraft", "textures/misc/unknown_pack.png");

    private final LocalPacksScreen120 screen;
    private final LocalPackSelectionModel selection;
    private final Component title;
    private final boolean selectedColumn;

    BendsPackSelectionList120(
            Minecraft minecraft,
            LocalPacksScreen120 screen,
            int width,
            int height,
            Component title,
            boolean selectedColumn,
            LocalPackSelectionModel selection) {
        super(minecraft, width, height, 32, height - 55 + 4, 36);
        this.screen = screen;
        this.selection = selection;
        this.title = title;
        this.selectedColumn = selectedColumn;
        this.centerListVertically = false;
        setRenderHeader(true, (int) (9.0F * 1.5F));
    }

    void replacePacks(List<LocalBendsPack> packs) {
        String previousId = getSelected() == null ? "" : getSelected().pack.getKey();
        clearEntries();
        for (LocalBendsPack pack : packs) {
            PackEntry entry = new PackEntry(pack);
            addEntry(entry);
            if (pack.getKey().equals(previousId)) {
                setSelected(entry);
            }
        }
    }

    @Override
    public int getRowWidth() {
        return getWidth();
    }

    @Override
    protected int getScrollbarPosition() {
        return getRight() - 6;
    }

    @Override
    protected void renderHeader(GuiGraphics graphics, int x, int y) {
        Component heading =
                Component.empty()
                        .append(title)
                        .withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        graphics.drawString(
                minecraft.font,
                heading,
                x + getWidth() / 2 - minecraft.font.width(heading) / 2,
                Math.min(getTop() + 3, y),
                0xFFFFFF,
                false);
    }

    final class PackEntry extends ObjectSelectionList.Entry<PackEntry> {
        private static final int TEXT_X = 34;
        private static final int DESCRIPTION_WIDTH = 157;
        private final LocalBendsPack pack;

        private PackEntry(LocalBendsPack pack) {
            this.pack = pack;
        }

        @Override
        public Component getNarration() {
            return Component.translatable(
                    "narrator.select", Component.literal(pack.getDisplayName()));
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
            graphics.blit(DEFAULT_PACK_ICON, left, top, 0.0F, 0.0F, 32, 32, 32, 32);

            Component name = Component.literal(pack.getDisplayName());
            graphics.drawString(minecraft.font, name, left + TEXT_X, top + 1, 0xFFFFFF, false);
            List<net.minecraft.util.FormattedCharSequence> description =
                    minecraft.font.split(screen.packDescription(pack), DESCRIPTION_WIDTH);
            for (int line = 0; line < Math.min(2, description.size()); line++) {
                graphics.drawString(
                        minecraft.font,
                        description.get(line),
                        left + TEXT_X,
                        top + 12 + line * 9,
                        0x808080,
                        false);
            }

            if (hovered && mouseX - left <= 32) {
                graphics.fill(left, top, left + 32, top + 32, 0xA0909090);
                renderControls(graphics, left, top, mouseX, mouseY);
            }
        }

        private void renderControls(
                GuiGraphics graphics, int left, int top, int mouseX, int mouseY) {
            boolean highlightLeft = mouseX - left < 16;
            boolean highlightTop = mouseY - top < 16;
            int textureX;
            int textureY = selectedColumn ? 32 : 0;
            if (!selectedColumn) {
                textureX = 0;
            } else if (highlightLeft) {
                textureX = 32;
            } else if (highlightTop) {
                textureX = 96;
            } else {
                textureX = 64;
            }
            graphics.blit(
                    new ResourceLocation("minecraft", "textures/gui/resource_packs.png"),
                    left,
                    top,
                    textureX,
                    textureY,
                    32,
                    32,
                    256,
                    256);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            if (!selectedColumn) {
                selection.select(pack.getKey());
            } else if (mouseX - BendsPackSelectionList120.this.getRowLeft() < 16.0D) {
                selection.unselect(pack.getKey());
            } else if (mouseY - BendsPackSelectionList120.this.getRowTop(children().indexOf(this))
                    < 16.0D) {
                selection.move(pack.getKey(), -1);
            } else {
                selection.move(pack.getKey(), 1);
            }
            screen.populateLists();
            return true;
        }

        private boolean canMoveUp() {
            return selection.indexOf(pack.getKey()) > 0;
        }

        private boolean canMoveDown() {
            int index = selection.indexOf(pack.getKey());
            return index >= 0 && index + 1 < selection.size();
        }
    }
}*/
// ?}
