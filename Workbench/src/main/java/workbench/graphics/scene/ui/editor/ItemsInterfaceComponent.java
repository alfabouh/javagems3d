package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiSelectableFlags;
import javagems3d.graphics.objects.SceneObject;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.EditorInterface;

import java.util.Map;
import java.util.Set;

public class ItemsInterfaceComponent {
    private final EditorInterface editorInterface;

    public ItemsInterfaceComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.clear();
    }

    public void clear() {
    }

    public void itemsContent() {
        for (SceneObject wBenchObject : this.getEditorInterface().getOpenGLRenderer().getWorld().getSceneObjects()) {
            WBenchObject wBenchObject1 = (WBenchObject) wBenchObject;
            boolean flag = this.getEditorInterface().getCurrentSelectedObject() == wBenchObject1;
            float x = ImGui.getContentRegionAvailX() - 30f;
            ImGui.pushID(wBenchObject1.getId());
            if (ImGui.selectable("(" + wBenchObject1.getId() + ") " + wBenchObject1.getName(), flag, ImGuiSelectableFlags.AllowItemOverlap, x, 18f)) {
                if (!flag) {
                    this.getEditorInterface().setCurrentSelectedObject(wBenchObject1);
                    this.getEditorInterface().setCurrentOperation(this.getEditorInterface().chooseDefaultGuizmoOperation());
                } else {
                    this.getEditorInterface().setCurrentSelectedObject(null);
                }
            }
            ImGui.sameLine();
            if (ImGui.button("X")) {
                if (wBenchObject1.equals(this.getEditorInterface().getCurrentSelectedObject())) {
                    this.getEditorInterface().setCurrentSelectedObject(null);
                }
                wBenchObject1.setDead();
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("id: " + wBenchObject1.getId());
            }
            ImGui.popID();
        }
    }

    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
