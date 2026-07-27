/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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