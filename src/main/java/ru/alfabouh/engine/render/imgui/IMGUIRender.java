package ru.alfabouh.engine.render.imgui;

import imgui.*;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.controller.ControllerDispatcher;
import ru.alfabouh.engine.system.controller.input.MouseKeyboardController;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.system.resources.cache.GameCache;
import ru.alfabouh.engine.physics.entities.player.IPlayer;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.debug.constants.GlobalRenderDebugConstants;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;
import ru.alfabouh.engine.render.screen.Screen;
import ru.alfabouh.engine.render.screen.window.Window;

import java.nio.ByteBuffer;

public class IMGUIRender {
    private final ShaderManager shaderManager;
    private IMGUIMesh imguiMesh;
    private TextureSample textureSample;
    private GLFWKeyCallback prevKeyCallback;

    public IMGUIRender(Window window, GameCache gameCache) {
        this.shaderManager = ResourceManager.shaderAssets.imgui;

        this.createUIResources(gameCache, window.getWindowDimensions());
        this.createUICallbacks(window);
    }

    private void createUIResources(GameCache gameCache, Vector2i windowSize) {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setDisplaySize(windowSize.x, windowSize.y);

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();

        ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        this.textureSample = TextureSample.createTexture(gameCache, "imgui_fonts", width.get(), height.get(), buffer);
        this.imguiMesh = new IMGUIMesh();
    }

    private void createUICallbacks(Window window) {
        ImGuiIO io = this.getImGuiIO();

        this.prevKeyCallback = GLFW.glfwSetKeyCallback(window.getDescriptor(), (descriptor, key, scanCode, action, mods) -> {
            if (!io.getWantCaptureKeyboard()) {
                if (prevKeyCallback != null) {
                    prevKeyCallback.invoke(descriptor, key, scanCode, action, mods);
                }
                return;
            }

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

    private ImGuiIO getImGuiIO() {
        ImGuiIO io = ImGui.getIO();
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
        return io;
    }

    public void render(double partialTicks) {
        ControllerDispatcher controllerDispatcher = JGems.get().getScreen().getControllerDispatcher();
        if (JGems.DEBUG_MODE && controllerDispatcher.getCurrentController() instanceof MouseKeyboardController) {
            this.drawGui(controllerDispatcher);

            ImDrawData drawData = ImGui.getDrawData();

            ImGuiIO io = ImGui.getIO();
            ImVec2 dSize = new ImVec2();
            io.getDisplaySize(dSize);

            this.getShaderManager().bind();
            this.getShaderManager().performUniform("scale", new Vector2f(2.0f / dSize.x, -2.0f / dSize.y));
            this.getShaderManager().performUniform("texture_sampler", 0);

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

    private boolean fullBright;
    private boolean debugLines;
    private boolean freeCam;
    private boolean psx = true;

    private void drawGui(ControllerDispatcher controllerDispatcher) {
        if (!JGems.get().isValidPlayer()) {
            return;
        }
        MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controllerDispatcher.getCurrentController();
        ICamera camera = JGems.get().getScreen().getCamera();
        IPlayer entityPlayerSP = JGems.get().getPlayerSP();
        SceneWorld sceneWorld = JGems.get().getSceneWorld();
        SceneRender sceneRender = JGems.get().getScreen().getScene().getSceneRender();

        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        //ImGui.setNextWindowSize(600, 600);
        ImGui.begin("Debug");

        //GlobalRenderDebugConstants.ENABLE_PSX = false;

        ImGui.text("FPS: " + Screen.RENDER_FPS + " | TPS: " + Screen.PHYS_TPS);
        if (entityPlayerSP instanceof KinematicPlayerSP) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;
            ImGui.text(String.format("%s %s %s | %s %s %s", (float) kinematicPlayerSP.getPosition().x, (float) kinematicPlayerSP.getPosition().y, (float) kinematicPlayerSP.getPosition().z, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().x, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().y, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().z));
            ImGui.text("entities: " + JGems.get().getPhysicsWorld().countItems());
            ImGui.text("tick: " + sceneWorld.getTicks());
            ImGui.text("current speed(scalar): " + String.format("%.4f", kinematicPlayerSP.getScalarSpeed()));
        }

        if (ImGui.checkbox("FreeCam", this.freeCam)) {
            this.freeCam = !this.freeCam;
            if (this.freeCam) {
                JGems.get().getScreen().getScene().enableFreeCamera(mouseKeyboardController, camera.getCamPosition(), camera.getCamRotation());
                JGems.get().getScreen().getControllerDispatcher().detachController();
            } else {
                JGems.get().getScreen().getScene().enableAttachedCamera((WorldItem) JGems.get().getPlayerSP());
                JGems.get().getScreen().getControllerDispatcher().attachControllerTo(mouseKeyboardController, JGems.get().getPlayerSP());
            }
        }
        if (ImGui.checkbox("Full Bright", this.fullBright)) {
            this.fullBright = !this.fullBright;
            GlobalRenderDebugConstants.FULL_BRIGHT = this.fullBright;
        }
        if (ImGui.checkbox("Show Debug Lines", this.debugLines)) {
            this.debugLines = !this.debugLines;
            GlobalRenderDebugConstants.SHOW_DEBUG_LINES = this.debugLines;
        }
        if (ImGui.checkbox("PSX", this.psx)) {
            this.psx = !this.psx;
            GlobalRenderDebugConstants.ENABLE_PSX = this.psx;
        }

        if (ImGui.collapsingHeader("Frame Buffers")) {
            GL30.glScissor(0, 0, 1, 1);
            ImGui.beginChild("Images", Window.defaultW / 2.0f + 50.0f, 600.0f, true);

            ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(0).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.sameLine();
            ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(1).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

            ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(2).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.sameLine();
            ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(3).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

            ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(4).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.sameLine();
            ImGui.image(sceneRender.getGBuffer().getTexturePrograms().get(5).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

            ImGui.image(sceneRender.getSceneFbo().getTexturePrograms().get(0).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            ImGui.sameLine();
            ImGui.image(sceneRender.getShadowScene().getFrameBufferObjectProgram().getTexturePrograms().get(0).getTextureId(), Window.defaultW / 4.0f, Window.defaultH / 4.0f, 0.0f, 1.0f, 1.0f, 0.0f);

            ImGui.endChild();
        }

        ImGui.end();
        ImGui.endFrame();
        ImGui.render();

        ImGuiIO imGuiIO = ImGui.getIO();

        imGuiIO.setMousePos((float) mouseKeyboardController.getMouse().getCursorCoordinates()[0], (float) mouseKeyboardController.getMouse().getCursorCoordinates()[1]);
        imGuiIO.setMouseDown(0, mouseKeyboardController.getMouse().isLeftKeyPressed());
        imGuiIO.setMouseDown(1, mouseKeyboardController.getMouse().isRightKeyPressed());
        imGuiIO.setMouseWheel(mouseKeyboardController.getMouse().getScrollVector());
    }

    public void onResize(Vector2i size) {
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(size.x, size.y);
    }

    public IMGUIMesh getImguiMesh() {
        return this.imguiMesh;
    }

    public ShaderManager getShaderManager() {
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
