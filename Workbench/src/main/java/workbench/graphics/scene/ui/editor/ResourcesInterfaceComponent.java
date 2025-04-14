package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.help.JGemsUtils;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.WBenchPointLightObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.EditorInterface;
import workbench.project.ProjectTemplates;

import java.util.Collection;
import java.util.Map;

public class ResourcesInterfaceComponent {
    private final EditorInterface editorInterface;

    public ResourcesInterfaceComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.clear();
    }

    public void clear() {

    }

    public void resourcesContent() {
        if (this.getEditorInterface().getSelectedScene().equals(SelectedScene.MAIN)) {
            if (ImGui.collapsingHeader("Generate Light")) {
                ImGui.treePush();
                if (ImGui.selectable("Point Light", false)) {
                    ICamera camera = this.getEditorInterface().getOpenGLRenderer().getCamera();
                    Vector3f posToSpawn = camera.getCamPosition();
                    posToSpawn.add(JGemsUtils.calcLookVector(camera.getCamRotation()).mul(3.0f));

                    WBenchPointLightObject pointLightObject = WBenchPointLightObject.create("plmarker", this.getEditorInterface().getOpenGLRenderer().getWorld());
                    pointLightObject.setPosition(posToSpawn);
                    this.getEditorInterface().addObjectInWorld(pointLightObject);
                }
                ImGui.treePop();
            }
            ImGui.separator();
            if (ImGui.collapsingHeader("Entities")) {
                this.renderObjectGroupsList(WBench.get().getProjectObjects().getEntityGroups());
            }
            if (ImGui.collapsingHeader("Markers")) {
                this.renderObjectGroupsList(WBench.get().getProjectObjects().getMarkerGroups());
            }
        }
        if (ImGui.collapsingHeader("Props")) {
            this.renderObjectGroupsList(WBench.get().getProjectObjects().getPropGroups());
        }
        ImGui.newLine();
        if (ImGui.collapsingHeader("Scripts")) {

        }
        ImGui.separator();
    }

    private <T extends WBenchObjectTemplate> void renderObjectGroupsList(Map<String, ProjectTemplates.TemplatesTable<T>> tableMap) {
        for (Map.Entry<String, ProjectTemplates.TemplatesTable<T>> entry : tableMap.entrySet()) {
            String groupName = entry.getKey();
            Collection<T> objects = entry.getValue().getTemplateMap().values();

            ImGui.treePush();
            String groupNameTree = groupName != null ? groupName : "Other";
            if (ImGui.treeNode(groupNameTree)) {
                ImGui.treePush();
                for (T object : objects) {
                    boolean flag = this.getEditorInterface().getCurrentSelectedTemplate() == object;
                    if (ImGui.selectable(object.getObjectId().getNameId(), flag)) {
                        if (!flag) {
                            this.getEditorInterface().setCurrentSelectedTemplate(object);
                            this.getEditorInterface().setPreviewDistance(1.0f);
                        } else {
                            this.getEditorInterface().setCurrentSelectedTemplate(null);
                        }
                    }
                }
                ImGui.treePop();
                ImGui.treePop();
            }
            ImGui.treePop();
        }
    }


    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
