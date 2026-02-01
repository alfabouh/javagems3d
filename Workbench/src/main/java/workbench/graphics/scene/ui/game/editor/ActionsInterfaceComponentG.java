package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import workbench.graphics.scene.ui.game.editor.scenes.mapping.ScenePreviewMapG;
import workbench.graphics.scene.ui.game.editor.scenes.mapping.ScenePreviewSkyBoxG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewModelG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewTagG;
import workbench.graphics.scene.ui.game.editor.scenes.misc.ScenePreviewTextureG;
import workbench.graphics.scene.ui.game.editor.scenes.world.ScenePreviewWorldObjectG;
import workbench.project.managing.instances.world.GameResourceEntityObjectAsset;
import workbench.project.managing.instances.world.GameResourcePropObjectAsset;

public class ActionsInterfaceComponentG {
    private final ScenePreviewMapG scenePreviewMapG;
    private final ScenePreviewModelG scenePreviewModelG;
    private final ScenePreviewWorldObjectG<GameResourcePropObjectAsset> scenePreviewPropObjectG;
    private final ScenePreviewWorldObjectG<GameResourceEntityObjectAsset> scenePreviewEntityObjectG;
    private final ScenePreviewTextureG scenePreviewTextureG;
    private final ScenePreviewTagG scenePreviewTagG;
    private final ScenePreviewSkyBoxG scenePreviewSkyBoxG;

    public ActionsInterfaceComponentG(ResourcesInterfaceComponentG resourcesInterfaceComponentG) {
        this.scenePreviewMapG = new ScenePreviewMapG(resourcesInterfaceComponentG);
        this.scenePreviewModelG = new ScenePreviewModelG(resourcesInterfaceComponentG);
        this.scenePreviewPropObjectG = new ScenePreviewWorldObjectG<>("Prop", () -> resourcesInterfaceComponentG.getPropResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewEntityObjectG = new ScenePreviewWorldObjectG<>("Entity", () -> resourcesInterfaceComponentG.getEntityResourceTreeDrawer().getCurrentSelectedAsset(), resourcesInterfaceComponentG);
        this.scenePreviewTextureG = new ScenePreviewTextureG(resourcesInterfaceComponentG);
        this.scenePreviewTagG = new ScenePreviewTagG(resourcesInterfaceComponentG);
        this.scenePreviewSkyBoxG = new ScenePreviewSkyBoxG(resourcesInterfaceComponentG);
    }

    public void actionsContent() {
        this.scenePreviewMapG.render();
        this.scenePreviewModelG.render();
        this.scenePreviewTagG.render();
        this.scenePreviewPropObjectG.render();
        this.scenePreviewEntityObjectG.render();
        this.scenePreviewTextureG.render();
        this.scenePreviewSkyBoxG.render();
        ImGui.dummy(0.0f, 20.0f);
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
