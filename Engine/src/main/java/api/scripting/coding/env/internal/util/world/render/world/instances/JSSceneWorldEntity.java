package api.scripting.coding.env.internal.util.world.render.world.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.objects.entities.world.SceneWorldEntity;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneWorldEntity", description = "Wrapper for SceneWorldEntity objects, representing entities in the scene.")
public class JSSceneWorldEntity extends JSSceneEntity {

    @JSCodingConstructor(description = "Create a JSSceneWorldEntity instance", paramNames = {"sceneWorld", "worldItem", "renderData"})
    public JSSceneWorldEntity(@NotNull JSSceneWorld sceneWorld, @NotNull JSWorldItem worldItem, @NotNull JSEntityRenderData renderData) {
        super(new SceneWorldEntity(sceneWorld.getJavaSceneWorld(), worldItem.getJavaWorldItem(), renderData.getJavaEntityRenderData()));
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java SceneWorldEntity object (unsafe, internal use)")
    public SceneWorldEntity getJavaSceneWorldEntity() {
        return (SceneWorldEntity) this.getJavaSceneEntity();
    }
}