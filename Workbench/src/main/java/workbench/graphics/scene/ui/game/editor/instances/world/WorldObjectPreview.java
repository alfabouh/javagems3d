package workbench.graphics.scene.ui.game.editor.instances.world;

import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import workbench.project.managing.instances.world.GameResourceWorldObjectAsset;

public abstract class WorldObjectPreview <T extends GameResourceWorldObjectAsset> implements IPreviewWrapperObject<T> {
    private final T gameResourcePropObjectAsset;

    public WorldObjectPreview(T gameResourcePropObjectAsset) {
        this.gameResourcePropObjectAsset = gameResourcePropObjectAsset;
    }

    @Override
    public T getAsset() {
        return this.gameResourcePropObjectAsset;
    }
}
