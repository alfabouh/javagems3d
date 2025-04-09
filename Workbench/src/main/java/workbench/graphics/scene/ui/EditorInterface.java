package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsMathHelper;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.binding.Binding;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.controller.WBenchControllerDispatcher;
import workbench.controller.binding.WBenchBindingManager;
import workbench.controller.objects.WBenchMouseKeyboardController;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.editor.*;
import workbench.graphics.screen.WBenchScreen;
import workbench.project.ProjectManager;
import workbench.resources.WBenchResourceManager;
import workbench.resources.shaders.WBenchShaderManager;

import java.lang.Math;
import java.util.*;

public class EditorInterface implements DearUIInterface {
    public static boolean VIEW_SHADOWS = true;
    public static boolean VIEW_CHESS_TERRAIN = true;
    public static boolean VIEW_HDR = true;
    public static boolean FULL_BRIGHT = false;

    public static final Object monitor = new Object();
    private final WBenchOpenGLRenderer openGLRenderer;
    private final ProjectManager projectManager;

    private float previewDistance;

    private WBenchObject currentSelectedObject;
    private WBenchObjectTemplate currentSelectedTemplate;
    private int currentOperation;

    private ICamera oldCamera;
    private final FBOTexture2DProgram scenePreview;
    private Collection<SceneObject> visibleObjects;

    private final ContextComponent contextComponent;
    private final ActionsInterfaceComponent actionsContent;
    private final ItemsInterfaceComponent itemsComponent;
    private final ResourcesInterfaceComponent resourcesComponent;
    private final SceneInterfaceComponent sceneComponent;

    public static boolean isCursorInsideScene;

    public EditorInterface(WBenchOpenGLRenderer openGLRenderer, FBOTexture2DProgram scenePreview, @NotNull ProjectManager projectManager) {
        this.projectManager = projectManager;
        this.openGLRenderer = openGLRenderer;
        this.scenePreview = scenePreview;

        this.contextComponent = new ContextComponent(this);
        this.actionsContent = new ActionsInterfaceComponent(this);
        this.itemsComponent = new ItemsInterfaceComponent(this);
        this.resourcesComponent = new ResourcesInterfaceComponent(this);
        this.sceneComponent = new SceneInterfaceComponent(this);

        this.clear();
    }

    public void clear() {
        this.getContextComponent().clear();
        this.getSceneComponent().clear();
        this.getActionsContent().clear();
        this.getSceneComponent().clear();
        this.getItemsComponent().clear();

        this.currentOperation = Operation.TRANSLATE;

        this.currentSelectedObject = null;
        this.currentSelectedTemplate = null;
        this.oldCamera = null;
        this.previewDistance = 1.0f;
        this.visibleObjects = null;
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

        boolean deleteCurrentObject = ImGui.isKeyPressed(WBench.get().getBindingManager().keyDelete.getKeyCode(), false);
        if (deleteCurrentObject) {
            this.getCurrentSelectedObject().setDead();
        }

        boolean removeObjectSelection1 = this.getCurrentSelectedObject() != null && this.getCurrentSelectedObject().isDead();
        boolean removeObjectSelection2 = ImGui.isKeyPressed(WBench.get().getBindingManager().keyEsc.getKeyCode(), false);
        if (removeObjectSelection1 || removeObjectSelection2) {
            this.setCurrentSelectedObject(null);
        }

        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("WBenchProject")) {
            if (ImGui.menuItem("Save Map")) {
                WBench.get().getProjectManager().saveProject(false);
            }
           // if (ImGui.menuItem("Compile")) {
//
           // }
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
        if (ImGui.beginMenu("Environment")) {
            if (ImGui.menuItem("Fog")) {
                this.getContextComponent().setOpenEnvironmentFogSettings(!this.getContextComponent().isOpenEnvironmentFogSettings());
            }
            if (ImGui.menuItem("SkyBox")) {
                this.getContextComponent().setOpenEnvironmentSkySettings(!this.getContextComponent().isOpenEnvironmentSkySettings());
            }
            ImGui.endMenu();
        }
        JGemsConfig.DEBUG.FULL_BRIGHT = EditorInterface.FULL_BRIGHT;
        if (ImGui.beginMenu("View")) {
            if (ImGui.checkbox("Full Bright", EditorInterface.FULL_BRIGHT)) {
                EditorInterface.FULL_BRIGHT = !EditorInterface.FULL_BRIGHT;
            }
            if (ImGui.checkbox("Shadows", EditorInterface.VIEW_SHADOWS)) {
                EditorInterface.VIEW_SHADOWS = !EditorInterface.VIEW_SHADOWS;
            }
            if (ImGui.checkbox("Chess Terrain", EditorInterface.VIEW_CHESS_TERRAIN)) {
                EditorInterface.VIEW_CHESS_TERRAIN = !EditorInterface.VIEW_CHESS_TERRAIN;
            }
            if (ImGui.checkbox("HDR", EditorInterface.VIEW_HDR)) {
                EditorInterface.VIEW_HDR = !EditorInterface.VIEW_HDR;
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Controls")) {
            WBenchBindingManager wBenchBindingManager = WBench.get().getBindingManager();
            float camSpeedRaw = WBench.get().getSettings().getCamSpeed();
            float min = 0.0001f;
            float max = 0.0025f;
            float[] camSpeedPercent = new float[] { (camSpeedRaw / max) * 100.0f };

            if (ImGui.treeNode("Options")) {
                if (ImGui.sliderFloat("Camera Sensitivity", camSpeedPercent, (min / max) * 100.0f, 100.0f, "%.1f%%")) {
                    float newCamSpeed = JGemsMathHelper.clamp((camSpeedPercent[0] / 100.0f) * max, min, max);
                    WBench.get().getSettings().setCamSpeed(newCamSpeed);
                }
                ImGui.treePop();
            }

            if (ImGui.treeNode("Keys")) {
                for (Binding binding : wBenchBindingManager.getBindingSet()) {
                    ImGui.text(binding.toString());
                }
                ImGui.separator();
                ImGui.text("Left Mouse Key - Select Object");
                ImGui.text("Right Mouse Key - Drag Camera");
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
        this.getItemsComponent().itemsContent();
        ImGui.end();

        ImGui.begin("Scene", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        if (ImGui.isWindowHovered()) {
            if (ImGui.isMouseClicked(1)) {
                ImGui.setWindowFocus();
            }
            EditorInterface.isCursorInsideScene = true;
        } else if (!WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().isRightKeyPressed()) {
            EditorInterface.isCursorInsideScene = false;
        }

        Vector3f camPos = this.getOpenGLRenderer().getCamera().getCamPosition();
        ImGui.text("FPS: " + WBenchScreen.RENDER_FPS);
        ImGui.sameLine();
        ImGui.text("[" + camPos.x + ", " + camPos.y + ", " + camPos.z + "]");
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
        this.consoleContent();
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

        this.getContextComponent().context();
    }

    public int chooseDefaultGuizmoOperation() {
        WBenchObject currentSelectedObject = this.getCurrentSelectedObject();
        int f1 = this.getOperationMask(currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        if (f1 != 0) {
            return f1;
        }
        int f2 = this.getOperationMask(currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        if (f2 != 0) {
            return f2;
        }
        return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
    }

    public int chooseGuizmoOperation(boolean translation, boolean rotation, boolean scaling) {
        WBenchObject currentSelectedObject = this.getCurrentSelectedObject();
        if (translation) {
            return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        }
        if (rotation) {
            return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        }
        return this.getOperationMask(currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
    }

    private int getOperationMask(int flag, int xOp, int yOp, int zOp) {
        if (flag == 0) {
            return 0;
        }
        int result = 0;
        if ((flag & 1) != 0) {
            result |= xOp;
        }
        if ((flag & 2) != 0) {
            result |= yOp;
        }
        if ((flag & 4) != 0) {
            result |= zOp;
        }
        return result;
    }

    public void renderPreviewItem() {
        if (this.currentSelectedTemplate == null) {
            return;
        }
        OpenGLRenderer.setViewPort(new Vector2i(256, 256));
        this.scenePreview.bindFBO();
        GL46.glClearColor(0.0f, 0.0f, 0.3f, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.renderPreviewItem(this.getPreviewDistance(), WBenchResourceManager.localShaderAssets.preview, this.currentSelectedTemplate.getMeshGroup());
        this.scenePreview.unBindFBO();
        WBenchScreen.clearColor();
        OpenGLRenderer.setViewPort(this.getOpenGLRenderer().getRenderingResolution());
    }

    private void renderPreviewItem(float distance, WBenchShaderManager shaderManager, MeshGroup meshGroup) {
        final Pose3D pose3D = new Pose3D(new Vector3f(0.0f, 0.0f, -3.5f));
        CullingAABB cullingAABB = meshGroup.getMeshAABBData().getNormalizedAABB(pose3D);
        float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
        float scale = diagonal / 5.0f;
        pose3D.setScaling(new Vector3f(1.0f / scale).mul(distance));

        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(TransformUtils.getPerspectiveMatrix(1.0f, (float) (Math.PI / 2.0f), 0.01f, 100.0f)));
        shaderManager.performModel3DMatrix(new UniformString("model_matrix"), TransformUtils.getModelMatrix(pose3D).lookAt(new Vector3f(1.0f), new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f)));
        for (MeshNode3D<RenderMesh> meshNode3D : meshGroup.getAllNodes()) {
            ITexture2DProgram diffuseMap = meshNode3D.getMaterial().getDiffuseMap();
            ISampleColor4 diffuseColor = meshNode3D.getMaterial().getDiffuseColor();
            shaderManager.performUniform(new UniformString("diffuse_color"), UniformFunctions.VEC4F(diffuseColor.getColor()));
            if (diffuseMap != null) {
                shaderManager.performUniformTextureBindless(new UniformString("diffuse_map"), diffuseMap);
                shaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(true));
            } else {
                shaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(false));
            }
            GL46.glBindVertexArray(meshNode3D.getMeshData().getVao());
            meshNode3D.getMeshData().enableAllMeshAttributes();
            GL46.glDrawElements(GL46.GL_TRIANGLES, meshNode3D.getMeshData().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
            meshNode3D.getMeshData().disableAllMeshAttributes();
            GL46.glBindVertexArray(0);
        }
        shaderManager.endShading();
    }

    public void setNewCamera(@Nullable ICamera camera) {
        if (camera == null) {
            if (this.oldCamera != null) {
                this.getOpenGLRenderer().getWorld().setCamera(this.oldCamera);
                this.oldCamera = null;
            }
        } else {
            this.oldCamera = this.getOpenGLRenderer().getCamera();
            WBench.get().getScreen().getScene().setCamera(camera);
        }
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

    public void setCurrentSelectedTemplate(WBenchObjectTemplate currentSelectedTemplate) {
        this.currentSelectedTemplate = currentSelectedTemplate;
    }

    public void setPreviewDistance(float previewDistance) {
        this.previewDistance = previewDistance;
    }

    public void setCurrentSelectedObject(WBenchObject currentSelectedObject) {
        synchronized (EditorInterface.monitor) {
            this.currentSelectedObject = currentSelectedObject;
        }
    }

    public void setVisibleObjects(Collection<SceneObject> visibleObjects) {
        this.visibleObjects = visibleObjects;
    }

    public void setCurrentOperation(int currentOperation) {
        this.currentOperation = currentOperation;
    }

    public float getPreviewDistance() {
        return this.previewDistance;
    }

    public int getCurrentOperation() {
        return this.currentOperation;
    }

    public ContextComponent getContextComponent() {
        return this.contextComponent;
    }

    public ActionsInterfaceComponent getActionsContent() {
        return this.actionsContent;
    }

    public ItemsInterfaceComponent getItemsComponent() {
        return this.itemsComponent;
    }

    public ResourcesInterfaceComponent getResourcesComponent() {
        return this.resourcesComponent;
    }

    public SceneInterfaceComponent getSceneComponent() {
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

    public Collection<SceneObject> getVisibleObjects() {
        return this.visibleObjects;
    }

    public WBenchOpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    public ProjectManager getProjectManager() {
        return this.projectManager;
    }
}