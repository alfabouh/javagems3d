package workbench.graphics.scene.ui.map.editor.scenes.resources.environment;

import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.scene.ui.map.MapEditorInterface;

import java.util.Map;
import java.util.Set;

public class InterfaceEnvSkyM {
    private final MapEditorInterface mapEditorInterface;
    private final FixedCamera sunCamera;
    private boolean cameraCheckBox;

    public InterfaceEnvSkyM(MapEditorInterface mapEditorInterface) {
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
        ImGui.text("Sun");

        if (ImGui.checkbox("Sun's View", this.isCameraCheckBox())) {
            if (this.isCameraCheckBox()) {
                this.setCameraCheckBox(false);
                this.mapEditorInterface.overrideCamera(null);
            } else {
                this.setCameraCheckBox(true);
                Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getSkyBox().getSun());
                this.sunCamera.setCameraPosition(camData.getFirst());
                this.sunCamera.setLookAt(camData.getSecond());
                this.mapEditorInterface.overrideCamera(this.sunCamera);
            }
        }

        float[] brightness = new float[] {environment.getSkyBox().getSun().getSunBrightness()};
        if (ImGui.sliderFloat("Brightness", brightness, 0.0f, 5.0f)) {
            environment.getSkyBox().getSun().setSunBrightness(brightness[0]);
        }

        float[] fogColor = new float[] {environment.getSkyBox().getSun().getLightColor().x, environment.getSkyBox().getSun().getLightColor().y, environment.getSkyBox().getSun().getLightColor().z};
        if (ImGui.colorEdit3("Color", fogColor)) {
            environment.getSkyBox().getSun().setLightColor(new Vector3f(fogColor[0], fogColor[1], fogColor[2]));
        }

        float[] sunPosition = {environment.getSkyBox().getSun().getLightPosition().x, environment.getSkyBox().getSun().getLightPosition().y, environment.getSkyBox().getSun().getLightPosition().z};
        if (ImGui.sliderFloat3("Angle", sunPosition, -1.0f, 1.0f)) {
            environment.getSkyBox().getSun().setLightPosition(new Vector3f(sunPosition[0], sunPosition[1], sunPosition[2]));
            Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getSkyBox().getSun());
            this.sunCamera.setCameraPosition(camData.getFirst());
            this.sunCamera.setLookAt(camData.getSecond());
        }

        ImGui.separator();
        ImGui.text("Sky Texture");
        Set<Map.Entry<String, ICubeMapProgram>> skyBoxes = WBench.get().getMapProjectManager().getMapObjectTemplates().getSkyBoxes().entrySet();
        if (!skyBoxes.isEmpty()) {
            SkyBox skyBox = this.mapEditorInterface.getOpenGLRenderer().getWorld().getEnvironment().getSkyBox();
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

    public boolean isCameraCheckBox() {
        return this.cameraCheckBox;
    }

    public void setCameraCheckBox(boolean cameraCheckBox) {
        this.cameraCheckBox = cameraCheckBox;
    }
}
