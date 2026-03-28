package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSMeshStructureConstructor", description = "Functional interface for constructing mesh structure from input data")
@FunctionalInterface
public interface JSMeshStructureConstructor<T> {
    @JSCodingFunctionOrMethod(description = "Construct mesh structure from input")
    JSMeshStructure3D construct(T data);

    @JSHideFromDoc
    default IModelConstructor<T, IMesh> toJavaConstructor() {
        return (t) -> {
            MeshStructure3D<?> mesh = this.construct(t).getJavaMeshStructure3D();
            @SuppressWarnings("unchecked")
            MeshStructure3D<IMesh> castedMesh = (MeshStructure3D<IMesh>) mesh;
            return castedMesh;
        };
    }
}