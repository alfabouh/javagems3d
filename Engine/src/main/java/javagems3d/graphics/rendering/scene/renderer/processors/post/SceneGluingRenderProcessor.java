package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IDeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public class SceneGluingRenderProcessor extends IRenderProcessor.Template {
    private FBOTexture2DProgram glued;
    private final IDeferredRenderNode deferredRenderNode;

    public SceneGluingRenderProcessor(@NotNull IDeferredRenderNode deferredRenderNode,  @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.deferredRenderNode = deferredRenderNode;
    }

    @Override
    public void createResources() {
        this.glued = new FBOTexture2DProgram(true);
        T2DAttachmentContainer glued1 = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.glued.createFrameBuffer2DTexture(this.getRenderingResolution(), glued1, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        if (this.getGluedScene() != null) {
            this.getGluedScene().clearFBO();
        }
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.getGluedScene().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getDeferredRenderNode().getOutColorBuffer().copyFBOtoFBOColor(this.getGluedScene().getFrameBufferId(), Pair.get(new Pair<>(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_COLOR_ATTACHMENT0)), this.getRenderingResolution());
        this.getGluedScene().unBindFBO();
    }

    public IDeferredRenderNode getDeferredRenderNode() {
        return this.deferredRenderNode;
    }

    public FBOTexture2DProgram getGluedScene() {
        return this.glued;
    }
}
