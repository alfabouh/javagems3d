package workbench.graphics.fabrics;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import workbench.graphics.objects.WBenchMarkerObject;

import java.util.function.Consumer;

public class MarkerSimpleTransparentRenderFabric extends DirectRenderFabric {
    public MarkerSimpleTransparentRenderFabric(Stage stage) {
        super(stage);
    }

    @Override
    public void onPreRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {

    }

    @Override
    public void onRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {
        if (renderedItem instanceof WBenchMarkerObject modeled) {
            WBenchMarkerObject markerObject = (WBenchMarkerObject) renderedItem;
            if (renderedItem.canBeRendered()) {
                Model3D model = modeled.getModel();
                Consumer<Pair<JGemsShaderManager, IRendered>> functionToHandleUniforms = metaData.getterFunc().getObject(0);
                if (functionToHandleUniforms != null) {
                    functionToHandleUniforms.accept(new Pair<>(shaderManager, renderedItem));
                }
                shaderManager.performMatrix4(new UniformString("projection_matrix"), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
                shaderManager.performModel3DMatrix(new UniformString("model_matrix"), model);
                shaderManager.performMatrix4(new UniformString("view_matrix"), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
                shaderManager.performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(markerObject.getColor(), 0.5f)));
                JGemsHelper.render().renderModel3D(model, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
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
