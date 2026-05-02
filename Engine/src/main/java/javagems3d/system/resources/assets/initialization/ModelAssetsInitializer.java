package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public MeshBuffer grassCube;

    @Override
    public void load(SystemResources systemResources) {
        this.grassCube = systemResources.createMeshBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "cube/cube.gltf"), ISource.Source.INSIDE_JAR), true);
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
