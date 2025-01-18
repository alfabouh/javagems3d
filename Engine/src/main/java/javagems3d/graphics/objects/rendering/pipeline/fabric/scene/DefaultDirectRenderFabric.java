package javagems3d.graphics.objects.rendering.pipeline.fabric.scene;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.lwjgl.opengl.GL46;

public class DefaultDirectRenderFabric extends DirectRenderFabric {
    public DefaultDirectRenderFabric(Stage stage) {
        super(stage);
    }

    @Override
    public void onPreRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {

    }

    @Override
    public void onRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {
        if (renderedItem instanceof IModeled) {
            IModeled modeled = (IModeled) renderedItem;
            if (renderedItem.canBeRendered()) {
                if (renderedItem.canBeRendered()) {
                    Model<Format3D> model = modeled.getModel();
                    shaderManager.getUtils().performModel3DMatrix(model);
                    JGemsHelper.RENDERING.renderModel(model, GL46.GL_TRIANGLES);
                }
            }
        }
    }

    @Override
    public void onPostRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {

    }

    @Override
    public void createResources(IRendered renderedItem) {

    }

    @Override
    public void destroyResources(IRendered renderedItem) {

    }
}
