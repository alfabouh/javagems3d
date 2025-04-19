package workbench.graphics.environment.components;

import javagems3d.graphics.environment.fog.JGemsFogScene;
import workbench.graphics.scene.ui.EditorInterface;

public class WBenchFogScene extends JGemsFogScene {
    @Override
    public float getFogDensity() {
        return EditorInterface.VIEW_FOG ? super.getFogDensity() : 0.0f;
    }
}