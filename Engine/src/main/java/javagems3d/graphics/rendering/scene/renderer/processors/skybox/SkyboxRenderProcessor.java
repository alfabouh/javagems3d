package javagems3d.graphics.rendering.scene.renderer.processors.skybox;

import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;

import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

public class SkyboxRenderProcessor extends IRenderProcessor.Template {
    private final Model3D skyBoxModel;
    private final ISkyBox skyBox;
    private ITexture2DProgram backgroundTexture;
    private final JGemsShaderManager skyBoxShader;

    public SkyboxRenderProcessor(@NotNull ISkyBox skyBox, JGemsShaderManager skyBoxShader, MeshGroup cube, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.skyBoxShader = skyBoxShader;
        this.skyBox = skyBox;
        this.backgroundTexture = null;
        this.skyBoxModel = new Model3D(new Pose3D(), cube);
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.renderSkyBox(this.getSkyBoxShader());
    }

    protected void renderSkyBox(JGemsShaderManager skyShaderManager) {
        if (this.getSkyBox().getTexture() == null) {
            return;
        }
        Model3D model = this.skyBoxModel;
        skyShaderManager.beginShading();
        GL46.glDisable(GL46.GL_CULL_FACE);
        GL46.glDepthFunc(GL46.GL_LEQUAL);
        skyShaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
        Matrix4f viewMatrix = JGemsTransformManager.getModelViewMatrix(model);
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        if (this.getSkyBox() != null) {
            skyShaderManager.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.SKYBOX_CUBE), this.getSkyBox().getTexture());
        }
        if (this.getBackgroundTexture() != null) {
            skyShaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.SKYBOX_BACKGROUND), this.getBackgroundTexture());
        }

        skyShaderManager.performUniform(new UniformString(DefaultUniformDefinitions.COVERED_BY_FOG), UniformFunctions.BOOLEAN(this.getSkyBox().isSkyCoveredByFog()));
        skyShaderManager.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MAT_INVERTED), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix().invert()));
        skyShaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.MODEL_VIEW_MATRIX), viewMatrix);
        skyShaderManager.performUniform(new UniformString(DefaultUniformDefinitions.DRAW_SUN_MULT), UniformFunctions.FLOAT(!this.getSkyBox().isDrawSunOnSkyBox() ? 0.0f : 1.0f));
        JGemsHelper.render().renderModel3D(model, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
        skyShaderManager.endShading();
        GL46.glDepthFunc(GL46.GL_LESS);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    public void setBackgroundTexture(ITexture2DProgram backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public JGemsShaderManager getSkyBoxShader() {
        return this.skyBoxShader;
    }

    protected ITexture2DProgram getBackgroundTexture() {
        return this.backgroundTexture;
    }

    public ISkyBox getSkyBox() {
        return this.skyBox;
    }
}
