package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneProp;
import javagems3d.graphics.objects.rendering.constructors.IScenePropConstructor;

@JSCodingClass(binding = "JSScenePropConstructor", description = "Functional interface for constructing SceneProp instances from scripts")
@FunctionalInterface
public interface JSScenePropConstructor {
    @JSCodingFunctionOrMethod(description = "Create a JSSceneProp from a name, JSSceneWorld, and JSPropRenderData", paramNames = {"name", "world", "renderData"})
    JSSceneProp create(String name, JSSceneWorld world, JSPropRenderData renderData);

    @JSHideFromDoc
    default IScenePropConstructor toJavaConstructor() {
        return (name, sceneWorld, propRenderData) -> {
            JSSceneWorld jsWorld = new JSSceneWorld(sceneWorld);
            JSPropRenderData jsRenderData = new JSPropRenderData(propRenderData);
            return this.create(name, jsWorld, jsRenderData).getJavaSceneProp();
        };
    }
}