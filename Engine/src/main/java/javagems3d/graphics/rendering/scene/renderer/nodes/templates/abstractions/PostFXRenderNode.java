package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IPostFXRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.BloomRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.FXAARenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.HDRRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public abstract class PostFXRenderNode extends IRenderNode.Template implements IPostFXRenderNode {
    private final FBOTexture2DProgram inColorScene;
    private FBOTexture2DProgram outColor;
    private FBOTexture2DProgram buffer;

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
        this.getBloomRenderProcessor().setUseBloom(JGems3D.get().getGameSettings().bloom.getValue() != 0);
        this.getBloomRenderProcessor().runProcessorRendering(frameTicking);

        this.getHdrRenderProcessor().prepare();
        this.getOutColorBuffer().bindFBO();
        if (!EventLauncher.pushEvent(new EventBus.PostFXOGLRenderInHDRFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.PRE), null).isCancelled()) {
            this.getHdrRenderProcessor().runProcessorRendering(frameTicking);
            EventLauncher.pushEvent(new EventBus.PostFXOGLRenderInHDRFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.POST), null);
        }
        this.getOutColorBuffer().unBindFBO();

        this.getFxaaRenderProcessor().prepare();
        this.getOutColorBuffer().bindFBO();
        if (!EventLauncher.pushEvent(new EventBus.PostFXOGLRenderInFXAAFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.PRE), null).isCancelled()) {
            this.getFxaaRenderProcessor().setValue((float) Math.pow(JGems3D.get().getGameSettings().fxaa.getValue(), 2));
            this.getFxaaRenderProcessor().runProcessorRendering(frameTicking);
            EventLauncher.pushEvent(new EventBus.PostFXOGLRenderInFXAAFBOEvent(this.getOpenGLRenderer(), this, frameTicking, EventBus.Run.POST), null);
        }
        this.getOutColorBuffer().unBindFBO();
    }

    public void initFBOs() {
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{ add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB); }};
        this.outColor = new FBOTexture2DProgram(true, false);
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        this.buffer = new FBOTexture2DProgram(true, false);
        this.getBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void createResources() {
        this.initFBOs();

        this.bloomRenderProcessor = new BloomRenderProcessor(this.getOutColorBuffer(), this.getInColorBuffer(), this.getOpenGLRenderer(), this.getBlurringShader(), 4);
        this.hdrRenderProcessor = new HDRRenderProcessor(this.getBuffer(), this.getOpenGLRenderer(), this.getInColorBuffer(), this.getOutColorBuffer(), this.getHDRShader());
        this.fxaaRenderProcessor = new FXAARenderProcessor(this.getBuffer(), this.getOpenGLRenderer(), this.getOutColorBuffer(), this.getFXAAShader());

        this.getBloomRenderProcessor().createResources();
        this.getHdrRenderProcessor().createResources();
        this.getFxaaRenderProcessor().createResources();
    }

    @Override
    public void destroyResources() {
        if (this.getOutColorBuffer() != null) {
            this.getOutColorBuffer().clearFBO();
        }
        if (this.getBuffer() != null) {
            this.getBuffer().clearFBO();
        }

        this.getBloomRenderProcessor().destroyResources();
        this.getHdrRenderProcessor().destroyResources();
        this.getFxaaRenderProcessor().destroyResources();
    }

    public abstract @NotNull JGemsShaderManager getBlurringShader();
    public abstract @NotNull JGemsShaderManager getHDRShader();
    public abstract @NotNull JGemsShaderManager getFXAAShader();

    public FBOTexture2DProgram getBuffer() {
        return this.buffer;
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

    @Override
    public NodeID getNodeID() {
        return JGemsOpenGLRenderer.POST_EFFECTS_RENDER_PASS;
    }
}
