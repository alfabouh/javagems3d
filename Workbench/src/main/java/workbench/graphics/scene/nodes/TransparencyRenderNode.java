package workbench.graphics.scene.nodes;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.scene.nodes.templates.ITransparencyRenderNode;

import java.util.Collection;
import java.util.Collections;

public final class TransparencyRenderNode extends IRenderNode.Template implements ITransparencyRenderNode {

    public TransparencyRenderNode(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return null;
    }

    @Override
    public FBOTexture2DProgram getInColorBuffer() {
        return null;
    }

    @Override
    public void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects) {

    }

    @Override
    public void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects) {

    }

    @Override
    public Collection<SceneObject> getIndirectDeferredRenderingObjects() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SceneObject> getDirectDeferredRenderingObjects() {
        return Collections.emptyList();
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
