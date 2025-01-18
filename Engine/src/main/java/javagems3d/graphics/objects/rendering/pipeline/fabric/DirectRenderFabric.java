package javagems3d.graphics.objects.rendering.pipeline.fabric;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;

public abstract class DirectRenderFabric implements IRenderFabric {
    private final Stage stage;

    public DirectRenderFabric(@NotNull Stage stage) {
        if (!stage.getType().equals(Type.DIRECT)) {
            JGemsHelper.getLogger().warn("RenderFabric type doesn't belong to DIRECT");
        }
        this.stage = stage;
    }

    public abstract void onPreRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData);
    public abstract void onRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData);
    public abstract void onPostRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData);

    public @NotNull Stage getRenderingStage() {
        return this.stage;
    }
}