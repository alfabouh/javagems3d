package javagems3d.graphics.rendering.scene.culling;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface ISceneCulling extends IResourceInit, IWindow.ResizeEvent {
    @NotNull OpenGLRenderer getOpenGLRender();
    void cull(@NotNull Collection<SceneObject> sceneObjects);

    boolean disableDistanceCulling();
    boolean disableFrustumCulling();
}
