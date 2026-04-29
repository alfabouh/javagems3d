package workbench.graphics.scene.ui.map.editor.scenes.actions;

import imgui.ImGui;
import imgui.type.ImBoolean;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.help.JGemsHelper;
import org.joml.Vector3f;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class InterfaceActionsEnvFogM {
    private final MapEditorInterface mapEditorInterface;

    public InterfaceActionsEnvFogM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
    }

    public void render() {
        WBenchEnvironment environment = this.mapEditorInterface.getOpenGLRenderer().getWorld().getEnvironment();
        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_fogIntensity", WBenchUITrackingHelper::INSTANCE)) {
            float[] fogIntensity = new float[]{JGemsHelper.math().clamp(environment.getFogScene().getFogDensity(), 0.0f, 1.0f)};
            if (ImGui.dragFloat("Intensity", fogIntensity, 1.0e-6f, 0.0f, 1.0f, "%.6f")) {
                uiTrackingHelper.saveSnapshot();
                environment.getFogScene().setFogDensity(fogIntensity[0]);
            }
        }

        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_fogColor", WBenchUITrackingHelper::INSTANCE)) {
            float[] fogColor = new float[]{environment.getFogScene().getFogColor().x, environment.getFogScene().getFogColor().y, environment.getFogScene().getFogColor().z};
            if (ImGui.colorEdit3("Color", fogColor)) {
                uiTrackingHelper.saveSnapshot();
                environment.getFogScene().setFogColor(new Vector3f(fogColor[0], fogColor[1], fogColor[2]));
            }
        }

        ImBoolean fogCoversSky = new ImBoolean(environment.getSkyBox().isSkyCoveredByFog());
        if (ImGui.checkbox("Cover Sky", fogCoversSky)) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            environment.getSkyBox().setSkyCoveredByFog(!environment.getSkyBox().isSkyCoveredByFog());
        }
    }
}
