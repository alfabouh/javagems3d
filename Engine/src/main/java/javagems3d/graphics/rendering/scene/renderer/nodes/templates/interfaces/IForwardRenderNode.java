package javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IForwardRenderNode extends IRenderNode {
    FBOTexture2DProgram getInColorBuffer();
    FBOTexture2DProgram getOutColorBuffer();

    void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects);
    Collection<SceneObject> getForwardRenderingObjects();

    Collection<SceneObject> getRejectedDirectForwardRenderingObjects();
}
