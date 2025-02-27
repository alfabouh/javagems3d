package jgems_app.resources;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;


public class ModelInitializer implements IAssetsInitializer {
    public MeshGroup ground2;

    @Override
    public void load(SystemResources systemResources) {
        this.ground2 = null;
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

