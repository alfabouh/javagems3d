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

package engine_api.resources;

import javagems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import javagems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public interface IAppResourceLoader {
    /**
     * Using this method, you can add a resource loader to the initialization process of
     * the game (sounds, textures, etc., <b>except shaders</b>)
     *
     * @param assetsLoader
     */
    void addAssetsLoader(IAssetsLoader assetsLoader);

    /**
     * Using this method, you can add a shaders loader to the initialization process of
     * the game
     *
     * @param assetsLoader
     */
    void addShadersLoader(ShadersLoader<JGemsShaderManager> shadersLoader);
}
