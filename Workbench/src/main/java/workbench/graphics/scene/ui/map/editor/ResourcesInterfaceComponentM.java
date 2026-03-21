package workbench.graphics.scene.ui.map.editor;

import imgui.ImGui;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.scenes.resources.InterfaceResourcesObjectsM;

public class ResourcesInterfaceComponentM {
    private final MapEditorInterface mapEditorInterface;
    private final InterfaceResourcesObjectsM interfaceResourcesObjectsM;

    public ResourcesInterfaceComponentM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.interfaceResourcesObjectsM = new InterfaceResourcesObjectsM(mapEditorInterface);
    }

    public void clear() {
        this.getSceneResourcesObjectsM().reset();
    }

    public void resourcesContent() {
        this.getSceneResourcesObjectsM().render();
    }

    public InterfaceResourcesObjectsM getSceneResourcesObjectsM() {
        return this.interfaceResourcesObjectsM;
    }

    public MapEditorInterface getEditorInterface() {
        return this.mapEditorInterface;
    }
}
