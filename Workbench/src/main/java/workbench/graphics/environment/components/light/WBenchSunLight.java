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

    public static class WBenchSunLightSnapshotData implements ISnapshotCompatible.SnapshotData {
        public final float sunBrightness;
        public final Vector3f offset;
        public final Vector3f lightColor;
        public final Vector3f lightPos;
        public final boolean isActive;

        public WBenchSunLightSnapshotData(float sunBrightness, Vector3f offset, Vector3f lightColor, Vector3f lightPos, boolean isActive) {
            this.sunBrightness = sunBrightness;
            this.offset = offset;
            this.lightColor = lightColor;
            this.lightPos = lightPos;
            this.isActive = isActive;
        }
    }
}
