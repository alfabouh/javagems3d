package javagems3d.system.resources.managing.resources.data.cache;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;

import java.util.*;

public final class MeshBuffersDataCache implements IDataCache {
    private final Map<Material, Integer> materialsIdMap;
    private final List<Material> materials;
    private final Set<MeshBuffer> staticMeshBuffers;
    private final Set<MeshBuffer> animatedMeshBuffers;

    public MeshBuffersDataCache() {
        this.materialsIdMap = new HashMap<>();
        this.materials = new ArrayList<>();
        this.staticMeshBuffers = new HashSet<>();
        this.animatedMeshBuffers = new HashSet<>();
    }

    public void writeData(Set<MeshBuffersDataArray> arraySet) {
        int i = 0;
        for (MeshBuffersDataArray bindlessTexturesDataArray : arraySet) {
            for (MeshBuffer meshBuffer : bindlessTexturesDataArray.getMeshBuffers()) {
                if (meshBuffer.isAnimatedStructure()) {
                    this.addAnimatedMeshBuffer(meshBuffer);
                } else {
                    this.addStaticMeshBuffer(meshBuffer);
                }
            }
            for (Material material : bindlessTexturesDataArray.getMaterials()) {
                this.materialsIdMap.put(material, i++);
                this.addMaterial(material);
            }
        }
    }

    public void clear() {
        this.animatedMeshBuffers.clear();
        this.staticMeshBuffers.clear();
        this.materials.clear();
        this.materialsIdMap.clear();
    }

    public int getMaterialId(Material material) {
        return this.materialsIdMap.get(material);
    }

    public void addStaticMeshBuffer(MeshBuffer meshBuffer) {
        this.staticMeshBuffers.add(meshBuffer);
    }

    public void addAnimatedMeshBuffer(MeshBuffer meshBuffer) {
        this.animatedMeshBuffers.add(meshBuffer);
    }

    public void addMaterial(Material material) {
        this.materials.add(material);
    }

    public Set<MeshBuffer> getAnimatedMeshBuffers() {
        return new HashSet<>(this.animatedMeshBuffers);
    }

    public Set<MeshBuffer> getStaticMeshBuffers() {
        return new HashSet<>(this.staticMeshBuffers);
    }

    public List<Material> getMaterials() {
        return new ArrayList<>(this.materials);
    }
}
