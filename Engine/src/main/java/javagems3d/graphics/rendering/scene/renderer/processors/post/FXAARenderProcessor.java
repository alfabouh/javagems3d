package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public class FXAARenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColor;
    private final JGemsShaderManager fxaaShader;
    private float value;

    public FXAARenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram inSceneColor, @NotNull JGemsShaderManager fxaaShader) {
        super(openGLRenderer);
        this.inColor = inSceneColor;
        this.fxaaShader = fxaaShader;
        this.value = 0;
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
        fxaaFilter.performUniform(new UniformString(DefaultUniformDefinitions.USE_FXAA), UniformFunctions.BOOLEAN(JGemsConfig.SYSTEM.USE_FXAA));
        fxaaFilter.performUniform(new UniformString(DefaultUniformDefinitions.RESOLUTION), UniformFunctions.VEC2I(this.getRenderingResolution()));
        fxaaFilter.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getInColor().getTextureByIndex(0));
        fxaaFilter.performUniform(new UniformString(DefaultUniformDefinitions.FXAA_SPAN_MAX), UniformFunctions.FLOAT(this.getValue()));
        fxaaFilter.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        fxaaFilter.endShading();
    }

    public float getValue() {
        return this.value;
    }

    public FXAARenderProcessor setValue(float value) {
        this.value = value;
        return this;
    }

    public JGemsShaderManager getFxaaShader() {
        return this.fxaaShader;
    }

    public FBOTexture2DProgram getInColor() {
        return this.inColor;
    }
}
