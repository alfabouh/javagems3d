package ru.jgems3d.engine_api.resources;

import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;

import java.util.HashSet;
import java.util.Set;

public final class AppResourceLoader implements IAppResourceLoader {
    private final Set<IAssetsLoader> assetsLoaderSet;
    private final Set<ShadersLoader> shadersLoaders;

    public AppResourceLoader() {
        this.assetsLoaderSet = new HashSet<>();
        this.shadersLoaders = new HashSet<>();
    }

    public Set<ShadersLoader> getShadersLoaders() {
        return this.shadersLoaders;
    }

    public Set<IAssetsLoader> getAssetsLoaderSet() {
        return this.assetsLoaderSet;
    }

    public void addAssetsLoader(IAssetsLoader assetsLoader) {
        this.assetsLoaderSet.add(assetsLoader);
    }

    public void addShadersLoader(ShadersLoader shadersLoader) {
        this.shadersLoaders.add(shadersLoader);
    }
}
