package javagems3d.system.resources.assets.models.mesh.structures.nodes;

import javagems3d.system.resources.assets.models.mesh.IMesh;
import org.jetbrains.annotations.NotNull;

public abstract class MeshNode<T extends IMesh> {
    private T meshData;

    public MeshNode(@NotNull T meshData) {
        this.meshData = meshData;
    }

    public void clear() {
        if (this.getMeshData() != null) {
            this.getMeshData().clearMesh();
        }
        this.meshData = null;
    }

    public void clearData(boolean keepTrianglesInMemory) {
        if (this.getMeshData() != null) {
            this.getMeshData().clearData(keepTrianglesInMemory);
        }
    }

    public T getMeshData() {
        return this.meshData;
    }

    public void setMeshStructure(T meshData) {
        this.meshData = meshData;
    }
}