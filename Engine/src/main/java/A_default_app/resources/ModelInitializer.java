package A_default_app.resources;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.managing.resources.SystemResources;


public class ModelInitializer implements IAssetsInitializer {
    @Override
    public void load(SystemResources systemResources) {
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

