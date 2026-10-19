package io.github.recrivenvi.mobends.core.client.gui;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.core.pack.IBendsPack;
import io.github.recrivenvi.mobends.core.pack.InvalidPackFormatException;
import io.github.recrivenvi.mobends.core.pack.LocalBendsPack;
import io.github.recrivenvi.mobends.core.pack.PackManager;
import io.github.recrivenvi.mobends.core.util.ErrorReporter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

// Minecraft 1.21.1 resource-pack layout adapted to local BendsPacks.
public final class LocalPacksScreen121 extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LIST_WIDTH = 200;

    private final Screen parent;
    private final LocalPackSelectionModel selection;
    private final Path packDirectory;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private List<LocalBendsPack> localPacks = List.of();
    private PackDirectoryWatcher watcher;
    private long ticksToReload;
    private BendsPackSelectionList121 availablePackList;
    private BendsPackSelectionList121 selectedPackList;

    public LocalPacksScreen121(Screen parent) {
        super(Component.translatable("mobends.gui.local_packs"));
        this.parent = parent;
        this.selection = new LocalPackSelectionModel(currentAppliedKeys());
        loadLocalPacks();
        this.packDirectory = PackManager.INSTANCE.getLocalDirectory().toPath();
        this.watcher = createWatcher(packDirectory);
    }

    @Override
    protected void init() {
        LinearLayout header = layout.addToHeader(LinearLayout.vertical().spacing(5));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(getTitle(), font));
        header.addChild(
                new StringWidget(
                        Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY),
                        font));

        availablePackList =
                addRenderableWidget(
                        new BendsPackSelectionList121(
                                minecraft,
                                this,
                                LIST_WIDTH,
                                height - 66,
                                Component.translatable("pack.available.title"),
                                false,
                                selection));
        selectedPackList =
                addRenderableWidget(
                        new BendsPackSelectionList121(
                                minecraft,
                                this,
                                LIST_WIDTH,
                                height - 66,
                                Component.translatable("pack.selected.title"),
                                true,
                                selection));

        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(
                Button.builder(
                                Component.translatable("pack.openFolder"),
                                ignored -> Util.getPlatform().openPath(packDirectory))
                        .build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, ignored -> onClose()).build());
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
        populateLists();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        if (availablePackList != null) {
            availablePackList.updateSize(LIST_WIDTH, layout);
            availablePackList.setX(width / 2 - 15 - LIST_WIDTH);
        }
        if (selectedPackList != null) {
            selectedPackList.updateSize(LIST_WIDTH, layout);
            selectedPackList.setX(width / 2 + 15);
        }
    }

    @Override
    public void tick() {
        tickPackDirectoryWatcher();
    }

    void populateLists() {
        if (availablePackList == null || selectedPackList == null) {
            return;
        }
        selectedPackList.replacePacks(
                selection.selectedKeys().stream()
                        .map(this::findPack)
                        .filter(pack -> pack != null)
                        .toList());
        availablePackList.replacePacks(
                localPacks.stream().filter(pack -> !selection.isSelected(pack.getKey())).toList());
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

    private LocalBendsPack findPack(String key) {
        return localPacks.stream()
                .filter(pack -> pack.getKey().equals(key))
                .findFirst()
                .orElse(null);
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
                                List<Path> rejected =
                                        BendsPackFileInstaller.install(files, packDirectory);
                                reloadPacks();
                                if (!rejected.isEmpty()) {
                                    String rejectedNames =
                                            rejected.stream()
                                                    .map(Path::getFileName)
                                                    .map(Path::toString)
                                                    .collect(Collectors.joining(", "));
                                    ScreenNavigator.show(
                                            new AlertScreen(
                                                    () -> ScreenNavigator.show(this),
                                                    Component.translatable(
                                                            "pack.dropRejected.title"),
                                                    Component.translatable(
                                                            "pack.dropRejected.message",
                                                            rejectedNames)));
                                    return;
                                }
                            }
                            ScreenNavigator.show(this);
                        },
                        Component.translatable("pack.dropConfirm"),
                        Component.literal(names)));
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
                // Closing remains safe if the watched directory disappeared.
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
