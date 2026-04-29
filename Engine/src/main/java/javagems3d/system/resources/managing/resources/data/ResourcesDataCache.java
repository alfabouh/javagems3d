package javagems3d.system.resources.managing.resources.data;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.data.arrays.BindlessTexturesDataArray;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.BindlessTexturesDataCache;
import javagems3d.system.resources.managing.resources.data.bindless_rendering_cache.MeshBuffersDataCache;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ResourcesDataCache {
    private final MeshBuffersDataCache meshBuffersDataArray;
    private final BindlessTexturesDataCache bindlessTexturesDataArray;

    public ResourcesDataCache(@NotNull MeshBuffersDataCache meshBuffersDataCache, @NotNull BindlessTexturesDataCache bindlessTexturesDataCache) {
        this.meshBuffersDataArray = meshBuffersDataCache;
        this.bindlessTexturesDataArray = bindlessTexturesDataCache;
    }

    public void writeAll(Collection<ResourcesDataArrays> arrays) {
        Set<BindlessTexturesDataArray> bindlessTexturesDataArraySet = new HashSet<>();
        Set<MeshBuffersDataArray> meshBuffersDataArraySet = new HashSet<>();
        arrays.forEach(e -> {
            bindlessTexturesDataArraySet.add(e.getBindlessTexturesArray().copy());
            meshBuffersDataArraySet.add(e.getMeshBuffersDataArray().copy());
            e.getBindlessTexturesArray().clear();
            e.getMeshBuffersDataArray().clear();
        });
        {
            final MeshBuffersDataArray defCube = new MeshBuffersDataArray();
            defCube.addMeshBuffer(ResourceManager.DEFAULT_CUBE_MESHBUFFER());
            defCube.addMaterial(new Material());
            meshBuffersDataArraySet.add(defCube);
        }
        this.clearAll();
        this.getBindlessTexturesCache().writeData(bindlessTexturesDataArraySet);
        this.getMeshBuffersDataCache().writeData(meshBuffersDataArraySet);
    }

    public void writeAll(ResourcesDataArrays... arrays) {
        List<ResourcesDataArrays> arrays1 = new ArrayList<>(Arrays.asList(arrays));
        this.writeAll(arrays1);
    }

    public void clearAll() {
        this.getBindlessTexturesCache().clear();
        this.getMeshBuffersDataCache().clear();
    }

    public MeshBuffersDataCache getMeshBuffersDataCache() {
        return this.meshBuffersDataArray;
    }

    public BindlessTexturesDataCache getBindlessTexturesCache() {
        return this.bindlessTexturesDataArray;
    }
}
