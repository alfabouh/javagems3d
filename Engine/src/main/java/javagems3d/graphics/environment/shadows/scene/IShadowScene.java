package javagems3d.graphics.environment.shadows.scene;

import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

import java.util.List;

public interface IShadowScene {
    void createResources(OpenGLRenderer openGLRenderer);
    void destroyResources();

    SunLightShadow getSunLightShadow();
    List<PointLightShadow> getPointLightShadows();

    default void recreateResources(OpenGLRenderer openGLRenderer) {
        this.destroyResources();
        this.createResources(openGLRenderer);
    }
}
