package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.post.GluingRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.scene.nodes.templates.IGluingRenderNode;

public final class GluingRenderNode extends IRenderNode.Template implements IGluingRenderNode {

    public GluingRenderNode(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
    }

    @Override
    public FBOTexture2DProgram getInColorTransparencyBuffer() {
        return null;
    }

    @Override
    public FBOTexture2DProgram getInColorSceneBuffer() {
        return null;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return null;
    }

    @Override
    public void onRender(FrameTicking frameTicking) {

    }

    @Override
    public void createResources() {

    }

    @Override
    public void destroyResources() {

    }
}
