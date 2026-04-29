package javagems3d.system.resources.managing.resources.data.arrays;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;

import java.util.*;

public final class MeshBuffersDataArray implements IDataArray {
    private final List<Material> materials;
    private final Set<MeshBuffer> meshBuffers;

    private MeshBuffersDataArray(List<Material> materials, Set<MeshBuffer> meshBuffers) {
        this.materials = materials;
        this.meshBuffers = meshBuffers;
    }

    public MeshBuffersDataArray() {
        this.materials = new ArrayList<>();
        this.meshBuffers = new HashSet<>();
    }

    public void clear() {
        this.getMeshBuffers().clear();
        this.getMaterials().clear();
    }

    public int getTotalMaterials() {
        return this.getMaterials().size();
    }

    public void addMeshBuffer(MeshBuffer meshBuffer) {
        this.getMeshBuffers().add(meshBuffer);
    }

    public void addMaterial(Material material) {
        this.getMaterials().add(material);
    }

    public Set<MeshBuffer> getMeshBuffers() {
        return this.meshBuffers;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public MeshBuffersDataArray copy() {
        return new MeshBuffersDataArray(new ArrayList<>(this.materials), new HashSet<>(this.meshBuffers));
    }
}