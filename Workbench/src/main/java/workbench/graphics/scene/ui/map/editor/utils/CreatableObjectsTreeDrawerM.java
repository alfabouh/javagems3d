package workbench.graphics.scene.ui.map.editor.utils;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.system.service.collections.AbstractObjectsFolder;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.objects.templates.WBenchTemplate;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.project.map.MapObjectTemplatesFolder;

import java.util.function.Supplier;

public class CreatableObjectsTreeDrawerM <T extends WBenchObjectTemplate> {
    private final MapEditorInterface mapEditorInterface;
    private final Supplier<MapObjectTemplatesFolder<T>> folderSupplier;
    private final String tab;

    public CreatableObjectsTreeDrawerM(MapEditorInterface mapEditorInterface, Supplier<MapObjectTemplatesFolder<T>> folderSupplier, String tab) {
        this.mapEditorInterface = mapEditorInterface;
        this.folderSupplier = folderSupplier;
        this.tab = tab;
    }

    private void tree(AbstractObjectsFolder<T> folder, boolean root) {
        ImGui.pushID(this.tab + "_" + folder.getName());
        String folderName = root ? "View" : folder.getName();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffffb0b0);
        if (ImGui.treeNodeEx(folderName, ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.popStyleColor();
            for (T t : folder.getObjectsThere()) {
                ImGui.pushID(t.getName());
                final boolean selected = this.getMapEditorInterface().getCurrentSelectedTemplate() != null && this.getMapEditorInterface().getCurrentSelectedTemplate().equals(t);
                if (ImGui.selectable("./" + t.getName(), selected)) {
                    if (selected) {
                        this.getMapEditorInterface().setCurrentSelectedTemplate(null);
                    } else {
                        this.getMapEditorInterface().getActionsContent().resetTemplatePreview();
                        this.getMapEditorInterface().setCurrentSelectedTemplate(t);
                    }
                }
                ImGui.popID();
            }
            for (AbstractObjectsFolder<T> child : folder.getFoldersThere()) {
                this.tree(child, false);
            }
            ImGui.treePop();
        } else {
            ImGui.popStyleColor();
        }

        ImGui.popID();
    }

    public void render() {
        if (ImGui.collapsingHeader(this.getTab(), ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##MResChild_" + this.tab, ImGui.getColumnWidth(), ImGui.getWindowHeight() * 0.3f, true, ImGuiWindowFlags.HorizontalScrollbar);
            this.tree(this.folderSupplier.get(), true);
            ImGui.endChild();
        }
    }

    public MapEditorInterface getMapEditorInterface() {
        return this.mapEditorInterface;
    }

    public Supplier<MapObjectTemplatesFolder<T>> getFolderSupplier() {
        return this.folderSupplier;
    }

    public String getTab() {
        return this.tab;
    }
}
