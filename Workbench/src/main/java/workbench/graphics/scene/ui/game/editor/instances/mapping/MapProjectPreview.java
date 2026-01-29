package workbench.graphics.scene.ui.game.editor.instances.mapping;

import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import workbench.project.managing.instances.mapping.GameResourceMapAsset;

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
