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
