package javagems3d.system.resources.assets.models.mesh.structures.nodes;

import javagems3d.system.resources.assets.models.mesh.IMesh;
import org.jetbrains.annotations.NotNull;

public abstract class MeshNode<T extends IMesh> {
    private T meshData;

    public MeshNode(@NotNull T meshData) {
        this.meshData = meshData;
    }

    public void clearNode() {
        this.getMeshData().clearMesh();
    }

    public T getMeshData() {
        return this.meshData;
    }

    public void setMeshData(T meshData) {
        this.meshData = meshData;
    }
}