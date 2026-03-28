package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.bound.JSBoxAABB;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;

@JSCodingClass(binding = "JSSceneObjectWithModelI", description = "Interface for objects that have 3D model and animation control")
public interface JSSceneObjectWithModelI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java modeled object")
    IModeled getJavaModeledObject();

    @JSCodingFunctionOrMethod(description = "Get model of the object")
    default JSModel3D getModel() {
        return new JSModel3D(this.getJavaModeledObject().getModel());
    }

    @JSCodingFunctionOrMethod(description = "Check if object has valid model")
    default boolean hasModel() {
        return this.getJavaModeledObject().hasModel();
    }

    @JSCodingFunctionOrMethod(description = "Update animation state")
    default void updateAnimation() {
        this.getJavaModeledObject().updateAnimation();
    }

    @JSCodingFunctionOrMethod(description = "Get bounding box from mesh or animation")
    default JSBoxAABB getAABB() {
        CullingAABB aabb = this.getJavaModeledObject().pickAABBDataFromMesh();
        return aabb == null ? null : new JSBoxAABB(aabb);
    }
}