package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.type.ImInt;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record AssetsChooseCombo<T extends VirtualObjectsFolder.ObjectWithName>(String tab, Supplier<VirtualObjectsFolder<T>> folderSupplier, @Nullable Collection<Pair<String, Supplier<T>>> additional) {
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

        this.parseTree(this.folderSupplier().get(), allAssets);
        String[] listForCombo = allAssets.stream().map(Pair::first).toList().toArray(new String[]{});
        ImInt selectInt = new ImInt(0);
        if (ImGui.combo(this.tab(), selectInt, listForCombo)) {
            T getAsset = allAssets.get(selectInt.get()).second();
            if (getAsset != null) {
                onSetObj.accept(getAsset);
            } else {
                onSetNull.accept(null);
            }
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
