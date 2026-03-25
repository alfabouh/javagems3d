package api.scripting.coding.env.internal.util.resources.cache;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;

@JSCodingClass(binding = "JSMeshStructure2D", description = "Interface representing a 2D mesh structure in scripting, providing access to the underlying Java MeshStructure2D object.")
public interface JSMeshStructure2D {
    @JSCodingFunctionOrMethod(description = "Get the underlying Java MeshStructure2D instance.")
    MeshStructure2D getJavaMeshStructure2D();
}
