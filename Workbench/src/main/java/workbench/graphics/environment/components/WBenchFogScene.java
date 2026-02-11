package workbench.graphics.environment.components;

import javagems3d.graphics.environment.fog.JGemsFogScene;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import org.joml.Vector3f;
import workbench.graphics.scene.ui.map.editor.utils.GlobalWBenchSceneRenderingVars;

public class WBenchFogScene extends JGemsFogScene implements ISnapshotCompatible<WBenchFogScene.WBenchFogSceneSnapshotData> {
    @Override
    public float getFogDensity() {
        return GlobalWBenchSceneRenderingVars.VIEW_FOG ? super.getFogDensity() : 0.0f;
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

    public static class WBenchFogSceneSnapshotData implements ISnapshotCompatible.SnapshotData {
        public final float density;
        public final Vector3f color;

        public WBenchFogSceneSnapshotData(float density, Vector3f color) {
            this.density = density;
            this.color = color;
        }
    }
}