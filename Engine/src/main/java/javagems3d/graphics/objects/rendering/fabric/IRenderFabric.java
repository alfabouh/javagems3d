package javagems3d.graphics.objects.rendering.fabric;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.fabric.args.ArbitraryArguments;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

public interface IRenderFabric {
    void onRender(OpenGLRenderer openGLRenderer, IRendered rendered, ArbitraryArguments arbitraryArguments);

    void createResources(IRendered rendered);
    void destroyResources(IRendered rendered);

    abstract class Template implements IRenderFabric {
    }
}