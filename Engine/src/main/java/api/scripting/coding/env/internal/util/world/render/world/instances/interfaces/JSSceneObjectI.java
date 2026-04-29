package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.render.processing.JSCullingRules;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.table.JSRenderTable;
import api.scripting.coding.env.internal.util.world.render.table.fabrics.JSRenderFabricI;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSPipeline;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;

import java.util.HashSet;
import java.util.Set;

@JSCodingClass(binding = "JSSceneObjectI", description = "Base interface for scene objects with rendering control")
public interface JSSceneObjectI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneObject", paramNames = {})
    SceneObject getJavaSceneObject();

    @JSCodingFunctionOrMethod(description = "Check if object has valid render attributes", paramNames = {})
    default boolean canBeRendered() {
        return ((IRendered) this.getJavaSceneObject()).canBeRendered();
    }

    @JSCodingFunctionOrMethod(description = "Check if object can be rendered in specific pipeline", paramNames = {"pipeline"})
    default boolean canBeRendered(JSPipeline pipeline) {
        return ((IRendered) this.getJavaSceneObject()).canBeRendered(pipeline.getJavaPipeline());
    }

    @JSCodingFunctionOrMethod(description = "Get culling rules", paramNames = {})
    default JSCullingRules getCullingRules() {
        return new JSCullingRules(((IRendered) this.getJavaSceneObject()).getCullingRules());
    }

    @JSCodingFunctionOrMethod(description = "Get render attributes", paramNames = {})
    default JSRenderAttributes getRenderAttributes() {
        return new JSRenderAttributes(((IRendered) this.getJavaSceneObject()).getRenderAttributes());
    }

    @JSCodingFunctionOrMethod(description = "Get render table", paramNames = {})
    default JSRenderTable getRenderTable() {
        return new JSRenderTable(((IRendered) this.getJavaSceneObject()).getRenderTable());
    }

    @JSCodingFunctionOrMethod(description = "Get render fabrics set", paramNames = {})
    default Set<JSRenderFabricI> getRenderFabricsSet() {
        Set<IRenderFabric> set = this.getJavaSceneObject().getRenderFabricsSet();
        Set<JSRenderFabricI> result = new HashSet<>();
        for (IRenderFabric fabric : set) {
            result.add(() -> fabric);
        }
        return result;
    }

    @JSCodingFunctionOrMethod(description = "Get render fabric for pipeline", paramNames = {"pipeline"})
    default JSRenderFabricI getRenderFabric(JSPipeline pipeline) {
        return () -> JSSceneObjectI.this.getJavaSceneObject().getRenderFabric(pipeline.getJavaPipeline());
    }
}