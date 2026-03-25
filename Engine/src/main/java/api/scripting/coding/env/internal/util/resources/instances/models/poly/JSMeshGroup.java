package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import api.scripting.coding.env.internal.util.resources.cache.JSMeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

@JSCodingClass(binding = "JSMeshGroup", description = "Represents a group of 3D meshes, exposing access to the underlying MeshGroup and supporting caching.")
public class JSMeshGroup implements JSCanBeCachedInMemory, JSMeshStructure3D {
    @JSHideFromDoc
    private final MeshGroup meshGroup;

    public JSMeshGroup(MeshGroup meshGroup) {
        this.meshGroup = meshGroup;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying MeshGroup instance.")
    public MeshGroup getMeshGroup() {
        return this.meshGroup;
    }

    @JSHideFromDoc
    @Override
    public MeshStructure3D<? extends IMesh> getJavaMeshStructure3D() {
        return this.getMeshGroup();
    }
}