package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abtractions;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IPostFXRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.BloomRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.FXAARenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.HDRRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public abstract class PostFXRenderNode extends IRenderNode.Template implements IPostFXRenderNode {
    private final FBOTexture2DProgram inColorScene;
    private FBOTexture2DProgram outColor;
    private BloomRenderProcessor bloomRenderProcessor;
    private HDRRenderProcessor hdrRenderProcessor;
    private FXAARenderProcessor fxaaRenderProcessor;

    public PostFXRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.inColorScene = inColor;
    }

    @Override
    public FBOTexture2DProgram getInColorBuffer() {
        return this.inColorScene;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return this.outColor;
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        this.getBloomRenderProcessor().runProcessorRendering(frameTicking);

        this.getOutColorBuffer().bindFBO();
        this.getHdrRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();

        this.getOutColorBuffer().bindFBO();
        this.getFxaaRenderProcessor().setValue((float) Math.pow(JGems3D.get().getGameSettings().fxaa.getValue(), 2));
        this.getFxaaRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();
    }

    public void initFBOs() {
        this.outColor = new FBOTexture2DProgram(true);
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void createResources() {
        this.initFBOs();

        this.bloomRenderProcessor = new BloomRenderProcessor(this.getOutColorBuffer(), this.getInColorBuffer(), this.getOpenGLRenderer(), this.getBlurringShader(), 6);
        this.hdrRenderProcessor = new HDRRenderProcessor(this.getOpenGLRenderer(), this.getInColorBuffer(), this.getOutColorBuffer(), this.getHDRShader());
        this.fxaaRenderProcessor = new FXAARenderProcessor(this.getOpenGLRenderer(), this.getOutColorBuffer(), this.getFXAAShader());

        this.getBloomRenderProcessor().createResources();
        this.getHdrRenderProcessor().createResources();
        this.getFxaaRenderProcessor().createResources();
    }

    public abstract @NotNull JGemsShaderManager getBlurringShader();
    public abstract @NotNull JGemsShaderManager getHDRShader();
    public abstract @NotNull JGemsShaderManager getFXAAShader();

    @Override
    public void destroyResources() {
        if (this.getOutColorBuffer() != null) {
            this.getOutColorBuffer().clearFBO();
        }

        this.getBloomRenderProcessor().destroyResources();
        this.getHdrRenderProcessor().destroyResources();
        this.getFxaaRenderProcessor().destroyResources();
    }

    public FXAARenderProcessor getFxaaRenderProcessor() {
        return this.fxaaRenderProcessor;
    }

    public HDRRenderProcessor getHdrRenderProcessor() {
        return this.hdrRenderProcessor;
    }

    public BloomRenderProcessor getBloomRenderProcessor() {
        return this.bloomRenderProcessor;
    }
}
