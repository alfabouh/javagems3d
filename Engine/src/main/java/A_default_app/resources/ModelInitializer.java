package A_default_app.resources;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;


public class ModelInitializer implements IAssetsInitializer {
    public static MeshBuffer test_anim;
    public static MeshGroup glock;

    @Override
    public void load(SystemResources systemResources) {
        test_anim = systemResources.createMeshBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "CesiumMan/glTF/CesiumMan.gltf"), ISource.Source.INSIDE_JAR), true);
        glock = systemResources.createMeshGroup(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "glock/glock.gltf"), ISource.Source.INSIDE_JAR), false);
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}

