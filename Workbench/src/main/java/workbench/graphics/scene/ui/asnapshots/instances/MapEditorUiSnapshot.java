package workbench.graphics.scene.ui.asnapshots.instances;

import javagems3d.graphics.rendering.ui.snapshots.ISnapshot;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class MapEditorUiSnapshot implements ISnapshot<MapEditorInterface> {
    public final MapEditorInterface.MapEditorInterfaceSnapshotData mapEditorInterfaceSnapshotData;

    public MapEditorUiSnapshot(MapEditorInterface.MapEditorInterfaceSnapshotData mapEditorInterfaceSnapshotData) {
        this.mapEditorInterfaceSnapshotData = mapEditorInterfaceSnapshotData;
    }

    @Override
    public void fix(MapEditorInterface mapEditorInterface) {
        mapEditorInterface.fixSnapshot(this.mapEditorInterfaceSnapshotData);
    }
}
