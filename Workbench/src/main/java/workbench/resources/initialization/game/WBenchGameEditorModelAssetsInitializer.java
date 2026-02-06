package workbench.resources.initialization.game;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;

public class WBenchGameEditorModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup markerDefault;
    public MeshGroup markerCursor;
    public MeshGroup markerAabb;
    public MeshGroup markerCube;

    public WBenchGameEditorModelAssetsInitializer() {
    }

    @Override
    public void load(SystemResources systemResources) {
        this.createDefaults(systemResources);
    }

    private void createDefaults(SystemResources systemResources) {
        this.markerDefault = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/jgems/models/marker/marker.gltf"), ISource.Source.INSIDE_JAR), true);
        this.markerCursor = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/jgems/models/marker_cursor/marker.gltf"), ISource.Source.INSIDE_JAR), true);
        this.markerAabb = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/jgems/models/marker_aabb/marker.gltf"), ISource.Source.INSIDE_JAR), true);
        this.markerCube = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath("/assets/jgems/models/marker_cube/marker.gltf"), ISource.Source.INSIDE_JAR), true);
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
