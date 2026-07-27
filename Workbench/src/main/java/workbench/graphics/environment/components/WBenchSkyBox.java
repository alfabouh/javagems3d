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

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.environment.components.light.WBenchSunLight;

public class WBenchSkyBox extends SkyBox implements ISnapshotCompatible<WBenchSkyBox.WBenchSkyBoxSnapshotData> {
    public WBenchSkyBox(IWorld world, float backGroundViewScaling, @Nullable ICubeMapProgram sky2DTexture) {
        super(new WBenchSkyBackground(world, backGroundViewScaling), sky2DTexture);
    }

    @Override
    public WBenchSkyBoxSnapshotData takeSnapshot() {
        return new WBenchSkyBoxSnapshotData(this.getTexture(), this.isSkyCoveredByFog(), this.isDrawSunOnSkyBox(), ((WBenchSkyBackground) (this.getBackground())).takeSnapshot());
    }

    @Override
    public void fixSnapshot(WBenchSkyBoxSnapshotData wBenchSkyBoxSnapshotData) {
        this.setSky2DTexture(wBenchSkyBoxSnapshotData.sky2DTexture);
        this.setSkyCoveredByFog(wBenchSkyBoxSnapshotData.isSkyCoveredByFog);
        this.setDrawSunOnSkyBox(wBenchSkyBoxSnapshotData.drawSunOnSkyBox);
        ((WBenchSkyBackground) this.getBackground()).fixSnapshot(wBenchSkyBoxSnapshotData.wBenchSkyBackgroundSnapshotData);
    }

    public record WBenchSkyBoxSnapshotData(ICubeMapProgram sky2DTexture, boolean isSkyCoveredByFog,
                                           boolean drawSunOnSkyBox,
                                           WBenchSkyBackground.WBenchSkyBackgroundSnapshotData wBenchSkyBackgroundSnapshotData) implements SnapshotData {
    }
}
