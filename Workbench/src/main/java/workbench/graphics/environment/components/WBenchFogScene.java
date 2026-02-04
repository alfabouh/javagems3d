package workbench.graphics.environment.components;

import javagems3d.graphics.environment.fog.JGemsFogScene;
import workbench.graphics.scene.ui.map.editor.utils.GlobalSceneRenderingVars;

public class WBenchFogScene extends JGemsFogScene {
    @Override
    public float getFogDensity() {
        return GlobalSceneRenderingVars.VIEW_FOG ? super.getFogDensity() : 0.0f;
    }
}