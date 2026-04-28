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