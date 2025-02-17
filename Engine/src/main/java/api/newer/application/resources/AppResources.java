package api.newer.application.resources;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;

import java.util.HashSet;
import java.util.Set;

public final class AppResources implements IAppResources {
    private final Set<IAssetsInitializer> assetsInitializers;
    private final Set<ShadersInitializer<? extends ShaderManager>> shadersInitializers;

    public AppResources() {
        this.assetsInitializers = new HashSet<>();
        this.shadersInitializers = new HashSet<>();
    }

    public void putGlobalShadersInitializer(ShadersInitializer<? extends ShaderManager> shadersInitialization) {
        this.getShadersInitializers().add(shadersInitialization);
    }

    public void putGlobalAssetsInitializer(IAssetsInitializer assetsInitialization) {
        this.getGlobalAssetsInitializers().add(assetsInitialization);
    }

    public Set<IAssetsInitializer> getGlobalAssetsInitializers() {
        return this.assetsInitializers;
    }

    public Set<ShadersInitializer<? extends ShaderManager>> getShadersInitializers() {
        return this.shadersInitializers;
    }
}
