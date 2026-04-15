package workbench.graphics.scene.ui.map;

import api.system.JGemsAPI;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.JGems3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.ui.snapshots.instances.ISnapshotCompatible;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.binding.Binding;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.JGemsPath;
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
import workbench.graphics.scene.ui.asnapshots.helper.WBenchUITrackingHelper;
import workbench.graphics.scene.ui.map.editor.*;
import workbench.graphics.scene.ui.map.editor.utils.GlobalWBenchSceneRenderingVars;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.graphics.screen.WBenchScreen;

import java.util.*;
import java.util.stream.Collectors;

public class MapEditorInterface implements DearUIInterface, ISnapshotCompatible<MapEditorInterface.MapEditorInterfaceSnapshotData> {
    private SelectedScene selectedScene;

    public static final Object monitor = new Object();
    private final WBenchOpenGLRenderer openGLRenderer;

    private final SelectedObjectsManager selectedObjectsManager;
    private WBenchObjectTemplate currentSelectedTemplate;

    private ICamera oldCamera;
    private final FBOTexture2DProgram scenePreview;

    private final ActionsInterfaceComponentM actionsContent;
    private final ItemsInterfaceComponentM itemsComponent;
    private final ResourcesInterfaceComponentM resourcesComponent;
    private final SceneInterfaceComponentM sceneComponent;

    public static boolean isCursorInsideSceneAndFocused;
    public static boolean isCursorInsideScene;

    public MapEditorInterface(WBenchOpenGLRenderer openGLRenderer, FBOTexture2DProgram scenePreview) {
        this.selectedObjectsManager = new SelectedObjectsManager(new HashSet<>());

        this.openGLRenderer = openGLRenderer;
        this.scenePreview = scenePreview;

        this.actionsContent = new ActionsInterfaceComponentM(this);
        this.itemsComponent = new ItemsInterfaceComponentM(this);
        this.resourcesComponent = new ResourcesInterfaceComponentM(this);
        this.sceneComponent = new SceneInterfaceComponentM(this);

        this.clear();
    }

    public void resetSelected() {
        this.currentSelectedTemplate = null;
        this.selectedObjectsManager.clear();
    }

    public void clear() {
        this.getSceneComponent().clear();
        this.getActionsContent().clear();
        this.getSceneComponent().clear();
        this.getItemsComponent().clear();

        this.selectedScene = SelectedScene.MAIN;

        this.selectedObjectsManager.clear();
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
        this.getSelectedObjectsManager().preFrame();

        if (ProjectUIUtils.ctrlS()) {
            WBench.get().getMapProjectManager().saveMapProject(false);
            Log.get().info("Saved...");
        }
        if (ProjectUIUtils.ctrlZ()) {
            WBench.get().getMapProjectManager().undo();
        }
        if (ProjectUIUtils.ctrlY()) {
            WBench.get().getMapProjectManager().redo();
        }
        boolean deleteCurrentObject = ImGui.isKeyPressed(WBench.get().getBindingManager().keyDelete.getKeyCode(), false);
        if (deleteCurrentObject) {
            this.getSelectedObjectsManager().deleteSelected();
        }

        if (ImGui.isKeyPressed(WBench.get().getBindingManager().keyEsc.getKeyCode(), false)) {
            this.getSelectedObjectsManager().clear();
        }

        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("Map Project")) {
            if (ImGui.menuItem("Run Map InGame")) {
                JGems3D.IsolatedProcessLauncher.EXEC(JGemsLaunchArgsRegistry.getArgumentFrom(
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.MAP_TEST, "true"),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.DEBUG, "true"),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.NO_SOUND, "true"),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.EXTERNAL_GAME_DEF, WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath().fullPath()),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.API_APP_CLASSPATH, JGemsAPI.getExternalClassApiDef()),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.TEST_MAP_ID, new JGemsPath(WBench.get().getMapProjectManager().getCurrentMapProject().getMapAbsolutePath(), WBench.get().getMapProjectManager().getCurrentMapProject().getMapName() + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_PROJECT_FILE).fullPath())
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
        JGemsConfig.DEBUG.WIREFRAME_RENDERING = GlobalWBenchSceneRenderingVars.WIREFRAME_RENDERING;
        JGemsConfig.DEBUG.FULL_BRIGHT = GlobalWBenchSceneRenderingVars.FULL_BRIGHT;
        if (ImGui.beginMenu("View")) {
            if (ImGui.checkbox("Fog", GlobalWBenchSceneRenderingVars.VIEW_FOG)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                GlobalWBenchSceneRenderingVars.VIEW_FOG = !GlobalWBenchSceneRenderingVars.VIEW_FOG;
            }
            if (ImGui.checkbox("Full Bright", GlobalWBenchSceneRenderingVars.FULL_BRIGHT)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                GlobalWBenchSceneRenderingVars.FULL_BRIGHT = !GlobalWBenchSceneRenderingVars.FULL_BRIGHT;
            }
            if (ImGui.checkbox("Shadows", GlobalWBenchSceneRenderingVars.VIEW_SHADOWS)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                GlobalWBenchSceneRenderingVars.VIEW_SHADOWS = !GlobalWBenchSceneRenderingVars.VIEW_SHADOWS;
            }
            if (ImGui.checkbox("Chess Terrain", GlobalWBenchSceneRenderingVars.VIEW_CHESS_TERRAIN)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                GlobalWBenchSceneRenderingVars.VIEW_CHESS_TERRAIN = !GlobalWBenchSceneRenderingVars.VIEW_CHESS_TERRAIN;
            }
            if (ImGui.checkbox("WireFrame Rendering", GlobalWBenchSceneRenderingVars.WIREFRAME_RENDERING)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                GlobalWBenchSceneRenderingVars.WIREFRAME_RENDERING = !GlobalWBenchSceneRenderingVars.WIREFRAME_RENDERING;
            }
            if (ImGui.checkbox("HDR", GlobalWBenchSceneRenderingVars.VIEW_HDR)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                GlobalWBenchSceneRenderingVars.VIEW_HDR = !GlobalWBenchSceneRenderingVars.VIEW_HDR;
            }
            if (ImGui.checkbox("Animations", GlobalWBenchSceneRenderingVars.ANIMATIONS)) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                GlobalWBenchSceneRenderingVars.ANIMATIONS = !GlobalWBenchSceneRenderingVars.ANIMATIONS;
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
                ImGui.text("Ctrl+G - Clone Selected Object");
                ImGui.text("Ctrl+Z - Undo");
                ImGui.text("Ctrl+Y - Redo");
                ImGui.treePop();
            }
            ImGui.endMenu();
        }
        ImGui.pushStyleColor(ImGuiCol.Text, 0xff45ff9a);
        {
            ImGui.beginDisabled(WBench.get().getMapProjectManager().getSnapshotsTrace().getUndoStack().isEmpty());
            if (ImGui.button("<--- [" + WBench.get().getMapProjectManager().getSnapshotsTrace().getUndoStack().size() + "]")) {
                WBench.get().getMapProjectManager().undo();
            }
            ImGui.endDisabled();
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.setTooltip("Undo");
                ImGui.endTooltip();
            }
        }
        {
            ImGui.beginDisabled(WBench.get().getMapProjectManager().getSnapshotsTrace().getRedoStack().isEmpty());
            if (ImGui.button("[" + WBench.get().getMapProjectManager().getSnapshotsTrace().getRedoStack().size() + "] --->")) {
                WBench.get().getMapProjectManager().redo();
            }
            ImGui.endDisabled();
            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.setTooltip("Redo");
                ImGui.endTooltip();
            }
        }
        ImGui.popStyleColor();
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

        ImGui.begin("Items", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.MenuBar);
        {
            ImGui.beginMenuBar();
            if (ImGui.beginMenu("Organize")) {
                if (ImGui.checkbox("Sort By ID", this.getItemsComponent().sortingMode == 0)) {
                    this.getItemsComponent().sortingMode = 0;
                }
                if (ImGui.checkbox("Sort By Name", this.getItemsComponent().sortingMode == 1)) {
                    this.getItemsComponent().sortingMode = 1;
                }
                if (ImGui.checkbox("Sort By Type", this.getItemsComponent().sortingMode == 2)) {
                    this.getItemsComponent().sortingMode = 2;
                }
                ImGui.separator();
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }
        ImGui.setWindowSize(entitiesWindowSizeX, entitiesWindowSizeY - YOffset);
        ImGui.setWindowPos(0, YOffset);
        {
            ImGui.beginChild("##ItemsChild", ImGui.getColumnWidth(), ImGui.getWindowHeight() - 54, true, ImGuiWindowFlags.HorizontalScrollbar);
            this.getItemsComponent().itemsContent();
            ImGui.endChild();
        }
        ImGui.end();

        ImGui.begin("Scene " + this.getSelectedScene().name(), ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.MenuBar);
        ImGui.beginMenuBar();
        ImGui.pushStyleColor(ImGuiCol.Text, 0xffaaff67);
        if (ImGui.menuItem(SelectedScene.MAIN.name(), "##" + SelectedScene.MAIN.name(), this.getSelectedScene().equals(SelectedScene.MAIN))) {
            this.setSelectedScene(SelectedScene.MAIN);
            this.resetSelected();
        }
        if (ImGui.menuItem(SelectedScene.BACKGROUND.name(), "##" + SelectedScene.BACKGROUND.name(), this.getSelectedScene().equals(SelectedScene.BACKGROUND))) {
            this.setSelectedScene(SelectedScene.BACKGROUND);
            this.resetSelected();
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
            MapEditorInterface.isCursorInsideSceneAndFocused = true;
            MapEditorInterface.isCursorInsideScene = true;
        } else if (!WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().isRightKeyPressed()) {
            MapEditorInterface.isCursorInsideSceneAndFocused = false;
        } else {
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
        ProjectUIUtils.consoleContent();
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

        this.getSelectedObjectsManager().postFrame();
    }

    public void overrideCamera(@Nullable ICamera camera) {
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

    public void addObjectInWorld(WBenchObject<?> wBenchObject) {
        WBenchUITrackingHelper.instantlyTrackAndPush();
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

    public Set<WBenchObject<?>> setOfSceneObjects() {
        switch (this.getSelectedScene()) {
            case MAIN: {
                return this.getOpenGLRenderer().getWorld().getSceneObjects();
            }
            case BACKGROUND: {
                return this.getOpenGLRenderer().getWorld().getEnvironment().getSkyBox().getBackground().getSkySceneObjects().stream().map(e -> (WBenchObject<?>) e).collect(Collectors.toSet());
            }
            default: {
                return null;
            }
        }
    }

    public MapEditorInterface setSelectedScene(SelectedScene selectedScene) {
        WBenchUITrackingHelper.instantlyTrackAndPush();
        this.selectedScene = selectedScene;
        return this;
    }

    public void setCurrentSelectedTemplate(WBenchObjectTemplate currentSelectedTemplate) {
        if ((currentSelectedTemplate != null && this.currentSelectedTemplate == null) || (this.currentSelectedTemplate != null && !this.currentSelectedTemplate.equals(currentSelectedTemplate))) {
            WBenchUITrackingHelper.instantlyTrackAndPush();
        }
        this.currentSelectedTemplate = currentSelectedTemplate;
    }

    public SelectedObjectsManager getSelectedObjectsManager() {
        return this.selectedObjectsManager;
    }

    public ICamera getOldCamera() {
        return this.oldCamera;
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

    public WBenchOpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    public @NotNull WBenchWorld getWorld() {
        return this.getOpenGLRenderer().getWorld();
    }

    @Override
    public MapEditorInterfaceSnapshotData takeSnapshot() {
        return new MapEditorInterfaceSnapshotData(this.getSelectedScene(), this.getSelectedObjectsManager().takeSnapshot(), this.getCurrentSelectedTemplate());
    }

    @Override
    public void fixSnapshot(MapEditorInterfaceSnapshotData mapEditorInterfaceSnapshotData) {
        this.selectedScene = mapEditorInterfaceSnapshotData.selectedScene;
        this.currentSelectedTemplate = mapEditorInterfaceSnapshotData.currentSelectedTemplate;
        this.selectedObjectsManager.fixSnapshot(mapEditorInterfaceSnapshotData.selectedObjectsManageSnapshot);
        {
            JGemsConfig.DEBUG.SHOW_CASCADES = mapEditorInterfaceSnapshotData.globalVars.SHOW_CASCADES;
            GlobalWBenchSceneRenderingVars.VIEW_FOG = mapEditorInterfaceSnapshotData.globalVars.VIEW_FOG;
            GlobalWBenchSceneRenderingVars.FULL_BRIGHT = mapEditorInterfaceSnapshotData.globalVars.FULL_BRIGHT;
            GlobalWBenchSceneRenderingVars.VIEW_SHADOWS = mapEditorInterfaceSnapshotData.globalVars.VIEW_SHADOWS;
            GlobalWBenchSceneRenderingVars.VIEW_CHESS_TERRAIN = mapEditorInterfaceSnapshotData.globalVars.VIEW_CHESS_TERRAIN;
            GlobalWBenchSceneRenderingVars.VIEW_HDR = mapEditorInterfaceSnapshotData.globalVars.VIEW_HDR;
            GlobalWBenchSceneRenderingVars.ANIMATIONS = mapEditorInterfaceSnapshotData.globalVars.ANIMATIONS;
        }
        this.getItemsComponent().scrollToSelection();
    }

    public static class MapEditorInterfaceSnapshotData implements ISnapshotCompatible.SnapshotData {
        public SelectedScene selectedScene;
        public SelectedObjectsManager.SelectedObjectsManageSnapshot selectedObjectsManageSnapshot;
        public WBenchObjectTemplate currentSelectedTemplate;
        public GlobalVars globalVars;

        public MapEditorInterfaceSnapshotData(SelectedScene selectedScene, SelectedObjectsManager.SelectedObjectsManageSnapshot currentSelectedObjects, WBenchObjectTemplate currentSelectedTemplate) {
            this.selectedScene = selectedScene;
            this.selectedObjectsManageSnapshot = currentSelectedObjects;
            this.currentSelectedTemplate = currentSelectedTemplate;
            this.globalVars = new GlobalVars();
        }

        public static class GlobalVars {
            public final boolean SHOW_CASCADES = JGemsConfig.DEBUG.SHOW_CASCADES;
            public final boolean VIEW_FOG = GlobalWBenchSceneRenderingVars.VIEW_FOG;
            public final boolean FULL_BRIGHT = GlobalWBenchSceneRenderingVars.FULL_BRIGHT;
            public final boolean VIEW_SHADOWS = GlobalWBenchSceneRenderingVars.VIEW_SHADOWS;
            public final boolean VIEW_CHESS_TERRAIN = GlobalWBenchSceneRenderingVars.VIEW_CHESS_TERRAIN;
            public final boolean VIEW_HDR = GlobalWBenchSceneRenderingVars.VIEW_HDR;
            public final boolean ANIMATIONS = GlobalWBenchSceneRenderingVars.ANIMATIONS;
        }
    }

    public static class SelectedObjectsManager implements ISnapshotCompatible<SelectedObjectsManager.SelectedObjectsManageSnapshot> {
        private final Set<WBenchObject<?>> currentSelectedObjects;
        private final Vector3f prevGroupPosition;
        private final Vector3f prevGroupRotation;
        private final Vector3f prevGroupScaling;
        private final Vector3f groupPosition;
        private final Vector3f groupRotation;
        private final Vector3f groupScaling;
        private Vector3f savedCenter;
        private boolean rotateGroupAroundOwnCenter;
        private boolean scaleTranslator;
        private boolean oneDirScaling;
        private boolean scalingOneDirMaxPlane;

        public SelectedObjectsManager(Set<WBenchObject<?>> currentSelectedObjects) {
            this.currentSelectedObjects = currentSelectedObjects;
            this.savedCenter = new Vector3f();
            this.groupPosition = new Vector3f();
            this.groupRotation = new Vector3f();
            this.groupScaling = new Vector3f();
            this.prevGroupPosition = new Vector3f();
            this.prevGroupRotation = new Vector3f();
            this.prevGroupScaling = new Vector3f(1.0f);
            this.rotateGroupAroundOwnCenter = false;
            this.scaleTranslator = false;
            this.oneDirScaling = false;
            this.scalingOneDirMaxPlane = false;
        }

        public boolean isScalingOneDirMaxPlane() {
            return this.scalingOneDirMaxPlane;
        }

        public SelectedObjectsManager setScalingOneDirMaxPlane(boolean scalingOneDirMaxPlane) {
            this.scalingOneDirMaxPlane = scalingOneDirMaxPlane;
            return this;
        }

        public boolean isOneDirScaling() {
            return this.oneDirScaling;
        }

        public boolean isScaleTranslator() {
            return this.scaleTranslator;
        }

        public void setDefaultMetaMods() {
            this.setOneDirScaling(false);
            this.setScaleTranslator(false);
        }

        public SelectedObjectsManager setScaleTranslator(boolean scaleTranslator) {
            this.groupScaling.set(1.0f);
            this.prevGroupScaling.set(1.0f);
            this.scaleTranslator = scaleTranslator;
            return this;
        }

        public SelectedObjectsManager setOneDirScaling(boolean oneDirScaling) {
            this.groupScaling.set(1.0f);
            this.prevGroupScaling.set(1.0f);
            this.oneDirScaling = oneDirScaling;
            return this;
        }

        public boolean isRotateGroupAroundOwnCenter() {
            return this.rotateGroupAroundOwnCenter;
        }

        public SelectedObjectsManager setRotateGroupAroundOwnCenter(boolean rotateGroupAroundOwnCenter) {
            this.rotateGroupAroundOwnCenter = rotateGroupAroundOwnCenter;
            return this;
        }

        public void reset() {
            this.groupPosition.set(this.center());
            this.groupRotation.set(1.0f);
            this.groupScaling.set(1.0f);
            this.prevGroupPosition.set(this.groupPosition);
            this.prevGroupRotation.set(this.groupRotation);
            this.prevGroupScaling.set(this.groupScaling);
        }

        public void clear() {
            this.currentSelectedObjects.clear();
            this.reset();
        }

        public void setGroupPosition(Vector3f pos) {
            this.groupPosition.set(pos);
        }

        public void setGroupRotation(Vector3f rot) {
            this.groupRotation.set(rot);
        }

        public void setGroupScaling(Vector3f scaling) {
            this.groupScaling.set(scaling);
        }

        public Vector3f getRealGroupPosition() {
            return new Vector3f(this.groupPosition);
        }

        public Vector3f getGroupPosition() {
            return new Vector3f(this.savedCenter);
        }

        public Vector3f getGroupRotation() {
            return new Vector3f(this.groupRotation);
        }

        public Vector3f getGroupScaling() {
            return new Vector3f(this.groupScaling);
        }

        public Vector3f center() {
            if (this.currentSelectedObjects.isEmpty()) {
                return new Vector3f(0.0f);
            }
            final Vector3f center = new Vector3f();
            for (WBenchObject<?> thisObj : this.currentSelectedObjects) {
                center.add(thisObj.getPosition());
            }
            center.div(this.currentSelectedObjects.size());
            return center;
        }

        public void preFrame() {
            this.savedCenter.set(this.center());
        }

        public void postFrame() {
            this.update();
        }

        public void update() {
            this.currentSelectedObjects.removeIf(WBenchObject::isDead);
            this.updateMultipleObjectsTransforms();
            this.prevGroupPosition.set(this.getRealGroupPosition());
            this.prevGroupRotation.set(this.groupRotation);
            this.prevGroupScaling.set(this.groupScaling);
        }

        private void updateMultipleObjectsTransforms() {
            if (currentSelectedObjects.size() <= 1) {
                return;
            }

            Vector3f rotationOffset = new Vector3f(this.groupRotation).sub(this.prevGroupRotation);
            Vector3f posOffset = new Vector3f(this.groupPosition).sub(this.prevGroupPosition);
            Vector3f scaleOffset = new Vector3f(this.groupScaling).div(this.prevGroupScaling);
            Quaternionf deltaRot = new Quaternionf().rotateXYZ(rotationOffset.x, rotationOffset.y, rotationOffset.z);
            Matrix4f groupMatrix = new Matrix4f().identity().translate(savedCenter).rotate(deltaRot).translate(-savedCenter.x, -savedCenter.y, -savedCenter.z);
            for (WBenchObject<?> obj : currentSelectedObjects) {
                obj.setForceConstraints(true);
                Vector3f pos = new Vector3f(obj.getPosition());
                {
                    obj.setPosition(obj.getPosition().add(posOffset));
                    if (this.isScaleTranslator()) {
                        Matrix4f scaleMatrix = new Matrix4f().identity().translate(savedCenter).scale(scaleOffset).translate(-savedCenter.x, -savedCenter.y, -savedCenter.z);
                        Vector3f newPos = scaleMatrix.transformPosition(pos, new Vector3f());
                        obj.setPosition(newPos);
                    } else {
                        Vector3f newScale = obj.getScaling().mul(scaleOffset);
                        if (this.isOneDirScaling()) {
                            Vector3f oldScale = new Vector3f(obj.getScaling());
                            Vector3f scaleRatio = new Vector3f(newScale).div(oldScale);
                            CullingAABB aabb = obj.getCullingData();
                            Vector3f pivot = new Vector3f(this.scalingOneDirMaxPlane ? aabb.getAabbMax() : aabb.getAabbMin());
                            Vector3f oldPos = new Vector3f(obj.getPosition());
                            Vector3f newPosScaled = new Vector3f(oldPos).sub(pivot).mul(scaleRatio).add(pivot);
                            if (newPosScaled.isFinite()) {
                                obj.setPosition(newPosScaled);
                            }
                        }
                        obj.setScaling(newScale);
                    }
                }
                if (!this.isOneDirScaling()) {
                    Matrix4f objMatrix = TransformUtils.getModelMatrix(obj.getModel().getPose());
                    groupMatrix.mul(objMatrix, objMatrix);
                    Vector3f newPos = objMatrix.getTranslation(new Vector3f());
                    Quaternionf newRotQ = objMatrix.getUnnormalizedRotation(new Quaternionf());
                    Vector3f newEuler = newRotQ.getEulerAnglesXYZ(new Vector3f()).negate();
                    obj.setPosition(newPos);
                    obj.setRotation(newEuler);
                    obj.setForceConstraints(false);
                }
            }
        }

        public void deleteSelected() {
            WBenchUITrackingHelper.instantlyTrackAndPush();
            synchronized (MapEditorInterface.monitor) {
                this.getCurrentSelectedObjects().forEach(e -> this.deleteObject(false, e));
            }
            this.reset();
        }

        public void deleteObject(boolean snapshot, WBenchObject<?> wBenchObject) {
            if (snapshot) {
                WBenchUITrackingHelper.instantlyTrackAndPush();
                this.reset();
            }
            wBenchObject.setDead();
        }

        public void setCurrentSelectedObject(boolean snapshot, WBenchObject<?> select) {
            synchronized (MapEditorInterface.monitor) {
                if (snapshot) {
                    if ((select == null && !this.currentSelectedObjects.isEmpty()) || (select != null && !this.currentSelectedObjects.contains(select))) {
                        WBenchUITrackingHelper.instantlyTrackAndPush();
                    }
                }
                this.currentSelectedObjects.clear();
                if (select != null) {
                    this.currentSelectedObjects.add(select);
                }
            }
            this.reset();
        }

        public void addObjectInSelection(boolean snapshot, WBenchObject<?> select) {
            synchronized (MapEditorInterface.monitor) {
                if (snapshot) {
                    if (!this.currentSelectedObjects.contains(select)) {
                        WBenchUITrackingHelper.instantlyTrackAndPush();
                    }
                }
                this.currentSelectedObjects.add(select);
            }
            this.reset();
        }

        public void removeObjectFromSelection(boolean snapshot, WBenchObject<?> unSelect) {
            synchronized (MapEditorInterface.monitor) {
                if (this.currentSelectedObjects.remove(unSelect)) {
                    if (snapshot) {
                        if (!this.currentSelectedObjects.contains(unSelect)) {
                            WBenchUITrackingHelper.instantlyTrackAndPush();
                        }
                    }
                }
            }
            this.reset();
        }

        public Set<WBenchObject<?>> getCurrentSelectedObjects() {
            synchronized (MapEditorInterface.monitor) {
                return this.currentSelectedObjects;
            }
        }

        @Override
        public SelectedObjectsManageSnapshot takeSnapshot() {
            return new SelectedObjectsManageSnapshot(new HashSet<>(this.getCurrentSelectedObjects()), new Vector3f(this.prevGroupPosition), new Vector3f(this.prevGroupRotation), new Vector3f(this.prevGroupScaling), new Vector3f(this.getGroupPosition()), new Vector3f(this.getGroupRotation()), new Vector3f(this.getGroupScaling()), new Vector3f(this.savedCenter));
        }

        @Override
        public void fixSnapshot(SelectedObjectsManageSnapshot selectedObjectsManager) {
            this.currentSelectedObjects.clear();
            this.currentSelectedObjects.addAll(selectedObjectsManager.currentSelectedObjects);
            this.setGroupPosition(selectedObjectsManager.groupPosition);
            this.setGroupRotation(selectedObjectsManager.groupRotation);
            this.setGroupScaling(selectedObjectsManager.groupScaling);
            this.prevGroupPosition.set(selectedObjectsManager.prevGroupPosition);
            this.prevGroupRotation.set(selectedObjectsManager.prevGroupRotation);
            this.prevGroupScaling.set(selectedObjectsManager.prevGroupScaling);
            this.savedCenter = selectedObjectsManager.savedCenter;
        }

        public record SelectedObjectsManageSnapshot(Set<WBenchObject<?>> currentSelectedObjects, Vector3f groupPosition,
                                                    Vector3f groupRotation, Vector3f groupScaling,
                                                    Vector3f prevGroupPosition, Vector3f prevGroupRotation,
                                                    Vector3f prevGroupScaling,
                                                    Vector3f savedCenter) implements SnapshotData {
        }
    }
}