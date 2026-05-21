package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.screen.JGemsScreen;
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
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

public class BloomRenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColor;
    private final FBOTexture2DProgram outColor;
    private  FBOTexture2DProgram buffer1;
    private  FBOTexture2DProgram buffer2;
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

    private Vector2i newDim() {
        return this.getRenderingResolution().div(JGemsConfig.SYSTEM.BLOOM_RES_DIV);
    }

    @Override
    public void createResources() {
        if (this.useBloom) {
            T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
                add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
            }};
            this.buffer1 = new FBOTexture2DProgram(true, false);
            this.buffer1.createFrameBuffer2DTexture(this.newDim(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

            this.buffer2 = new FBOTexture2DProgram(true, false);
            this.buffer2.createFrameBuffer2DTexture(this.newDim(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        }
    }

    @Override
    public void destroyResources() {
        this.buffer1.clearFBO();
        this.buffer2.clearFBO();
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        if (!this.getWorld().getEnvironment().getLightScene().isBloomEnabled() || !JGemsConfig.SYSTEM.USE_BLOOM || !this.isUseBloom()) {
            this.getOutColor().bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            this.getOutColor().unBindFBO();
            return;
        }
        this.getInColor().copyFBOtoFBOColor(this.buffer1.getFrameBufferId(), new Pair[] {new Pair<>(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_COLOR_ATTACHMENT0)}, this.getOpenGLRenderer().getRenderingResolution(), this.newDim());
        JGemsShaderManager blurShader = this.getBlurShader();
        FBOTexture2DProgram ping = this.buffer1;
        FBOTexture2DProgram pong = this.buffer2;
        int steps = this.getSteps();

        OpenGLRenderer.setViewPort(this.newDim());
        blurShader.beginShading();
        for (int i = 0; i < this.getSteps(); i++) {
            pong.bindFBO();
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            blurShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), ping.getTextureByIndex(0));
            blurShader.performUniform(new UniformString(DefaultUniformDefinitions.DIRECTION), UniformFunctions.VEC2F(i % 2 == 0 ? new Vector2f(1.0f, 0.0f) : new Vector2f(0.0f, 1.0f)));
            blurShader.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
            JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
            pong.unBindFBO();
            if (i % 2 == 0) {
                pong = this.buffer1;
                ping = this.buffer2;
            } else {
                ping = this.buffer1;
                pong = this.buffer2;
            }
        }
        blurShader.endShading();
        OpenGLRenderer.setViewPort(this.getRenderingResolution());

        this.buffer2.copyFBOtoFBOColor(this.getOutColor().getFrameBufferId(), new Pair[] {new Pair<>(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_COLOR_ATTACHMENT0)}, this.newDim(), this.getOpenGLRenderer().getRenderingResolution());
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
