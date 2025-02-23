package javagems3d.system.resources.assets.models.mesh;

import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IMesh {
    int DEFAULT_POS_IDX = DefaultAttributePointers.ATTR_POSITIONS.getIndex();

    @NotNull List<Integer> getVertexIndexes();
    @NotNull List<Float> getVertexPositions();

    default int numVertexIndexes() {
        return this.getVertexIndexes().size();
    }

    default int numPositions() {
        return this.getVertexPositions().size();
    }

    void clearData();
    void clearMesh();
    int positionsIndex();
}
