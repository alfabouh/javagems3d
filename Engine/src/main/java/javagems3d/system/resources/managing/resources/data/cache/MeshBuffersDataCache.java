package javagems3d.system.resources.managing.resources.data.cache;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MeshBuffersDataCache implements IDataCache {
    private final List<Material> materials;
    private final Set<MeshBuffer> meshBuffers;

    public MeshBuffersDataCache() {
        this.materials = new ArrayList<>();
        this.meshBuffers = new HashSet<>();
    }

    public void writeData(Set<MeshBuffersDataArray> arraySet) {
        for (MeshBuffersDataArray bindlessTexturesDataArray : arraySet) {
            int totalMaterials = this.getMaterials().size();
            for (MeshBuffer meshBuffer : bindlessTexturesDataArray.getMeshBuffers()) {
                for (MeshBuffer.PassData data : meshBuffer.getPassData()) {
                    data.setMaterialId(data.getMaterialId() + totalMaterials);
                }
                this.addMeshBuffer(meshBuffer);
            }
            for (Material material : bindlessTexturesDataArray.getMaterials()) {
                material.setId(material.getId() + totalMaterials);
                this.addMaterial(material);
            }
        }
    }

    public void clear() {
        this.meshBuffers.clear();
        this.materials.clear();
    }

    public void addMeshBuffer(MeshBuffer meshBuffer) {
        this.meshBuffers.add(meshBuffer);
    }

    public void addMaterial(Material material) {
        this.materials.add(material);
    }

    public Set<MeshBuffer> getMeshBuffers() {
        return new HashSet<>(this.meshBuffers);
    }

    public List<Material> getMaterials() {
        return new ArrayList<>(this.materials);
    }
}
