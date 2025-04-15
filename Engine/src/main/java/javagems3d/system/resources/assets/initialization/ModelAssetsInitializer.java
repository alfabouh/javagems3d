package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup defaultCube_gr;
    public MeshBuffer defaultCube_bff;
    public MeshBuffer grassCube;

    @Override
    public void load(SystemResources systemResources) {
        this.createDefaults(systemResources);
        this.grassCube = systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "cube/cube.gltf"), true, false);
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
