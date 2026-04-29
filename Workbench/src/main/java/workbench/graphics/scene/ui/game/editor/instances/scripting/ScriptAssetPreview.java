package workbench.graphics.scene.ui.game.editor.instances.scripting;

import javagems3d.system.external.gaming.def.misc.GameResourceScriptAsset;
import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;

public final class ScriptAssetPreview implements IPreviewWrapperObject<GameResourceScriptAsset> {
    private final GameResourceScriptAsset scriptAsset;

    public ScriptAssetPreview(GameResourceScriptAsset scriptAsset) {
        this.scriptAsset = scriptAsset;
    }

    @Override
    public GameResourceScriptAsset getAsset() {
        return this.scriptAsset;
    }
}
