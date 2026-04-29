package workbench.graphics.scene.ui.game.editor.instances.mapping;

import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import javagems3d.system.external.gaming.def.misc.GameResourceSkyboxAsset;

public class SkyBoxAssetPreview implements IPreviewWrapperObject<GameResourceSkyboxAsset> {
    private final GameResourceSkyboxAsset gameResourceSkyboxAsset;

    public SkyBoxAssetPreview(GameResourceSkyboxAsset gameResourceSkyboxAsset) {
        this.gameResourceSkyboxAsset = gameResourceSkyboxAsset;
    }

    public GameResourceSkyboxAsset getAsset() {
        return this.gameResourceSkyboxAsset;
    }
}
