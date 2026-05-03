package javagems3d.graphics.rendering.ui.dear_imgui.interfaces;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.ITransparencyRenderNode;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IDeferredRenderNode;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.service.graph.Graph;
import javagems3d.system.service.profiler.SpeedProfiler;
import javagems3d.system.resources.managing.JGemsResourceManager;
import logger.managers.LoggingManager;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DearUIGameInterface implements DearUIInterface {
    private boolean snapStop = false;
    private Set<Map.Entry<String, SpeedProfiler.Group>> snapShot = null;
    private static boolean showAllConsoleLines = false;

    public static void consoleContent() {
        if (ImGui.checkbox("Show All Lines", DearUIGameInterface.showAllConsoleLines)) {
            DearUIGameInterface.showAllConsoleLines = !DearUIGameInterface.showAllConsoleLines;
            LoggingManager.markConsoleDirty = true;
        }
        String[] textLines = LoggingManager.consoleText().split("\n");
        if (!DearUIGameInterface.showAllConsoleLines) {
            textLines = Arrays.stream(textLines).skip(Math.max(textLines.length - 128, 0)).toArray(String[]::new);
        }
        ImGui.beginChild("##console_window", ImGui.getColumnWidth(), ImGui.getWindowHeight() - 60, true);
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
        ImGui.endChild();
    }

    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        ICamera camera = JGems3D.get().getScreen().getCamera();
        IPlayer entityPlayerSP = JGems3D.get().getCurrentGameMapPlayer();
        SceneWorld sceneWorld = JGemsHelper.get().getSceneWorld();
        JGemsOpenGLRenderer sceneRender = (JGemsOpenGLRenderer) JGems3D.get().getScreen().getScene().getSceneRenderer();

        float logX = (float) windowSize.x / 3;
        float logY = (float) windowSize.y / 1.5f;

        ImGui.setNextWindowPos(windowSize.x - logX, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(logX, logY);
        ImGui.setNextWindowCollapsed(true, ImGuiCond.Once);
        ImGui.begin("Output", ImGuiWindowFlags.AlwaysVerticalScrollbar | ImGuiWindowFlags.NoResize);
        DearUIGameInterface.consoleContent();
        ImGui.end();

        ImGui.setNextWindowSize(JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 3.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 3.0f, ImGuiCond.Once);
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.begin("Debug");
        ImGui.text("FPS: " + JGemsScreen.RENDER_FPS + " | TPS: " + JGemsScreen.PHYS_TPS);
        if (entityPlayerSP != null) {
            WorldItem dynamicPlayer = (WorldItem) entityPlayerSP;
            if (JGemsHelper.camera().getCurrentCamera() instanceof ControlledCamera) {
                ImGui.text(String.format("%s %s %s", JGemsHelper.camera().getCurrentCamera().getCamPosition().x, JGemsHelper.camera().getCurrentCamera().getCamPosition().y, JGemsHelper.camera().getCurrentCamera().getCamPosition().z));
            } else {
                ImGui.text(String.format("%s %s %s", dynamicPlayer.getPosition().x, dynamicPlayer.getPosition().y, dynamicPlayer.getPosition().z));
            }
            ImGui.text("current speed(scalar): " + String.format("%.4f", entityPlayerSP.getScalarSpeed()));
        } else if (JGemsHelper.camera().getCurrentCamera() != null) {
            ImGui.text(String.format("%s %s %s", JGemsHelper.camera().getCurrentCamera().getCamPosition().x, JGemsHelper.camera().getCurrentCamera().getCamPosition().y, JGemsHelper.camera().getCurrentCamera().getCamPosition().z));
        }
        ImGui.text("entities: " + JGemsHelper.get().getPhysicsWorld().countItems());
        ImGui.text("tick: " + sceneWorld.getTicks());

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
            if (ImGui.checkbox("HDR", JGemsConfig.SYSTEM.USE_HDR)) {
                JGemsConfig.SYSTEM.USE_HDR = !JGemsConfig.SYSTEM.USE_HDR;
            }
            if (ImGui.treeNode("HDR Settings")) {
                float[] exposure = new float[]{JGemsHelper.get().getSceneWorld().getEnvironment().getLightScene().getHdrExposure()};
                ImGui.sliderFloat("exposure", exposure, 0.0f, 5.0f);
                ((LightScene) JGemsHelper.get().getSceneWorld().getEnvironment().getLightScene()).setHdrExposure(exposure[0]);
                float[] gamma = new float[]{JGemsHelper.get().getSceneWorld().getEnvironment().getLightScene().getHdrGamma()};
                ImGui.sliderFloat("gamma", gamma, 0.0f, 3.0f);
                ((LightScene) JGemsHelper.get().getSceneWorld().getEnvironment().getLightScene()).setHdrGamma(gamma[0]);
                ImGui.treePop();
            }

            if (ImGui.checkbox("FXAA", JGemsConfig.SYSTEM.USE_FXAA)) {
                JGemsConfig.SYSTEM.USE_FXAA = !JGemsConfig.SYSTEM.USE_FXAA;
            }
            if (ImGui.checkbox("Bloom", JGemsConfig.SYSTEM.USE_BLOOM)) {
                JGemsConfig.SYSTEM.USE_BLOOM = !JGemsConfig.SYSTEM.USE_BLOOM;
            }

            if (ImGui.checkbox("SSAO", JGemsConfig.SYSTEM.USE_SSAO)) {
                JGemsConfig.SYSTEM.USE_SSAO = !JGemsConfig.SYSTEM.USE_SSAO;
            }
            if (ImGui.treeNode("SSAO Settings")) {
                float[] radius = new float[]{JGemsConfig.SYSTEM.SSAO_RADIUS};
                ImGui.sliderFloat("radius", radius, 0.0f, 5.0f);
                JGemsConfig.SYSTEM.SSAO_RADIUS = radius[0];
                float[] bias = new float[]{JGemsConfig.SYSTEM.SSAO_BIAS};
                ImGui.sliderFloat("bias", bias, 0.0f, 0.1f);
                JGemsConfig.SYSTEM.SSAO_BIAS = bias[0];
                float[] range = new float[]{JGemsConfig.SYSTEM.SSAO_RANGE};
                ImGui.sliderFloat("range", range, 1.0f, 10.0f);
                JGemsConfig.SYSTEM.SSAO_RANGE = range[0];
                ImGui.treePop();
            }

            if (ImGui.checkbox("Shadows", JGemsConfig.SYSTEM.USE_SHADOWS)) {
                JGemsConfig.SYSTEM.USE_SHADOWS = !JGemsConfig.SYSTEM.USE_SHADOWS;
            }
        }

        boolean flag = JGemsHelper.camera().getCurrentCamera() instanceof ControlledCamera;
        if (ImGui.collapsingHeader("Tools")) {
            ImGui.text("Culled Prev Frame (OBJ): " + JGemsOpenGLRenderer.DEBUG_CULLED_OBJECTS);
            ImGui.text("Culled Prev Frame (SBMSH): " + JGemsOpenGLRenderer.DEBUG_CULLED_SUBMESHES);
            if (ImGui.checkbox("Freeze Frustum", sceneRender.getSceneCulling().isFrozen())) {
                sceneRender.getSceneCulling().setFreeze(!sceneRender.getSceneCulling().isFrozen());
            }
            ImGui.spacing();
            if (ImGui.checkbox("FreeCam", flag)) {
                if (!flag) {
                    JGemsHelper.camera().enableFreeCamera(mouseKeyboardController, camera.getCamPosition(), camera.getCamRotation());
                    JGemsHelper.controller().detachController();
                } else {
                    JGemsHelper.camera().enableAttachedCamera((WorldItem) JGems3D.get().getCurrentGameMapPlayer());
                    JGemsHelper.controller().attachControllerTo(mouseKeyboardController, JGems3D.get().getCurrentGameMapPlayer());
                }
            }

            if (ImGui.checkbox("Full Bright", JGemsConfig.DEBUG.FULL_BRIGHT)) {
                JGemsConfig.DEBUG.FULL_BRIGHT = !JGemsConfig.DEBUG.FULL_BRIGHT;
            }

            if (ImGui.checkbox("WireFrame Rendering", JGemsConfig.DEBUG.WIREFRAME_RENDERING)) {
                JGemsConfig.DEBUG.WIREFRAME_RENDERING = !JGemsConfig.DEBUG.WIREFRAME_RENDERING;
            }

            if (ImGui.checkbox("Show Debug Lines", JGemsConfig.DEBUG.SHOW_DEBUG_LINES)) {
                JGemsConfig.DEBUG.SHOW_DEBUG_LINES = !JGemsConfig.DEBUG.SHOW_DEBUG_LINES;
            }

           // if (ImGui.button("Generate NavMesh")) {
           //     Graph graph = JGemsWorldHelper.genSimpleMapGraphFromStartPoint(JGemsCameraHelper.getCurrentCamera().getCamPosition());
           //     String mapName = JGemsCoreHelper.getCurrentMap().getName();
           //     Graph.saveInFile(graph);
           //     if (graph == null || graph.getGraph().isEmpty()) {
           //         LoggingManager.showWindowInfo("Couldn't create NavMesh");
           //     } else {
           //         LoggingManager.showWindowInfo("Created NavMesh(" + graph.getGraph().size() + ") and saved in game folder. " + mapName + ".nav");
           //     }
           //     JGemsCoreHelper.getPhysicsWorld().setMapNavGraph(graph);
           // }
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.setTooltip("Generates NavMesh, starting from current camera position");
                ImGui.endTooltip();
            }
        }

        if (ImGui.collapsingHeader("Frame Buffers")) {
            GL46.glScissor(0, 0, 1, 1);
            ImGui.beginChild("inner1");
            if (ImGui.collapsingHeader("GBuffer")) {
                ImGui.beginChild("Images1", JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 2.0f + 50.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f + 60, true);

                IDeferredRenderNode iDeferredRenderNode = (IDeferredRenderNode) sceneRender.getConveyorNodes().get(JGemsOpenGLRenderer.DEFERRED_RENDER_PASS);

                ImGui.image(iDeferredRenderNode.getOutGBuffer().getTexturePrograms().get(0).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(iDeferredRenderNode.getOutGBuffer().getTexturePrograms().get(1).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.image(iDeferredRenderNode.getOutGBuffer().getTexturePrograms().get(2).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(iDeferredRenderNode.getOutGBuffer().getTexturePrograms().get(3).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.image(iDeferredRenderNode.getOutGBuffer().getTexturePrograms().get(4).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();

                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("Bloom")) {
                IDeferredRenderNode iDeferredRenderNode = (IDeferredRenderNode) sceneRender.getConveyorNodes().get(JGemsOpenGLRenderer.DEFERRED_RENDER_PASS);
                ImGui.beginChild("Images25", JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 2.0f + 50.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f + 60, true);
                ImGui.image(iDeferredRenderNode.getOutColorBuffer().getTextureIDByIndex(1), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("Transparency")) {
                ITransparencyRenderNode iDeferredRenderNode = (ITransparencyRenderNode) sceneRender.getConveyorNodes().get(JGemsOpenGLRenderer.TRANSPARENCY_RENDER_PASS);
                ImGui.beginChild("Images25", JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 2.0f + 50.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f + 60, true);
                ImGui.image(iDeferredRenderNode.getOutColorBuffer().getTextureIDByIndex(0), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("Animations")) {
                ImGui.beginChild("Images2", JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 2.0f + 50.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f + 60, true);
                ImGui.image(JGemsHelper.resources().getAnimationsTextureBuffer().getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
//
                //ImGui.image(sceneRender.getSceneGluingBuffer().getTexturePrograms().get(1).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                //ImGui.sameLine();
                //ImGui.image(sceneRender.getTransparencySceneBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
//
                //ImGui.image(sceneRender.getSkyBoxBackGroundBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                //ImGui.sameLine();
                //ImGui.image(sceneRender.getHdrBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
//
                //ImGui.image(sceneRender.getForwardAndDeferredScenesBuffer().getTexturePrograms().get(0).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("Shadows")) {
                ImGui.beginChild("Images3", JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 2.0f + 50.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f + 60, true);
                JGemsEnvironment environment = (JGemsEnvironment)  sceneRender.getWorld().getEnvironment();

                ImGui.image(environment.getShadowScene().getSunLightShadow().getSunShadowFBO().getTexturePrograms().get(0).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.sameLine();
                ImGui.image(environment.getShadowScene().getSunLightShadow().getSunShadowFBO().getTexturePrograms().get(1).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.image(environment.getShadowScene().getSunLightShadow().getSunShadowFBO().getTexturePrograms().get(2).getTextureId(), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                ImGui.endChild();
            }

            if (ImGui.collapsingHeader("SSAO")) {
                IDeferredRenderNode iDeferredRenderNode = (IDeferredRenderNode) sceneRender.getConveyorNodes().get(JGemsOpenGLRenderer.DEFERRED_RENDER_PASS);

                ImGui.beginChild("Images4", JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 2.0f + 50.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f + 60, true);

                ImGui.image(iDeferredRenderNode.getOutSSAOBuffer().getTextureIDByIndex(0), JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH / 4.0f, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

                ImGui.endChild();
            }
            ImGui.endChild();
        }
        ImGui.end();
    }
}
