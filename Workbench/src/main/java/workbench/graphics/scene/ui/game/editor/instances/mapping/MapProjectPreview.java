package workbench.graphics.scene.ui.game.editor.instances.mapping;

import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import workbench.project.managing.instances.mapping.WBenchResourceMapAsset;

public final class MapProjectPreview implements IPreviewWrapperObject<WBenchResourceMapAsset> {
    private final WBenchResourceMapAsset mapProjectData;

    public MapProjectPreview(WBenchResourceMapAsset mapProjectData) {
        this.mapProjectData = mapProjectData;
    }

    @Override
    public WBenchResourceMapAsset getAsset() {
        return this.mapProjectData;
    }
}
