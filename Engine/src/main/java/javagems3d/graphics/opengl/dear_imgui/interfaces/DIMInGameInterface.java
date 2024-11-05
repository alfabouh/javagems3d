package javagems3d.graphics.opengl.dear_imgui.interfaces;

import api.app.events.bus.Events;
import api.bridge.events.APIEventsLauncher;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.camera.FreeControlledCamera;
import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.screen.JGemsScreen;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.controller.objects.MouseKeyboardController;
import javagems3d.system.graph.Graph;
import javagems3d.system.profiler.SpeedProfiler;
import logger.managers.LoggingManager;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL30;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DIMInGameInterface implements DIMInterface {
    private boolean snapStop = false;
    private Set<Map.Entry<String, SpeedProfiler.Group>> snapShot = null;

    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        if (!JGems3D.get().isValidPlayer()) {
            return;
        }
        ICamera camera = JGems3D.get().getScreen().getCamera();
        IPlayer entityPlayerSP = JGems3D.get().getPlayer();
        SceneWorld sceneWorld = JGems3D.get().getSceneWorld();
        JGemsOpenGLRenderer sceneRender = JGems3D.get().getScreen().getScene().getSceneRenderer();

        float logX = (float) windowSize.x / 3;
        float logY = (float) windowSize.y / 1.5f;

        ImGui.setNextWindowPos(windowSize.x - logX, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(logX, logY);
        ImGui.setNextWindowCollapsed(true, ImGuiCond.Once);
        ImGui.begin("Output", ImGuiWindowFlags.AlwaysVerticalScrollbar | ImGuiWindowFlags.NoResize);
        String[] textLines = LoggingManager.consoleText().split("\n");
        for (String s : textLines) {
            if (s.isEmpty()) {
                continue;
            }
            ImGui.textWrapped(s);
        }
        if (LoggingManager.markConsoleDirty) {
            ImGui.setScrollHereY(1.0f);
            LoggingManager.markConsoleDirty = false;
        }
        ImGui.end();

        ImGui.setNextWindowSize(JGemsSceneGlobalConstants.defaultW / 3.0f, JGemsSceneGlobalConstants.defaultH / 3.0f, ImGuiCond.Once);
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.begin("Debug");
        ImGui.text("FPS: " + JGemsScreen.RENDER_FPS + " | TPS: " + JGemsScreen.PHYS_TPS);
        if (entityPlayerSP != null) {
            WorldItem dynamicPlayer = (WorldItem) entityPlayerSP;
            if (JGemsHelper.CAMERA.getCurrentCamera() instanceof FreeControlledCamera) {
                ImGui.text(String.format("%s %s %s", JGemsHelper.CAMERA.getCurrentCamera().getCamPosition().x, JGemsHelper.CAMERA.getCurrentCamera().getCamPosition().y, JGemsHelper.CAMERA.getCurrentCamera().getCamPosition().z));
            } else {
                ImGui.text(String.format("%s %s %s", dynamicPlayer.getPosition().x, dynamicPlayer.getPosition().y, dynamicPlayer.getPosition().z));
            }
            ImGui.text("entities: " + JGems3D.get().getPhysicsWorld().countItems());
            ImGui.text("tick: " + sceneWorld.getTicks());
            ImGui.text("current speed(scalar): " + String.format("%.4f", entityPlayerSP.getScalarSpeed()));
        }

        if (ImGui.collapsingHeader("Speed Profiler")) {
            Set<Map.Entry<String, SpeedProfiler.Group>> currProfSet = this.snapShot != null ? this.snapShot : SpeedProfiler.getAllProfilerGroups();
            for (Map.Entry<String, SpeedProfiler.Group> groupEntry : currProfSet) {
                if (ImGui.treeNode(groupEntry.getKey())) {
                    for (Map.Entry<String, SpeedProfiler.Section> sectionEntry : groupEntry.getValue().getAllEntries()) {
                        ImGui.textColored(0xffff00ff, sectionEntry.getKey() + ": " + sectionEntry.getValue().getTotalTime() + "ms");
                    }
                    if (groupEntry.getValue().getAllEntries().size() > 1) {
                        ImGui.newLine();
                        ImGui.textColored(0xff0000ff, "total: " + groupEntry.getValue().totalTimeInAllSections() + "ms");
                    }
                    ImGui.treePop();
                }
            }
            if (ImGui.checkbox("Snapshot", this.snapStop)) {
                this.snapStop = !this.snapStop;
                if (this.snapStop) {
                    this.snapShot = new HashSet<>(SpeedProfiler.getAllProfilerGroups());
                } else {
                    this.snapShot = null;
                }
            }
        }

        if (ImGui.collapsingHeader("Scene")) {
            if (ImGui.checkbox("HDR", JGemsSceneGlobalConstants.USE_HDR)) {
                JGemsSceneGlobalConstants.USE_HDR = !JGemsSceneGlobalConstants.USE_HDR;
            }
            if (ImGui.treeNode("HDR Settings")) {
                float[] exposure = new float[]{JGemsSceneGlobalConstants.HDR_EXPOSURE};
                ImGui.sliderFloat("exposure", exposure, 0.0f, 5.0f);
                JGemsSceneGlobalConstants.HDR_EXPOSURE = exposure[0];
                float[] gamma = new float[]{JGemsSceneGlobalConstants.HDR_GAMMA};
                ImGui.sliderFloat("gamma", gamma, 0.0f, 3.0f);
                JGemsSceneGlobalConstants.HDR_GAMMA = gamma[0];
                ImGui.treePop();
            }

            if (ImGui.checkbox("FXAA", JGemsSceneGlobalConstants.USE_FXAA)) {
                JGemsSceneGlobalConstants.USE_FXAA = !JGemsSceneGlobalConstants.USE_FXAA;
            }
            if (ImGui.checkbox("Bloom", JGemsSceneGlobalConstants.USE_BLOOM)) {
                JGemsSceneGlobalConstants.USE_BLOOM = !JGemsSceneGlobalConstants.USE_BLOOM;
            }

            if (ImGui.checkbox("SSAO", JGemsSceneGlobalConstants.USE_SSAO)) {
                JGemsSceneGlobalConstants.USE_SSAO = !JGemsSceneGlobalConstants.USE_SSAO;
            }
            if (ImGui.treeNode("SSAO Settings")) {
                float[] radius = new float[]{JGemsSceneGlobalConstants.SSAO_RADIUS};
                ImGui.sliderFloat("radius", radius, 0.0f, 5.0f);
                JGemsSceneGlobalConstants.SSAO_RADIUS = radius[0];
                float[] bias = new float[]{JGemsSceneGlobalConstants.SSAO_BIAS};
                ImGui.sliderFloat("bias", bias, 0.0f, 0.1f);
                JGemsSceneGlobalConstants.SSAO_BIAS = bias[0];
                float[] range = new float[]{JGemsSceneGlobalConstants.SSAO_RANGE};
                ImGui.sliderFloat("range", range, 1.0f, 10.0f);
                JGemsSceneGlobalConstants.SSAO_RANGE = range[0];
                ImGui.treePop();
            }

            if (ImGui.checkbox("Shadows", JGemsSceneGlobalConstants.USE_SHADOWS)) {
                JGemsSceneGlobalConstants.USE_SHADOWS = !JGemsSceneGlobalConstants.USE_SHADOWS;
            }
        }

        boolean flag = JGemsHelper.CAMERA.getCurrentCamera() instanceof FreeControlledCamera;
        if (ImGui.collapsingHeader("Tools")) {
            if (ImGui.checkbox("FreeCam", flag)) {
                if (!flag) {
                    JGemsHelper.CAMERA.enableFreeCamera(mouseKeyboardController, camera.getCamPosition(), camera.getCamRotation());
                    JGemsHelper.CONTROLLER.detachController();
                } else {
                    JGemsHelper.CAMERA.enableAttachedCamera((WorldItem) JGems3D.get().getPlayer());
                    JGemsHelper.CONTROLLER.attachControllerTo(mouseKeyboardController, JGems3D.get().getPlayer());
                }
            }

            if (ImGui.checkbox("Full Bright", JGemsDebugGlobalConstants.FULL_BRIGHT)) {
                JGemsDebugGlobalConstants.FULL_BRIGHT = !JGemsDebugGlobalConstants.FULL_BRIGHT;
            }

            if (ImGui.checkbox("Show Debug Lines", JGemsDebugGlobalConstants.SHOW_DEBUG_LINES)) {
                JGemsDebugGlobalConstants.SHOW_DEBUG_LINES = !JGemsDebugGlobalConstants.SHOW_DEBUG_LINES;
            }

            if (ImGui.button("Generate NavMesh")) {
                Graph graph = JGemsHelper.WORLD.genSimpleMapGraphFromStartPoint(JGemsHelper.CAMERA.getCurrentCamera().getCamPosition());
                String mapName = JGemsHelper.GAME.getCurrentMap().getLevelInfo().toString();
                Graph.saveInFile(graph);
                if (graph == null || graph.getGraph().isEmpty()) {
                    LoggingManager.showWindowInfo("Couldn't create NavMesh!");
                } else {
                    LoggingManager.showWindowInfo("Created NavMesh(" + graph.getGraph().size() + ") and saved in game folder. " + mapName + ".nav");
                }
                JGemsHelper.getPhysicsWorld().setMapNavGraph(graph);
            }
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.setTooltip("Generates NavMesh, starting from current camera position!");
                ImGui.endTooltip();
            }
        }

        if (ImGui.collapsingHeader("Frame Buffers")) {
            GL30.glScissor(0, 0, 1, 1);
            ImGui.beginChild("inner1");
            if (ImGui.collapsingHeader("GBuffer")) {
                ImGui.beginChild("Images1", JGemsSceneGlobalConstants.defaultW / 2.0f + 50.0f, JGemsSceneGlobalConstants.defaultW / 4.0f + 60, true);

                ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(1).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(2).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(3).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(4).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();

                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("Scene")) {
                ImGui.beginChild("Images2", JGemsSceneGlobalConstants.defaultW / 2.0f + 50.0f, JGemsSceneGlobalConstants.defaultW / 4.0f + 60, true);

                ImGui.image(sceneRender.getSceneGluingBuffer().getTexturePrograms().get(1).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(sceneRender.getTransparencySceneBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.image(sceneRender.getSkyBoxBackGroundBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(sceneRender.getHdrBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.image(sceneRender.getForwardAndDeferredScenesBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("Shadows")) {
                ImGui.beginChild("Images3", JGemsSceneGlobalConstants.defaultW / 2.0f + 50.0f, JGemsSceneGlobalConstants.defaultW / 4.0f + 60, true);

                ImGui.image(sceneRender.getShadowScene().getShadowPostFBO().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(sceneRender.getShadowScene().getShadowPostFBO().getTexturePrograms().get(1).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);


                ImGui.image(sceneRender.getShadowScene().getShadowPostFBO().getTexturePrograms().get(2).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("SSAO")) {
                ImGui.beginChild("Images4", JGemsSceneGlobalConstants.defaultW / 2.0f + 50.0f, JGemsSceneGlobalConstants.defaultW / 4.0f + 60, true);

                ImGui.image(sceneRender.getSsaoBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.endChild();
            }
            ImGui.endChild();
        }
        ImGui.end();
    }
}
