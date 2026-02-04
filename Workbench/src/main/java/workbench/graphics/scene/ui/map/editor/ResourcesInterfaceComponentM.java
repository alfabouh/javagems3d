package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.scenes.resources.InterfaceResourcesObjectsM;
import workbench.graphics.scene.ui.map.editor.scenes.resources.InterfaceResourcesScriptsM;

public class ResourcesInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final InterfaceResourcesScriptsM interfaceResourcesScriptsM;
    private final InterfaceResourcesObjectsM interfaceResourcesObjectsM;

    public ResourcesInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.interfaceResourcesScriptsM = new InterfaceResourcesScriptsM(mapEditorInterface);
        this.interfaceResourcesObjectsM = new InterfaceResourcesObjectsM(mapEditorInterface);
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

    public InterfaceResourcesObjectsM getSceneResourcesObjectsM() {
        return this.interfaceResourcesObjectsM;
    }

    public InterfaceResourcesScriptsM getSceneResourcesScriptsM() {
        return this.interfaceResourcesScriptsM;
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
