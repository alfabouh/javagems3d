package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSMeshStructure3D", description = "Interface representing a 3D mesh structure in scripting, providing access to the underlying Java MeshStructure3D object.")
public interface JSMeshStructure3D {
    @JSCodingFunctionOrMethod(description = "Get the underlying Java MeshStructure3D instance.")
    MeshStructure3D<?> getJavaMeshStructure3D();
}