package workbench.graphics.scene.ui.map.editor.scenes.actions;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.global.JGemsConfig;
import org.joml.Vector3f;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class InterfaceActionsEnvLightingM {
    private final MapEditorInterface mapEditorInterface;

    public InterfaceActionsEnvLightingM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
    }

    public void render() {
        WBenchEnvironment environment = this.mapEditorInterface.getWorld().getEnvironment();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xfff5cc19);
        if (ImGui.button("Defaults")) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            environment.getLightScene().setHdrExposure(JGemsConfig.SYSTEM.HDR_EXPOSURE_DEFAULT);
            environment.getLightScene().setHdrGamma(JGemsConfig.SYSTEM.HDR_GAMMA_DEFAULT);
        }
        ImGui.popStyleColor();
        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_lightHDRExposure", WBenchUITrackingHelper::INSTANCE)) {
            float[] exposure = new float[]{environment.getLightScene().getHdrExposure()};
            if (ImGui.dragFloat("HDR Exposure", exposure, 0.01f, -24.0f, 24.0f)) {
                uiTrackingHelper.saveSnapshot();
                environment.getLightScene().setHdrExposure(exposure[0]);
            }
        }
        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_lightHDRGamma", WBenchUITrackingHelper::INSTANCE)) {
            float[] gamma = new float[]{environment.getLightScene().getHdrGamma()};
            if (ImGui.dragFloat("HDR Gamma", gamma, 0.01f, 0.0f, 24.0f)) {
                uiTrackingHelper.saveSnapshot();
                environment.getLightScene().setHdrGamma(gamma[0]);
            }
        }
        if (ImGui.checkbox("Bloom Enabled", environment.getLightScene().isBloomEnabled())) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            environment.getLightScene().setBloomEnabled(!environment.getLightScene().isBloomEnabled());
        }
    }
}
