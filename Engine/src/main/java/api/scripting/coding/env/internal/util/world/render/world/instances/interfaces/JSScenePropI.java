package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.graphics.objects.entities.SceneProp;

@JSCodingClass(binding = "JSSceneEntityI", description = "Interface for script wrappers of SceneProp")
public interface JSScenePropI extends JSSceneObjectI {
    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneProp")
    SceneProp getJavaSceneProp();
}