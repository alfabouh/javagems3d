package workbench.graphics.scene.ui.game.editor.instances;

import javagems3d.system.external.gaming.def.IAsset;

public interface IPreviewWrapperObject<I extends IAsset> {
    I getAsset();
}
