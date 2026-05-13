package workbench.resources.initialization.game;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

public class WBenchGameEditorModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup markerDefault;
    public MeshGroup markerCursor;
    public MeshGroup markerAabb;
    public MeshGroup markerCube;
    public MeshGroup markerCubeDir;

    public WBenchGameEditorModelAssetsInitializer() {
    }

    @Override
    public void load(SystemResources systemResources) {
        this.createDefaults(systemResources);
    }

    private void createDefaults(SystemResources systemResources) {
        this.markerDefault = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerCursor = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_cursor/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerAabb = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_aabb/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerCube = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_cube/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
        this.markerCubeDir = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/models/marker_cube_dir/marker.gltf"), ISource.Source.INSIDE_JAR), true, false);
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
