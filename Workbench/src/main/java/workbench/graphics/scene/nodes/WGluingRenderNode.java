package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.GluingRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.scene.nodes.templates.WIGluingRenderNode;
import workbench.graphics.scene.processors.WGluingRenderProcessor;
import workbench.resources.WBenchResourceManager;

public final class WGluingRenderNode extends IRenderNode.Template implements WIGluingRenderNode {
    private WGluingRenderProcessor gluingRenderProcessor;
    private final FBOTexture2DProgram inColorScene;
    private final FBOTexture2DProgram inColorTransparency;

    public WGluingRenderNode(@NotNull FBOTexture2DProgram inColorTransparency, @NotNull FBOTexture2DProgram inColorScene, OpenGLRenderer openGLRenderer) {
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

    @Override
    public void createResources() {
        this.gluingRenderProcessor = new WGluingRenderProcessor(this.getInColorTransparencyBuffer(), this.getOutColorBuffer(), this.getOpenGLRenderer(), WBenchResourceManager.localShaderAssets.scene_gluing);
        this.getSceneGluingRenderProcessor().createResources();
    }

    @Override
    public void destroyResources() {
        this.getSceneGluingRenderProcessor().destroyResources();
    }

    public WGluingRenderProcessor getSceneGluingRenderProcessor() {
        return this.gluingRenderProcessor;
    }
}
