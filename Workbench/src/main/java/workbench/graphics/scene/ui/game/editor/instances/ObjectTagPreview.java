package workbench.graphics.scene.ui.game.editor.instances;

import workbench.project.managing.instances.GameResourceObjectTagData;

public class ObjectTagPreview implements IPreviewWrapperObject<GameResourceObjectTagData> {
    private final GameResourceObjectTagData gameResourceObjectTagData;

    public ObjectTagPreview(GameResourceObjectTagData gameResourceObjectTagData) {
        this.gameResourceObjectTagData = gameResourceObjectTagData;
    }

    public GameResourceObjectTagData getAsset() {
        return this.gameResourceObjectTagData;
    }
}
