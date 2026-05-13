package workbench.graphics.scene.ui.map.editor.scenes.actions;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiSelectableFlags;
import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.project.map.MapObjectTemplatesManager;

import java.util.Map;
import java.util.Set;

public class InterfaceActionsEnvSkyM {
    private final MapEditorInterface mapEditorInterface;
    private final FixedCamera sunCamera;
    private boolean cameraCheckBox;

    public InterfaceActionsEnvSkyM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
        this.sunCamera = new FixedCamera(new Vector3f(), new Vector3f());
    }

    private Pair<Vector3f, Vector3f> adjustCamera(SunLight sun) {
        Vector3f sunPos = sun.getLightPosition().normalize().mul(64f);
        return new Pair<>(sunPos, new Vector3f(0.0f));
    }

    public void reset() {
        this.mapEditorInterface.overrideCamera(null);
        this.setCameraCheckBox(false);
    }

    public void render() {
        WBenchEnvironment environment = this.mapEditorInterface.getOpenGLRenderer().getWorld().getEnvironment();
        ImGui.bulletText("Sun Adjusting");

        if (ImGui.checkbox("Sun's View", this.isCameraCheckBox())) {
            if (this.isCameraCheckBox()) {
                this.setCameraCheckBox(false);
                this.mapEditorInterface.overrideCamera(null);
            } else {
                this.setCameraCheckBox(true);
                Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getLightScene().getSunLight());
                this.sunCamera.setCameraPosition(camData.first());
                this.sunCamera.setLookAt(camData.second());
                this.mapEditorInterface.overrideCamera(this.sunCamera);
            }
        }
        if (ImGui.checkbox("Render Sun Effect", environment.getSkyBox().isDrawSunOnSkyBox())) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            environment.getSkyBox().setDrawSunOnSkyBox(!environment.getSkyBox().isDrawSunOnSkyBox());
        }
        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_brightness", WBenchUITrackingHelper::INSTANCE)) {
            float[] brightness = new float[]{environment.getLightScene().getSunLight().getSunBrightness()};
            if (ImGui.dragFloat("Brightness", brightness, 0.001f,0.0f, 5.0f)) {
                uiTrackingHelper.saveSnapshot();
                environment.getLightScene().getSunLight().setSunBrightness(brightness[0]);
            }
        }
        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_fogColor", WBenchUITrackingHelper::INSTANCE)) {
            float[] fogColor = new float[]{environment.getLightScene().getSunLight().getLightColor().x, environment.getLightScene().getSunLight().getLightColor().y, environment.getLightScene().getSunLight().getLightColor().z};
            if (ImGui.colorEdit3("Color", fogColor)) {
                uiTrackingHelper.saveSnapshot();
                environment.getLightScene().getSunLight().setLightColor(new Vector3f(fogColor[0], fogColor[1], fogColor[2]));
            }
        }

        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_sunPosition", WBenchUITrackingHelper::INSTANCE)) {
            float[] sunPosition = {environment.getLightScene().getSunLight().getLightPosition().x, environment.getLightScene().getSunLight().getLightPosition().y, environment.getLightScene().getSunLight().getLightPosition().z};
            if (ImGui.sliderFloat3("Angle", sunPosition, -1.0f, 1.0f)) {
                uiTrackingHelper.saveSnapshot();
                environment.getLightScene().getSunLight().setLightPosition(new Vector3f(sunPosition[0], sunPosition[1], sunPosition[2]));
                Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getLightScene().getSunLight());
                this.sunCamera.setCameraPosition(camData.first());
                this.sunCamera.setLookAt(camData.second());
            }
        }

        ImGui.pushStyleColor(ImGuiCol.Text, 0xffffffa7);
        final ISkyBackground background = this.mapEditorInterface.getWorld().getEnvironment().getSkyBox().getBackground();
        final String[] scales = {"2.0", "4.0", "8.0", "16.0"};
        ImGui.setNextItemWidth(60);
        if (ImGui.beginCombo("SkyWorld Scale", String.valueOf(background.getViewScaling()))) {
            for (String scale : scales) {
                float selectedScaling = Float.parseFloat(scale);
                if (ImGui.selectable(scale, background.getViewScaling() == selectedScaling)) {
                    WBenchUITrackingHelper.instantlyTrackAndPush();
                    background.setViewScaling(selectedScaling);
                }
            }
            ImGui.endCombo();
        }
        ImGui.popStyleColor();

        ImGui.separator();
        if (ImGui.treeNodeEx("Sky Textures")) {
            Set<Map.Entry<String, MapObjectTemplatesManager.SkyBoxTemplate>> skyBoxes = WBench.get().getMapProjectManager().getMapObjectTemplates().getSkyBoxes().entrySet();
            if (!skyBoxes.isEmpty()) {
                SkyBox skyBox = this.mapEditorInterface.getOpenGLRenderer().getWorld().getEnvironment().getSkyBox();
                ICubeMapProgram currentSky = skyBox.getTexture();
                ImGui.bullet();
                if (ImGui.selectable("None", currentSky == null, ImGuiSelectableFlags.AllowItemOverlap)) {
                    WBenchUITrackingHelper.instantlyTrackAndPush();
                    skyBox.setSky2DTexture(null);
                }
                for (Map.Entry<String, MapObjectTemplatesManager.SkyBoxTemplate> cubeMapProgramPair : skyBoxes) {
                    boolean flag = currentSky == cubeMapProgramPair.getValue().getCubeMapProgram();
                    ImGui.pushID(cubeMapProgramPair.getKey());
                    ImGui.bullet();
                    if (ImGui.selectable(cubeMapProgramPair.getKey(), flag, ImGuiSelectableFlags.AllowItemOverlap)) {
                        WBenchUITrackingHelper.instantlyTrackAndPush();
                        if (!flag) {
                            skyBox.setSky2DTexture(cubeMapProgramPair.getValue().getCubeMapProgram());
                        } else {
                            skyBox.setSky2DTexture(null);
                        }
                    }
                    ImGui.popID();
                }
            } else {
                ImGui.text("Empty");
            }
            ImGui.treePop();
        }
    }

    public boolean isCameraCheckBox() {
        return this.cameraCheckBox;
    }

    public void setCameraCheckBox(boolean cameraCheckBox) {
        this.cameraCheckBox = cameraCheckBox;
    }
}
