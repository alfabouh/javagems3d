package api.scripting.coding.env.internal.util.world.render.world.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.render.data.JSPropRenderData;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneWorldProp", description = "Wrapper for SceneWorldProp objects, representing props in the scene.")
public class JSSceneWorldProp extends JSSceneProp {
    @JSHideFromDoc
    public JSSceneWorldProp(SceneWorldProp sceneWorldProp) {
        super(sceneWorldProp);
    }

    @JSCodingConstructor(description = "Create a JSSceneWorldProp instance", paramNames = {"name", "sceneWorld", "propRenderData"})
    public JSSceneWorldProp(@NotNull String name, @NotNull JSSceneWorld sceneWorld, @NotNull JSPropRenderData propRenderData) {
        super(new SceneWorldProp(name, sceneWorld.getJavaSceneWorld(), propRenderData.getJavaPropRenderData()));
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java SceneWorldProp object (unsafe, internal use)")
    public SceneWorldProp getJavaSceneWorldProp() {
        return (SceneWorldProp) this.getJavaSceneProp();
    }

    @JSHideFromDoc
    @Override
    public IAnimated getJavaAnimated() {
        return this.prop;
    }
}