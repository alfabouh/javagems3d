package javagems3d.graphics.environment.shadows.scene;

import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

public interface IShadowScene {
    void createResources(OpenGLRenderer openGLRenderer);
    void destroyResources();

    default void recreateResources(OpenGLRenderer openGLRenderer) {
        this.destroyResources();
        this.createResources(openGLRenderer);
    }
}
