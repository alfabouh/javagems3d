/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package workbench.graphics.scene.ui.map.editor.scenes.actions;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.global.JGemsConfig;
import org.joml.Vector2i;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.environment.components.WBenchShadowScene;
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class InterfaceActionsEnvShadowsM {
    private final MapEditorInterface mapEditorInterface;

    public InterfaceActionsEnvShadowsM(MapEditorInterface mapEditorInterface) {
        this.mapEditorInterface = mapEditorInterface;
    }

    private void shadowResCombo(String id, int[] f1) {
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffffffa7);
        final WBenchShadowScene shadowScene = this.mapEditorInterface.getWorld().getEnvironment().getShadowScene();
        final String[] resolutions = {"256", "512", "1024", "2048", "4096"};
        ImGui.setNextItemWidth(80);
        final int currentResolution = f1[0];
        if (ImGui.beginCombo("Resolution##" + id, String.valueOf(currentResolution))) {
            for (String resolution : resolutions) {
                int parsed = Integer.parseInt(resolution);
                if (ImGui.selectable(resolution, currentResolution == parsed)) {
                    WBenchUITrackingHelper.instantlyTrackAndPush();
                    f1[0] = parsed;
                }
            }
            ImGui.endCombo();
        }
        ImGui.popStyleColor();
    }

    public void render() {
        WBenchEnvironment environment = this.mapEditorInterface.getWorld().getEnvironment();
        if (ImGui.collapsingHeader("PointLight Shadows", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##ShadowsContent1", ImGui.getColumnWidth(), 60, true, ImGuiWindowFlags.HorizontalScrollbar);
            {
                int[] f1 = new int[]{environment.getShadowScene().pointLightShadowMapResolution};
                this.shadowResCombo("pl", f1);
                environment.getShadowScene().pointLightShadowMapResolution = f1[0];
            }
            ImGui.endChild();
        }
        if (ImGui.collapsingHeader("SpotLight Shadows", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##ShadowsContent3", ImGui.getColumnWidth(), 60, true, ImGuiWindowFlags.HorizontalScrollbar);
            {
                int[] f1 = new int[]{environment.getShadowScene().spotLightShadowMapResolution};
                this.shadowResCombo("sp", f1);
                environment.getShadowScene().spotLightShadowMapResolution = f1[0];
            }
            ImGui.endChild();
        }
        if (ImGui.collapsingHeader("Sun Shadows", ImGuiTreeNodeFlags.DefaultOpen)) {
            ImGui.beginChild("##ShadowsContent2", ImGui.getColumnWidth(), 180, true, ImGuiWindowFlags.HorizontalScrollbar);
            //ImGui.image(WBench.get().getScreen().getScene().getWorld().getEnvironment().getShadowScene().getSpotLightShadows().get(0).getSpotLightFBO().getTextureIDByIndex(0), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            //ImGui.image(WBench.get().getScreen().getScene().getWorld().getEnvironment().getShadowScene().getSpotLightShadows().get(1).getSpotLightFBO().getTextureIDByIndex(0), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            //ImGui.image(WBench.get().getScreen().getScene().getWorld().getEnvironment().getShadowScene().getSpotLightShadows().get(2).getSpotLightFBO().getTextureIDByIndex(0), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

            {
                int[] f1 = new int[]{environment.getShadowScene().sunShadowMapResolution};
                this.shadowResCombo("sun", f1);
                environment.getShadowScene().sunShadowMapResolution = f1[0];
            }
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_shadowSplits", WBenchUITrackingHelper::INSTANCE)) {
                float[] shadowSplits = new float[]{environment.getShadowScene().getSunLightShadow().getCascadeSplits().x, environment.getShadowScene().getSunLightShadow().getCascadeSplits().y, 0.0f};
                if (ImGui.dragFloat2("LOD Splits", shadowSplits, 0.01f, 0.0f, 5.0f)) {
                    uiTrackingHelper.saveSnapshot();
                    environment.getShadowScene().getSunLightShadow().setCascadeSplits(new Vector3f(shadowSplits));
                }
            }
            if (ImGui.checkbox("Shadows Enabled", environment.getShadowScene().getSunLightShadow().isEnabled())) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                environment.getShadowScene().getSunLightShadow().setEnabled(!environment.getShadowScene().getSunLightShadow().isEnabled());
            }
            if (ImGui.checkbox("(Debug) Show Cascades", JGemsConfig.DEBUG.SHOW_CASCADES)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
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
            ImGui.endChild();
        }
        //if (ImGui.collapsingHeader("PointLight Shadows")) {
        //    if (ImGui.treeNodeEx("Idx 1 (Shadow Map)")) {
        //        ImGui.image(environment.getShadowScene().getPointLightShadows().get(0).getPointLightCubeMap().getCubeMapProgram().get.getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 5.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 5.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        //        ImGui.treePop();
        //    }
        //}
    }
}
