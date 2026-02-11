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

    protected void setSunLight() {
        this.sunLight = new WBenchSunLight(new Vector3f(1.0f), new Vector3f(1.0f), 1.0f);
    }

    @Override
    public WBenchSkyBoxSnapshotData takeSnapshot() {
        return new WBenchSkyBoxSnapshotData(this.getTexture(), this.isSkyCoveredByFog(), this.isDrawSunOnSkyBox(), ((WBenchSkyBackground) (this.getBackground())).takeSnapshot(), ((WBenchSunLight) this.sunLight).takeSnapshot());
    }

    @Override
    public void fixSnapshot(WBenchSkyBoxSnapshotData wBenchSkyBoxSnapshotData) {
        this.setSky2DTexture(wBenchSkyBoxSnapshotData.sky2DTexture);
        this.setSkyCoveredByFog(wBenchSkyBoxSnapshotData.isSkyCoveredByFog);
        this.setDrawSunOnSkyBox(wBenchSkyBoxSnapshotData.drawSunOnSkyBox);
        ((WBenchSkyBackground) this.getBackground()).fixSnapshot(wBenchSkyBoxSnapshotData.wBenchSkyBackgroundSnapshotData);
        ((WBenchSunLight) this.sunLight).fixSnapshot(wBenchSkyBoxSnapshotData.sunLightSnapshotData);
    }

    public static class WBenchSkyBoxSnapshotData implements ISnapshotCompatible.SnapshotData {
        public final ICubeMapProgram sky2DTexture;
        public final boolean isSkyCoveredByFog;
        public final boolean drawSunOnSkyBox;

        public final WBenchSkyBackground.WBenchSkyBackgroundSnapshotData wBenchSkyBackgroundSnapshotData;
        public final WBenchSunLight.WBenchSunLightSnapshotData sunLightSnapshotData;

        public WBenchSkyBoxSnapshotData(ICubeMapProgram sky2DTexture, boolean isSkyCoveredByFog, boolean drawSunOnSkyBox, WBenchSkyBackground.WBenchSkyBackgroundSnapshotData wBenchSkyBackgroundSnapshotData, WBenchSunLight.WBenchSunLightSnapshotData sunLightSnapshotData) {
            this.sky2DTexture = sky2DTexture;
            this.isSkyCoveredByFog = isSkyCoveredByFog;
            this.drawSunOnSkyBox = drawSunOnSkyBox;
            this.wBenchSkyBackgroundSnapshotData = wBenchSkyBackgroundSnapshotData;
            this.sunLightSnapshotData = sunLightSnapshotData;
        }
    }
}
