package goblinbob.mobends.core.client.gui;

import com.mojang.logging.LogUtils;
import goblinbob.mobends.core.pack.IBendsPack;
import goblinbob.mobends.core.pack.InvalidPackFormatException;
import goblinbob.mobends.core.pack.LocalBendsPack;
import goblinbob.mobends.core.pack.PackManager;
import goblinbob.mobends.core.util.ErrorReporter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

/**
 * Local BendsPack selector using the resource-pack layout native to the active Minecraft version.
 */
public final class LocalPacksScreen120 extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LIST_WIDTH = 200;
    private final Screen parent;
    private final LocalPackSelectionModel selection;
    private final Path packDirectory;
    private List<LocalBendsPack> localPacks = List.of();
    private PackDirectoryWatcher watcher;
    private long ticksToReload;
    private BendsPackSelectionList120 availablePackList;
    private BendsPackSelectionList120 selectedPackList;

    public LocalPacksScreen120(Screen parent) {
        super(Component.translatable("mobends.gui.local_packs"));
        this.parent = parent;
        this.selection = new LocalPackSelectionModel(currentAppliedKeys());
        loadLocalPacks();
        this.packDirectory = PackManager.INSTANCE.getLocalDirectory().toPath();
        this.watcher = createWatcher(packDirectory);
    }

    @Override
    protected void init() {
        availablePackList =
                new BendsPackSelectionList120(
                        minecraft,
                        this,
                        LIST_WIDTH,
                        height,
                        Component.translatable("pack.available.title"),
                        false,
                        selection);
        availablePackList.setLeftPos(width / 2 - 4 - LIST_WIDTH);
        addWidget(availablePackList);
        selectedPackList =
                new BendsPackSelectionList120(
                        minecraft,
                        this,
                        LIST_WIDTH,
                        height,
                        Component.translatable("pack.selected.title"),
                        true,
                        selection);
        selectedPackList.setLeftPos(width / 2 + 4);
        addWidget(selectedPackList);
        addRenderableWidget(
                Button.builder(
                                Component.translatable("pack.openFolder"),
                                ignored -> Util.getPlatform().openUri(packDirectory.toUri()))
                        .bounds(width / 2 - 154, height - 48, 150, 20)
                        .build());
        addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, ignored -> onClose())
                        .bounds(width / 2 + 4, height - 48, 150, 20)
                        .build());
        populateLists();
    }

    @Override
    public void render(
            net.minecraft.client.gui.GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick) {
        renderDirtBackground(graphics);
        availablePackList.render(graphics, mouseX, mouseY, partialTick);
        selectedPackList.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF);
        graphics.drawCenteredString(
                font,
                Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY),
                width / 2,
                20,
                0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        tickPackDirectoryWatcher();
    }

    void populateLists() {
        selectedPackList.replacePacks(
                selection.selectedKeys().stream()
                        .map(this::findPack)
                        .filter(pack -> pack != null)
                        .toList());
        availablePackList.replacePacks(
                localPacks.stream().filter(pack -> !selection.isSelected(pack.getKey())).toList());
    }

    public void updateFocus(BendsPackSelectionList120 list) {
        // Pack rows call this when keyboard focus crosses between the two vanilla-style columns.
        setFocused(list == selectedPackList ? availablePackList : selectedPackList);
    }

    public void clearSelected() {
        selectedPackList.setSelected(null);
        availablePackList.setSelected(null);
    }

    @Override
    public void onFilesDrop(List<Path> files) {
        String names =
                files.stream()
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.joining(", "));
        ScreenNavigator.show(
                new ConfirmScreen(
                        accepted -> {
                            if (accepted) {
                                BendsPackFileInstaller.install(files, packDirectory);
                                reloadPacks();
                            }
                            ScreenNavigator.show(this);
                        },
                        Component.translatable("pack.dropConfirm"),
                        Component.literal(names)));
    }

    private LocalBendsPack findPack(String key) {
        return localPacks.stream()
                .filter(pack -> pack.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    Component packDescription(LocalBendsPack pack) {
        if (pack.getAuthor().isBlank()) {
            return Component.literal(pack.getDescription());
        }
        if (pack.getDescription().isBlank()) {
            return Component.translatable("mobends.gui.pack_by", pack.getAuthor());
        }
        return Component.translatable(
                "mobends.gui.pack_by_with_description", pack.getAuthor(), pack.getDescription());
    }

    private static PackDirectoryWatcher createWatcher(Path packDirectory) {
        try {
            return new PackDirectoryWatcher(packDirectory);
        } catch (IOException exception) {
            LOGGER.warn(
                    "Failed to initialize local Mo' Bends pack directory monitor {}",
                    packDirectory,
                    exception);
            return null;
        }
    }

    private void tickPackDirectoryWatcher() {
        if (watcher != null) {
            try {
                if (watcher.pollForChanges()) {
                    ticksToReload = 20L;
                }
            } catch (IOException exception) {
                LOGGER.warn(
                        "Failed to monitor local Mo' Bends pack directory {}",
                        packDirectory,
                        exception);
                closeWatcher();
            }
        }
        if (ticksToReload > 0L && --ticksToReload == 0L) {
            reloadPacks();
        }
    }

    private void reloadPacks() {
        PackManager.INSTANCE.reload();
        loadLocalPacks();
        selection.retainAvailable(localPacks.stream().map(LocalBendsPack::getKey).toList());
        populateLists();
        ticksToReload = 0L;
    }

    private void commitSelection() {
        List<String> selectedKeys = selection.selectedKeys();
        if (selectedKeys.equals(currentAppliedKeys())) {
            return;
        }
        try {
            PackManager.INSTANCE.setAppliedPacks(selectedKeys, true);
        } catch (InvalidPackFormatException exception) {
            ErrorReporter.showErrorToPlayer(exception);
            selection.replaceWith(currentAppliedKeys());
        }
    }

    private static List<String> currentAppliedKeys() {
        List<String> keys = new ArrayList<>();
        for (IBendsPack pack : PackManager.INSTANCE.getAppliedPacks()) {
            keys.add(pack.getKey());
        }
        return keys;
    }

    private void loadLocalPacks() {
        localPacks =
                PackManager.INSTANCE.getLocalPacks().stream()
                        .sorted(
                                Comparator.comparing(
                                        LocalBendsPack::getDisplayName,
                                        String.CASE_INSENSITIVE_ORDER))
                        .toList();
    }

    private void closeWatcher() {
        if (watcher != null) {
            try {
                watcher.close();
            } catch (IOException ignored) {
                // Closing a menu must remain safe if the underlying directory disappears.
            }
            watcher = null;
        }
    }

    @Override
    public void onClose() {
        commitSelection();
        closeWatcher();
        ScreenNavigator.show(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
