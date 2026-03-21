package api.scripting.coding.env.internal.util.resources.cache;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;

@JSCodingClass(binding = "JSMeshStructure2D", description = "...")
public interface JSMeshStructure2D {
    @JSCodingFunctionOrMethod(description = "...")
    MeshStructure2D getJavaMeshStructure2D();
}
