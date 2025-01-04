package javagems3d.system.resources.managing.arrays;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;

import java.util.*;

public final class MeshBuffersDataArray {
    private final List<Material> materials;
    private final Set<MeshBuffer> meshBuffers;

    public MeshBuffersDataArray() {
        this.materials = new ArrayList<>();
        this.meshBuffers = new HashSet<>();
    }

    public void clear() {
        this.getMeshBuffers().clear();
        this.getMaterials().clear();
    }
    
    public void addMeshBuffer(MeshBuffer meshBuffer) {
        this.getMeshBuffers().add(meshBuffer);
    }

    public void addMaterial(Material material) {
        this.getMaterials().add(material);
    }

    public int getTotalMaterials() {
        return this.getMaterials().size();
    }

    public int getTotalMeshBuffers() {
        return this.getMeshBuffers().size();
    }

    public Set<MeshBuffer> getMeshBuffers() {
        return this.meshBuffers;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }
}