package workbench.graphics.scene.ui.asnapshots.instances;

import javagems3d.graphics.rendering.ui.snapshots.instances.SnapshotsContainer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchSnapshotsContainer extends SnapshotsContainer {
    public final MapEditorWorldSnapshot mapEditorWorldSnapshot;
    public final MapEditorUiSnapshot mapEditorUiSnapshot;
    private final MapEditorInterface mapEditorInterface;
    private final WBenchWorld world;

    public WBenchSnapshotsContainer(MapEditorInterface mapEditorInterface, WBenchWorld world, MapEditorWorldSnapshot mapEditorWorldSnapshot, MapEditorUiSnapshot mapEditorUiSnapshot) {
        this.mapEditorInterface = mapEditorInterface;
        this.world = world;
        this.mapEditorWorldSnapshot = mapEditorWorldSnapshot;
        this.mapEditorUiSnapshot = mapEditorUiSnapshot;
    }

    public MapEditorWorldSnapshot getMapEditorWorldSnapshot() {
        return this.mapEditorWorldSnapshot;
    }

    public MapEditorUiSnapshot getMapEditorUiSnapshot() {
        return this.mapEditorUiSnapshot;
    }

    @Override
    public void fixAll() {
        this.getMapEditorWorldSnapshot().fix(this.world);
        this.getMapEditorUiSnapshot().fix(this.mapEditorInterface);
    }
}
