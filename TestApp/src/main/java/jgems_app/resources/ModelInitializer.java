package jgems_app.resources;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.gltf.GLTF2ModelLoader;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;


public class ModelInitializer implements IAssetsInitializer {
    public MeshBuffer test_anim;

    @Override
    public void load(SystemResources systemResources) {
        this.test_anim = systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "test_anim/boblampclean.md5mesh"), true, true);
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

