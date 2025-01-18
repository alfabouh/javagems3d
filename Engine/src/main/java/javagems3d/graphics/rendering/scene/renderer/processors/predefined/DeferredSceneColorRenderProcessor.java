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

public class DeferredSceneColorRenderProcessor extends IRenderProcessor.Template {
    private FBOTexture2DProgram colorBuffer;
    private final JGemsShaderManager lightPassShader;
    private final FBOTexture2DProgram gBuffer;
    private final FBOTexture2DProgram ssaoBuffer;
    private boolean isSsaoValid;

    public DeferredSceneColorRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull FBOTexture2DProgram gBuffer, @NotNull FBOTexture2DProgram ssaoBuffer, @NotNull JGemsShaderManager lightPassShader) {
        super(openGLRenderer);
        this.lightPassShader = lightPassShader;
        this.gBuffer = gBuffer;
        this.ssaoBuffer = ssaoBuffer;
        this.isSsaoValid = false;
    }

    @Override
    public void createResources() {
        this.colorBuffer = new FBOTexture2DProgram(true);
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB16F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.colorBuffer.createFrameBuffer2DTexture(this.getOpenGLRenderer().getRenderingResolution(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        if (this.getColorBuffer() != null) {
            this.getColorBuffer().clearFBO();
        }
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        FBOTexture2DProgram gBuffer = this.getGBuffer();
        FBOTexture2DProgram ssaoBuffer = this.getSsaoBuffer();
        //this.getIndirectGeometryRenderProcessor().getGBuffer().copyFBOtoFBOColor(this.getColorBuffer().getFrameBufferId(), new Pair[]{new Pair<>(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_COLOR_ATTACHMENT0)}, this.getRenderingResolution());
        this.getColorBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        JGemsShaderManager deferredShader = this.getLightPassShader();
        deferredShader.beginShading();
        deferredShader.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(JGemsTransformation.INSTANCE.getCameraViewMatrix()));
        deferredShader.performUniformTexture(new UniformString("gPositions"), gBuffer.getTextureByIndex(0));
        deferredShader.performUniformTexture(new UniformString("gNormals"), gBuffer.getTextureByIndex(1));
        deferredShader.performUniformTexture(new UniformString("gTexture"), gBuffer.getTextureByIndex(2));
        deferredShader.performUniformTexture(new UniformString("gEmission"), gBuffer.getTextureByIndex(3));
        deferredShader.performUniformTexture(new UniformString("gSpecular"), gBuffer.getTextureByIndex(4));
        deferredShader.performUniformTexture(new UniformString("ssaoSampler"), ssaoBuffer.getTextureByIndex(0));
        deferredShader.performUniform(new UniformString("isSsaoValid"), UniformFunctions.BOOLEAN(this.isSsaoValid()));
        deferredShader.getUtils().performShadowsInfo();
        deferredShader.getUtils().performOrthographicMatrix(this.getOpenGLRenderer().getScreenModel());
        JGemsHelper.RENDERING.renderModel(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        deferredShader.endShading();
        this.getColorBuffer().unBindFBO();
    }

    public boolean isSsaoValid() {
        return this.isSsaoValid;
    }

    public void setSsaoValid(boolean ssaoValid) {
        isSsaoValid = ssaoValid;
    }

    protected FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    protected FBOTexture2DProgram getSsaoBuffer() {
        return this.ssaoBuffer;
    }

    public JGemsShaderManager getLightPassShader() {
        return this.lightPassShader;
    }

    public FBOTexture2DProgram getColorBuffer() {
        return colorBuffer;
    }
}