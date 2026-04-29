package workbench.graphics.scene.ui.asnapshots;

import javagems3d.graphics.rendering.ui.snapshots.SnapshotsTrace;
import javagems3d.graphics.rendering.ui.snapshots.instances.SnapshotsContainer;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.scene.ui.asnapshots.instances.MapEditorUiSnapshot;
import workbench.graphics.scene.ui.asnapshots.instances.MapEditorWorldSnapshot;
import workbench.graphics.scene.ui.asnapshots.instances.WBenchSnapshotsContainer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.project.game.settings.GameProjectSettings;

public class WBenchSnapshotsTrace extends SnapshotsTrace {
    public WBenchSnapshotsTrace(MapEditorInterface mapEditorInterface, WBenchWorld world) {
        super(() -> new WBenchSnapshotsContainer(mapEditorInterface, world, new MapEditorWorldSnapshot(world.takeSnapshot()), new MapEditorUiSnapshot(mapEditorInterface.takeSnapshot())));
    }

    private void tryToSave() {
        if (WBench.get().getGameProjectManager().gameProjectSettings.mapProjectAutoSaveMode == GameProjectSettings.MapProjectAutoSaveMode.STEPS) {
            if (this.getStepCount() % WBench.get().getGameProjectManager().gameProjectSettings.saveEachStep == 0) {
                WBench.get().getScreen().triggerAutoSave = true;
            }
        }
    }

    @Override
    public void pushSnapshot(@NotNull SnapshotsContainer snapshot) {
        super.pushSnapshot(snapshot);
        this.tryToSave();
    }

    @Override
    public void undo() {
        super.undo();
        this.tryToSave();
    }

    @Override
    public void redo() {
        super.redo();
        this.tryToSave();
    }
}