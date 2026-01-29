package workbench.graphics.scene.ui.game.editor.instances.misc;

import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import workbench.project.managing.instances.misc.GameResourceTextureAsset;

public final class TextureAssetPreview implements IPreviewWrapperObject<GameResourceTextureAsset> {
    private final GameResourceTextureAsset textureAsset;

    public TextureAssetPreview(GameResourceTextureAsset textureAsset) {
        this.textureAsset = textureAsset;
    }

    public GameResourceTextureAsset getAsset() {
        return this.textureAsset;
    }
}
