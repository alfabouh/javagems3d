package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.help.JGemsUtils;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.WBenchPointLightObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.EditorInterface;

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
        if (ImGui.collapsingHeader("Generate Light")) {
            ImGui.treePush();
            if (ImGui.selectable("Point Light", false)) {
                ICamera camera = this.getEditorInterface().getOpenGLRenderer().getCamera();
                Vector3f posToSpawn = camera.getCamPosition();
                posToSpawn.add(JGemsUtils.calcLookVector(camera.getCamRotation()).mul(3.0f));

                WBenchPointLightObject pointLightObject = WBenchPointLightObject.create("point_light_marker", this.getEditorInterface().getOpenGLRenderer().getWorld());
                pointLightObject.setId(this.getEditorInterface().getOpenGLRenderer().getWorld().getSceneObjects().size());
                pointLightObject.setPosition(posToSpawn);
                this.getEditorInterface().getOpenGLRenderer().getWorld().addObjectInWorld(pointLightObject);
            }
            ImGui.treePop();
        }
        ImGui.separator();
        if (ImGui.collapsingHeader("Entities")) {
            this.renderObjectGroupsList(WBench.get().getProjectObjects().getEntityGroups());
        }
        if (ImGui.collapsingHeader("Props")) {
            this.renderObjectGroupsList(WBench.get().getProjectObjects().getPropGroups());
        }
        if (ImGui.collapsingHeader("Markers")) {
            this.renderObjectGroupsList(WBench.get().getProjectObjects().getMarkerGroups());
        }
        if (ImGui.collapsingHeader("Scripts")) {

        }
        ImGui.separator();
    }

    private <T extends WBenchObjectTemplate> void renderObjectGroupsList(Map<String, Set<T>> map) {
        for (Map.Entry<String, Set<T>> entry : map.entrySet()) {
            String groupName = entry.getKey();
            Set<T> objects = entry.getValue();

            ImGui.treePush();
            String groupNameTree = groupName != null ? groupName : "Other";
            if (ImGui.treeNode(groupNameTree)) {
                ImGui.treePush();
                for (T object : objects) {
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


    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
