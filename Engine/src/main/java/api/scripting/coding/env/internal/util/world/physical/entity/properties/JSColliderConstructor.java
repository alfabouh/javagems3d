package api.scripting.coding.env.internal.util.world.physical.entity.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import javagems3d.physics.colliders.IColliderConstructor;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSColliderConstructor", description = "Factory for collision shapes")
public class JSColliderConstructor {
    private final IColliderConstructor constructor;

    public static IColliderConstructor getDynamic(JSMeshStructure3D meshStructure) {
        return new MeshCollider(meshStructure.getJavaMeshStructure3D(), true);
    }

    public static IColliderConstructor getStatic(JSMeshStructure3D meshStructure) {
        return new MeshCollider(meshStructure.getJavaMeshStructure3D(), false);
    }

    public static IColliderConstructor get(JSMeshStructure3D meshStructure, boolean isBodyDynamic) {
        return new MeshCollider(meshStructure.getJavaMeshStructure3D(), isBodyDynamic);
    }

    public JSColliderConstructor(IColliderConstructor constructor) {
        this.constructor = constructor;
    }

    @JSCodingFunctionOrMethod(description = "Create collider")
    public Object create() {
        return this.constructor.execute();
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public IColliderConstructor getJavaConstructor() {
        return this.constructor;
    }
}