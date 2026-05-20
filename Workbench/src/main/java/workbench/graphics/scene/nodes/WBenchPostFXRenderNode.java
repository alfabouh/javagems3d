package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IPostFXRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.BloomRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.post.HDRRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.project.map.settings.MapProjectSettings;
import workbench.resources.WBenchResourceManager;

public class WBenchPostFXRenderNode extends IRenderNode.Template implements IPostFXRenderNode {
    private final FBOTexture2DProgram inColorScene;
    private FBOTexture2DProgram outColor;
    private FBOTexture2DProgram buffer;
    private BloomRenderProcessor bloomRenderProcessor;
    private HDRRenderProcessor hdrRenderProcessor;

    public WBenchPostFXRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
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

        this.getHdrRenderProcessor().prepare();
        this.getOutColorBuffer().bindFBO();
        this.getHdrRenderProcessor().setUseHDR(WBench.get().getMapProjectManager().mapProjectSettings.VIEW_HDR);
        this.getHdrRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();
    }

    public void initFBOs() {
        this.outColor = new FBOTexture2DProgram(true, false);
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.getOutColorBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);

        this.buffer = new FBOTexture2DProgram(true, false);
        this.getBuffer().createFrameBuffer2DTexture(this.getRenderingResolution(), clr, false, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void createResources() {
        this.initFBOs();
        this.bloomRenderProcessor = new BloomRenderProcessor(this.getOutColorBuffer(), this.getInColorBuffer(), this.getOpenGLRenderer(), this.getBlurringShader(), 4);
        this.hdrRenderProcessor = new HDRRenderProcessor(this.getBuffer(), this.getOpenGLRenderer(), this.getInColorBuffer(), this.getOutColorBuffer(), this.getHDRShader());

        this.getBloomRenderProcessor().createResources();
        this.getHdrRenderProcessor().createResources();
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
    }

    public FBOTexture2DProgram getBuffer() {
        return this.buffer;
    }

    public JGemsShaderManager getHDRShader() {
        return WBenchResourceManager.localShaderAssets.hdr;
    }

    public JGemsShaderManager getBlurringShader() {
        return WBenchResourceManager.localShaderAssets.blur5;
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