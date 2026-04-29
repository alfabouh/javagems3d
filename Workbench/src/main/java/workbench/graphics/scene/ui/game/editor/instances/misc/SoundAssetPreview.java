package workbench.graphics.scene.ui.game.editor.instances.misc;

import javagems3d.system.external.gaming.def.misc.GameResourceSoundAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceTextureAsset;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;

public final class SoundAssetPreview implements IPreviewWrapperObject<GameResourceSoundAsset> {
    private final GameResourceSoundAsset soundAsset;

    public SoundAssetPreview(GameResourceSoundAsset soundAsset) {
        this.soundAsset = soundAsset;
    }

    public GameResourceSoundAsset getAsset() {
        return this.soundAsset;
    }
}
