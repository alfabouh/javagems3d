package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.scenes.resources.SceneResourcesObjectsM;
import workbench.graphics.scene.ui.map.editor.scenes.resources.SceneResourcesScriptsM;

public class ResourcesInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final SceneResourcesScriptsM sceneResourcesScriptsM;
    private final SceneResourcesObjectsM sceneResourcesObjectsM;

    public ResourcesInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.sceneResourcesScriptsM = new SceneResourcesScriptsM(mapEditorInterface);
        this.sceneResourcesObjectsM = new SceneResourcesObjectsM(mapEditorInterface);
    }

    public void clear() {
        this.getSceneResourcesScriptsM().reset();
        this.getSceneResourcesObjectsM().reset();
    }

    public void resourcesContent() {
        this.getSceneResourcesObjectsM().render();
        ImGui.separator();
        this.getSceneResourcesScriptsM().render();
    }

    public SceneResourcesObjectsM getSceneResourcesObjectsM() {
        return this.sceneResourcesObjectsM;
    }

    public SceneResourcesScriptsM getSceneResourcesScriptsM() {
        return this.sceneResourcesScriptsM;
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
