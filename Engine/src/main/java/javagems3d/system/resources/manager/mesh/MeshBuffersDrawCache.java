package javagems3d.system.resources.manager.mesh;

import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MeshBuffersDrawCache {
    public static final int MAT_START_IDX = 0;
    private final Set<MeshBuffer> meshBuffers;
    private final List<Material> materials;

    public MeshBuffersDrawCache() {
        this.materials = new ArrayList<>();
        this.meshBuffers = new HashSet<>();
    }

    public void clear() {
        this.getMaterials().clear();
        this.getMeshBuffers().clear();
    }

    public void init(Set<MeshBuffersArray> meshBuffersArraySet) {
        this.clear();
        for (MeshBuffersArray meshBuffersArray : meshBuffersArraySet) {
            this.spreadArray(meshBuffersArray);
        }
    }

    private void spreadArray(MeshBuffersArray array) {
        int currentMaterialsSize = this.getMaterials().size();
        for (MeshBuffer meshBuffer : array.getMeshBuffers()) {
            for (MeshBuffer.MeshBufferNode node : meshBuffer.getMeshNodes()) {
                node.setMaterialId(currentMaterialsSize + node.getMaterialId());
            }
            this.getMeshBuffers().add(meshBuffer);
        }
        for (Material material : array.getMaterials()) {
            material.setId(currentMaterialsSize + material.getId());
            this.getMaterials().add(material);
        }
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public Set<MeshBuffer> getMeshBuffers() {
        return this.meshBuffers;
    }
}