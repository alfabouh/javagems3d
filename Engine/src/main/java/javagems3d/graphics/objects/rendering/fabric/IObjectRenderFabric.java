package javagems3d.graphics.objects.rendering.fabric;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

@FunctionalInterface
public interface IObjectRenderFabric {
    void onRender(OpenGLRenderer openGLRenderer, IRendered renderedObject, Object... metaData);
}