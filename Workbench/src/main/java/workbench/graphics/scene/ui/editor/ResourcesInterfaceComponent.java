package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.items.TagItem;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.EditorInterface;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ResourcesInterfaceComponent {
    private final EditorInterface editorInterface;

    public ResourcesInterfaceComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.clear();
    }

    public void clear() {

    }

    public void resourcesContent() {
        if (ImGui.collapsingHeader("Entities")) {
            Map<String, Set<WBenchObjectTemplate>> objectTemplates = WBench.get().getProjectObjects().getEntityGroups();
            for (Map.Entry<String, Set<WBenchObjectTemplate>> entry : objectTemplates.entrySet()) {
                String groupName = entry.getKey();
                Set<WBenchObjectTemplate> objects = entry.getValue();

                ImGui.treePush();
                String groupNameTree = groupName != null ? groupName : "Other";
                if (ImGui.treeNode(groupNameTree)) {
                    ImGui.treePush();
                    for (WBenchObjectTemplate object : objects) {
                        boolean flag = this.getEditorInterface().getCurrentSelectedTemplate() == object;
                        if (ImGui.selectable(object.getId(), flag)) {
                            if (!flag) {
                                this.getEditorInterface().setCurrentSelectedTemplate(object);
                                this.getEditorInterface().setPreviewDistance(1.0f);
                            } else {
                                this.getEditorInterface().setCurrentSelectedObject(null);
                            }
                        }
                    }
                    ImGui.treePop();
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
        if (ImGui.collapsingHeader("Props")) {

        }
        if (ImGui.collapsingHeader("Sounds")) {

        }
        if (ImGui.collapsingHeader("Scripts")) {

        }
        ImGui.separator();
    }

    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
