package workbench.graphics.scene.ui.game.editor.utils;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import javagems3d.system.service.files.AbstractObjectsFolder;
import javagems3d.system.external.gaming.def.IAsset;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FolderResourcesTreeDrawerG<E extends IAsset, T extends IPreviewWrapperObject<E>> {
    private final String tab;
    private final Consumer<Void> openFolderAction;
    private T previewWrapperObject;
    private final Supplier<AbstractObjectsFolder<E>> gameResourceAssetsFolder;
    private final Function<E, T> previewInstanceFactory;
    private final @Nullable Consumer<Void> onRefreshButton;

    public FolderResourcesTreeDrawerG(@NotNull Supplier<AbstractObjectsFolder<E>> gameResourceAssetsFolder, @NotNull String tab, @Nullable Consumer<Void> onRefreshButton, @NotNull Consumer<Void> openFolderAction, @NotNull Function<E, T> previewInstanceFactory) {
        this.tab = tab;
        this.openFolderAction = openFolderAction;
        this.gameResourceAssetsFolder = gameResourceAssetsFolder;
        this.previewInstanceFactory = previewInstanceFactory;
        this.onRefreshButton = onRefreshButton;
    }

    private void tree(AbstractObjectsFolder<E> folder, boolean root) {
        ImGui.pushID(this.tab + "_" + folder.getName());
        String folderName = root ? "View" : folder.getName();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffffb0b0);
        if (ImGui.treeNodeEx(folderName, ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.popStyleColor();
            for (E asset : folder.getObjectsThere()) {
                ImGui.pushID(asset.getName());
                final boolean selected = this.getPreviewWrapperObject() != null && asset.equals(this.getPreviewWrapperObject().getAsset());
                if (ImGui.selectable("./" + asset.getName(), selected)) {
                    this.setPreviewWrapperObject(selected ? null : this.getPreviewInstanceFactory().apply(asset));
                }
                ImGui.popID();
            }
            for (AbstractObjectsFolder<E> child : folder.getFoldersThere()) {
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
        if (this.onRefreshButton != null) {
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff8c8cff);
            if (ImGui.button("Refresh")) {
                this.onRefreshButton.accept(null);
                this.setPreviewWrapperObject(null);
            }
            ImGui.popStyleColor();
        }
        ImGui.spacing();
        this.tree(this.getGameResourceAssetsFolder().get(), true);
    }

    public void render() {
        if (ImGui.collapsingHeader(this.getTab(), ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##ResChild_" + this.tab, ImGui.getColumnWidth(), ImGui.getWindowHeight() * 0.5f, true, ImGuiWindowFlags.HorizontalScrollbar);
            this.insides();
            ImGui.endChild();
        }
    }

    public Function<E, T> getPreviewInstanceFactory() {
        return this.previewInstanceFactory;
    }

    public Supplier<AbstractObjectsFolder<E>> getGameResourceAssetsFolder() {
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
