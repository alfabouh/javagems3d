package workbench.graphics.scene.ui.map.editor.scenes.resources;

import imgui.ImGui;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.help.JGemsHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.WBenchPointLightObject;
import workbench.graphics.objects.templates.WBenchMarkerTemplate;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.SelectedScene;
import workbench.graphics.scene.ui.map.editor.utils.CreatableObjectsTreeDrawerM;

public class SceneResourcesObjectsM {
    private final MapEditorInterface mapEditorInterface;
    private final CreatableObjectsTreeDrawerM<WBenchObjectTemplate> props;
    private final CreatableObjectsTreeDrawerM<WBenchObjectTemplate> entities;
    private final CreatableObjectsTreeDrawerM<WBenchMarkerTemplate> markers;

    public SceneResourcesObjectsM(@NotNull MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.props = new CreatableObjectsTreeDrawerM<>(mapEditorInterface, () -> WBench.get().getMapProjectManager().getMapObjectTemplates().getProps(), "Props");
        this.entities = new CreatableObjectsTreeDrawerM<>(mapEditorInterface, () -> WBench.get().getMapProjectManager().getMapObjectTemplates().getEntities(), "Entities");
        this.markers = new CreatableObjectsTreeDrawerM<>(mapEditorInterface, () -> WBench.get().getMapProjectManager().getMapObjectTemplates().getMarkers(), "Markers");
    }

    public void reset() {
    }

    public void render() {
        final boolean flag = this.getEditorInterface().getSelectedScene().equals(SelectedScene.MAIN);
        if (flag) {
            if (ImGui.collapsingHeader("Generate Light")) {
                ImGui.treePush();
                if (ImGui.selectable("Point Light", false)) {
                    ICamera camera = this.getEditorInterface().getOpenGLRenderer().getCamera();
                    Vector3f posToSpawn = camera.getCamPosition();
                    posToSpawn.add(JGemsHelper.math().calcLookVector(camera.getCamRotation()).mul(3.0f));

                    WBenchPointLightObject pointLightObject = WBenchPointLightObject.create("plmarker", this.getEditorInterface().getOpenGLRenderer().getWorld());
                    pointLightObject.setPosition(posToSpawn);
                    this.getEditorInterface().addObjectInWorld(pointLightObject);
                }
                ImGui.treePop();
            }
            ImGui.separator();
            this.entities.render();
        }
        this.props.render();
        this.markers.render();
    }

    /*
    private <T extends WBenchObjectTemplate> void renderObjectGroupsList(@NotNull String unique_prefix, Map<String, MapObjectTemplatesManager.TemplatesTable<T>> tableMap) {
        for (Map.Entry<String, MapObjectTemplatesManager.TemplatesTable<T>> entry : tableMap.entrySet()) {
            String groupName = entry.getKey();
            Collection<T> objects = entry.getValue().getTemplateMap().values();

            ImGui.treePush();
            String groupNameTree = (groupName != null ? groupName : "Other");
            if (ImGui.treeNode(unique_prefix, groupNameTree)) {
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
    */

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
