package workbench.resources.initialization.game;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;

public class WBenchGameEditorModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup markerDefault;
    public MeshGroup markerCursor;
    public MeshGroup markerAabb;
    public MeshGroup markerCube;
    public MeshGroup defaultCube_gr;
    public MeshBuffer defaultCube_bff;

    public WBenchGameEditorModelAssetsInitializer() {
    }

    @Override
    public void load(SystemResources systemResources) {
        this.createDefaults(systemResources);
    }

    private void createDefaults(SystemResources systemResources) {
        this.defaultCube_bff = IAssetsInitializer.createDefaultCube_MBuffer();
        systemResources.getResourceCache().registerInCache("DEFAULT_CUBE_BFF", this.defaultCube_bff);
        systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(this.defaultCube_bff);

        this.defaultCube_gr = IAssetsInitializer.createDefaultCube_MGroup();
        systemResources.getResourceCache().registerInCache("DEFAULT_CUBE_GR", this.defaultCube_gr);

        this.markerDefault = systemResources.createMeshGroup(JGems3D.GetSource.JAR, new JGemsPath("/assets/jgems/models/marker/marker.gltf"), true);
        this.markerCursor = systemResources.createMeshGroup(JGems3D.GetSource.JAR, new JGemsPath("/assets/jgems/models/marker_cursor/marker.gltf"), true);
        this.markerAabb = systemResources.createMeshGroup(JGems3D.GetSource.JAR, new JGemsPath("/assets/jgems/models/marker_aabb/marker.gltf"), true);
        this.markerCube = systemResources.createMeshGroup(JGems3D.GetSource.JAR, new JGemsPath("/assets/jgems/models/marker_cube/marker.gltf"), true);
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
