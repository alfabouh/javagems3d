package javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IGluingRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.GluingRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;

public abstract class GluingRenderNode extends IRenderNode.Template implements IGluingRenderNode {
    private GluingRenderProcessor gluingRenderProcessor;
    private final FBOTexture2DProgram inColorScene;
    private final FBOTexture2DProgram inColorTransparency;

    public GluingRenderNode(@NotNull FBOTexture2DProgram inColorTransparency, @NotNull FBOTexture2DProgram inColorScene, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.inColorScene = inColorScene;
        this.inColorTransparency = inColorTransparency;
    }

    @Override
    public FBOTexture2DProgram getInColorTransparencyBuffer() {
        return this.inColorTransparency;
    }

    @Override
    public FBOTexture2DProgram getInColorSceneBuffer() {
        return this.inColorScene;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return this.getInColorSceneBuffer();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        this.getOutColorBuffer().bindFBO();
        this.getSceneGluingRenderProcessor().runProcessorRendering(frameTicking);
        this.getOutColorBuffer().unBindFBO();
    }

    public abstract @NotNull JGemsShaderManager getGluingShader();

    @Override
    public void createResources() {
        this.gluingRenderProcessor = new GluingRenderProcessor(this.getInColorTransparencyBuffer(), this.getOutColorBuffer(), this.getOpenGLRenderer(), this.getGluingShader());
        this.getSceneGluingRenderProcessor().createResources();
    }

    @Override
    public void destroyResources() {
        this.getSceneGluingRenderProcessor().destroyResources();
    }

    public GluingRenderProcessor getSceneGluingRenderProcessor() {
        return this.gluingRenderProcessor;
    }

    @Override
    public NodeID getNodeID() {
        return JGemsOpenGLRenderer.GLUING_RENDER_PASS;
    }
}
