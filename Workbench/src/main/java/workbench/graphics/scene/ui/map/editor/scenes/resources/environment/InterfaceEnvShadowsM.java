package workbench.graphics.scene.ui.map.editor.scenes.resources.environment;

import imgui.ImGui;
import javagems3d.system.global.JGemsConfig;
import org.joml.Vector3f;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class InterfaceEnvShadowsM {
    private final MapEditorInterface mapEditorInterface;

    public InterfaceEnvShadowsM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
    }

    public void render() {
        WBenchEnvironment environment = this.mapEditorInterface.getOpenGLRenderer().getWorld().getEnvironment();
        float[] shadowSplits = new float[]{environment.getShadowScene().getSunLightShadow().getCascadeSplits().x, environment.getShadowScene().getSunLightShadow().getCascadeSplits().y, 0.0f};
        if (ImGui.dragFloat2("LOD Splits", shadowSplits, 0.01f, 0.0f, 5.0f)) {
            environment.getShadowScene().getSunLightShadow().setCascadeSplits(new Vector3f(shadowSplits));
        }
        if (ImGui.checkbox("Show Cascades", JGemsConfig.DEBUG.SHOW_CASCADES)) {
            JGemsConfig.DEBUG.SHOW_CASCADES = !JGemsConfig.DEBUG.SHOW_CASCADES;
        }
        if (ImGui.treeNodeEx("Level 1 (Shadow Map)")) {
            ImGui.image(environment.getShadowScene().getSunLightShadow().getSunShadowFBO().getTexturePrograms().get(0).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 5.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 5.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("Level 2 (Shadow Map)")) {
            ImGui.image(environment.getShadowScene().getSunLightShadow().getSunShadowFBO().getTexturePrograms().get(1).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 5.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 5.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.treePop();
        }
        if (ImGui.treeNodeEx("Level 3 (Shadow Map)")) {
            ImGui.image(environment.getShadowScene().getSunLightShadow().getSunShadowFBO().getTexturePrograms().get(2).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 5.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.treePop();
        }
    }
}
