/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine_api.resources;

import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.HashSet;
import java.util.Set;

public final class AppResourceLoader implements IAppResourceLoader {
    private final Set<IAssetsLoader> assetsLoaderSet;
    private final Set<ShadersLoader<JGemsShaderManager>> shadersLoaders;

    public AppResourceLoader() {
        this.assetsLoaderSet = new HashSet<>();
        this.shadersLoaders = new HashSet<>();
    }

    public void addAssetsLoader(IAssetsLoader assetsLoader) {
        this.assetsLoaderSet.add(assetsLoader);
    }

    public void addShadersLoader(ShadersLoader<JGemsShaderManager> shadersLoader) {
        this.shadersLoaders.add(shadersLoader);
    }

    public Set<ShadersLoader<JGemsShaderManager>> getShadersLoaders() {
        return this.shadersLoaders;
    }

    public Set<IAssetsLoader> getAssetsLoaderSet() {
        return this.assetsLoaderSet;
    }
}
