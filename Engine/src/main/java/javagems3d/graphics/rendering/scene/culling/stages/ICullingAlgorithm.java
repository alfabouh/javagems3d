package javagems3d.graphics.rendering.scene.culling.stages;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

import java.util.Collection;
import java.util.Set;

public interface ICullingAlgorithm {
    void filter(Collection<SceneObject> sceneObjects);

    default void createResources(OpenGLRenderer openGLRenderer) {
    }

    default void destroyResources(OpenGLRenderer openGLRenderer) {
    }
}
