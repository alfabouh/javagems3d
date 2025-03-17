package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.items.TagItem;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.udata.MeshAABBData;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.collections.Pair;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.environment.WBenchEnvironment;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.objects.templates.WBenchObjectTemplate;
import workbench.graphics.scene.nodes.templates.WIGluingRenderNode;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.screen.WBenchScreen;
import workbench.project.ProjectManager;
import workbench.resources.WBenchResourceManager;
import workbench.resources.shaders.WBenchShaderManager;

import java.lang.Math;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EditorInterface implements DearUIInterface {
    public static boolean isCursorInsideScene;

    private final ProjectManager projectManager;
    private boolean openEnvironmentFogSettings;
    private boolean openEnvironmentSkySettings;
    private boolean openProjectSettings;

    private boolean cameraCheckBox;
    private final FixedCamera sunCamera;
    private ICamera oldCamera;

    private WBenchObject currentSelectedObject;
    private WBenchObjectTemplate currentSelectedTemplate;
    private final FBOTexture2DProgram scenePreview;
    private float previewDistance;

    private int currentOperation;
    private final WBenchOpenGLRenderer openGLRenderer;

    public EditorInterface(WBenchOpenGLRenderer openGLRenderer, FBOTexture2DProgram scenePreview, @NotNull ProjectManager projectManager) {
        this.projectManager = projectManager;
        this.openGLRenderer = openGLRenderer;
        this.scenePreview = scenePreview;
        this.sunCamera = new FixedCamera(new Vector3f(), new Vector3f());
        this.clear();
    }

    public void clear() {
        this.currentSelectedObject = null;
        this.currentSelectedTemplate = null;
        this.openEnvironmentFogSettings = false;
        this.openEnvironmentSkySettings = false;
        this.openProjectSettings = false;
        this.oldCamera = null;
        this.cameraCheckBox = false;
        this.previewDistance = 1.0f;
        this.currentOperation = Operation.TRANSLATE;
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
        if (ImGui.beginMenu("Environment")) {
            if (ImGui.menuItem("Fog")) {
                this.openEnvironmentFogSettings = !this.openEnvironmentFogSettings;
            }
            if (ImGui.menuItem("SkyBox")) {
                this.openEnvironmentSkySettings = !this.openEnvironmentSkySettings;
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("View")) {

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

        ImGui.begin("Scene", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        if (ImGui.isWindowHovered()) {
            EditorInterface.isCursorInsideScene = true;
        } else if (!WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().isRightKeyPressed()) {
            EditorInterface.isCursorInsideScene = false;
        }

        Vector3f camPos = this.getOpenGLRenderer().getCamera().getCamPosition();
        ImGui.text("FPS: " + WBenchScreen.RENDER_FPS);
        ImGui.sameLine();
        ImGui.text("[" + camPos.x + ", " + camPos.y + ", " + camPos.z + "]");

        WBench.get().getScreen().getWindow().setInFocus(ImGui.isWindowFocused());
        ImGui.setWindowSize(sceneWindowSizeX, sceneWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowOffset, YOffset);
        this.sceneContent(sceneWindowSizeX, sceneWindowSizeY - YOffset);
        ImGui.end();

        ImGui.begin("Output", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(consoleWindowSizeX, consoleWindowSizeY);
        ImGui.setWindowPos(sceneWindowOffset, windowSize.y - consoleWindowSizeY);
        this.consoleContent();
        ImGui.end();

        ImGui.begin("Items", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(entitiesWindowSizeX, entitiesWindowSizeY - YOffset);
        ImGui.setWindowPos(0, YOffset);
        this.itemsContent();
        ImGui.end();

        ImGui.begin("Resources", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(resourcesWindowSizeX, resourcesWindowSizeY);
        ImGui.setWindowPos(0, windowSize.y - entitiesWindowSizeY);
        this.resourcesContent();
        ImGui.end();

        ImGui.begin("Actions", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(propertiesWindowSizeX, propertiesWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowSizeX + sceneWindowOffset, YOffset);
        this.actionsContent();
        ImGui.end();

        this.context();
    }

    private void context() {
        WBenchEnvironment environment = this.getOpenGLRenderer().getWorld().getEnvironment();
        ImVec2 screenSize = ImGui.getIO().getDisplaySize();

        if (this.openEnvironmentFogSettings) {
            ImVec2 windowSize = new ImVec2(400, 200);
            ImGui.setNextWindowSize(windowSize.x, windowSize.y, ImGuiCond.Appearing);
            ImGui.setNextWindowPos((screenSize.x - windowSize.x) / 2, (screenSize.y - windowSize.y) / 2, ImGuiCond.Appearing);
            ImBoolean opened = new ImBoolean(true);
            if (ImGui.begin("Fog", opened, ImGuiWindowFlags.NoResize)) {
                float[] fogIntensity = new float[] {environment.getFogManager().getDensity()};
                if (ImGui.sliderFloat("Fog Intensity", fogIntensity, 0.0f, 1.0f)) {
                    environment.getFogManager().setDensity(fogIntensity[0]);
                }

                float[] fogColor = new float[] {environment.getFogManager().getColor().x, environment.getFogManager().getColor().y, environment.getFogManager().getColor().z};
                if (ImGui.colorEdit3("Fog Color", fogColor)) {
                    environment.getFogManager().setColor(new Vector3f(fogColor[0], fogColor[1], fogColor[2]));
                }
            }
            ImGui.end();
            if (!opened.get()) {
                this.openEnvironmentFogSettings = false;
            }
        }

        if (this.openEnvironmentSkySettings) {
            ImVec2 windowSize = new ImVec2(400, 200);
            ImGui.setNextWindowSize(windowSize.x, windowSize.y, ImGuiCond.Appearing);
            ImGui.setNextWindowPos((screenSize.x - windowSize.x) / 2, (screenSize.y - windowSize.y) / 2, ImGuiCond.Appearing);
            ImBoolean opened = new ImBoolean(true);
            if (ImGui.begin("SkyBox", opened, ImGuiWindowFlags.NoResize)) {
                ImGui.text("Sun");

                if (ImGui.checkbox("Sun's View", this.cameraCheckBox)) {
                    if (this.cameraCheckBox) {
                        this.cameraCheckBox = false;
                        this.setNewCamera(null);
                    } else {
                        this.cameraCheckBox = true;
                        Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getSkyBox().getSun());
                        this.sunCamera.setCameraPosition(camData.getFirst());
                        this.sunCamera.setLookAt(camData.getSecond());
                        this.setNewCamera(this.sunCamera);
                    }
                }

                float[] brightness = new float[] {environment.getSkyBox().getSun().getSunBrightness()};
                if (ImGui.sliderFloat("Sun Brightness", brightness, 0.0f, 1.0f)) {
                    environment.getSkyBox().getSun().setSunBrightness(brightness[0]);
                }

                float[] fogColor = new float[] {environment.getSkyBox().getSun().getLightColor().x, environment.getSkyBox().getSun().getLightColor().y, environment.getSkyBox().getSun().getLightColor().z};
                if (ImGui.colorEdit3("Sun Color", fogColor)) {
                    environment.getSkyBox().getSun().setLightColor(new Vector3f(fogColor[0], fogColor[1], fogColor[2]));
                }

                float[] sunPosition = {environment.getSkyBox().getSun().getLightPosition().x, environment.getSkyBox().getSun().getLightPosition().y, environment.getSkyBox().getSun().getLightPosition().z};
                if (ImGui.sliderFloat3("Position", sunPosition, -1.0f, 1.0f)) {
                    environment.getSkyBox().getSun().setLightPosition(new Vector3f(sunPosition[0], sunPosition[1], sunPosition[2]));
                    Pair<Vector3f, Vector3f> camData = this.adjustCamera(environment.getSkyBox().getSun());
                    this.sunCamera.setCameraPosition(camData.getFirst());
                    this.sunCamera.setLookAt(camData.getSecond());
                }

                ImGui.separator();
                ImGui.text("Sky Texture");
            }
            ImGui.end();
            if (!opened.get()) {
                if (this.oldCamera != null) {
                    this.setNewCamera(null);
                }
                this.openEnvironmentSkySettings = false;
                this.cameraCheckBox = false;
            }
        }

        if (this.openProjectSettings) {
            ImVec2 windowSize = new ImVec2(400, 300);
            ImGui.setNextWindowSize(windowSize.x, windowSize.y, ImGuiCond.Appearing);
            ImGui.setNextWindowPos((screenSize.x - windowSize.x) / 2, (screenSize.y - windowSize.y) / 2, ImGuiCond.Appearing);
            ImBoolean opened = new ImBoolean(true);
            if (ImGui.begin("Project", opened, ImGuiWindowFlags.NoResize)) {
                ImGui.text("Map Size");

            }
            ImGui.end();
            if (!opened.get()) {
                this.openProjectSettings = false;
            }
        }
    }

    //===============================================
    private void itemsContent() {
        for (SceneObject wBenchObject : this.getOpenGLRenderer().getWorld().getSceneObjects()) {
            WBenchObject wBenchObject1 = (WBenchObject) wBenchObject;
            boolean flag = this.currentSelectedObject == wBenchObject1;
            float x = ImGui.getContentRegionAvailX() - 30f;
            ImGui.pushID(wBenchObject1.getId());
            if (ImGui.selectable("(" + wBenchObject1.getId() + ") " + wBenchObject1.getName(), flag, ImGuiSelectableFlags.AllowItemOverlap, x, 18f)) {
                if (!flag) {
                    this.currentSelectedObject = wBenchObject1;
                    this.currentOperation = this.chooseDefaultGuizmoOperation();
                } else {
                    this.currentSelectedObject = null;
                }
            }
            ImGui.sameLine();
            if (ImGui.button("X")) {
                if (wBenchObject1.equals(this.currentSelectedObject)) {
                    this.currentSelectedObject = null;
                }
                wBenchObject1.setDead();
            }
            if (ImGui.isItemHovered()) {
                ImGui.setTooltip("id: " + wBenchObject1.getId());
            }
            ImGui.popID();
        }
    }

    private void resourcesContent() {
        if (ImGui.collapsingHeader("Entities")) {
            Map<String, Set<WBenchObjectTemplate>> objectTemplates = WBench.get().getProjectObjects().getEntityGroups();
            for (Map.Entry<String, Set<WBenchObjectTemplate>> entry : objectTemplates.entrySet()) {
                String groupName = entry.getKey();
                Set<WBenchObjectTemplate> objects = entry.getValue();

                ImGui.treePush();
                String groupNameTree = groupName != null ? groupName : "Other";
                if (ImGui.treeNode(groupNameTree)) {
                    ImGui.treePush();
                    for (WBenchObjectTemplate object : objects) {
                        boolean flag = this.currentSelectedTemplate == object;
                        if (ImGui.selectable(object.getId(), flag)) {
                            if (!flag) {
                                this.currentSelectedTemplate = object;
                                this.zeroPreviewParams();
                            } else {
                                this.currentSelectedTemplate = null;
                            }
                        }
                    }
                    ImGui.treePop();
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
        if (ImGui.collapsingHeader("Props")) {

        }
        if (ImGui.collapsingHeader("Sounds")) {

        }
        if (ImGui.collapsingHeader("Scripts")) {

        }
        ImGui.separator();
    }

    private void actionsContent() {
        if (this.currentSelectedTemplate != null && ImGui.collapsingHeader("Preview", ImGuiTreeNodeFlags.DefaultOpen)) {
            float available = Math.min(ImGui.getContentRegionAvailX(), 256);
            ImGui.text("Preview: " + this.currentSelectedTemplate.getId());
            ImGui.image(this.scenePreview.getTextureIDByIndex(0), available, available, 0.0f, 1.0f, 1.0f, 0.0f);

            float[] scaling = new float[]{this.previewDistance};
            if (ImGui.sliderFloat("Distance", scaling, 1.0f, 10.0f)) {
                this.previewDistance = scaling[0];
            }
            if (ImGui.button("Generate")) {
                WBenchObject wBenchObject = new WBenchObject(this.getOpenGLRenderer().getWorld(), this.currentSelectedTemplate);
                wBenchObject.setId(this.getOpenGLRenderer().getWorld().getSceneObjects().size());
                this.getOpenGLRenderer().getWorld().addObjectInWorld(wBenchObject);
            }
            ImGui.separator();
        }
        if (this.currentSelectedObject != null) {
            if (ImGui.collapsingHeader("Object", ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.treePush();
                if (this.currentSelectedObject.hasTranslationConstraints() && ImGui.treeNode("Transformation")) {
                    int objectFlagTranslate = this.currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag();
                    int objectFlagRotate = this.currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag();
                    int objectFlagScaling = this.currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag();

                    if (objectFlagTranslate != 0) {
                        if (ImGui.radioButton("Translate", (this.currentOperation & (Operation.TRANSLATE_X | Operation.TRANSLATE_Y | Operation.TRANSLATE_Z)) != 0)) {
                            this.currentOperation = this.chooseGuizmoOperation(true, false, false);
                        }
                    }

                    if (objectFlagRotate != 0) {
                        if (ImGui.radioButton("Rotation", (this.currentOperation & (Operation.ROTATE_X | Operation.ROTATE_Y | Operation.ROTATE_Z)) != 0)) {
                            this.currentOperation = this.chooseGuizmoOperation(false, true, false);
                        }
                    }

                    if (objectFlagScaling != 0) {
                        if (ImGui.radioButton("Scaling", (this.currentOperation & (Operation.SCALE_X | Operation.SCALE_Y | Operation.SCALE_Z)) != 0)) {
                            this.currentOperation = this.chooseGuizmoOperation(false, false, true);
                        }
                    }

                    this.processTranslations();
                    ImGui.treePop();
                }
                Collection<Tag<? extends TagItem>> tags = this.currentSelectedObject.getTagsContainer().getTagCollection();
                if (!tags.isEmpty() && ImGui.treeNode("Tags")) {
                    for (Tag<? extends TagItem> tag : tags) {
                        this.processTag(tag);
                    }
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
    }

    private void sceneContent(float sizeX, float sizeY) {
        float[] view = JGemsTransformManager.INSTANCE.getCameraViewMatrix().get(new float[16]);
        float[] projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix().get(new float[16]);
        float[] model = TransformUtils.getModelMatrix(new Pose3D()).get(new float[16]);
        ImGuizmo.setAllowAxisFlip(true);
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();

        float availableX = ImGui.getContentRegionAvailX();
        float availableY = ImGui.getContentRegionAvailY();

        if (!this.cameraCheckBox) {
            ImGuizmo.drawGrid(view, projection, model, (int) WBench.MAP_SIZE);
        }

        WIGluingRenderNode gluingRenderNode = (WIGluingRenderNode) this.getOpenGLRenderer().getRenderNodeByPass(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
        ImGui.image(gluingRenderNode.getOutColorBuffer().getTextureByIndex(0).getTextureId(), availableX, availableY, 0.0f, 1.0f, 1.0f, 0.0f);

        if (!this.cameraCheckBox) {
            ImVec2 imageSize = ImGui.getItemRectSize();
            ImVec2 imagePos = ImGui.getItemRectMin();

            float imGuizmoX = imageSize.x;
            float imGuizmoY = imageSize.y;
            ImGuizmo.setRect(imagePos.x, imagePos.y, imGuizmoX, imGuizmoY);

            float windowWidth = ImGui.getWindowWidth();
            float viewManipulateRight = ImGui.getWindowPosX() + windowWidth;
            float viewManipulateTop = ImGui.getWindowPosY();
           // ImGuizmo.viewManipulate(view, 1f, new float[]{viewManipulateRight - 128, viewManipulateTop + 24}, new float[]{128f, 128f}, 0x10101010);

            if (this.currentSelectedObject != null) {
                MeshAABBData meshAABBData = (MeshAABBData) this.currentSelectedObject.getModel().getMeshStructure().getMeshUserData(MeshStructure3D.MESH_AABB_UD);
                CullingAABB cullingAABB = meshAABBData.getNormalizedAABB(this.currentSelectedObject.getModel().getPose());

                WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));

                float[] modelMatrix = TransformUtils.getModelMatrix(this.currentSelectedObject.getModel().getPose()).get(new float[16]);
                ImGuizmo.manipulate(view, projection, modelMatrix, this.currentOperation, Mode.WORLD);

                Vector3f position = new Vector3f();
                Vector3f rotation = new Vector3f();
                Vector3f scaling = new Vector3f();
                Matrix4f newMatrix = this.getMatrixFromArray(modelMatrix);
                newMatrix.getTranslation(position);
                newMatrix.getScale(scaling);
                newMatrix.getUnnormalizedRotation(new Quaternionf()).getEulerAnglesXYZ(rotation);

                this.currentSelectedObject.setPosition(position);
                this.currentSelectedObject.setRotation(rotation.negate());
                this.currentSelectedObject.setScaling(scaling);
            }
        }
    }

    private void processTranslations() {
        int operationFlag = this.currentOperation;

        float[] posArrayX = new float[] {this.currentSelectedObject.getPosition().x};
        float[] posArrayY = new float[] {this.currentSelectedObject.getPosition().y};
        float[] posArrayZ = new float[] {this.currentSelectedObject.getPosition().z};

        float[] rotArrayX = new float[] {this.currentSelectedObject.getRotation().x};
        float[] rotArrayY = new float[] {this.currentSelectedObject.getRotation().y};
        float[] rotArrayZ = new float[] {this.currentSelectedObject.getRotation().z};

        float[] sclArrayX = new float[] {this.currentSelectedObject.getScaling().x};
        float[] sclArrayY = new float[] {this.currentSelectedObject.getScaling().y};
        float[] sclArrayZ = new float[] {this.currentSelectedObject.getScaling().z};

        if ((operationFlag & Operation.TRANSLATE_X) != 0) {
            ImGui.dragFloat("X", posArrayX, 0.01f);
        }
        if ((operationFlag & Operation.TRANSLATE_Y) != 0) {
            ImGui.dragFloat("Y", posArrayY, 0.01f);
        }
        if ((operationFlag & Operation.TRANSLATE_Z) != 0) {
            ImGui.dragFloat("Z", posArrayZ, 0.01f);
        }

        if ((operationFlag & Operation.ROTATE_X) != 0) {
            ImGui.sliderAngle("X", rotArrayX, -180.0f, 180.0f);
        }
        if ((operationFlag & Operation.ROTATE_Y) != 0) {
            ImGui.sliderAngle("Y", rotArrayY, -180.0f, 180.0f);
        }
        if ((operationFlag & Operation.ROTATE_Z) != 0) {
            ImGui.sliderAngle("Z", rotArrayZ, -180.0f, 180.0f);
        }

        if ((operationFlag & Operation.SCALE_X) != 0) {
            ImGui.dragFloat("X", sclArrayX, 0.01f, 0.001f, 1000.0f);
        }
        if ((operationFlag & Operation.SCALE_Y) != 0) {
            ImGui.dragFloat("Y", sclArrayY, 0.01f, 0.001f, 1000.0f);
        }
        if ((operationFlag & Operation.SCALE_Z) != 0) {
            ImGui.dragFloat("Z", sclArrayZ, 0.01f, 0.001f, 1000.0f);
        }

        currentSelectedObject.setPosition(new Vector3f(posArrayX[0], posArrayY[0], posArrayZ[0]));
        currentSelectedObject.setRotation(new Vector3f(rotArrayX[0], rotArrayY[0], rotArrayZ[0]));
        currentSelectedObject.setScaling(new Vector3f(sclArrayX[0], sclArrayY[0], sclArrayZ[0]));
    }

    private void processTag(Tag<? extends TagItem> tag) {
        ImGui.text(tag.getTagID().getDescription());

        ImGui.separator();
    }

    private int chooseDefaultGuizmoOperation() {
        int f1 = this.getOperationMask(this.currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        if (f1 != 0) {
            return f1;
        }
        int f2 = this.getOperationMask(this.currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        if (f2 != 0) {
            return f2;
        }
        return this.getOperationMask(this.currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
    }

    private int chooseGuizmoOperation(boolean translation, boolean rotation, boolean scaling) {
        if (translation) {
            return this.getOperationMask(this.currentSelectedObject.getTranslationConstraints().getPositionConstraints().getFlag(), Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z);
        }
        if (rotation) {
            return this.getOperationMask(this.currentSelectedObject.getTranslationConstraints().getRotationConstraints().getFlag(), Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z);
        }
        return this.getOperationMask(this.currentSelectedObject.getTranslationConstraints().getScalingConstraints().getFlag(), Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z);
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

    private Matrix4f getMatrixFromArray(float[] arr) {
        return new Matrix4f(arr[0], arr[1], arr[2], arr[3],
                            arr[4], arr[5], arr[6], arr[7],
                            arr[8], arr[9], arr[10], arr[11],
                            arr[12], arr[13], arr[14], arr[15]);
    }

    private Vector3f getRotationsFromMatrix(Matrix4f matrix4f) {
        Vector3f rotations = new Vector3f();
        Quaternionf quaternionf = new Quaternionf();
        matrix4f.getUnnormalizedRotation(quaternionf);
        quaternionf.getEulerAnglesXYZ(rotations);
        return rotations;
    }

    public void renderPreviewItem() {
        if (this.currentSelectedTemplate == null) {
            return;
        }
        OpenGLRenderer.setViewPort(new Vector2i(256, 256));
        this.scenePreview.bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.renderPreviewItem(this.previewDistance, WBenchResourceManager.localShaderAssets.preview, this.currentSelectedTemplate.getMeshGroup());
        this.scenePreview.unBindFBO();
        OpenGLRenderer.setViewPort(this.getOpenGLRenderer().getRenderingResolution());
    }

    private void renderPreviewItem(float distance, WBenchShaderManager shaderManager, MeshGroup meshGroup) {
        final Pose3D pose3D = new Pose3D(new Vector3f(0.0f, 0.0f, -3.5f));
        MeshAABBData meshAABBData = (MeshAABBData) meshGroup.getMeshUserData(MeshStructure3D.MESH_AABB_UD);
        CullingAABB cullingAABB = meshAABBData.getNormalizedAABB(pose3D);
        float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
        float scale = diagonal / 5.0f;
        pose3D.setScaling(new Vector3f(1.0f / scale).mul(distance));

        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(TransformUtils.getPerspectiveMatrix(1.0f, (float) (Math.PI / 2.0f), 0.01f, 100.0f)));
        shaderManager.performModel3DMatrix(new UniformString("model_matrix"), TransformUtils.getModelMatrix(pose3D).lookAt(new Vector3f(1.0f), new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f)));
        for (MeshNode3D<RenderMesh> meshNode3D : meshGroup.getAllNodes()) {
            if (meshNode3D.getMaterial().getDiffuse() instanceof ITexture2DProgram) {
                ITexture2DProgram imageBasedTexture = (ITexture2DProgram) meshNode3D.getMaterial().getDiffuse();
                shaderManager.performUniformTexture(new UniformString("diffuse_map"), imageBasedTexture);
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


    private void zeroPreviewParams() {
        this.previewDistance = 1.0f;
    }

    private Pair<Vector3f, Vector3f> adjustCamera(SunLight sun) {
        Vector3f sunPos = sun.getLightPosition().normalize().mul(100f);
        return new Pair<>(sunPos, new Vector3f(0.0f));
    }

    private void setNewCamera(@Nullable ICamera camera) {
        if (camera == null) {
            this.getOpenGLRenderer().getWorld().setCamera(this.oldCamera);
            this.oldCamera = null;
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

    public WBenchOpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    public ProjectManager getProjectManager() {
        return this.projectManager;
    }
}