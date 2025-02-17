package api.newer.application.resources;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;

public interface IAppResources {
    void putGlobalShadersInitializer(ShadersInitializer<? extends ShaderManager> shadersInitialization);
    void putGlobalAssetsInitializer(IAssetsInitializer assetsInitialization);
}
