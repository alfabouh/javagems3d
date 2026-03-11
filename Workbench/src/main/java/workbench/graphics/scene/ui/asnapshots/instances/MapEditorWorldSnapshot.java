package workbench.graphics.scene.ui.asnapshots.instances;

import javagems3d.graphics.rendering.ui.snapshots.ISnapshot;
import workbench.graphics.scene.world.WBenchWorld;

public record MapEditorWorldSnapshot(
        WBenchWorld.WBenchWorldSnapshotData wBenchWorldSnapshotData) implements ISnapshot<WBenchWorld> {

    @Override
    public void fix(WBenchWorld world) {
        world.fixSnapshot(this.wBenchWorldSnapshotData);
    }
}
