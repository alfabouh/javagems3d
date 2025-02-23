package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.JGemsHelper;
import javagems3d.global.JGemsRenderingGlobalConstants;
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

public class HDRRenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColor;
    private final FBOTexture2DProgram inBloomColor;
    private final JGemsShaderManager hdrShader;

    public HDRRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram inSceneColor, @NotNull FBOTexture2DProgram inBloomColor, @NotNull JGemsShaderManager hdrShader) {
        super(openGLRenderer);
        this.inColor = inSceneColor;
        this.inBloomColor = inBloomColor;
        this.hdrShader = hdrShader;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        JGemsShaderManager hdr = this.getHdrShader();
        hdr.beginShading();
        hdr.performUniform(new UniformString("exposure"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.HDR_EXPOSURE));
        hdr.performUniform(new UniformString("gamma"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.HDR_GAMMA));
        hdr.performUniform(new UniformString("use_hdr"), UniformFunctions.BOOLEAN(JGemsRenderingGlobalConstants.USE_HDR));
        hdr.performUniformTexture(new UniformString("texture_sampler"), this.getInColor().getTextureByIndex(0));
        hdr.performUniformTexture(new UniformString("bloom_sampler"), this.getInBloomColor().getTextureByIndex(0));
        hdr.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.RENDERING.renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        hdr.endShading();
    }

    public JGemsShaderManager getHdrShader() {
        return this.hdrShader;
    }

    public FBOTexture2DProgram getInBloomColor() {
        return this.inBloomColor;
    }

    public FBOTexture2DProgram getInColor() {
        return this.inColor;
    }
}
