package api.scripting.coding.env.internal.util.world.render.world.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import api.scripting.coding.env.internal.util.world.render.data.JSMeshStructureConstructor;
import api.scripting.coding.env.internal.util.world.render.data.JSPropRenderData;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithModelI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSScenePropI;
import javagems3d.graphics.objects.ILighted;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.models.mesh.IMesh;

@JSCodingClass(binding = "JSSceneProp", description = "Base wrapper for SceneProp, handling visibility, death state, and model access.")
public abstract class JSSceneProp implements JSScenePropI, JSSceneObjectWithModelI, JSSceneObjectWithLightsI {
    @JSHideFromDoc
    protected final SceneProp prop;

    @JSCodingConstructor(description = "Wrap existing SceneProp", paramNames = {"prop"})
    public JSSceneProp(SceneProp prop) {
        this.prop = prop;
    }

    @JSCodingConstructor(description = "Create SceneProp from name, world, and render data", paramNames = {"name", "world", "renderData"})
    public JSSceneProp(String name, JSSceneWorld world, JSPropRenderData renderData) {
        this.prop = new SceneWorldProp(name, world.getJavaSceneWorld(), renderData.getJavaPropRenderData());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneProp")
    public SceneProp getJavaSceneProp() {
        return this.prop;
    }

    @JSCodingFunctionOrMethod(description = "Get the name of the prop")
    public String getName() {
        return this.prop.getName();
    }

    @JSCodingFunctionOrMethod(description = "Set visibility of the prop", paramNames = {"visible"})
    public void setVisible(boolean visible) {
        this.prop.setVisible(visible);
    }

    @JSCodingFunctionOrMethod(description = "Check if prop is visible")
    public boolean isVisible() {
        return this.prop.canBeRendered();
    }

    @JSCodingFunctionOrMethod(description = "Mark prop as dead")
    public void setDead() {
        this.prop.setDead();
    }

    @JSCodingFunctionOrMethod(description = "Check if prop is dead")
    public boolean isDead() {
        return this.prop.isDead();
    }

    @JSCodingFunctionOrMethod(description = "Get model of this prop")
    @Override
    public JSModel3D getModel() {
        return this.prop.hasModel() ? new JSModel3D(this.prop.getModel()) : null;
    }

    @JSCodingFunctionOrMethod(description = "Get model constructor of this prop, JSMeshStructureConstructor<Void>")
    public JSMeshStructureConstructor<Void> getPropModelConstructor() {
        IModelConstructor<Void, ? extends IMesh> javaConstructor = this.prop.getPropModelConstructor();
        if (javaConstructor == null) return null;
        return t -> (JSMeshStructure3D) () -> javaConstructor.constructMeshDataGroup(null);
    }

    @JSCodingFunctionOrMethod(description = "Get world of this prop")
    public JSSceneWorld getWorld() {
        return new JSSceneWorld((SceneWorld) this.prop.getWorld());
    }

    @JSHideFromDoc
    @Override
    public SceneObject getJavaSceneObject() {
        return this.prop;
    }

    @Override
    public IModeled getJavaModeledObject() {
        return this.prop;
    }

    @Override
    public ILighted getJavaLightedObject() {
        return this.prop;
    }

    @JSCodingFunctionOrMethod(description = "Check if prop can be rendered")
    public boolean canBeRendered() {
        return this.prop.canBeRendered();
    }
}