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

package workbench.graphics.environment.components.light;

import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class WBenchSunLight extends SunLight implements ISnapshotCompatible<WBenchSunLight.WBenchSunLightSnapshotData> {
    public WBenchSunLight(@NotNull Vector3f sunPos, @NotNull Vector3f sunColor, float sunBrightness) {
        super(sunPos, sunColor, sunBrightness);
    }

    @Override
    public WBenchSunLightSnapshotData takeSnapshot() {
        return new WBenchSunLightSnapshotData(this.getSunBrightness(), this.getOffset(), this.getLightColor(), this.getLightPosition(), this.isActive());
    }

    @Override
    public void fixSnapshot(WBenchSunLightSnapshotData wBenchSunLightSnapshotData) {
        this.setSunBrightness(wBenchSunLightSnapshotData.sunBrightness);
        this.setLightColor(wBenchSunLightSnapshotData.lightColor);
        this.setLightPosition(wBenchSunLightSnapshotData.lightPos);
        this.setOffset(wBenchSunLightSnapshotData.offset);
    }

    public record WBenchSunLightSnapshotData(float sunBrightness, Vector3f offset, Vector3f lightColor,
                                             Vector3f lightPos, boolean isActive) implements SnapshotData {
    }
}
