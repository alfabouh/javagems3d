package javagems3d.system.resources.manager.mesh;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MeshBuffersArray {
    private final List<Material> materials;
    private final Set<MeshBuffer> meshBuffers;

    public MeshBuffersArray() {
        this.materials = new ArrayList<>();
        this.meshBuffers = new HashSet<>();
    }

    public void clear() {
        this.getMeshBuffers().clear();
        this.getMaterials().clear();
    }
    
    public void putMeshBuffer(MeshBuffer meshBuffer) {
        this.getMeshBuffers().add(meshBuffer);
    }

    public void putMaterial(Material material) {
        this.getMaterials().add(material);
    }

    public Set<MeshBuffer> getMeshBuffers() {
        return this.meshBuffers;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }
}