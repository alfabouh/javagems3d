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
