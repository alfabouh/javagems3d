package workbench.graphics.scene.ui.asnapshots.instances;

import javagems3d.graphics.rendering.ui.snapshots.ISnapshot;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public record MapEditorUiSnapshot(
        MapEditorInterface.MapEditorInterfaceSnapshotData mapEditorInterfaceSnapshotData) implements ISnapshot<MapEditorInterface> {

    @Override
    public void fix(MapEditorInterface mapEditorInterface) {
        mapEditorInterface.fixSnapshot(this.mapEditorInterfaceSnapshotData);
    }
}
