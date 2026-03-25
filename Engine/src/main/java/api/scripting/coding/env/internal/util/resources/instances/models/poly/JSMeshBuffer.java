package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import api.scripting.coding.env.internal.util.resources.cache.JSMeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;

@JSCodingClass(binding = "JSMeshBuffer", description = "Wrapper for a 3D mesh buffer, allowing caching and access to the underlying Java MeshStructure3D object.")
public class JSMeshBuffer implements JSCanBeCachedInMemory, JSMeshStructure3D {
    @JSHideFromDoc
    private final MeshBuffer meshBuffer;

    public JSMeshBuffer(MeshBuffer meshBuffer) {
        this.meshBuffer = meshBuffer;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java MeshBuffer object.")
    public MeshBuffer getMeshBuffer() {
        return this.meshBuffer;
    }

    @JSHideFromDoc
    @Override
    public MeshStructure3D<? extends IMesh> getJavaMeshStructure3D() {
        return this.getMeshBuffer();
    }
}