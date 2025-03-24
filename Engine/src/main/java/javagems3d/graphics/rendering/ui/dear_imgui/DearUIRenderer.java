package javagems3d.graphics.rendering.ui.dear_imgui;

import api.events.EventBus;
import imgui.*;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.help.JGemsFilesHelper;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import api.events.EventLauncher;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class DearUIRenderer implements IWindow.ResizeEvent {
    private final JGemsShaderManager shaderManager;
    private DearUIMesh dearImGuiMesh;
    private ITexture2DProgram textureSample;
    private GLFWKeyCallback prevKeyCallback;
    private final IWindow window;

    public DearUIRenderer(@NotNull IWindow window, @NotNull JGemsShaderManager imguiShader, @Nullable JGemsPath pathToJarFont, @NotNull SystemResources systemResources) {
        this.shaderManager = imguiShader;
        this.window = window;

        this.createUIResources(systemResources, pathToJarFont);
        this.createUICallbacks(this.getWindow());
    }

    private void createUIResources(SystemResources systemResources, @Nullable JGemsPath pathToJarFont) {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setDisplaySize(this.getWindow().getWindowSize().x, this.getWindow().getWindowSize().y);

        ImFontAtlas fontAtlas = imGuiIO.getFonts();

        if (pathToJarFont != null) {
            try (InputStream stream = JGems3D.loadFileFromJar(pathToJarFont)) {
                byte[] fontData = JGemsFilesHelper.toByteArray(stream, 8 * 1024);
                ImFontConfig fontConfig = new ImFontConfig();
                fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());
                fontAtlas.addFontFromMemoryTTF(fontData, 12, fontConfig);
                fontConfig.destroy();
            } catch (IOException e) {
                Log.get().exception(e);
            }
        }

        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        this.textureSample = systemResources.createTexture(null, "imgui_fonts", buffer, new Vector2i(width.get(), height.get()), new ImageTexture.Properties(false, false, false, false, false));

        this.dearImGuiMesh = new DearUIMesh();
    }

    private void createUICallbacks(IWindow window) {
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

    public void onRender(MouseKeyboardController mouseKeyboardController, DearUIInterface dearUIInterface, FrameTicking frameTicking) {
        ImGui.newFrame();
        dearUIInterface.drawGui(this.getWindow().getWindowSize(), mouseKeyboardController);
        EventLauncher.pushEvent(new EventBus.DearIMGUIRender(this.getWindow().getWindowSize(), this));
        ImGui.endFrame();
        ImGui.render();

        ImDrawData drawData = ImGui.getDrawData();

        ImGuiIO io = ImGui.getIO();
        float delta = frameTicking.getFrameDeltaTime();
        if (delta == 0.0f) {
            delta = 1.0f;
        }
        io.setDeltaTime(delta);

        ImVec2 dSize = new ImVec2();
        io.getDisplaySize(dSize);

        this.getShaderManager().beginShading();
        this.getShaderManager().performUniform(new UniformString("scale"), UniformFunctions.VEC2F(new Vector2f(2.0f / dSize.x, -2.0f / dSize.y)));
        this.getShaderManager().performUniform(new UniformString("texture_sampler"), UniformFunctions.INTEGER(0));

        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendEquation(GL46.GL_FUNC_ADD);
        GL46.glBlendFuncSeparate(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA, GL46.GL_ONE, GL46.GL_ONE_MINUS_SRC_ALPHA);
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        GL46.glDisable(GL46.GL_CULL_FACE);

        GL46.glBindVertexArray(this.getImguiMesh().getVaoId());
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.getImguiMesh().getVerticesVbo());
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, this.getImguiMesh().getIndicesVbo());

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
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL46.GL_STREAM_DRAW);
            GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL46.GL_STREAM_DRAW);

            for (int j = 0; j < drawData.getCmdListCmdBufferSize(i); j++) {
                final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
                final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

                int textureId = drawData.getCmdListCmdBufferTextureId(i, j);
                GL46.glActiveTexture(GL46.GL_TEXTURE0);
                if (textureId > 0) {
                    GL46.glBindTexture(GL46.GL_TEXTURE_2D, textureId);
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

                GL46.glEnable(GL46.GL_SCISSOR_TEST);
                GL46.glScissor((int) clipMinX, (int) (fbHeight - clipMaxY), (int) (clipMaxX - clipMinX), (int) (clipMaxY - clipMinY));
                GL46.glDrawElements(GL46.GL_TRIANGLES, elemCount, GL46.GL_UNSIGNED_SHORT, indices);
                GL46.glDisable(GL46.GL_SCISSOR_TEST);
            }
        }

        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_CULL_FACE);
        GL46.glDisable(GL46.GL_BLEND);

        this.getShaderManager().endShading();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setMousePos((float) mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinates()[0], (float) mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinates()[1]);
        imGuiIO.setMouseDown(0, mouseKeyboardController.getMouseAndKeyboard().isLeftKeyPressed());
        imGuiIO.setMouseDown(1, mouseKeyboardController.getMouseAndKeyboard().isRightKeyPressed());
        imGuiIO.setMouseWheel(mouseKeyboardController.getMouseAndKeyboard().getScrollVector());
    }

    public IWindow getWindow() {
        return this.window;
    }

    public DearUIMesh getImguiMesh() {
        return this.dearImGuiMesh;
    }

    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public ITexture2DProgram getTextureSample() {
        return this.textureSample;
    }

    public void destroyUI() {
        this.getImguiMesh().clear();
        if (this.getTextureSample() != null) {
            this.getTextureSample().clear();
        }
        if (this.prevKeyCallback != null) {
            this.prevKeyCallback.free();
        }
    }

    @Override
    public void onWindowResize(IWindow window) {
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(window.getWindowSize().x, window.getWindowSize().y);
    }
}
