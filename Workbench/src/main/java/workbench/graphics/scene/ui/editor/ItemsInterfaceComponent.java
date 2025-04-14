package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiSelectableFlags;
import javagems3d.graphics.objects.SceneObject;
import org.joml.Vector3f;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.ui.EditorInterface;

public class ItemsInterfaceComponent {
    private final EditorInterface editorInterface;

    public ItemsInterfaceComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.clear();
    }

    public void clear() {
    }

    public void itemsContent() {
        for (WBenchObject wBenchObject : this.getEditorInterface().setOfSceneObjects()) {
            boolean flag = this.getEditorInterface().getCurrentSelectedObject() == wBenchObject;
            float x = ImGui.getContentRegionAvailX() - 30f;
            ImGui.pushID(wBenchObject.getId());
            Vector3f color = wBenchObject.textInMenuColor();
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, 1.0f);

            String fullText = wBenchObject.toString(false);
            String displayText = fullText;
            float textWidth = ImGui.calcTextSize(displayText).x;
            float maxWidth = Math.max(ImGui.getContentRegionAvailX() - 30.0f, 0.0f);

            float ratio = textWidth / maxWidth;

            if (ratio > 1.0f) {
                int endIndex = (int) (fullText.length() / (ratio + 0.1f));
                endIndex = Math.max(endIndex, 0);
                displayText = fullText.substring(0, endIndex);
            }
            if (ImGui.selectable(displayText, flag, ImGuiSelectableFlags.AllowItemOverlap, x, 18f)) {
                if (!flag) {
                    this.getEditorInterface().setCurrentSelectedObject(wBenchObject);
                    this.getEditorInterface().setCurrentOperation(this.getEditorInterface().chooseDefaultGuizmoOperation());
                } else {
                    this.getEditorInterface().setCurrentSelectedObject(null);
                }
            }
            if (ImGui.isItemHovered() && !displayText.equals(fullText)) {
                ImGui.setTooltip(fullText);
            }
            ImGui.popStyleColor();
            ImGui.sameLine();
            if (ImGui.button("X")) {
                if (wBenchObject.equals(this.getEditorInterface().getCurrentSelectedObject())) {
                    this.getEditorInterface().setCurrentSelectedObject(null);
                }
                this.getEditorInterface().removeObjectFromWorld(wBenchObject);
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("id: " + wBenchObject.getId());
            }
            ImGui.popID();
        }
    }

    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
