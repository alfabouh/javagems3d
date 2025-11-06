package workbench.graphics.scene.ui.game;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.SelectedScene;
import workbench.graphics.screen.WBenchScreen;
import workbench.project.map.WBenchMapProjectManager;

public class GameEditorInterface implements DearUIInterface {
    private final WBenchOpenGLRenderer openGLRenderer;
    private final WBenchMapProjectManager WBenchMapProjectManager;
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;

    public GameEditorInterface(WBenchOpenGLRenderer openGLRenderer, @NotNull WBenchMapProjectManager WBenchMapProjectManager) {
        this.WBenchMapProjectManager = WBenchMapProjectManager;
        this.openGLRenderer = openGLRenderer;
        this.resourcesInterfaceComponentG = new ResourcesInterfaceComponentG();
        this.clear();
    }

    public void clear() {
        this.resourcesInterfaceComponentG.clear();
    }

    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        if (false) {
            ImGui.setNextWindowFocus();
            ImGui.showDemoWindow();
        }

        if (ProjectUIUtils.ctrlS()) {
            WBench.get().getGameProjectManager().saveGameProject(false);
            Log.get().info("Saved...");
        }

        final float YOffset = ImGui.getFrameHeight();

        final float sceneWindowSizeX = windowSize.x * 0.6f;
        final float sceneWindowSizeY = windowSize.y * 0.7f;

        final float consoleWindowSizeX = sceneWindowSizeX;
        final float consoleWindowSizeY = windowSize.y - sceneWindowSizeY;
        final float sceneWindowOffset = (windowSize.x - sceneWindowSizeX) * 0.5f;

        final float resourcesWindowSizeX = (windowSize.x - sceneWindowSizeX) - sceneWindowOffset;
        final float resourcesWindowSizeY =  windowSize.y * 1.0f;

        final float propertiesWindowSizeX = (windowSize.x - sceneWindowSizeX) - sceneWindowOffset;
        final float propertiesWindowSizeY =  windowSize.y;

        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("Game Project")) {
            //TODO
            if (ImGui.menuItem("Run Game")) {
                //JGems3D.IsolatedProcessLauncher.EXEC(JGemsLaunchArgsRegistry.getArgumentFrom(
                //        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.MAP_TEST, "true"),
                //        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.DEBUG, "true"),
                //        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.NO_SOUND, "true"),
                //        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.NO_FULL_SCREEN, "true")
                //));
            }
            ImGui.separator();
            if (ImGui.menuItem("Save")) {
                Log.get().info("Saved...");
                WBench.get().getGameProjectManager().saveGameProject(false);
            }
            //TODO
            if (ImGui.menuItem("Exit")) {
                if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                    WBench.get().getGameProjectManager().closeGameProject();
                    ImGui.endMenu();
                    ImGui.endMainMenuBar();
                    return;
                }
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Build")) {
            if (ImGui.menuItem("Compile")) {

            }
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();

        ImGui.begin("Window", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);

        ImGui.sameLine();
        int posX = (int) sceneWindowOffset;
        int posY = (int) YOffset;
        int sizeX = (int) sceneWindowSizeX;
        int sizeY = (int) (sceneWindowSizeY - YOffset);
        ImGui.setWindowSize(sizeX, sizeY);
        ImGui.setWindowPos(posX, posY);
        //this.getSceneComponent().sceneContent();
        ImGui.end();

        ImGui.begin("Output", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(consoleWindowSizeX, consoleWindowSizeY);
        ImGui.setWindowPos(sceneWindowOffset, windowSize.y - consoleWindowSizeY);
        MapEditorInterface.consoleContent();
        ImGui.end();

        ImGui.begin("Resources", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(resourcesWindowSizeX, resourcesWindowSizeY);
        ImGui.setWindowPos(0, 0);
        //this.getResourcesComponent().resourcesContent();
        this.getResourcesInterfaceComponentG().resourcesContent();
        ImGui.end();

        ImGui.begin("Actions", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(propertiesWindowSizeX, propertiesWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowSizeX + sceneWindowOffset, YOffset);
        //this.getActionsContent().actionsContent();
        ImGui.end();

        //this.getContextComponent().context();
    }

    public ResourcesInterfaceComponentG getResourcesInterfaceComponentG() {
        return this.resourcesInterfaceComponentG;
    }

    public WBenchOpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    public WBenchMapProjectManager getProjectManager() {
        return this.WBenchMapProjectManager;
    }
}