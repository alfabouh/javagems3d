package javagems3d.graphics.rendering.scene.culling.stages;

import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface ICullingAlgorithm {
    void filter(@NotNull Collection<? extends ICulled> sceneObjects);

    default void createResources() {
    }

    default void destroyResources() {
    }
}
