package workbench.graphics.scene.ui.game.editor.instances;

import javagems3d.JGems3D;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import workbench.project.managing.WBenchGameResourcesManager;

public class TexturePreviewAsset {
    private final WBenchGameResourcesManager.TextureAsset textureAsset;

    public TexturePreviewAsset(WBenchGameResourcesManager.TextureAsset textureAsset) {
        this.textureAsset = textureAsset;
    }

    public WBenchGameResourcesManager.TextureAsset getTextureAsset() {
        return this.textureAsset;
    }
}
