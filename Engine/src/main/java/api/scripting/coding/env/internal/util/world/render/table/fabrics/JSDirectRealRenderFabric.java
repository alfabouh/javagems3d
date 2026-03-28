package api.scripting.coding.env.internal.util.world.render.table.fabrics;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSPipeline;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultDirectRenderFabric;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@JSCodingClass(binding = "JSDirectRealRenderFabric", description = "Wrapper for DefaultDirectRenderFabric. Allows scripts to use and extend the default direct render factory.")
public abstract class JSDirectRealRenderFabric extends JSDirectRenderFabric {
    @JSCodingConstructor(description = "Constructs the JS wrapper for a DefaultDirectRenderFabric")
    public JSDirectRealRenderFabric(@NotNull DefaultDirectRenderFabric fabric) {
        super(fabric);
    }

    @JSCodingFunctionOrMethod(description = "Pre-render hook for a scene object", paramNames = {"pipeline", "jsShader", "jsRenderer", "jsSceneObject", "functionToHandleUniforms"})
    public void onPreRender(JSPipeline pipeline, JSShader jsShader, JSOpenGLRenderer jsRenderer, JSSceneObjectI jsSceneObject, @Nullable Consumer<JSShader> functionToHandleUniforms) {
        this.fabric.onPreRender(pipeline.getJavaPipeline(), jsShader.getJavaShaderManager(), jsRenderer.getJavaRenderer(), jsSceneObject.getJavaSceneObject(), ArbitraryArguments.pass(functionToHandleUniforms));
    }

    @JSCodingFunctionOrMethod(description = "Render a scene object", paramNames = {"pipeline", "jsShader", "jsRenderer", "jsSceneObject", "functionToHandleUniforms"})
    public void onRender(JSPipeline pipeline, JSShader jsShader, JSOpenGLRenderer jsRenderer, JSSceneObjectI jsSceneObject, @Nullable Consumer<JSShader> functionToHandleUniforms) {
        this.fabric.onRender(pipeline.getJavaPipeline(), jsShader.getJavaShaderManager(), jsRenderer.getJavaRenderer(), jsSceneObject.getJavaSceneObject(), ArbitraryArguments.pass(functionToHandleUniforms));
    }

    @JSCodingFunctionOrMethod(description = "Post-render hook for a scene object", paramNames = {"pipeline", "jsShader", "jsRenderer", "jsSceneObject", "functionToHandleUniforms"})
    public void onPostRender(JSPipeline pipeline, JSShader jsShader, JSOpenGLRenderer jsRenderer, JSSceneObjectI jsSceneObject, @Nullable Consumer<JSShader> functionToHandleUniforms) {
        this.fabric.onPostRender(pipeline.getJavaPipeline(), jsShader.getJavaShaderManager(), jsRenderer.getJavaRenderer(), jsSceneObject.getJavaSceneObject(), ArbitraryArguments.pass(functionToHandleUniforms));
    }

    @JSCodingFunctionOrMethod(description = "Render the mesh list of a 3D model", paramNames = {"jsRenderer", "jsShader", "jsModel", "layer"})
    @Override
    public void renderMeshList3D(JSOpenGLRenderer jsRenderer, JSShader jsShader, JSModel3D jsModel, int layer) {
        ((DefaultDirectRenderFabric) this.fabric).renderMeshList3D(jsRenderer.getJavaRenderer(), jsShader.getJavaShaderManager(), jsModel.getJavaModel3D(), layer);
    }
}