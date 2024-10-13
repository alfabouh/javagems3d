/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.dear_imgui;

import imgui.*;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import javagems3d.physics.world.basic.WorldItem;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL30;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import api.bridge.events.APIEventsLauncher;
import javagems3d.graphics.opengl.camera.FreeControlledCamera;
import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.graphics.opengl.screen.JGemsScreen;
import javagems3d.graphics.opengl.screen.window.Window;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.controller.objects.MouseKeyboardController;
import javagems3d.system.graph.Graph;
import javagems3d.system.resources.assets.material.samples.TextureSample;
import javagems3d.system.resources.assets.shaders.base.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.manager.JGemsResourceManager;
import api.app.events.bus.Events;
import logger.managers.LoggingManager;

import java.nio.ByteBuffer;

public class DIMGuiRenderJGems {
    private final JGemsShaderManager shaderManager;
    private DIMGuiMesh dearImGuiMesh;
    private TextureSample textureSample;
    private GLFWKeyCallback prevKeyCallback;

    public DIMGuiRenderJGems(Window window, ResourceCache resourceCache) {
        this.shaderManager = JGemsResourceManager.globalShaderAssets.imgui;

        this.createUIResources(resourceCache, window.getWindowDimensions());
        this.createUICallbacks(window);
    }

    private void createUIResources(ResourceCache resourceCache, Vector2i windowSize) {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setDisplaySize(windowSize.x, windowSize.y);

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();

        ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        this.textureSample = TextureSample.registerTexture(resourceCache, "imgui_fonts", new Vector2i(width.get(), height.get()), buffer, new TextureSample.Params(false, false, false, false));
        this.dearImGuiMesh = new DIMGuiMesh();
    }

    private void createUICallbacks(Window window) {
        ImGuiIO io = ImGui.getIO();
        io.setKeyMap(ImGuiKey.C, GLFW.GLFW_KEY_C);
        io.setKeyMap(ImGuiKey.X, GLFW.GLFW_KEY_X);
        io.setKeyMap(ImGuiKey.A, GLFW.GLFW_KEY_A);
        io.setKeyMap(ImGuiKey.V, GLFW.GLFW_KEY_V);
        io.setKeyMap(ImGuiKey.Z, GLFW.GLFW_KEY_Z);
        io.setKeyMap(ImGuiKey.Y, GLFW.GLFW_KEY_Y);

        io.setKeyMap(ImGuiKey.Tab, GLFW.GLFW_KEY_TAB);
        io.setKeyMap(ImGuiKey.LeftArrow, GLFW.GLFW_KEY_LEFT);
        io.setKeyMap(ImGuiKey.RightArrow, GLFW.GLFW_KEY_RIGHT);
        io.setKeyMap(ImGuiKey.UpArrow, GLFW.GLFW_KEY_UP);
        io.setKeyMap(ImGuiKey.DownArrow, GLFW.GLFW_KEY_DOWN);
        io.setKeyMap(ImGuiKey.PageUp, GLFW.GLFW_KEY_PAGE_UP);
        io.setKeyMap(ImGuiKey.PageDown, GLFW.GLFW_KEY_PAGE_DOWN);
        io.setKeyMap(ImGuiKey.Home, GLFW.GLFW_KEY_HOME);
        io.setKeyMap(ImGuiKey.End, GLFW.GLFW_KEY_END);
        io.setKeyMap(ImGuiKey.Insert, GLFW.GLFW_KEY_INSERT);
        io.setKeyMap(ImGuiKey.Delete, GLFW.GLFW_KEY_DELETE);
        io.setKeyMap(ImGuiKey.Backspace, GLFW.GLFW_KEY_BACKSPACE);
        io.setKeyMap(ImGuiKey.Space, GLFW.GLFW_KEY_SPACE);
        io.setKeyMap(ImGuiKey.Enter, GLFW.GLFW_KEY_ENTER);
        io.setKeyMap(ImGuiKey.Escape, GLFW.GLFW_KEY_ESCAPE);
        io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW.GLFW_KEY_KP_ENTER);

        this.prevKeyCallback = GLFW.glfwSetKeyCallback(window.getDescriptor(), (descriptor, key, scanCode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW.GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER));
        });

        GLFW.glfwSetCharCallback(window.getDescriptor(), (descriptor, c) -> {
            if (!io.getWantCaptureKeyboard()) {
                return;
            }
            io.addInputCharacter(c);
        });
    }

    public void onRender(Vector2i windowSize, FrameTicking frameTicking) {
        JGemsControllerDispatcher controllerDispatcher = JGems3D.get().getScreen().getControllerDispatcher();
        if (JGems3D.DEBUG_MODE && controllerDispatcher.getCurrentController() instanceof MouseKeyboardController) {
            this.drawGui(windowSize, controllerDispatcher);

            ImDrawData drawData = ImGui.getDrawData();

            ImGuiIO io = ImGui.getIO();
            float delta = frameTicking.getFrameDeltaTime();
            if (delta == 0.0f) {
                delta = 1.0f;
            }
            io.setDeltaTime(delta);

            ImVec2 dSize = new ImVec2();
            io.getDisplaySize(dSize);

            this.getShaderManager().bind();
            this.getShaderManager().performUniform(new UniformString("scale"), new Vector2f(2.0f / dSize.x, -2.0f / dSize.y));
            this.getShaderManager().performUniform(new UniformString("texture_sampler"), 0);

            GL30.glEnable(GL30.GL_BLEND);
            GL30.glBlendEquation(GL30.GL_FUNC_ADD);
            GL30.glBlendFuncSeparate(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA, GL30.GL_ONE, GL30.GL_ONE_MINUS_SRC_ALPHA);
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            GL30.glDisable(GL30.GL_CULL_FACE);

            GL30.glBindVertexArray(this.getImguiMesh().getVaoId());
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getImguiMesh().getVerticesVbo());
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.getImguiMesh().getIndicesVbo());

            int numLists = drawData.getCmdListsCount();

            ImVec2 dPos = new ImVec2();
            ImVec2 fbScale = new ImVec2();

            drawData.getDisplayPos(dPos);
            drawData.getFramebufferScale(fbScale);

            final float clipOffX = dPos.x;
            final float clipOffY = dPos.y;
            final float clipScaleX = fbScale.x;
            final float clipScaleY = fbScale.y;

            for (int i = 0; i < numLists; i++) {
                GL30.glBufferData(GL30.GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL30.GL_STREAM_DRAW);
                GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL30.GL_STREAM_DRAW);

                for (int j = 0; j < drawData.getCmdListCmdBufferSize(i); j++) {
                    final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
                    final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                    final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

                    int textureId = drawData.getCmdListCmdBufferTextureId(i, j);
                    GL30.glActiveTexture(GL30.GL_TEXTURE0);
                    if (textureId > 0) {
                        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId);
                    } else {
                        this.getTextureSample().bindTexture();
                    }

                    ImVec4 clipRect = drawData.getCmdListCmdBufferClipRect(i, j);

                    final float clipMinX = (clipRect.x - clipOffX) * clipScaleX;
                    final float clipMinY = (clipRect.y - clipOffY) * clipScaleY;
                    final float clipMaxX = (clipRect.z - clipOffX) * clipScaleX;
                    final float clipMaxY = (clipRect.w - clipOffY) * clipScaleY;
                    final int fbHeight = (int) (dSize.y * fbScale.y);

                    if (clipMaxX <= clipMinX || clipMaxY <= clipMinY) {
                        continue;
                    }

                    GL30.glEnable(GL30.GL_SCISSOR_TEST);
                    GL30.glScissor((int) clipMinX, (int) (fbHeight - clipMaxY), (int) (clipMaxX - clipMinX), (int) (clipMaxY - clipMinY));
                    GL30.glDrawElements(GL30.GL_TRIANGLES, elemCount, GL30.GL_UNSIGNED_SHORT, indices);
                    GL30.glDisable(GL30.GL_SCISSOR_TEST);
                }
            }

            GL30.glEnable(GL30.GL_DEPTH_TEST);
            GL30.glEnable(GL30.GL_CULL_FACE);
            GL30.glDisable(GL30.GL_BLEND);

            this.getShaderManager().unBind();
        }
    }

    private void drawGui(Vector2i windowSize, JGemsControllerDispatcher controllerDispatcher) {
        if (!JGems3D.get().isValidPlayer()) {
            return;
        }
        MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controllerDispatcher.getCurrentController();
        ICamera camera = JGems3D.get().getScreen().getCamera();
        IPlayer entityPlayerSP = JGems3D.get().getPlayer();
        SceneWorld sceneWorld = JGems3D.get().getSceneWorld();
        JGemsOpenGLRenderer sceneRender = JGems3D.get().getScreen().getScene().getSceneRenderer();

        ImGui.newFrame();

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

                ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(5).getTextureId(), JGemsSceneGlobalConstants.defaultW / 4.0f, JGemsSceneGlobalConstants.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
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

        APIEventsLauncher.pushEvent(new Events.DearIMGUIRender(windowSize, this));

        ImGui.endFrame();
        ImGui.render();

        ImGuiIO imGuiIO = ImGui.getIO();

        imGuiIO.setMousePos((float) mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinates()[0], (float) mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinates()[1]);
        imGuiIO.setMouseDown(0, mouseKeyboardController.getMouseAndKeyboard().isLeftKeyPressed());
        imGuiIO.setMouseDown(1, mouseKeyboardController.getMouseAndKeyboard().isRightKeyPressed());
        imGuiIO.setMouseWheel(mouseKeyboardController.getMouseAndKeyboard().getScrollVector());
    }

    public void onResize(Vector2i size) {
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(size.x, size.y);
    }

    public DIMGuiMesh getImguiMesh() {
        return this.dearImGuiMesh;
    }

    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public TextureSample getTextureSample() {
        return this.textureSample;
    }

    public void cleanUp() {
        this.getImguiMesh().cleanUp();
        if (this.prevKeyCallback != null) {
            this.prevKeyCallback.free();
        }
    }
}
