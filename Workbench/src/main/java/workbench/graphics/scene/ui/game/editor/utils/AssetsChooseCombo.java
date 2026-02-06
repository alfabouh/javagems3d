package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.type.ImInt;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import workbench.project.managing.instances.IAsset;
import javagems3d.system.service.files.AbstractObjectsFolder;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AssetsChooseCombo <T extends IAsset> {
    private final Supplier<GameResourceAssetsFolder<T>> folderSupplier;
    private final String tab;

    public AssetsChooseCombo(@NotNull String tab, @NotNull Supplier<GameResourceAssetsFolder<T>> folderSupplier) {
        this.folderSupplier = folderSupplier;
        this.tab = tab;
    }

    public void render(Supplier<T> extractAssetFromRelativePath, Consumer<T> onSetObj, Consumer<T> onSetNull) {
        final List<Pair<String, T>> allAssets = new ArrayList<>();
        T extractedAsset = extractAssetFromRelativePath.get();
        boolean checkExtraction = extractedAsset != null;

        if (checkExtraction) {
            allAssets.add(new Pair<>("(*) " + extractedAsset.getName(), null));
            allAssets.add(new Pair<>("None", null));
        } else {
            allAssets.add(new Pair<>("Select...", null));
        }

        this.parseTree(this.getFolderSupplier().get(), allAssets);
        String[] listForCombo = allAssets.stream().map(Pair::getFirst).collect(Collectors.toList()).toArray(new String[]{});
        ImInt selectInt = new ImInt(0);
        if (ImGui.combo(this.getTab(), selectInt, listForCombo)) {
            T getAsset = allAssets.get(selectInt.get()).getSecond();
            if (getAsset != null) {
                onSetObj.accept(getAsset);
            } else {
                onSetNull.accept(null);
            }
        }
    }

    private void parseTree(AbstractObjectsFolder<T> folder, List<Pair<String, T>> allAssets) {
        AssetsChooseCombo.parseTreeS(folder, allAssets);
    }

    public static <E extends IAsset> void parseTreeS(AbstractObjectsFolder<E> folder, List<Pair<String, E>> allModelsAsset) {
        for (E asset : folder.getObjectsThere()) {
            allModelsAsset.add(new Pair<>(folder.getHierarchy() + "/" + asset.getName(), asset));
        }
        for (AbstractObjectsFolder<E> child : folder.getFoldersThere()) {
            AssetsChooseCombo.parseTreeS(child, allModelsAsset);
        }
    }

    public String getTab() {
        return this.tab;
    }

    public Supplier<GameResourceAssetsFolder<T>> getFolderSupplier() {
        return this.folderSupplier;
    }
}
