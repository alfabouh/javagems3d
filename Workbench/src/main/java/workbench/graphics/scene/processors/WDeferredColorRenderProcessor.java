package workbench.graphics.scene.processors;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.help.JGemsShadersHelper;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;
import workbench.graphics.scene.world.WBenchWorld;

public class WDeferredColorRenderProcessor extends IRenderProcessor.Template {
    private final JGemsShaderManager lightPassShader;
    private final FBOTexture2DProgram gBuffer;

    public WDeferredColorRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram gBuffer, @NotNull JGemsShaderManager lightPassShader) {
        super(openGLRenderer);
        this.lightPassShader = lightPassShader;
        this.gBuffer = gBuffer;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        FBOTexture2DProgram gBuffer = this.getGBuffer();
        JGemsShaderManager deferredShader = this.getLightPassShader();
        deferredShader.beginShading();
        deferredShader.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix()));
        deferredShader.performUniformTextureBindless(new UniformString("gPositions"), gBuffer.getTextureByIndex(0));
        deferredShader.performUniformTextureBindless(new UniformString("gNormals"), gBuffer.getTextureByIndex(1));
        deferredShader.performUniformTextureBindless(new UniformString("gTexture"), gBuffer.getTextureByIndex(2));
        deferredShader.performUniformTextureBindless(new UniformString("gEmission"), gBuffer.getTextureByIndex(3));
        deferredShader.performUniformTextureBindless(new UniformString("gMetallicRoughness"), gBuffer.getTextureByIndex(4));
        deferredShader.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsShadersHelper.performShadowsInfo(((WBenchWorld) this.getOpenGLRenderer().getWorld()).getEnvironment(), deferredShader);
        JGemsRenderingHelper.renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        deferredShader.endShading();
    }

    protected FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public JGemsShaderManager getLightPassShader() {
        return this.lightPassShader;
    }
}