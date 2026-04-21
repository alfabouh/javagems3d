package javagems3d.graphics.environment.shadows.scene;

import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface IShadowScene {
    void createResources(OpenGLRenderer openGLRenderer);
    void destroyResources();

    SunLightShadow getSunLightShadow();
    List<PointLightShadow> getPointLightShadows();
    HashMap<PointLight, Integer> getSortedPointLightMapReadyToBind(Vector3f viewPoint, Collection<PointLight> pointLightsRaw);

    default void recreateResources(OpenGLRenderer openGLRenderer) {
        this.destroyResources();
        this.createResources(openGLRenderer);
    }
}
