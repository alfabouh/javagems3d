package api.scripting.coding.env.internal.util.world.render.table.fabrics;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;

@JSCodingClass(binding = "JSRenderFabricI", description = "Interface for accessing render fabric used in rendering pipeline")
public interface JSRenderFabricI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java render fabric")
    IRenderFabric getJavaRenderFabric();
}