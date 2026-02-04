package workbench.graphics.scene.ui.map;

import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.JGems3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.binding.Binding;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import workbench.WBench;
import workbench.controller.binding.WBenchBindingManager;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.map.editor.*;
import workbench.graphics.scene.ui.map.editor.utils.GlobalSceneRenderingVars;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.graphics.screen.WBenchScreen;
import workbench.project.map.WBenchMapProjectManager;

import java.util.*;
import java.util.stream.Collectors;

public class MapEditorInterface implements DearUIInterface {
    private SelectedScene selectedScene;
    private final TextEditor textEditor;

    public static final Object monitor = new Object();
    private final WBenchOpenGLRenderer openGLRenderer;
    private final WBenchMapProjectManager WBenchMapProjectManager;

    private WBenchObject currentSelectedObject;
    private WBenchObjectTemplate currentSelectedTemplate;

    private ICamera oldCamera;
    private final FBOTexture2DProgram scenePreview;

    private final ActionsInterfaceComponentM actionsContent;
    private final ItemsInterfaceComponentM itemsComponent;
    private final ResourcesInterfaceComponentM resourcesComponent;
    private final SceneInterfaceComponentM sceneComponent;

    public static boolean isCursorInsideScene;

    public MapEditorInterface(WBenchOpenGLRenderer openGLRenderer, FBOTexture2DProgram scenePreview, @NotNull WBenchMapProjectManager WBenchMapProjectManager) {
        this.textEditor = new TextEditor();

        this.WBenchMapProjectManager = WBenchMapProjectManager;
        this.openGLRenderer = openGLRenderer;
        this.scenePreview = scenePreview;

        this.actionsContent = new ActionsInterfaceComponentM(this);
        this.itemsComponent = new ItemsInterfaceComponentM(this);
        this.resourcesComponent = new ResourcesInterfaceComponentM(this);
        this.sceneComponent = new SceneInterfaceComponentM(this);

        this.clear();
    }

    public void clear() {
        this.getSceneComponent().clear();
        this.getActionsContent().clear();
        this.getSceneComponent().clear();
        this.getItemsComponent().clear();

        this.selectedScene = SelectedScene.MAIN;

        this.currentSelectedObject = null;
        this.currentSelectedTemplate = null;
        this.oldCamera = null;
    }

    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        if (WBench.get().getMapProjectManager().getCurrentMapProject() == null) {
            return;
        }
        if (false) {
            ImGui.setNextWindowFocus();
            ImGui.showDemoWindow();
        }

        if (ProjectUIUtils.ctrlS()) {
            WBench.get().getMapProjectManager().saveMapProject(false);
            Log.get().info("Saved...");
        }

        boolean deleteCurrentObject = ImGui.isKeyPressed(WBench.get().getBindingManager().keyDelete.getKeyCode(), false);
        if (deleteCurrentObject) {
            this.removeObjectFromWorld(this.getCurrentSelectedObject());
        }

        boolean removeObjectSelection1 = this.getCurrentSelectedObject() != null && this.getCurrentSelectedObject().isDead();
        boolean removeObjectSelection2 = ImGui.isKeyPressed(WBench.get().getBindingManager().keyEsc.getKeyCode(), false);
        if (removeObjectSelection1 || removeObjectSelection2) {
            this.setCurrentSelectedObject(null);
        }

        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("Map Project")) {
            if (ImGui.menuItem("Run Map InGame")) {
                JGems3D.IsolatedProcessLauncher.EXEC(JGemsLaunchArgsRegistry.getArgumentFrom(
                        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.MAP_TEST, "true"),
                        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.DEBUG, "true"),
                        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.NO_SOUND, "true"),
                        new Pair<>(JGemsLaunchArgsRegistry.JGemsLaunchArgs.NO_FULL_SCREEN, "true")
                ));
            }
            ImGui.separator();
            if (ImGui.menuItem("Save")) {
                Log.get().info("Saved...");
                WBench.get().getMapProjectManager().saveMapProject(false);
            }
           // if (ImGui.menuItem("Compile")) {
//
           // }
            if (ImGui.menuItem("Exit")) {
                if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                    WBench.get().getMapProjectManager().closeMapProject(true);
                    ImGui.endMenu();
                    ImGui.endMainMenuBar();
                    return;
                }
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Scene")) {
            SelectedScene oldSelected = this.getSelectedScene();
            if (ImGui.checkbox("Main", this.getSelectedScene().equals(SelectedScene.MAIN))) {
                this.selectedScene = SelectedScene.MAIN;
            }
            if (ImGui.checkbox("SkyBox", this.getSelectedScene().equals(SelectedScene.BACKGROUND))) {
                this.selectedScene = SelectedScene.BACKGROUND;
            }
            ISkyBackground background = this.getOpenGLRenderer().getWorld().getEnvironment().getSkyBox().getBackground();

            String[] scales = {"2.0", "4.0", "8.0", "16.0"};
            ImGui.beginDisabled(!this.getSelectedScene().equals(SelectedScene.BACKGROUND));
            if (ImGui.beginCombo("Scale", String.valueOf(background.getViewScaling()))) {
                for (String scale : scales) {
                    float selectedScaling = Float.parseFloat(scale);
                    if (ImGui.selectable(scale, background.getViewScaling() == selectedScaling)) {
                        background.setViewScaling(selectedScaling);
                    }
                }
                ImGui.endCombo();
            }
            ImGui.endDisabled();
            ImGui.endMenu();

            if (!oldSelected.equals(this.getSelectedScene())) {
                this.setCurrentSelectedTemplate(null);
                this.setCurrentSelectedObject(null);
            }
        }
        JGemsConfig.DEBUG.FULL_BRIGHT = GlobalSceneRenderingVars.FULL_BRIGHT;
        if (ImGui.beginMenu("View")) {
            if (ImGui.checkbox("Fog", GlobalSceneRenderingVars.VIEW_FOG)) {
                GlobalSceneRenderingVars.VIEW_FOG = !GlobalSceneRenderingVars.VIEW_FOG;
            }
            if (ImGui.checkbox("Full Bright", GlobalSceneRenderingVars.FULL_BRIGHT)) {
                GlobalSceneRenderingVars.FULL_BRIGHT = !GlobalSceneRenderingVars.FULL_BRIGHT;
            }
            if (ImGui.checkbox("Shadows", GlobalSceneRenderingVars.VIEW_SHADOWS)) {
                GlobalSceneRenderingVars.VIEW_SHADOWS = !GlobalSceneRenderingVars.VIEW_SHADOWS;
            }
            if (ImGui.checkbox("Chess Terrain", GlobalSceneRenderingVars.VIEW_CHESS_TERRAIN)) {
                GlobalSceneRenderingVars.VIEW_CHESS_TERRAIN = !GlobalSceneRenderingVars.VIEW_CHESS_TERRAIN;
            }
            if (ImGui.checkbox("HDR", GlobalSceneRenderingVars.VIEW_HDR)) {
                GlobalSceneRenderingVars.VIEW_HDR = !GlobalSceneRenderingVars.VIEW_HDR;
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Controls")) {
            WBenchBindingManager wBenchBindingManager = WBench.get().getBindingManager();
            float camSpeedRaw = WBench.get().getSettings().getCamSpeed();
            float min = 0.0001f;
            float max = 0.0025f;
            float[] camSpeedPercent = new float[] { (camSpeedRaw / max) * 100.0f };

            if (ImGui.sliderFloat("Camera Sensitivity", camSpeedPercent, (min / max) * 100.0f, 100.0f, "%.1f%%")) {
                float newCamSpeed = JGemsHelper.math().clamp((camSpeedPercent[0] / 100.0f) * max, min, max);
                WBench.get().getSettings().setCamSpeed(newCamSpeed);
            }

            if (ImGui.treeNode("Keys")) {
                for (Binding binding : wBenchBindingManager.getBindingSet()) {
                    ImGui.text(binding.toString());
                }
                ImGui.separator();
                ImGui.text("Left Mouse Key - Select Object/Action");
                ImGui.text("Right Mouse Key - Move Camera/Action");
                ImGui.text("Ctrl+S - Save");
                ImGui.text("Ctrl+C - Clone Selected Object");
                ImGui.treePop();
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

        ImGui.begin("Items", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(entitiesWindowSizeX, entitiesWindowSizeY - YOffset);
        ImGui.setWindowPos(0, YOffset);
        {
            ImGui.beginChild("##ItemsChild", ImGui.getColumnWidth(), ImGui.getWindowHeight() - 40, true, ImGuiWindowFlags.HorizontalScrollbar);
            this.getItemsComponent().itemsContent();
            ImGui.endChild();
        }
        ImGui.end();

        ImGui.begin("Scene " + this.getSelectedScene().name(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.MenuBar);
        ImGui.beginMenuBar();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffaaff67);
        if (ImGui.menuItem(SelectedScene.MAIN.name(), "##" + SelectedScene.MAIN.name(), this.getSelectedScene().equals(SelectedScene.MAIN))) {
            this.selectedScene = SelectedScene.MAIN;
        }
        if (ImGui.menuItem(SelectedScene.BACKGROUND.name(), "##" + SelectedScene.BACKGROUND.name(), this.getSelectedScene().equals(SelectedScene.BACKGROUND))) {
            this.selectedScene = SelectedScene.BACKGROUND;
        }
        ImGui.popStyleColor();
        {
            Vector3f camPos = this.getOpenGLRenderer().getCamera().getCamPosition();
            ImGui.text(" ( FPS: " + WBenchScreen.RENDER_FPS + " | Cam: " + "[" + String.format("%.2f", camPos.x) + "; " + String.format("%.2f", camPos.y) + "; " + String.format("%.2f", camPos.z) + "] )");
        }
        ImGui.endMenuBar();

        if (ImGui.isWindowHovered()) {
            if (ImGui.isMouseClicked(1)) {
                ImGui.setWindowFocus();
            }
            MapEditorInterface.isCursorInsideScene = true;
        } else if (!WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().isRightKeyPressed()) {
            MapEditorInterface.isCursorInsideScene = false;
        }

        int posX = (int) sceneWindowOffset;
        int posY = (int) YOffset;
        int sizeX = (int) sceneWindowSizeX;
        int sizeY = (int) (sceneWindowSizeY - YOffset);
        ImGui.setWindowSize(sizeX, sizeY);
        ImGui.setWindowPos(posX, posY);
        this.getSceneComponent().sceneContent();
        ImGui.end();

        ImGui.begin("Output", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(consoleWindowSizeX, consoleWindowSizeY);
        ImGui.setWindowPos(sceneWindowOffset, windowSize.y - consoleWindowSizeY);
        MapEditorInterface.consoleContent();
        ImGui.end();

        ImGui.begin("Resources", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(resourcesWindowSizeX, resourcesWindowSizeY);
        ImGui.setWindowPos(0, windowSize.y - entitiesWindowSizeY);
        this.getResourcesComponent().resourcesContent();
        ImGui.end();

        ImGui.begin("Actions", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(propertiesWindowSizeX, propertiesWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowSizeX + sceneWindowOffset, YOffset);
        this.getActionsContent().actionsContent();
        ImGui.end();
    }

    public void setNewCamera(@Nullable ICamera camera) {
        if (camera == null) {
            if (this.getOldCamera() != null) {
                this.getOpenGLRenderer().getWorld().setCamera(this.getOldCamera());
                this.oldCamera = null;
            }
        } else {
            this.oldCamera = this.getOpenGLRenderer().getCamera();
            WBench.get().getScreen().getScene().setCamera(camera);
        }
    }

    public static void consoleContent() {
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

    public void addObjectInWorld(WBenchObject wBenchObject) {
        switch (this.getSelectedScene()) {
            case MAIN: {
                this.getOpenGLRenderer().getWorld().addObject(wBenchObject);
                break;
            }
            case BACKGROUND: {
                this.getOpenGLRenderer().getWorld().getEnvironment().getSkyBox().getBackground().addObject(wBenchObject);
                break;
            }
        }
    }

    public void removeObjectFromWorld(WBenchObject wBenchObject) {
        wBenchObject.setDead();
    }

    public Set<WBenchObject> setOfSceneObjects() {
        switch (this.getSelectedScene()) {
            case MAIN: {
                return this.getOpenGLRenderer().getWorld().getSceneObjects();
            }
            case BACKGROUND: {
                return this.getOpenGLRenderer().getWorld().getEnvironment().getSkyBox().getBackground().getSkySceneObjects().stream().map(e -> (WBenchObject) e).collect(Collectors.toSet());
            }
            default: {
                return null;
            }
        }
    }

    public void setCurrentSelectedTemplate(WBenchObjectTemplate currentSelectedTemplate) {
        this.currentSelectedTemplate = currentSelectedTemplate;
    }

    public void setCurrentSelectedObject(WBenchObject currentSelectedObject) {
        synchronized (MapEditorInterface.monitor) {
            this.currentSelectedObject = currentSelectedObject;
        }
    }

    public ICamera getOldCamera() {
        return this.oldCamera;
    }

    public TextEditor getTextEditor() {
        return this.textEditor;
    }

    public SelectedScene getSelectedScene() {
        return this.selectedScene;
    }

    public ActionsInterfaceComponentM getActionsContent() {
        return this.actionsContent;
    }

    public ItemsInterfaceComponentM getItemsComponent() {
        return this.itemsComponent;
    }

    public ResourcesInterfaceComponentM getResourcesComponent() {
        return this.resourcesComponent;
    }

    public SceneInterfaceComponentM getSceneComponent() {
        return this.sceneComponent;
    }

    public FBOTexture2DProgram getScenePreview() {
        return this.scenePreview;
    }

    public WBenchObjectTemplate getCurrentSelectedTemplate() {
        return this.currentSelectedTemplate;
    }

    public WBenchObject getCurrentSelectedObject() {
        return this.currentSelectedObject;
    }

    public WBenchOpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    public WBenchMapProjectManager getProjectManager() {
        return this.WBenchMapProjectManager;
    }

    public @NotNull WBenchWorld getWorld() {
        return this.getOpenGLRenderer().getWorld();
    }
}