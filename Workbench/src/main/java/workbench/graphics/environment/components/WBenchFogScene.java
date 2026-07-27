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

package workbench.graphics.environment.components;

import javagems3d.graphics.environment.fog.JGemsFogScene;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.project.map.settings.MapProjectSettings;

public class WBenchFogScene extends JGemsFogScene implements ISnapshotCompatible<WBenchFogScene.WBenchFogSceneSnapshotData> {
    @Override
    public float getFogDensity() {
        return super.getFogDensity();
    }

    @Override
    public WBenchFogSceneSnapshotData takeSnapshot() {
        return new WBenchFogSceneSnapshotData(this.getFogDensity(), new Vector3f(this.getFogColor()));
    }

    @Override
    public void fixSnapshot(WBenchFogSceneSnapshotData wBenchFogSceneSnapshotData) {
        this.setFogDensity(wBenchFogSceneSnapshotData.density);
        this.setFogColor(wBenchFogSceneSnapshotData.color);
    }

    public record WBenchFogSceneSnapshotData(float density, Vector3f color) implements SnapshotData {
    }
}