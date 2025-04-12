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
        for (SceneObject wBenchObject : this.getEditorInterface().getOpenGLRenderer().getWorld().getSceneObjects()) {
            WBenchObject wBenchObject1 = (WBenchObject) wBenchObject;
            boolean flag = this.getEditorInterface().getCurrentSelectedObject() == wBenchObject1;
            float x = ImGui.getContentRegionAvailX() - 30f;
            ImGui.pushID(wBenchObject1.getId());
            Vector3f color = wBenchObject1.textInMenuColor();
            ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, 1.0f);

            String fullText = wBenchObject1.toString(false);
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
                    this.getEditorInterface().setCurrentSelectedObject(wBenchObject1);
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
