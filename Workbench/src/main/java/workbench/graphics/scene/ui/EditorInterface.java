package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.controller.base.MouseKeyboardController;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import workbench.WBench;
import workbench.graphics.scene.nodes.WDeferredRenderNode;
import workbench.graphics.scene.nodes.templates.WIGluingRenderNode;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.project.ProjectManager;

public class EditorInterface implements DearUIInterface {
    private final ProjectManager projectManager;

    public EditorInterface(@NotNull ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        if (this.getProjectManager().getCurrentProject() == null) {
            return;
        }
        if (false) {
            ImGui.setNextWindowFocus();
            ImGui.showDemoWindow();
        }

        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("Project")) {
            if (ImGui.menuItem("Compile")) {

            }
            if (ImGui.menuItem("Exit")) {
                if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                    WBench.get().getProjectManager().closeProject(false);
                    ImGui.endMenu();
                    ImGui.endMainMenuBar();
                    return;
                }
            }
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();
        final float YOffset = ImGui.getFrameHeight();

        final float sceneWindowSizeX = windowSize.x * 0.6f;
        final float sceneWindowSizeY = windowSize.y * 0.7f;

        final float consoleWindowSizeX = sceneWindowSizeX;
        final float consoleWindowSizeY = windowSize.y - sceneWindowSizeY;
        final float sceneWindowOffset = (windowSize.x - sceneWindowSizeX) * 0.5f;

        final float entitiesWindowSizeX = (windowSize.x - sceneWindowSizeX) - sceneWindowOffset;
        final float entitiesWindowSizeY = windowSize.y * 0.5f;

        final float resourcesWindowSizeX = (windowSize.x - sceneWindowSizeX) - sceneWindowOffset;
        final float resourcesWindowSizeY =  windowSize.y * 0.5f;

        final float propertiesWindowSizeX = (windowSize.x - sceneWindowSizeX) - sceneWindowOffset;
        final float propertiesWindowSizeY =  windowSize.y;

        ImGui.begin("Scene", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(sceneWindowSizeX, sceneWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowOffset, YOffset);
        this.sceneContent(sceneWindowSizeX, sceneWindowSizeY - YOffset);
        ImGui.end();

        ImGui.begin("Output", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(consoleWindowSizeX, consoleWindowSizeY);
        ImGui.setWindowPos(sceneWindowOffset, windowSize.y - consoleWindowSizeY);
        this.consoleContent();
        ImGui.end();

        ImGui.begin("Items", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(entitiesWindowSizeX, entitiesWindowSizeY - YOffset);
        ImGui.setWindowPos(0, YOffset);
        this.itemsContent();
        ImGui.end();

        ImGui.begin("Resources", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(resourcesWindowSizeX, resourcesWindowSizeY);
        ImGui.setWindowPos(0, windowSize.y - entitiesWindowSizeY);
        this.resourcesContent();
        ImGui.end();

        ImGui.begin("Properties", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.setWindowSize(propertiesWindowSizeX, propertiesWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowSizeX + sceneWindowOffset, YOffset);
        this.propertiesContent();
        ImGui.end();
    }

    private void itemsContent() {
    }

    private void resourcesContent() {
        if (ImGui.collapsingHeader("Models")) {
            if (ImGui.button("+")) {
            }
            ImGui.sameLine();
            if (ImGui.button("-")) {
            }
        }

        if (ImGui.collapsingHeader("Sounds")) {
        }

        if (ImGui.collapsingHeader("Scripts")) {
        }

        ImGui.separator();
    }

    private void propertiesContent() {
    }

    private void sceneContent(float sizeX, float sizeY) {
        WBenchOpenGLRenderer wBenchOpenGLRenderer = (WBenchOpenGLRenderer) WBench.get().getScreen().getScene().getSceneRenderer();
        //WIGluingRenderNode gluingRenderNode = (WIGluingRenderNode) wBenchOpenGLRenderer.getRenderNodeByPass(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
        WDeferredRenderNode gluingRenderNode = (WDeferredRenderNode) wBenchOpenGLRenderer.getRenderNodeByPass(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS);
        float availableX = ImGui.getContentRegionAvailX();
        float availableY = ImGui.getContentRegionAvailY();
        ImGui.image(gluingRenderNode.getOutColorBuffer().getTextureByIndex(0).getTextureId(), availableX, availableY, 0.0f, 1.0f, 1.0f, 0.0f);
    }

    private void consoleContent() {
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
    }

    public ProjectManager getProjectManager() {
        return this.projectManager;
    }
}