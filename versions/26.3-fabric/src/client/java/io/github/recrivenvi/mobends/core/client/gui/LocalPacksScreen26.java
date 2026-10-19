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
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import org.slf4j.Logger;

// 仿照 Minecraft 资源包选择器的本地 BendsPack 管理页面。
public final class LocalPacksScreen26 extends Screen {
    private final Screen parent;
    private final LocalPackSelectionModel selection;
    private List<LocalBendsPack> localPacks = List.of();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path packDirectory;
    private PackDirectoryWatcher watcher;
    private long ticksToReload;

    private static final int LIST_WIDTH = 200;
    private static final Identifier DEFAULT_PACK_ICON =
            Identifier.withDefaultNamespace("textures/misc/unknown_pack.png");

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private TransferableSelectionList availablePackList;
    private TransferableSelectionList selectedPackList;
    private EditBox search;

    public LocalPacksScreen26(Screen parent) {
        super(Component.translatable("mobends.gui.local_packs"));
        this.parent = parent;
        this.selection = new LocalPackSelectionModel(currentAppliedKeys());
        loadLocalPacks();
        this.packDirectory = PackManager.INSTANCE.getLocalDirectory().toPath();
        this.watcher = createWatcher(packDirectory);
    }

    @Override
    protected void init() {
        layout.setHeaderHeight(4 + 9 + 4 + 9 + 4 + 15 + 4);

        LinearLayout header = layout.addToHeader(LinearLayout.vertical().spacing(4));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(getTitle(), font));
        header.addChild(
                new StringWidget(
                        Component.translatable("pack.dropInfo").withStyle(ChatFormatting.GRAY),
                        font));
        search = header.addChild(new EditBox(font, 0, 0, LIST_WIDTH, 15, Component.empty()));
        search.setHint(
                Component.translatable("gui.packSelection.search")
                        .withStyle(EditBox.SEARCH_HINT_STYLE));
        search.setResponder(ignored -> populateLists(null));

        availablePackList =
                layout.addToContents(
                        new TransferableSelectionList(
                                minecraft,
                                null,
                                LIST_WIDTH,
                                height - 66,
                                Component.translatable("pack.available.title")));
        selectedPackList =
                layout.addToContents(
                        new TransferableSelectionList(
                                minecraft,
                                null,
                                LIST_WIDTH,
                                height - 66,
                                Component.translatable("pack.selected.title")));

        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(
                Button.builder(
                                Component.translatable("pack.openFolder"),
                                ignored ->
                                        com.mojang.blaze3d.Blaze3D.openPath(
                                                PackManager.INSTANCE.getLocalDirectory().toPath()))
                        .build());
        footer.addChild(
                Button.builder(Component.translatable("gui.done"), ignored -> onClose()).build());

        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
        populateLists(null);
    }

    @Override
    protected void setInitialFocus() {
        if (search != null) {
            setInitialFocus(search);
        } else {
            super.setInitialFocus();
        }
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        if (availablePackList != null) {
            availablePackList.updateSizeAndPosition(
                    LIST_WIDTH,
                    layout.getContentHeight(),
                    width / 2 - 15 - LIST_WIDTH,
                    layout.getHeaderHeight());
        }
        if (selectedPackList != null) {
            selectedPackList.updateSizeAndPosition(
                    LIST_WIDTH,
                    layout.getContentHeight(),
                    width / 2 + 15,
                    layout.getHeaderHeight());
        }
    }

    @Override
    public void tick() {
        tickPackDirectoryWatcher();
    }

    private void populateLists(String transferredKey) {
        if (availablePackList == null || selectedPackList == null) {
            return;
        }

        String query = search == null ? "" : search.getValue().strip().toLowerCase(Locale.ROOT);
        Stream<PackSelectionModel.Entry> availableEntries =
                localPacks.stream()
                        .filter(pack -> !selection.isSelected(pack.getKey()))
                        .filter(pack -> matches(pack, query))
                        .map(pack -> (PackSelectionModel.Entry) new BendsPackEntry(pack));
        Stream<PackSelectionModel.Entry> selectedEntries =
                selection.selectedKeys().stream()
                        .map(this::findPack)
                        .filter(pack -> pack != null && matches(pack, query))
                        .map(pack -> (PackSelectionModel.Entry) new BendsPackEntry(pack));

        availablePackList.updateList(availableEntries, null);
        selectedPackList.updateList(selectedEntries, null);

        if (transferredKey != null) {
            TransferableSelectionList target =
                    selection.isSelected(transferredKey) ? selectedPackList : availablePackList;
            TransferableSelectionList.Entry transferred =
                    target.children().stream()
                            .filter(entry -> transferredKey.equals(entry.getPackId()))
                            .findFirst()
                            .orElse(null);
            if (transferred != null) {
                setFocused(target);
                target.setFocused(transferred);
            }
        }
    }

    private boolean matches(LocalBendsPack pack, String query) {
        return query.isEmpty()
                || pack.getKey().toLowerCase(Locale.ROOT).contains(query)
                || pack.getDisplayName().toLowerCase(Locale.ROOT).contains(query)
                || pack.getAuthor().toLowerCase(Locale.ROOT).contains(query)
                || pack.getDescription().toLowerCase(Locale.ROOT).contains(query);
    }

    private LocalBendsPack findPack(String key) {
        return localPacks.stream()
                .filter(pack -> pack.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    private Component packDescription(LocalBendsPack pack) {
        if (pack.getAuthor().isBlank()) {
            return Component.literal(pack.getDescription());
        }
        if (pack.getDescription().isBlank()) {
            return Component.translatable("mobends.gui.pack_by", pack.getAuthor());
        }
        return Component.translatable(
                "mobends.gui.pack_by_with_description", pack.getAuthor(), pack.getDescription());
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
                                List<Path> rejected = copyDroppedPackFiles(files);
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

    private List<Path> copyDroppedPackFiles(List<Path> files) {
        return BendsPackFileInstaller.install(files, packDirectory);
    }

    private final class BendsPackEntry implements PackSelectionModel.Entry {
        private final LocalBendsPack pack;

        private BendsPackEntry(LocalBendsPack pack) {
            this.pack = pack;
        }

        @Override
        public Identifier getIconTexture() {
            return DEFAULT_PACK_ICON;
        }

        @Override
        public PackCompatibility getCompatibility() {
            return PackCompatibility.COMPATIBLE;
        }

        @Override
        public String getId() {
            return pack.getKey();
        }

        @Override
        public Component getTitle() {
            return Component.literal(pack.getDisplayName());
        }

        @Override
        public Component getDescription() {
            return packDescription(pack);
        }

        @Override
        public PackSource getPackSource() {
            return PackSource.DEFAULT;
        }

        @Override
        public boolean isFixedPosition() {
            return false;
        }

        @Override
        public boolean isRequired() {
            return false;
        }

        @Override
        public void select() {
            if (selection.select(pack.getKey())) {
                rebuild(pack.getKey());
            }
        }

        @Override
        public void unselect() {
            if (selection.unselect(pack.getKey())) {
                rebuild(pack.getKey());
            }
        }

        @Override
        public void moveUp() {
            move(pack.getKey(), -1);
        }

        @Override
        public void moveDown() {
            move(pack.getKey(), 1);
        }

        @Override
        public boolean isSelected() {
            return selection.isSelected(pack.getKey());
        }

        @Override
        public boolean canMoveUp() {
            return selection.indexOf(pack.getKey()) > 0;
        }

        @Override
        public boolean canMoveDown() {
            int index = selection.indexOf(pack.getKey());
            return index >= 0 && index + 1 < selection.size();
        }
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
        populateLists(null);
        ticksToReload = 0L;
    }

    private void move(String key, int offset) {
        if (selection.move(key, offset)) {
            rebuild(key);
        }
    }

    private void rebuild(String transferredKey) {
        populateLists(transferredKey);
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
