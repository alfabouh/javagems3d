package workbench.graphics.scene.ui.asnapshots.instances;

import javagems3d.graphics.rendering.ui.snapshots.ISnapshot;
import workbench.graphics.scene.world.WBenchWorld;

public class MapEditorWorldSnapshot implements ISnapshot<WBenchWorld> {
    public final WBenchWorld.WBenchWorldSnapshotData wBenchWorldSnapshotData;

    public MapEditorWorldSnapshot(WBenchWorld.WBenchWorldSnapshotData wBenchWorldSnapshotData) {
        this.wBenchWorldSnapshotData = wBenchWorldSnapshotData;
    }

    @Override
    public void fix(WBenchWorld world) {
        world.fixSnapshot(this.wBenchWorldSnapshotData);
    }
}
