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

public class HDRRenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColor;
    private final FBOTexture2DProgram inBloomColor;
    private final JGemsShaderManager hdrShader;
    private boolean useHDR;

    public HDRRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram inSceneColor, @NotNull FBOTexture2DProgram inBloomColor, @NotNull JGemsShaderManager hdrShader) {
        super(openGLRenderer);
        this.inColor = inSceneColor;
        this.inBloomColor = inBloomColor;
        this.hdrShader = hdrShader;
        this.useHDR = true;
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
        hdr.performUniform(new UniformString(DefaultUniformDefinitions.EXPOSURE), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.HDR_EXPOSURE));
        hdr.performUniform(new UniformString(DefaultUniformDefinitions.GAMMA), UniformFunctions.FLOAT(JGemsConfig.SYSTEM.HDR_GAMMA));
        hdr.performUniform(new UniformString(DefaultUniformDefinitions.USE_HDR), UniformFunctions.BOOLEAN(JGemsConfig.SYSTEM.USE_HDR && this.isUseHDR()));
        hdr.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getInColor().getTextureByIndex(0));
        hdr.performUniformTexture(new UniformString(DefaultUniformDefinitions.BLOOM_MAP), this.getInBloomColor().getTextureByIndex(0));
        hdr.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        hdr.endShading();
    }

    public boolean isUseHDR() {
        return this.useHDR;
    }

    public HDRRenderProcessor setUseHDR(boolean useHDR) {
        this.useHDR = useHDR;
        return this;
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
