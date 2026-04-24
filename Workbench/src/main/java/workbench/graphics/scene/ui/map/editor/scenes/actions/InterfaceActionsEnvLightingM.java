package workbench.graphics.scene.ui.map.editor.scenes.actions;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.global.JGemsConfig;
import org.joml.Vector3f;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.scene.nodes.WBenchDeferredRenderNode;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class InterfaceActionsEnvLightingM {
    private final MapEditorInterface mapEditorInterface;

    public InterfaceActionsEnvLightingM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
    }

    public void render() {
        WBenchEnvironment environment = this.mapEditorInterface.getWorld().getEnvironment();
        if (ImGui.collapsingHeader("HDR##HDR1")) {
            ImGui.beginChild("##LightingContent1", ImGui.getColumnWidth(), 160, true, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.pushStyleColor(ImGuiCol.Text, 0xfff5cc19);
            if (ImGui.button("Defaults##DEF2")) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                environment.getLightScene().setHdrExposure(JGemsConfig.SYSTEM.HDR_EXPOSURE_DEFAULT);
                environment.getLightScene().setHdrGamma(JGemsConfig.SYSTEM.HDR_GAMMA_DEFAULT);
            }
            ImGui.popStyleColor();
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_lightHDRExposure", WBenchUITrackingHelper::INSTANCE)) {
                float[] exposure = new float[]{environment.getLightScene().getHdrExposure()};
                if (ImGui.dragFloat("Exposure", exposure, 0.01f, -24.0f, 24.0f)) {
                    uiTrackingHelper.saveSnapshot();
                    environment.getLightScene().setHdrExposure(exposure[0]);
                }
            }
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_lightHDRGamma", WBenchUITrackingHelper::INSTANCE)) {
                float[] gamma = new float[]{environment.getLightScene().getHdrGamma()};
                if (ImGui.dragFloat("Gamma", gamma, 0.01f, 0.0f, 24.0f)) {
                    uiTrackingHelper.saveSnapshot();
                    environment.getLightScene().setHdrGamma(gamma[0]);
                }
            }
            if (ImGui.checkbox("Bloom Enabled", environment.getLightScene().isBloomEnabled())) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                environment.getLightScene().setBloomEnabled(!environment.getLightScene().isBloomEnabled());
            }
            ImGui.endChild();
        }
        if (ImGui.collapsingHeader("SSAO##SSAO1")) {
            ImGui.beginChild("##LightingContent2", ImGui.getColumnWidth(), 200, true, ImGuiWindowFlags.HorizontalScrollbar);
            ImGui.pushStyleColor(ImGuiCol.Text, 0xfff5cc19);
            if (ImGui.button("Defaults##DEF1")) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                environment.getLightScene().setSsaoRadius(JGemsConfig.SYSTEM.SSAO_RADIUS);
                environment.getLightScene().setSsaoBias(JGemsConfig.SYSTEM.SSAO_BIAS);
                environment.getLightScene().setSsaoRange(JGemsConfig.SYSTEM.SSAO_RANGE);
            }
            ImGui.popStyleColor();
            ImGui.pushStyleColor(ImGuiCol.Text, 0xff00ff00);
            if (ImGui.checkbox("Enable SSAO Testing", WBenchDeferredRenderNode.enabledTest)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                WBenchDeferredRenderNode.enabledTest = !WBenchDeferredRenderNode.enabledTest;
            }
            ImGui.popStyleColor();
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_lightSSAOBias", WBenchUITrackingHelper::INSTANCE)) {
                float[] f1 = new float[]{environment.getLightScene().getSsaoBias()};
                if (ImGui.dragFloat("Bias", f1, 0.001f, 0.0f, 1.0f)) {
                    uiTrackingHelper.saveSnapshot();
                    environment.getLightScene().setSsaoBias(f1[0]);
                }
                if (ImGui.isItemHovered()) {
                    ImGui.beginTooltip();
                    ImGui.setTooltip("Self-Occlusion Threshold");
                    ImGui.endTooltip();
                }
            }
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_lightSSAORadius", WBenchUITrackingHelper::INSTANCE)) {
                float[] f1 = new float[]{environment.getLightScene().getSsaoRadius()};
                if (ImGui.dragFloat("Radius", f1, 0.001f, 0.0f, 10.0f)) {
                    uiTrackingHelper.saveSnapshot();
                    environment.getLightScene().setSsaoRadius(f1[0]);
                }
                if (ImGui.isItemHovered()) {
                    ImGui.beginTooltip();
                    ImGui.setTooltip("Occlusion Geometry check radius (more=laggy)");
                    ImGui.endTooltip();
                }
            }
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_lightSSAORange", WBenchUITrackingHelper::INSTANCE)) {
                float[] f1 = new float[]{environment.getLightScene().getSsaoRange()};
                if (ImGui.dragFloat("Range", f1, 0.001f, 0.0f, 10.0f)) {
                    uiTrackingHelper.saveSnapshot();
                    environment.getLightScene().setSsaoRange(f1[0]);
                }
                if (ImGui.isItemHovered()) {
                    ImGui.beginTooltip();
                    ImGui.setTooltip("Occlusion Geometry range check to prevent far geometry change the occlusion value.");
                    ImGui.endTooltip();
                }
            }
            ImGui.endChild();
        }
    }
}
