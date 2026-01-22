package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;
import workbench.project.managing.instances.IAsset;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FolderResourcesTreeDrawerG<E extends IAsset, T extends IPreviewWrapperObject<E>> {
    private final String tab;
    private final Consumer<Void> openFolderAction;
    private T previewWrapperObject;
    private final Supplier<GameResourceAssetsFolder<E>> gameResourceAssetsFolder;
    private final Function<E, T> previewInstanceFactory;

    public FolderResourcesTreeDrawerG(@NotNull Supplier<GameResourceAssetsFolder<E>> gameResourceAssetsFolder, @NotNull String tab, @NotNull Consumer<Void> openFolderAction, @NotNull Function<E, T> previewInstanceFactory) {
        this.tab = tab;
        this.openFolderAction = openFolderAction;
        this.gameResourceAssetsFolder = gameResourceAssetsFolder;
        this.previewInstanceFactory = previewInstanceFactory;
    }

    private void tree(GameResourceAssetsFolder<E> folder, boolean root) {
        ImGui.pushID(this.tab + "_" + folder.getName());
        String folderName = root ? "View" : folder.getName();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffffb0b0);
        if (ImGui.treeNodeEx(folderName, ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.popStyleColor();
            for (E asset : folder.getAssetsThere()) {
                ImGui.pushID(asset.getName());
                final boolean selected = this.getPreviewWrapperObject() != null && asset.equals(this.getPreviewWrapperObject().getAsset());
                if (ImGui.selectable("./" + asset.getName(), selected)) {
                    this.setPreviewWrapperObject(selected ? null : this.getPreviewInstanceFactory().apply(asset));
                }
                ImGui.popID();
            }
            for (GameResourceAssetsFolder<E> child : folder.getFoldersThere()) {
                this.tree(child, false);
            }
            ImGui.treePop();
        } else {
            ImGui.popStyleColor();
        }

        ImGui.popID();
    }

    private void insides() {
        ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8cff);
        if (ImGui.button("Open Explorer")) {
            this.getOpenFolderAction().accept(null);
        }
        ImGui.popStyleColor();
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
        if (ImGui.button("Refresh")) {
            WBench.get().getGameProjectManager().refreshModelFiles(true);
            this.setPreviewWrapperObject(null);
        }
        ImGui.popStyleColor();
        ImGui.spacing();
        this.tree(this.getGameResourceAssetsFolder().get(), true);
    }

    public void render() {
        if (ImGui.collapsingHeader(this.getTab(), ImGuiTreeNodeFlags.DefaultOpen)) {
            this.insides();
        }
    }

    public Function<E, T> getPreviewInstanceFactory() {
        return this.previewInstanceFactory;
    }

    public Supplier<GameResourceAssetsFolder<E>> getGameResourceAssetsFolder() {
        return this.gameResourceAssetsFolder;
    }

    public T getPreviewWrapperObject() {
        return this.previewWrapperObject;
    }

    public E getCurrentSelectedAsset() {
        return this.previewWrapperObject == null ? null : this.previewWrapperObject.getAsset();
    }

    public void setPreviewWrapperObject(T previewWrapperObject) {
        this.previewWrapperObject = previewWrapperObject;
    }

    public Consumer<Void> getOpenFolderAction() {
        return this.openFolderAction;
    }

    public String getTab() {
        return this.tab;
    }
}
