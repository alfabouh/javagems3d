package workbench.graphics.scene.ui.game.editor.window_interface.scenes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.game.editor.actions_interface.ActionsInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.misc.ModelAssetPreview;
import workbench.graphics.scene.ui.game.editor.window_interface.WindowInterfaceComponentG;
import workbench.graphics.screen.WBenchScreen;
import workbench.resources.WBenchResourceManager;
import workbench.resources.shaders.WBenchShaderManager;

public class ModelPreviewEditorWindow {
    private final ActionsInterfaceComponentG actionsInterfaceComponentG;
    private final OpenGLRenderer openGLRenderer;

    public ModelPreviewEditorWindow(ActionsInterfaceComponentG actionsInterfaceComponentG, OpenGLRenderer openGLRenderer) {
        this.actionsInterfaceComponentG = actionsInterfaceComponentG;
        this.openGLRenderer = openGLRenderer;
    }

    public void render(Matrix4f projection, Matrix4f model, Matrix4f view, CullingAABB cullingAABB, @NotNull ModelAssetPreview modelAsset, FBOTexture2DProgram modelScenePreview) {
        this.renderPreviewItem(model, projection, view, cullingAABB, modelAsset, modelScenePreview);
    }

    private void CHESS_TERRAIN(Matrix4f model, Matrix4f projection, Matrix4f view, FBOTexture2DProgram fboTexture2DProgram) {
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

    private void renderPreviewItem(Matrix4f model, Matrix4f projection, Matrix4f view, CullingAABB cullingAABB, @NotNull ModelAssetPreview modelAsset, FBOTexture2DProgram fboTexture2DProgram) {
        fboTexture2DProgram.bindFBO();
        GL46.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        OpenGLRenderer.setViewPort(new Vector2i(1024, 1024));
        if (cullingAABB != null && this.actionsInterfaceComponentG.getScenePreviewModelG().isShowAABB()) {
            WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.BoxRequest(cullingAABB.getAabbMin(), cullingAABB.getAabbMax(), new Vector3f(0.0f, 1.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.Depth()));
        }
        this.renderPreviewModel(model, projection, view, WBenchResourceManager.localShaderAssets.preview, modelAsset);
        if (this.actionsInterfaceComponentG.getScenePreviewModelG().isShowChessTerrain()) {
            this.CHESS_TERRAIN(model, projection, view, fboTexture2DProgram);
        }
        OpenGLRenderer.setViewPort(this.openGLRenderer.getRenderingResolution());
        fboTexture2DProgram.unBindFBO();
        WBenchScreen.clearColor();
    }

    private void renderPreviewModel(Matrix4f model, Matrix4f projection, Matrix4f view, WBenchShaderManager shaderManager, @NotNull ModelAssetPreview modelAsset) {
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
}
