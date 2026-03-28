package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.graphics.objects.entities.SceneEntity;

@JSCodingClass(binding = "JSSceneEntityI", description = "Interface for script wrappers of SceneEntity")
public interface JSSceneEntityI extends JSSceneObjectI {
    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneEntity")
    SceneEntity getJavaSceneEntity();
}