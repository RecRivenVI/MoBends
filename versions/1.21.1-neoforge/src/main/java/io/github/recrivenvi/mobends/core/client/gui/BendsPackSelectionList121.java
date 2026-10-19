package io.github.recrivenvi.mobends.core.client.gui;

import io.github.recrivenvi.mobends.core.pack.LocalBendsPack;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// Minecraft 1.21.1-style transferable BendsPack list.
final class BendsPackSelectionList121
        extends ObjectSelectionList<BendsPackSelectionList121.PackEntry> {
    private static final ResourceLocation DEFAULT_PACK_ICON =
            ResourceLocation.withDefaultNamespace("textures/misc/unknown_pack.png");

    private final LocalPacksScreen121 screen;
    private final LocalPackSelectionModel selection;
    private final Component title;
    private final boolean selectedColumn;

    BendsPackSelectionList121(
            Minecraft minecraft,
            LocalPacksScreen121 screen,
            int width,
            int height,
            Component title,
            boolean selectedColumn,
            LocalPackSelectionModel selection) {
        super(minecraft, width, height, 33, 36);
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
                Math.min(getY() + 3, y),
                0xFFFFFF,
                false);
    }

    final class PackEntry extends ObjectSelectionList.Entry<PackEntry> {
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
            graphics.drawString(
                    minecraft.font,
                    Component.literal(pack.getDisplayName()),
                    left + 34,
                    top + 1,
                    0xFFFFFF,
                    false);
            List<net.minecraft.util.FormattedCharSequence> description =
                    minecraft.font.split(screen.packDescription(pack), DESCRIPTION_WIDTH);
            for (int line = 0; line < Math.min(2, description.size()); line++) {
                graphics.drawString(
                        minecraft.font,
                        description.get(line),
                        left + 34,
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
            if (!selectedColumn) {
                graphics.blitSprite(
                        ResourceLocation.withDefaultNamespace(
                                highlightLeft
                                        ? "transferable_list/select_highlighted"
                                        : "transferable_list/select"),
                        left,
                        top,
                        32,
                        32);
                return;
            }

            graphics.blitSprite(
                    ResourceLocation.withDefaultNamespace(
                            highlightLeft
                                    ? "transferable_list/unselect_highlighted"
                                    : "transferable_list/unselect"),
                    left,
                    top,
                    32,
                    32);
            if (!highlightLeft) {
                String sprite =
                        highlightTop
                                ? (canMoveUp()
                                        ? "transferable_list/move_up_highlighted"
                                        : "transferable_list/move_up")
                                : (canMoveDown()
                                        ? "transferable_list/move_down_highlighted"
                                        : "transferable_list/move_down");
                graphics.blitSprite(
                        ResourceLocation.withDefaultNamespace(sprite), left, top, 32, 32);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            if (!selectedColumn) {
                selection.select(pack.getKey());
            } else if (mouseX - BendsPackSelectionList121.this.getRowLeft() < 16.0D) {
                selection.unselect(pack.getKey());
            } else if (mouseY - BendsPackSelectionList121.this.getRowTop(children().indexOf(this))
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
}
