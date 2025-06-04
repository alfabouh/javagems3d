package workbench.resources.initialization;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup markerDefault;
    public MeshGroup markerCursor;
    public MeshGroup markerAabb;
    public MeshGroup markerCube;

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

        this.markerDefault = systemResources.createMeshGroup(new JGemsPath("/assets/jgems/models/marker/marker.gltf"), true);
        this.markerCursor = systemResources.createMeshGroup(new JGemsPath("/assets/jgems/models/marker_cursor/marker.gltf"), true);
        this.markerAabb = systemResources.createMeshGroup(new JGemsPath("/assets/jgems/models/marker_aabb/marker.gltf"), true);
        this.markerCube = systemResources.createMeshGroup(new JGemsPath("/assets/jgems/models/marker_cube/marker.gltf"), true);
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
