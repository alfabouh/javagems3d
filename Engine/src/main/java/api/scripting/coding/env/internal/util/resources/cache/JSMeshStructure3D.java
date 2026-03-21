package api.scripting.coding.env.internal.util.resources.cache;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSMeshStructure3D", description = "...")
public interface JSMeshStructure3D {
    @JSCodingFunctionOrMethod(description = "...")
    MeshStructure3D<? extends IMesh> getJavaMeshStructure3D();
}