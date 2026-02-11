package workbench.graphics.scene.ui.asnapshots;

import javagems3d.graphics.rendering.ui.snapshots.SnapshotsTrace;
import workbench.graphics.scene.ui.asnapshots.instances.MapEditorUiSnapshot;
import workbench.graphics.scene.ui.asnapshots.instances.MapEditorWorldSnapshot;
import workbench.graphics.scene.ui.asnapshots.instances.WBenchSnapshotsContainer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchSnapshotsTrace extends SnapshotsTrace {
    public WBenchSnapshotsTrace(MapEditorInterface mapEditorInterface, WBenchWorld world) {
        super(() -> new WBenchSnapshotsContainer(mapEditorInterface, world, new MapEditorWorldSnapshot(world.takeSnapshot()), new MapEditorUiSnapshot(mapEditorInterface.takeSnapshot())));
    }
}