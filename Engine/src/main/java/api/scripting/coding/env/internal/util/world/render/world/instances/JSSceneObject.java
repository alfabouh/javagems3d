package api.scripting.coding.env.internal.util.world.render.world.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimationData;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.bound.JSBoxAABB;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithModelI;
import javagems3d.graphics.objects.ILighted;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.models.animation.AnimationData;

@JSCodingClass(binding = "JSSceneObject", description = "Base scene object with rendering, lighting and animation control")
public abstract class JSSceneObject implements JSSceneObjectI, JSSceneObjectWithModelI, JSSceneObjectWithLightsI {
    @JSHideFromDoc
    private final SceneObject sceneObject;

    @JSCodingConstructor(description = "Wrap existing SceneObject", paramNames = {"sceneObject"})
    public JSSceneObject(SceneObject sceneObject) {
        this.sceneObject = sceneObject;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneObject", paramNames = {})
    @Override
    public SceneEntity getJavaSceneObject() {
        return (SceneEntity) this.sceneObject;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying modeled object", paramNames = {})
    @Override
    public IModeled getJavaModeledObject() {
        return this.sceneObject;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying lighted object", paramNames = {})
    @Override
    public ILighted getJavaLightedObject() {
        return this.sceneObject;
    }

    @JSCodingFunctionOrMethod(description = "Get animation data", paramNames = {})
    public JSAnimationData getAnimationData() {
        AnimationData data = this.sceneObject.getAnimationData();
        return data == null ? null : new JSAnimationData(data);
    }

    @JSCodingFunctionOrMethod(description = "Set animation by ID", paramNames = {"id"})
    public JSAnimationData setAnimation(int id) {
        AnimationData data = this.sceneObject.setAnimationByID(id);
        return data == null ? null : new JSAnimationData(data);
    }

    @JSCodingFunctionOrMethod(description = "Set animation speed multiplier", paramNames = {"speed"})
    public void setAnimationSpeed(float speed) {
        this.sceneObject.setAnimationSpeed(speed);
    }

    @JSCodingFunctionOrMethod(description = "Set model", paramNames = {"model"})
    public JSSceneObject setModel(JSModel3D model) {
        this.sceneObject.setModel(model.getJavaModel3D());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set render attributes", paramNames = {"attributes"})
    public void setRenderAttributes(JSRenderAttributes attributes) {
        this.sceneObject.setRenderAttributes(attributes.getJavaRenderAttributes());
    }

    @JSCodingFunctionOrMethod(description = "Remove all attached lights", paramNames = {})
    public void clearLights() {
        this.sceneObject.clearLights();
    }

    @JSCodingFunctionOrMethod(description = "Get culling AABB", paramNames = {})
    public JSBoxAABB getCullingAABB() {
        CullingAABB aabb = this.sceneObject.getCullingData();
        return aabb == null ? null : new JSBoxAABB(aabb);
    }

    @JSCodingFunctionOrMethod(description = "Set culling AABB", paramNames = {"aabb"})
    public void setCullingAABB(JSBoxAABB aabb) {
        this.sceneObject.setCullingData(aabb == null ? null : aabb.getJavaCullingAABB());
    }

    @JSCodingFunctionOrMethod(description = "Get world of this object", paramNames = {})
    public JSSceneWorld getWorld() {
        return new JSSceneWorld((SceneWorld) this.sceneObject.getWorld());
    }
}