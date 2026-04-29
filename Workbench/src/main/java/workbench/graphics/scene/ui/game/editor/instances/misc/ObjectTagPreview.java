package workbench.graphics.scene.ui.game.editor.instances.misc;

import workbench.graphics.scene.ui.game.editor.instances.IPreviewWrapperObject;
import javagems3d.system.external.gaming.def.misc.GameResourceObjectTagData;

public class ObjectTagPreview implements IPreviewWrapperObject<GameResourceObjectTagData> {
    private final GameResourceObjectTagData gameResourceObjectTagData;

    public ObjectTagPreview(GameResourceObjectTagData gameResourceObjectTagData) {
        this.gameResourceObjectTagData = gameResourceObjectTagData;
    }

    public GameResourceObjectTagData getAsset() {
        return this.gameResourceObjectTagData;
    }
}
