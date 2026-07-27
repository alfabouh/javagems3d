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
