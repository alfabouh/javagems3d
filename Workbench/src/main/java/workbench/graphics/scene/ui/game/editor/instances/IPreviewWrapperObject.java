package workbench.graphics.scene.ui.game.editor.instances;

import workbench.project.managing.instances.IAsset;

public interface IPreviewWrapperObject<I extends IAsset> {
    I getAsset();
}
