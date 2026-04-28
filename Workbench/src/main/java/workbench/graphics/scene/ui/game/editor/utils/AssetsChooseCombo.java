package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import javagems3d.help.JGemsHelper;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AssetsChooseCombo<T extends VirtualObjectsFolder.ObjectWithName> {
    private final String tab;
    private final Supplier<VirtualObjectsFolder<T>> folderSupplier;
    private final @Nullable Collection<Pair<String, Supplier<T>>> additional;
    private final ImString finder;

    public AssetsChooseCombo(String tab, Supplier<VirtualObjectsFolder<T>> folderSupplier, @Nullable Collection<Pair<String, Supplier<T>>> additional) {
        this.tab = tab;
        this.folderSupplier = folderSupplier;
        this.additional = additional;
        this.finder = new ImString(64);
    }

    public AssetsChooseCombo(@NotNull String tab, @NotNull Supplier<VirtualObjectsFolder<T>> folderSupplier) {
        this(tab, folderSupplier, null);
    }

    public void render(Supplier<T> extractAssetFromRelativePath, Consumer<T> onSetObj, Consumer<T> onSetNull) {
        final List<Pair<String, T>> allAssets = new ArrayList<>();
        T extractedAsset = extractAssetFromRelativePath.get();
        boolean checkExtraction = extractedAsset != null;

        if (checkExtraction) {
            allAssets.add(new Pair<>("(*) " + extractedAsset.name(), null));
            allAssets.add(new Pair<>("None", null));
        } else {
            allAssets.add(new Pair<>("Select...", null));
        }

        if (additional != null) {
            additional.forEach(e -> {
                allAssets.add(new Pair<>(e.first(), e.second().get()));
            });
        }

        this.parseTree(this.folderSupplier.get(), allAssets);
        allAssets.sort(Comparator.comparingInt(e -> JGemsHelper.files().countChar(e.first(), '/')));
        String[] listForCombo = allAssets.stream().map(Pair::first).toList().toArray(new String[]{});
        ImInt selectInt = new ImInt(0);
        ImGui.setNextItemWidth(260);
        if (ImGui.combo("Select " + this.tab, selectInt, listForCombo)) {
            T getAsset = allAssets.get(selectInt.get()).second();
            if (getAsset != null) {
                onSetObj.accept(getAsset);
            } else {
                onSetNull.accept(null);
            }
        }

        ImGui.setNextItemWidth(260);
        if (ImGui.inputText("Find " + this.tab, this.finder)) {
            ImGui.openPopup("##asset_finder" + this.tab);
        }
        ImGui.spacing();

        if (!ImGui.isItemActive() && !ImGui.isPopupOpen("##asset_finder" + this.tab)) {
            this.finder.clear();
        }

        if (ImGui.beginPopup("##asset_finder" + this.tab, ImGuiWindowFlags.NoFocusOnAppearing)) {
            List<Pair<String, T>> filtered = new ArrayList<>(allAssets);
            filtered = filtered.stream().filter(e -> e.first().toLowerCase().contains("/") && e.first().toLowerCase().contains(this.finder.get())).toList();
            if (filtered.isEmpty()) {
                ImGui.text("Empty...");
            } else {
                filtered.forEach(e -> {
                    if (ImGui.selectable(e.first())) {
                        if (e.second() != null) {
                            onSetObj.accept(e.second());
                        } else {
                            onSetNull.accept(null);
                        }
                    }
                });
            }
            if (this.finder.isEmpty()) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }

    private void parseTree(VirtualObjectsFolder<T> folder, List<Pair<String, T>> allAssets) {
        AssetsChooseCombo.parseTreeS(folder, allAssets);
    }

    public static <E extends VirtualObjectsFolder.ObjectWithName> void parseTreeS(VirtualObjectsFolder<E> folder, List<Pair<String, E>> allModelsAsset) {
        for (E asset : folder.getObjectsThere()) {
            allModelsAsset.add(new Pair<>(folder.getHierarchy() + "/" + asset.name(), asset));
        }
        for (VirtualObjectsFolder<E> child : folder.getFoldersThere()) {
            AssetsChooseCombo.parseTreeS(child, allModelsAsset);
        }
    }
}
