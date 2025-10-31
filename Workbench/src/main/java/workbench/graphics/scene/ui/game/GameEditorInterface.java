package workbench.graphics.scene.ui.game;

import imgui.ImGui;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.project.map.WBenchMapProjectManager;

public class GameEditorInterface implements DearUIInterface {
    private final WBenchOpenGLRenderer openGLRenderer;
    private final WBenchMapProjectManager WBenchMapProjectManager;

    public GameEditorInterface(WBenchOpenGLRenderer openGLRenderer, @NotNull WBenchMapProjectManager WBenchMapProjectManager) {
        this.WBenchMapProjectManager = WBenchMapProjectManager;
        this.openGLRenderer = openGLRenderer;

        this.clear();
    }

    public void clear() {
    }

    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        if (false) {
            ImGui.setNextWindowFocus();
            ImGui.showDemoWindow();
        }
    }

    public WBenchOpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    public WBenchMapProjectManager getProjectManager() {
        return this.WBenchMapProjectManager;
    }
}