package ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui;

import imgui.*;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.render.opengl.dear_imgui.DIMGuiMesh;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.controller.TBoxControllerDispatcher;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.ImGuiContent;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;
import ru.alfabouh.jgems3d.toolbox.resources.samples.TextureSample;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import java.nio.ByteBuffer;

public class DIMGuiRenderTBox {
    private final TBoxShaderManager shaderManager;
    private DIMGuiMesh dearImGuiMesh;
    private TextureSample textureSample;
    private GLFWKeyCallback prevKeyCallback;
    private ImGuiContent currentContentToRender;

    public DIMGuiRenderTBox(IWindow window, ResourceCache ResourceCache) {
        this.shaderManager = ResourceManager.shaderAssets.imgui;

        this.createUIResources(ResourceCache, window.getWindowDimensions());
        this.createUICallbacks(window);
        this.currentContentToRender = null;
    }

    private void createUIResources(ResourceCache ResourceCache, Vector2i windowSize) {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setDisplaySize(windowSize.x, windowSize.y);

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();

        ByteBuffer buffer = fontAtlas.getTexDataAsRGBA32(width, height);
        this.textureSample = TextureSample.createTexture(ResourceCache, "imgui_fonts", width.get(), height.get(), buffer);
        this.dearImGuiMesh = new DIMGuiMesh();
    }

    private void createUICallbacks(IWindow window) {
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
        io.setKeyMap(ImGuiKey.C, GLFW.GLFW_KEY_C);
        io.setKeyMap(ImGuiKey.X, GLFW.GLFW_KEY_X);
        io.setKeyMap(ImGuiKey.A, GLFW.GLFW_KEY_A);
        io.setKeyMap(ImGuiKey.V, GLFW.GLFW_KEY_V);

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

    public void render(float partialTicks) {
        if (this.getCurrentContentToRender() == null) {
            return;
        }
        TBoxControllerDispatcher controllerDispatcher = ToolBox.get().getScreen().getControllerDispatcher();
        this.drawGui(controllerDispatcher, partialTicks);

        ImDrawData drawData = ImGui.getDrawData();

        ImGuiIO io = ImGui.getIO();
        io.setKeyCtrl(TBoxControllerDispatcher.bindingManager().keyCtrl.isPressed());
        io.setKeyShift(TBoxControllerDispatcher.bindingManager().keyShift.isPressed());

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

    private void drawGui(TBoxControllerDispatcher controllerDispatcher, float partialTicks) {
        MouseKeyboardController mouseKeyboardController = controllerDispatcher.getMouseKeyboardController();
        Vector2i dim = ToolBox.get().getScreen().getDimensions();

        ImGui.newFrame();
        this.getCurrentContentToRender().drawContent(dim, partialTicks);
        ImGui.endFrame();
        ImGui.render();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.clearInputKeys();
        imGuiIO.setMousePos((float) mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinates()[0], (float) mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinates()[1]);
        imGuiIO.setMouseDown(0, mouseKeyboardController.getMouseAndKeyboard().isLeftKeyPressed());
        imGuiIO.setMouseDown(1, mouseKeyboardController.getMouseAndKeyboard().isRightKeyPressed());
        imGuiIO.setMouseWheel(mouseKeyboardController.getMouseAndKeyboard().getScrollVector());
    }

    public void onResize(Vector2i size) {
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(size.x, size.y);
    }

    public void setCurrentContentToRender(ImGuiContent currentContentToRender) {
        this.currentContentToRender = currentContentToRender;
    }

    public ImGuiContent getCurrentContentToRender() {
        return this.currentContentToRender;
    }

    public DIMGuiMesh getImguiMesh() {
        return this.dearImGuiMesh;
    }

    public TBoxShaderManager getShaderManager() {
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
