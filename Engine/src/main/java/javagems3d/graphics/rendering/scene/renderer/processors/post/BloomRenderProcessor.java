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
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;

public class BloomRenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColor;
    private final FBOTexture2DProgram outColor;
    private final JGemsShaderManager blurShader;
    private boolean useBloom;
    private final int steps;

    public BloomRenderProcessor(@NotNull FBOTexture2DProgram outColor, @NotNull FBOTexture2DProgram inColor, @NotNull OpenGLRenderer openGLRenderer, @NotNull JGemsShaderManager blurShader, int steps) {
        super(openGLRenderer);
        this.inColor = inColor;
        this.outColor = outColor;
        this.steps = steps;
        this.blurShader = blurShader;
        this.useBloom = true;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        if (!JGemsConfig.SYSTEM.USE_BLOOM || !this.isUseBloom()) {
            this.getOutColor().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            this.getOutColor().unBindFBO();
        }
        JGemsShaderManager blurShader = this.getBlurShader();
        FBOTexture2DProgram startFbo = this.getInColor();
        int startBinding = 1;
        int steps = this.getSteps();

        blurShader.beginShading();
        blurShader.performUniform(new UniformString("resolution"), UniformFunctions.VEC2I(this.getRenderingResolution().div(4.0f)));
        for (int i = 0; i < steps; i++) {
            this.getOutColor().bindFBO();
            blurShader.performUniformTexture(new UniformString("texture_map"), startFbo.getTextureByIndex(startBinding));
            blurShader.performUniform(new UniformString("direction"), UniformFunctions.VEC2F(i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f)));
            blurShader.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
            JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
            this.getOutColor().unBindFBO();
            startFbo = this.getOutColor();
            startBinding = 0;
        }
        blurShader.endShading();
    }

    public boolean isUseBloom() {
        return this.useBloom;
    }

    public BloomRenderProcessor setUseBloom(boolean useBloom) {
        this.useBloom = useBloom;
        return this;
    }

    public int getSteps() {
        return this.steps;
    }

    public JGemsShaderManager getBlurShader() {
        return this.blurShader;
    }

    public FBOTexture2DProgram getOutColor() {
        return this.outColor;
    }

    public FBOTexture2DProgram getInColor() {
        return this.inColor;
    }
}
