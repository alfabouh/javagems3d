package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneEntity;
import javagems3d.graphics.objects.rendering.constructors.ISceneEntityConstructor;

@JSCodingClass(binding = "JSSceneEntityConstructor", description = "Functional interface for constructing SceneEntity instances from scripts")
@FunctionalInterface
public interface JSSceneEntityConstructor {
    @JSCodingFunctionOrMethod(description = "Create a SceneEntity from JSSceneWorld, JSWorldItem, and JSEntityRenderData", paramNames = {"world", "worldItem", "entityRenderData"})
    JSSceneEntity create(JSSceneWorld world, JSWorldItem worldItem, JSEntityRenderData entityRenderData);
    @JSHideFromDoc
    default ISceneEntityConstructor toJavaConstructor() {
        return (sceneWorld, worldItem, entityRenderData) -> this.create(new JSSceneWorld(sceneWorld), new JSWorldItem(worldItem), new JSEntityRenderData(entityRenderData)).getJavaSceneEntity();
    }
}