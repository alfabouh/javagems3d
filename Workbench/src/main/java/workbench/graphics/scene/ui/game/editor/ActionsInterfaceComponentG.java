package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import javagems3d.system.external.gaming.def.world.GameResourceMarkerObjectAsset;
import workbench.graphics.scene.ui.game.editor.scenes.mapping.ScenePreviewMapG;
import workbench.graphics.scene.ui.game.editor.scenes.mapping.ScenePreviewSkyBoxG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewModelG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewTagG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewTextureG;
import workbench.graphics.scene.ui.game.editor.scenes.scripting.ScenePreviewScriptG;
import workbench.graphics.scene.ui.game.editor.scenes.world.ScenePreviewMarkerObjectG;
import workbench.graphics.scene.ui.game.editor.scenes.world.ScenePreviewWorldObjectG;
import javagems3d.system.external.gaming.def.world.GameResourceEntityObjectAsset;
import javagems3d.system.external.gaming.def.world.GameResourcePropObjectAsset;

public class ActionsInterfaceComponentG {
    private final ScenePreviewMapG scenePreviewMapG;
    private final ScenePreviewModelG scenePreviewModelG;
    private final ScenePreviewWorldObjectG<GameResourcePropObjectAsset> scenePreviewPropObjectG;
    private final ScenePreviewWorldObjectG<GameResourceEntityObjectAsset> scenePreviewEntityObjectG;
    private final ScenePreviewMarkerObjectG<GameResourceMarkerObjectAsset> scenePreviewMarkerObjectG;
    private final ScenePreviewTextureG scenePreviewTextureG;
    private final ScenePreviewTagG scenePreviewTagG;
    private final ScenePreviewSkyBoxG scenePreviewSkyBoxG;
    private final ScenePreviewScriptG scenePreviewScriptG;

    public ActionsInterfaceComponentG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.scenePreviewMapG = new ScenePreviewMapG(resourcesInterfaceComponentG);
        this.scenePreviewModelG = new ScenePreviewModelG(resourcesInterfaceComponentG);
        this.scenePreviewPropObjectG = new ScenePreviewWorldObjectG<>("Prop", () -> resourcesInterfaceComponentG.getPropResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewEntityObjectG = new ScenePreviewWorldObjectG<>("Entity", () -> resourcesInterfaceComponentG.getEntityResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewMarkerObjectG = new ScenePreviewMarkerObjectG<>("Marker", () -> resourcesInterfaceComponentG.getMarkerResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewTextureG = new ScenePreviewTextureG(resourcesInterfaceComponentG);
        this.scenePreviewTagG = new ScenePreviewTagG(resourcesInterfaceComponentG);
        this.scenePreviewSkyBoxG = new ScenePreviewSkyBoxG(resourcesInterfaceComponentG);
        this.scenePreviewScriptG = new ScenePreviewScriptG(resourcesInterfaceComponentG);
    }

    public void actionsContent() {
        this.scenePreviewMapG.render();
        this.scenePreviewScriptG.render();
        this.scenePreviewModelG.render();
        this.scenePreviewTagG.render();
        this.scenePreviewPropObjectG.render();
        this.scenePreviewEntityObjectG.render();
        this.scenePreviewMarkerObjectG.render();
        this.scenePreviewTextureG.render();
        this.scenePreviewSkyBoxG.render();
        ImGui.dummy(0.0f, 20.0f);
    }

    public ScenePreviewScriptG getScenePreviewScriptG() {
        return this.scenePreviewScriptG;
    }

    public ScenePreviewSkyBoxG getScenePreviewSkyBoxG() {
        return this.scenePreviewSkyBoxG;
    }

    public ScenePreviewTagG getScenePreviewTagG() {
        return this.scenePreviewTagG;
    }

    public ScenePreviewMapG getScenePreviewMapG() {
        return this.scenePreviewMapG;
    }

    public ScenePreviewModelG getScenePreviewModelG() {
        return this.scenePreviewModelG;
    }

    public ScenePreviewWorldObjectG<GameResourcePropObjectAsset> getScenePreviewPropObjectG() {
        return this.scenePreviewPropObjectG;
    }

    public ScenePreviewWorldObjectG<GameResourceEntityObjectAsset> getScenePreviewEntityObjectG() {
        return this.scenePreviewEntityObjectG;
    }

    public ScenePreviewTextureG getScenePreviewTextureG() {
        return this.scenePreviewTextureG;
    }
}
