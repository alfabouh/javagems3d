package workbench.graphics.scene.ui.game.editor.instances;

import workbench.project.managing.instances.GameResourcePropObjectAsset;

public class ObjectPropPreview implements IPreviewWrapperObject<GameResourcePropObjectAsset> {
    private final GameResourcePropObjectAsset gameResourcePropObjectAsset;

    public ObjectPropPreview(GameResourcePropObjectAsset gameResourcePropObjectAsset) {
        this.gameResourcePropObjectAsset = gameResourcePropObjectAsset;
    }

    @Override
    public GameResourcePropObjectAsset getAsset() {
        return this.gameResourcePropObjectAsset;
    }
}
