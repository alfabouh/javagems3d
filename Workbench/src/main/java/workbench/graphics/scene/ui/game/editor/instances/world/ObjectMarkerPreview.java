package workbench.graphics.scene.ui.game.editor.instances.world;

import javagems3d.system.external.gaming.def.world.GameResourceMarkerObjectAsset;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;

public class ObjectMarkerPreview implements IPreviewWrapperObject<GameResourceMarkerObjectAsset> {
    private final GameResourceMarkerObjectAsset gameResourceMarkerObjectAsset;

    public ObjectMarkerPreview(GameResourceMarkerObjectAsset gameResourceMarkerObjectAsset) {
        this.gameResourceMarkerObjectAsset = gameResourceMarkerObjectAsset;
    }

    @Override
    public GameResourceMarkerObjectAsset getAsset() {
        return this.gameResourceMarkerObjectAsset;
    }
}
