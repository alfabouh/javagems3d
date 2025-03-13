package workbench.graphics.scene.ui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.resources.assets.models.Model3D;
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
import workbench.graphics.scene.nodes.WDeferredRenderNode;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.screen.WBenchScreen;
import workbench.project.ProjectManager;
import workbench.resources.WBenchResourceManager;
import workbench.resources.shaders.WBenchShaderManager;

import java.lang.Math;
import java.util.Map;
import java.util.Set;

public class EditorInterface implements DearUIInterface {
    private final ProjectManager projectManager;
    private boolean openEnvironmentFogSettings;
    private boolean openEnvironmentSkySettings;
    private boolean openProjectSettings;

    private boolean cameraCheckBox;
    private final FixedCamera sunCamera;
    private ICamera oldCamera;

    private WBenchObjectTemplate currentTemplate;
    private final FBOTexture2DProgram scenePreview;
    private float previewDistance;

    public EditorInterface(FBOTexture2DProgram scenePreview, @NotNull ProjectManager projectManager) {
        this.projectManager = projectManager;
        this.openEnvironmentFogSettings = false;
        this.openEnvironmentSkySettings = false;
        this.openProjectSettings = false;

        this.sunCamera = new FixedCamera(new Vector3f(), new Vector3f());
        this.oldCamera = null;
        this.cameraCheckBox = false;

        this.scenePreview = scenePreview;
        this.previewDistance = 1.0f;
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
        Vector3f camPos = WBench.get().getScreen().getScene().getCamera().getCamPosition();

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

        ImGui.begin("Properties", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(propertiesWindowSizeX, propertiesWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowSizeX + sceneWindowOffset, YOffset);
        this.propertiesContent();
        ImGui.end();

        this.context();
    }

    private void context() {
        WBenchEnvironment environment = WBench.get().getScreen().getScene().getWorld().getEnvironment();
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

    private void zeroPreviewParams() {
        this.previewDistance = 1.0f;
    }

    private Pair<Vector3f, Vector3f> adjustCamera(SunLight sun) {
        Vector3f sunPos = sun.getLightPosition().normalize().mul(100f);
        return new Pair<>(sunPos, new Vector3f(0.0f));
    }

    private void setNewCamera(@Nullable ICamera camera) {
        if (camera == null) {
            WBench.get().getScreen().getScene().setCamera(this.oldCamera);
            this.oldCamera = null;
        } else {
            this.oldCamera = WBench.get().getScreen().getScene().getCamera();
            WBench.get().getScreen().getScene().setCamera(camera);
        }
    }

    //===============================================

    private void itemsContent() {
    }

    private void resourcesContent() {
        if (ImGui.collapsingHeader("Entities")) {
            Map<String, Set<WBenchObjectTemplate>> objectTemplates = WBench.get().getProjectObjects().getEntityGroups();
            for (Map.Entry<String, Set<WBenchObjectTemplate>> entry : objectTemplates.entrySet()) {
                String groupName = entry.getKey();
                Set<WBenchObjectTemplate> objects = entry.getValue();

                ImGui.treePush();
                if (groupName != null) {
                    if (ImGui.treeNode(groupName)) {
                        ImGui.treePush();
                        for (WBenchObjectTemplate object : objects) {
                            if (ImGui.selectable(object.getId(), this.currentTemplate == object)) {
                                this.currentTemplate = object;
                                this.zeroPreviewParams();
                            }
                        }
                        ImGui.treePop();
                        ImGui.treePop();
                    }
                } else {
                    if (ImGui.treeNode("Other")) {
                        ImGui.treePush();
                        for (WBenchObjectTemplate object : objects) {
                            if (ImGui.selectable(object.getId(), this.currentTemplate == object)) {
                                this.currentTemplate = object;
                                this.zeroPreviewParams();
                            }
                        }
                        ImGui.treePop();
                        ImGui.treePop();
                    }
                }
                ImGui.treePop();
            }

          //  if (selectedObject != null) {
          //      ImGui.separator();
          //      ImGui.text("Selected Object: " + selectedObject.getName());
          //      if (ImGui.button("Spawn")) {
          //          spawnObject(selectedObject);
          //      }
          //  }
        }
        if (ImGui.collapsingHeader("Props")) {

        }
        if (ImGui.collapsingHeader("Sounds")) {

        }
        if (ImGui.collapsingHeader("Scripts")) {

        }
        ImGui.separator();
    }

    private void propertiesContent() {
        if (this.currentTemplate != null) {
            float available = Math.min(ImGui.getContentRegionAvailX(), 256);
            ImGui.text("Preview: " + this.currentTemplate.getId());
            ImGui.image(this.scenePreview.getTextureIDByIndex(0), available, available, 0.0f, 1.0f, 1.0f, 0.0f);

            float[] scaling = new float[] {this.previewDistance};
            if (ImGui.sliderFloat("Distance", scaling, 1.0f, 10.0f)) {
                this.previewDistance = scaling[0];
            }
            if (ImGui.button("Spawn")) {
                WBench.get().getScreen().getScene().getWorld().addObjectInWorld(new WBenchObject(WBench.get().getScreen().getScene().getWorld(), new Model3D(new Pose3D(), this.currentTemplate.getMeshGroup()), this.currentTemplate.getRenderAttributes()));
            }
            ImGui.separator();
        }
    }

    private void sceneContent(float sizeX, float sizeY) {
        WBenchOpenGLRenderer wBenchOpenGLRenderer = (WBenchOpenGLRenderer) WBench.get().getScreen().getScene().getSceneRenderer();
        //WIGluingRenderNode gluingRenderNode = (WIGluingRenderNode) wBenchOpenGLRenderer.getRenderNodeByPass(WBenchOpenGLRenderer.GLUING_RENDER_PASS);
        WDeferredRenderNode gluingRenderNode = (WDeferredRenderNode) wBenchOpenGLRenderer.getRenderNodeByPass(WBenchOpenGLRenderer.DEFERRED_RENDER_PASS);
        float availableX = ImGui.getContentRegionAvailX();
        float availableY = ImGui.getContentRegionAvailY();
        ImGui.image(gluingRenderNode.getOutColorBuffer().getTextureByIndex(0).getTextureId(), availableX, availableY, 0.0f, 1.0f, 1.0f, 0.0f);
    }

    public void renderPreviewItem() {
        if (this.currentTemplate == null) {
            return;
        }
        OpenGLRenderer.setViewPort(new Vector2i(256, 256));
        this.scenePreview.bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.renderPreviewItem(this.previewDistance, WBenchResourceManager.localShaderAssets.preview, this.currentTemplate.getMeshGroup());
        this.scenePreview.unBindFBO();
        OpenGLRenderer.setViewPort(WBench.get().getScreen().getScene().getSceneRenderer().getRenderingResolution());
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