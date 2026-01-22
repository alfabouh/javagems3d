package workbench.graphics.scene.ui.game.editor.instances;

import workbench.project.managing.instances.GameResourceTextureAsset;

public final class TextureAssetPreview implements IPreviewWrapperObject<GameResourceTextureAsset> {
    private final GameResourceTextureAsset textureAsset;

    public TextureAssetPreview(GameResourceTextureAsset textureAsset) {
        this.textureAsset = textureAsset;
    }

    public GameResourceTextureAsset getAsset() {
        return this.textureAsset;
    }
}
