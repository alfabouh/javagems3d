package workbench.resources.initialization;

import api.application.workbench.manager.APIWBenchDataManager;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.data.WBenchObjectData;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import workbench.WBench;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup defaultCube_gr;
    public MeshBuffer defaultCube_bff;

    public ModelAssetsInitializer() {
    }

    @Override
    public void load(SystemResources systemResources) {
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
