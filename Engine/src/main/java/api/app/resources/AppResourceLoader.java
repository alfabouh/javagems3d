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

package api.app.resources;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.HashSet;
import java.util.Set;

public final class AppResourceLoader implements IAppResourceLoader {
    private final Set<IAssetsInitializer> assetsLoaderSet;
    private final Set<ShadersInitializer<JGemsShaderManager>> shadersLoaders;

    public AppResourceLoader() {
        this.assetsLoaderSet = new HashSet<>();
        this.shadersLoaders = new HashSet<>();
    }

    public void addAssetsLoader(IAssetsInitializer assetsLoader) {
        this.assetsLoaderSet.add(assetsLoader);
    }

    public void addShadersLoader(ShadersInitializer<JGemsShaderManager> shadersLoader) {
        this.shadersLoaders.add(shadersLoader);
    }

    public Set<ShadersInitializer<JGemsShaderManager>> getShadersLoaders() {
        return this.shadersLoaders;
    }

    public Set<IAssetsInitializer> getAssetsLoaderSet() {
        return this.assetsLoaderSet;
    }
}
