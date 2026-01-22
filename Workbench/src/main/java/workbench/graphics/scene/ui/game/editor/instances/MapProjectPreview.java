package workbench.graphics.scene.ui.game.editor.instances;

import workbench.project.managing.instances.GameResourceMapAsset;

public final class MapProjectPreview implements IPreviewWrapperObject<GameResourceMapAsset> {
    private final GameResourceMapAsset mapProjectData;

    public MapProjectPreview(GameResourceMapAsset mapProjectData) {
        this.mapProjectData = mapProjectData;
    }

    @Override
    public GameResourceMapAsset getAsset() {
        return this.mapProjectData;
    }
}
