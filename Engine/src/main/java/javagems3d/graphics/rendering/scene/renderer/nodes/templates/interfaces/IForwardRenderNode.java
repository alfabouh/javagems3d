package javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces;

import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IForwardRenderNode extends IRenderNode {
    FBOTexture2DProgram getInColorBuffer();
    FBOTexture2DProgram getOutColorBuffer();

    void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects);
    Collection<SceneObject> getForwardRenderingObjects();

    IForwardRenderNode setFilteredParticlesToRender(Collection<ParticleFX> filteredParticlesToRender);
    Collection<SceneObject> getRejectedDirectForwardRenderingObjects();
}
