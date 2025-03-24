package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.JGems3D;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public class FXAARenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColor;
    private final JGemsShaderManager fxaaShader;

    public FXAARenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram inSceneColor, @NotNull JGemsShaderManager fxaaShader) {
        super(openGLRenderer);
        this.inColor = inSceneColor;
        this.fxaaShader = fxaaShader;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        JGemsShaderManager fxaaFilter = this.getFxaaShader();
        fxaaFilter.beginShading();
        fxaaFilter.performUniform(new UniformString("use_fxaa"), UniformFunctions.BOOLEAN(JGemsConfig.SYSTEM.USE_FXAA));
        fxaaFilter.performUniform(new UniformString("resolution"), UniformFunctions.VEC2I(this.getRenderingResolution()));
        fxaaFilter.performUniformTexture(new UniformString("texture_bindless"), this.getInColor().getTextureByIndex(0));
        fxaaFilter.performUniform(new UniformString("FXAA_SPAN_MAX"), UniformFunctions.FLOAT((float) Math.pow(JGems3D.get().getGameSettings().fxaa.getValue(), 2)));
        fxaaFilter.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsRenderingHelper.renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        fxaaFilter.endShading();
    }

    public JGemsShaderManager getFxaaShader() {
        return this.fxaaShader;
    }

    public FBOTexture2DProgram getInColor() {
        return this.inColor;
    }
}
