package javagems3d.system.resources.assets.models.mesh;

import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IMesh {
    int DEFAULT_POS_IDX = DefaultAttributePointers.ATTR_POSITIONS.getIndex();

    @NotNull List<Integer> getVertexIndexes();
    @NotNull List<Float> getVertexPositions();

    default void putVertexIndexes(int[] a) {
        for (int i : a) {
            this.getVertexIndexes().add(i);
        }
    }

    default int numVertices() {
        return this.getVertexIndexes().size();
    }

    default int numPositions() {
        return this.getVertexPositions().size();
    }

    void clearMesh();
    int positionsIndex();
}
