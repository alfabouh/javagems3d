package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.MemMode;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup defaultCube_gr;
    public MeshBuffer defaultCube_bff;

    public MeshBuffer test;

    @Override
    public void load(SystemResources systemResources) {
        this.test = systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEF_PATHS.MODELS, "sponza/sponza.obj"), ModelLoaderFlags.DEFAULT & ~ModelLoaderFlags.CREATE_COLLISION_UD, MemMode.ERASE_NODES_DATA);

        this.createDefaults(systemResources);
    }

    private void createDefaults(SystemResources systemResources) {
        this.defaultCube_bff = IAssetsInitializer.createDefaultCubeBuffer();
        systemResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_BFF", this.defaultCube_bff);
        systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(this.defaultCube_bff);

        this.defaultCube_gr = IAssetsInitializer.createDefaultCubeGroup();
        systemResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_GR", this.defaultCube_gr);
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.NORMAL;
    }
}
