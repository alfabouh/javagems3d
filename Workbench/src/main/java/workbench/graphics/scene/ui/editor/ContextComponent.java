package workbench.graphics.scene.ui.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.help.JGemsMathHelper;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.scene.ui.EditorInterface;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ContextComponent {
    private final EditorInterface editorInterface;
    private boolean cameraCheckBox;
    private boolean openEnvironmentFogSettings;
    private boolean openEnvironmentSkySettings;
    private boolean openProjectSettings;
    private final FixedCamera sunCamera;

    public ContextComponent(EditorInterface editorInterface) {
        this.editorInterface = editorInterface;
        this.sunCamera = new FixedCamera(new Vector3f(), new Vector3f());
        this.clear();
    }

    public void clear() {
        this.cameraCheckBox = false;
        this.openEnvironmentFogSettings = false;
        this.openEnvironmentSkySettings = false;
        this.openProjectSettings = false;
    }

    public void context() {
        WBenchEnvironment environment = this.getEditorInterface().getOpenGLRenderer().getWorld().getEnvironment();
        ImVec2 screenSize = ImGui.getIO().getDisplaySize();

        if (this.isOpenEnvironmentFogSettings()) {
            ImVec2 windowSize = new ImVec2(400, 200);
            ImGui.setNextWindowSize(windowSize.x, windowSize.y, ImGuiCond.Appearing);
            ImGui.setNextWindowPos((screenSize.x - windowSize.x) / 2, (screenSize.y - windowSize.y) / 2, ImGuiCond.Appearing);
            ImBoolean opened = new ImBoolean(true);
            if (ImGui.begin("Fog", opened, ImGuiWindowFlags.NoResize)) {
                float[] fogIntensity = new float[] {JGemsMathHelper.clamp(environment.getFogManager().getDensity(), 0.0f, 1.0f)};
                if (ImGui.dragFloat("Fog Intensity", fogIntensity, 0.0001f, 0.0f, 1.0f)) {
                    environment.getFogManager().setDensity(fogIntensity[0]);
                }

                float[] fogColor = new float[] {environment.getFogManager().getColor().x, environment.getFogManager().getColor().y, environment.getFogManager().getColor().z};
                if (ImGui.colorEdit3("Fog Color", fogColor)) {
                    environment.getFogManager().setColor(new Vector3f(fogColor[0], fogColor[1], fogColor[2]));
                }

                ImBoolean fogCoversSky = new ImBoolean(environment.getSkyBox().isSkyCoveredByFog());
                if (ImGui.checkbox("Cover Sky", fogCoversSky)) {
                    environment.getSkyBox().setSkyCoveredByFog(!environment.getSkyBox().isSkyCoveredByFog());
                }
            }
            ImGui.end();
            if (!opened.get()) {
                this.setOpenEnvironmentFogSettings(false);
            }
        }

        if (this.isOpenEnvironmentSkySettings()) {
            ImVec2 windowSize = new ImVec2(400, 200);
            ImGui.setNextWindowSize(windowSize.x, windowSize.y, ImGuiCond.Appearing);
            ImGui.setNextWindowPos((screenSize.x - windowSize.x) / 2, (screenSize.y - windowSize.y) / 2, ImGuiCond.Appearing);
            ImBoolean opened = new ImBoolean(true);
            if (ImGui.begin("SkyBox", opened, ImGuiWindowFlags.NoResize)) {
                ImGui.text("Sun");

                if (ImGui.checkbox("Sun's View", this.isCameraCheckBox())) {
                    if (this.isCameraCheckBox()) {
                        this.setCameraCheckBox(false);
                        this.getEditorInterface().setNewCamera(null);
                    } else {
                        this.setCameraCheckBox(true);
                        Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getSkyBox().getSun());
                        this.sunCamera.setCameraPosition(camData.getFirst());
                        this.sunCamera.setLookAt(camData.getSecond());
                        this.getEditorInterface().setNewCamera(this.sunCamera);
                    }
                }

                float[] brightness = new float[] {environment.getSkyBox().getSun().getSunBrightness()};
                if (ImGui.sliderFloat("Sun Brightness", brightness, 0.0f, 5.0f)) {
                    environment.getSkyBox().getSun().setSunBrightness(brightness[0]);
                }

                float[] fogColor = new float[] {environment.getSkyBox().getSun().getLightColor().x, environment.getSkyBox().getSun().getLightColor().y, environment.getSkyBox().getSun().getLightColor().z};
                if (ImGui.colorEdit3("Sun Color", fogColor)) {
                    environment.getSkyBox().getSun().setLightColor(new Vector3f(fogColor[0], fogColor[1], fogColor[2]));
                }

                float[] sunPosition = {environment.getSkyBox().getSun().getLightPosition().x, environment.getSkyBox().getSun().getLightPosition().y, environment.getSkyBox().getSun().getLightPosition().z};
                if (ImGui.sliderFloat3("Position", sunPosition, -1.0f, 1.0f)) {
                    environment.getSkyBox().getSun().setLightPosition(new Vector3f(sunPosition[0], sunPosition[1], sunPosition[2]));
                    Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getSkyBox().getSun());
                    this.sunCamera.setCameraPosition(camData.getFirst());
                    this.sunCamera.setLookAt(camData.getSecond());
                }

                ImGui.separator();
                ImGui.text("Sky Texture");
                Set<Map.Entry<String, ICubeMapProgram>> skyBoxes = WBench.get().getProjectObjects().getSkyBoxes().entrySet();
                if (!skyBoxes.isEmpty()) {
                    SkyBox skyBox = this.getEditorInterface().getOpenGLRenderer().getWorld().getEnvironment().getSkyBox();
                    ICubeMapProgram currentSky = skyBox.getTexture();
                    ImGui.treePush();
                    for (Map.Entry<String, ICubeMapProgram> cubeMapProgramPair : skyBoxes) {
                        boolean flag = currentSky == cubeMapProgramPair.getValue();
                        ImGui.pushID(cubeMapProgramPair.getKey());
                        if (ImGui.selectable(cubeMapProgramPair.getKey(), flag, ImGuiSelectableFlags.AllowItemOverlap)) {
                            if (!flag) {
                                skyBox.setSky2DTexture(cubeMapProgramPair.getValue());
                            } else {
                                skyBox.setSky2DTexture(null);
                            }
                        }
                        ImGui.popID();
                    }
                    ImGui.treePop();
                } else {
                    ImGui.text("Empty");
                }
            }
            ImGui.end();
            if (!opened.get()) {
                this.getEditorInterface().setNewCamera(null);
                this.setOpenEnvironmentSkySettings(false);
                this.setCameraCheckBox(false);
            }
        }

        if (this.isOpenProjectSettings()) {
            ImVec2 windowSize = new ImVec2(400, 300);
            ImGui.setNextWindowSize(windowSize.x, windowSize.y, ImGuiCond.Appearing);
            ImGui.setNextWindowPos((screenSize.x - windowSize.x) / 2, (screenSize.y - windowSize.y) / 2, ImGuiCond.Appearing);
            ImBoolean opened = new ImBoolean(true);
            if (ImGui.begin("WBenchProject", opened, ImGuiWindowFlags.NoResize)) {
                ImGui.text("Map Size");

            }
            ImGui.end();
            if (!opened.get()) {
                this.setOpenProjectSettings(false);
            }
        }
    }

    private Pair<Vector3f, Vector3f> adjustCamera(SunLight sun) {
        Vector3f sunPos = sun.getLightPosition().normalize().mul(64f);
        return new Pair<>(sunPos, new Vector3f(0.0f));
    }

    public void setCameraCheckBox(boolean cameraCheckBox) {
        this.cameraCheckBox = cameraCheckBox;
    }

    public void setOpenEnvironmentFogSettings(boolean openEnvironmentFogSettings) {
        this.openEnvironmentFogSettings = openEnvironmentFogSettings;
    }

    public void setOpenEnvironmentSkySettings(boolean openEnvironmentSkySettings) {
        this.openEnvironmentSkySettings = openEnvironmentSkySettings;
    }

    public void setOpenProjectSettings(boolean openProjectSettings) {
        this.openProjectSettings = openProjectSettings;
    }

    public boolean isCameraCheckBox() {
        return this.cameraCheckBox;
    }

    public boolean isOpenEnvironmentFogSettings() {
        return this.openEnvironmentFogSettings;
    }

    public boolean isOpenEnvironmentSkySettings() {
        return this.openEnvironmentSkySettings;
    }

    public boolean isOpenProjectSettings() {
        return this.openProjectSettings;
    }

    public EditorInterface getEditorInterface() {
        return this.editorInterface;
    }
}
