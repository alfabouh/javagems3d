package workbench.graphics.scene.ui.game.editor;

import imgui.ImGui;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.game.editor.instances.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.instances.TextureAssetPreview;
import workbench.graphics.screen.WBenchScreen;
import workbench.resources.WBenchResourceManager;
import workbench.resources.shaders.WBenchShaderManager;

import java.lang.Math;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WindowInterfaceComponentG {
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final ActionsInterfaceComponentG actionsInterfaceComponentG;
    private final FBOTexture2DProgram modelScenePreview;
    private final OpenGLRenderer openGLRenderer;
    private final Vector2f previewRotation;
    private WindowSection<?> currentSection = null;

    public WindowInterfaceComponentG(OpenGLRenderer openGLRenderer, ActionsInterfaceComponentG actionsInterfaceComponentG, ResourcesInterfaceComponentG resourcesInterfaceComponentG, FBOTexture2DProgram modelScenePreview) {
        this.openGLRenderer = openGLRenderer;
        this.resourcesInterfaceComponentG = resourcesInterfaceComponentG;
        this.actionsInterfaceComponentG = actionsInterfaceComponentG;
        this.modelScenePreview = modelScenePreview;
        this.previewRotation = new Vector2f((float) (Math.PI / 4.0f), (float) (Math.PI / 4.0f));
    }

    public void windowContent() {
        WindowSection<?>[] sections = new WindowSection[] {
                new WindowSection<>(() -> this.resourcesInterfaceComponentG.getModelAssetsTreeDrawer().getPreviewWrapperObject(), this::modelPreview, "Model"),
                new WindowSection<>(() -> this.resourcesInterfaceComponentG.getTextureAssetsTreeDrawer().getPreviewWrapperObject(), this::texturePreview, "Texture")
        };
        List<WindowSection<?>> actualSections = Arrays.stream(sections).filter(WindowSection::check).collect(Collectors.toList());
        if (this.currentSection == null) {
            if (!actualSections.isEmpty()) {
                this.currentSection = actualSections.get(0);
            }
        } else {
            if (!this.currentSection.check()) {
                this.currentSection = null;
            } else {
                this.currentSection.render();
            }
        }

        if (!actualSections.isEmpty()) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("Choose Preview(" + actualSections.size() + ")")) {
                    for (WindowSection<?> windowSection : actualSections) {
                        if (ImGui.selectable(windowSection.id, this.currentSection != null && this.currentSection.id.equals(windowSection.id))) {
                            this.currentSection = windowSection;
                        }
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }
        }
    }

    private void texturePreview(TextureAssetPreview textureAssetPreview) {
        final float x = ImGui.getCursorPosX();
        final float y = ImGui.getCursorPosX();
        final float sx = ImGui.getWindowSizeX();
        final float sy = ImGui.getWindowSizeY();

        ImGui.image(textureAssetPreview.getAsset().getTexture2DProgram().getTextureId(), sx - 32, sy - 64, 1.0f, 0.0f, 0.0f, 1.0f);

        //try (Model2D screenModel = MeshHelper.generatePlane2DModel(new Vector2f(x, y), new Vector2f(x, y).add(sx,sy), 1.0f)) {
        //    JGemsShaderManager imgShader = WBenchResourceManager.localShaderAssets.gui_image;
        //    imgShader.beginShading();
        //    imgShader.performUniformTexture(new UniformString("texture_map"), textureAssetPreview.getTextureAsset().getTexture2DProgram());//finalFBO.getTextureByIndex(0)
        //    imgShader.performOrthographicMatrix(new UniformString("projection_model_matrix"), screenModel, JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        //    JGemsHelper.render().renderModel2D(screenModel, GL46.GL_TRIANGLES);
        //    imgShader.endShading();
        //}
    }

    private void modelPreview(ModelAssetPreview modelAssetPreview) {
        if (ImGui.isWindowFocused()) {
            this.actionsInterfaceComponentG.getScenePreviewModelG().modelPreviewScaling += WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().getScrollVector() * -0.25f;
        }
        final Pose3D pose = new Pose3D(new Vector3f(0.0f, 0.0f, 0.0f));
        final Vector2f inputRot = WBench.get().getControllerDispatcher().getCurrentController().getNormalizedRotationInput();
        if (this.actionsInterfaceComponentG.getScenePreviewModelG().isFlipModel()) {
            pose.setRotation(new Vector3f(0.0f, 0.0f, (float) Math.PI));
            inputRot.mul(-1, 1);
        } else {
            inputRot.mul(1, -1);
        }
        final Matrix4f model = TransformUtils.getModelMatrix(pose);
        final Matrix4f projection = TransformUtils.getPerspectiveMatrix(1.0f, (float) (Math.PI / 2.0f), 0.01f, 1024.0f);

        this.previewRotation.add(inputRot);
        this.previewRotation.x = (float) Math.max(-Math.PI / 2.0f + 0.01f, Math.min(Math.PI / 2.0f - 0.01f, this.previewRotation.x));
        float previewRotationC = this.previewRotation.x;

        final CullingAABB cullingAABB = modelAssetPreview.getAsset().getMeshGroup().getMeshAABBData().getNormalizedAABB(new Pose3D());
        final float diagonal = cullingAABB.getAabbMax().distance(cullingAABB.getAabbMin());
        final float dist = (float) (this.actionsInterfaceComponentG.getScenePreviewModelG().getModelPreviewScaling() + Math.sqrt(diagonal * 8.0f));
        float x = (float)(dist * Math.cos(previewRotationC) * Math.sin(previewRotation.y));
        float y = (float)(dist * Math.sin(previewRotationC));
        float z = (float)(dist * Math.cos(previewRotationC) * Math.cos(previewRotation.y));

        Matrix4f view = new Matrix4f().lookAt(new Vector3f(x, y, z), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));

        final int windowPosX = (int) ImGui.getWindowPosX();
        final int windowPosY = (int) ImGui.getWindowPosY();
        final int sizeX = (int) ImGui.getWindowSizeX();
        final int sizeY = (int) ImGui.getWindowSizeY();
        final int quadSize = Math.min(sizeX, sizeY);

        final float dX = 52.0f;

        this.renderPreviewItem(model, projection, view, cullingAABB, modelAssetPreview, quadSize, quadSize);
        final float cursorX = ImGui.getCursorPosX() + (ImGui.getWindowSizeX() / 2.0f - (quadSize - dX) / 2.0f);
        ImGui.setCursorPos(cursorX, ImGui.getCursorPosY());
        ImGui.image(this.modelScenePreview.getTextureIDByIndex(0), quadSize - dX, quadSize - dX, 0.0f, 1.0f, 1.0f, 0.0f);
    }

    public static void CHESS_TERRAIN(Matrix4f model, Matrix4f projection, Matrix4f view, FBOTexture2DProgram fboTexture2DProgram) {
        fboTexture2DProgram.bindFBO();
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        WBenchResourceManager.localShaderAssets.simple_flat.beginShading();
        WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
        WBenchResourceManager.localShaderAssets.simple_flat.performModel3DMatrix(new UniformString("model_matrix"), model);
        WBenchResourceManager.localShaderAssets.simple_flat.performViewMatrix(new UniformString("view_matrix"), view);
        WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.35f, 0.65f, 0.5f)));
        WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("drawCenterRect"), UniformFunctions.FLOAT(-1.0f));
        JGemsHelper.render().renderModel3D(WBenchOpenGLRenderer.flatTerrain, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
        WBenchResourceManager.localShaderAssets.simple_flat.endShading();
        GL46.glDisable(GL46.GL_BLEND);
        fboTexture2DProgram.unBindFBO();
    }

    private void renderPreviewItem(Matrix4f model, Matrix4f projection, Matrix4f view, CullingAABB cullingAABB, @NotNull ModelAssetPreview modelAsset, int sizeX, int sizeY) {
        this.modelScenePreview.bindFBO();
        GL46.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        OpenGLRenderer.setViewPort(new Vector2i(1024, 1024));
        if (cullingAABB != null && this.actionsInterfaceComponentG.getScenePreviewModelG().isShowAABB()) {
            WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(0.0f, 1.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
        }
        this.renderPreviewItem(model, projection, view, this.actionsInterfaceComponentG.getScenePreviewModelG().getModelPreviewScaling(), WBenchResourceManager.localShaderAssets.preview, modelAsset);
        if (this.actionsInterfaceComponentG.getScenePreviewModelG().isShowChessTerrain()) {
            WindowInterfaceComponentG.CHESS_TERRAIN(model, projection, view, this.modelScenePreview);
        }
        OpenGLRenderer.setViewPort(this.openGLRenderer.getRenderingResolution());
        this.modelScenePreview.unBindFBO();
        WBenchScreen.clearColor();
    }

    private void renderPreviewItem(Matrix4f model, Matrix4f projection, Matrix4f view, float distance, WBenchShaderManager shaderManager, @NotNull ModelAssetPreview modelAsset) {
        modelAsset.getAsset().getMeshGroup().getMeshAABBData().getNormalizedAABB(new Pose3D());
        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
        shaderManager.performModel3DMatrix(new UniformString("model_matrix"), model);
        shaderManager.performModel3DMatrix(new UniformString("view_matrix"), view);
        JGemsHelper.render().performAnimationsInfo(WBench.get().getResourceManager(), shaderManager, modelAsset);
        for (MeshNode3D<RenderMesh> meshNode3D : modelAsset.getAsset().getMeshGroup().getAllNodes()) {
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

    private static class WindowSection<T> {
        private final Supplier<T> object;
        private final Consumer<T> render;
        private final String id;

        public WindowSection(Supplier<T> object, Consumer<T> render, String id) {
            this.object = object;
            this.render = render;
            this.id = id;
        }

        public void render() {
            if (this.object.get() != null) {
                this.render.accept(this.object.get());
            }
        }

        public boolean check() {
            return this.object.get() != null;
        }

        public Supplier<T> getObject() {
            return this.object;
        }

        public Consumer<T> getRender() {
            return this.render;
        }

        public String getId() {
            return this.id;
        }
    }
}
