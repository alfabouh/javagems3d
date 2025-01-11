package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.JGemsHelper;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public class RawColorSceneRenderProcessor extends IRenderProcessor.Template {
    private FBOTexture2DProgram colorBuffer;
    private final JGemsShaderManager lightPassShader;
    private final IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
    private final SSAORenderProcessor ssaoRenderProcessor;

    public RawColorSceneRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull IndirectGeometryRenderProcessor indirectGeometryRenderProcessor, @NotNull SSAORenderProcessor ssaoRenderProcessor, @NotNull JGemsShaderManager lightPassShader) {
        super(openGLRenderer);
        this.lightPassShader = lightPassShader;
        this.indirectGeometryRenderProcessor = indirectGeometryRenderProcessor;
        this.ssaoRenderProcessor = ssaoRenderProcessor;
    }

    @Override
    public void createResources() {
        this.colorBuffer = new FBOTexture2DProgram(true);
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.colorBuffer.createFrameBuffer2DTexture(this.getOpenGLRenderer().getRenderingResolution(), clr, false, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        if (this.getColorBuffer() != null) {
            this.getColorBuffer().clearFBO();
        }
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        FBOTexture2DProgram gBuffer = this.getIndirectGeometryRenderProcessor().getGBuffer();
        FBOTexture2DProgram ssaoBuffer = this.getSsaoRenderProcessor().getSSAOBuffer();

        this.getColorBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        JGemsShaderManager deferredShader = this.getLightPassShader();
        deferredShader.beginShading();
        deferredShader.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(JGemsTransformation.INSTANCE.getCameraViewMatrix()));
        deferredShader.performUniformTexture(new UniformString("gPositions"), gBuffer.getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gNormals"), gBuffer.getTextureIDByIndex(1), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gTexture"), gBuffer.getTextureIDByIndex(2), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gEmission"), gBuffer.getTextureIDByIndex(3), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("gSpecular"), gBuffer.getTextureIDByIndex(4), GL46.GL_TEXTURE_2D);
        deferredShader.performUniformTexture(new UniformString("ssaoSampler"), ssaoBuffer.getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        deferredShader.performUniform(new UniformString("isSsaoValid"), UniformFunctions.BOOLEAN(this.getSsaoRenderProcessor().getSsaoBufferTexture() != null));
        deferredShader.getUtils().performShadowsInfo();
        deferredShader.getUtils().performOrthographicMatrix(this.getOpenGLRenderer().getScreenModel());
        JGemsHelper.RENDERING.renderModel(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        deferredShader.endShading();
        this.getColorBuffer().unBindFBO();
    }

    protected IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
        return this.indirectGeometryRenderProcessor;
    }

    protected SSAORenderProcessor getSsaoRenderProcessor() {
        return this.ssaoRenderProcessor;
    }

    public JGemsShaderManager getLightPassShader() {
        return this.lightPassShader;
    }

    public FBOTexture2DProgram getColorBuffer() {
        return colorBuffer;
    }
}