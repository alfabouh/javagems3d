package javagems3d.graphics.rendering.scene.renderer.processors.skybox;

import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.ITextureProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.system.resources.assets.initialization.ModelAssetsInitializer;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

public class SkyboxRenderProcessor extends IRenderProcessor.Template {
    private final Model<Format3D> skyBoxModel;
    private final SkyBox skyBox;
    private ITextureProgram backgroundTexture;

    public SkyboxRenderProcessor(@NotNull SkyBox skyBox, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.skyBox = skyBox;
        this.backgroundTexture = null;
        this.skyBoxModel = new Model<>(new Format3D(), JGemsResourceManager.globalModelAssets.defaultCube_gr);
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.renderSkyBox(JGemsOpenGLRenderer.SkyBoxShader());
    }

    protected void renderSkyBox(JGemsShaderManager skyShaderManager) {
        Model<Format3D> model = this.skyBoxModel;
        skyShaderManager.beginShading();
        GL46.glDisable(GL46.GL_CULL_FACE);
        GL46.glDepthFunc(GL46.GL_LEQUAL);
        skyShaderManager.getUtils().performPerspectiveMatrix();
        Matrix4f Matrix4f = JGemsTransformation.getModelViewMatrix(model);
        Matrix4f.m30(0);
        Matrix4f.m31(0);
        Matrix4f.m32(0);
        if (this.getBackgroundTexture() != null) {
            skyShaderManager.performUniformTexture(new UniformString("skybox_background_sampler"), this.getBackgroundTexture());
        }
        skyShaderManager.performUniform(new UniformString("covered_by_fog"), UniformFunctions.BOOLEAN(this.getSkyBox().isSkyCoveredByFog()));
        skyShaderManager.performUniform(new UniformString("view_mat_inverted"), UniformFunctions.MAT4F(JGemsTransformation.INSTANCE.getCameraViewMatrix().invert()));
        skyShaderManager.getUtils().performModel3DViewMatrix(Matrix4f);
        skyShaderManager.performUniformTexture(new UniformString("skybox"), this.getSkyBox().getSky2DTexture());
        JGemsHelper.RENDERING.renderModel(model, GL46.GL_TRIANGLES);
        skyShaderManager.endShading();
        GL46.glDepthFunc(GL46.GL_LESS);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    public void setBackgroundTexture(ITextureProgram backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    protected ITextureProgram getBackgroundTexture() {
        return this.backgroundTexture;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }
}
