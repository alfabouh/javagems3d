package api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode;

import java.util.List;

@JSCodingClass(binding = "JSMeshNodeI", description = "...")
public interface JSMeshNodeI {

    @JSCodingFunctionOrMethod(description = "...")
    default List<Integer> getVertexIndexes() {
        return this.meshNode().getMeshData().getVertexIndexes();
    }

    @JSCodingFunctionOrMethod(description = "...")
    default List<Float> getVertexPositions() {
        return this.meshNode().getMeshData().getVertexPositions();
    }

    @JSCodingFunctionOrMethod(description = "...")
    default int totalVertexes() {
        return this.meshNode().getMeshData().totalVertexes();
    }

    @JSCodingFunctionOrMethod(description = "...")
    default int numVertexIndexes() {
        return this.meshNode().getMeshData().numVertexIndexes();
    }

    @JSCodingFunctionOrMethod(description = "...")
    default int numPositions() {
        return this.meshNode().getMeshData().numPositions();
    }

    @JSHideFromDoc
    MeshNode<?> meshNode();
}
