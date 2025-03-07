package workbench.graphics.scene.nodes.templates;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface WIDeferredRenderNode extends IRenderNode {
    FBOTexture2DProgram getOutGBuffer();
    FBOTexture2DProgram getOutColorBuffer();

    void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects);
    void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects);

    Collection<SceneObject> getIndirectDeferredRenderingObjects();
    Collection<SceneObject> getDirectDeferredRenderingObjects();

    Collection<SceneObject> getRejectedIndirectDeferredRenderingObjects();
    Collection<SceneObject> getRejectedDirectDeferredRenderingObjects();
}