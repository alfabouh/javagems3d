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
import javagems3d.system.resources.assets.initialization.base.AbstractShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public interface IAppResourceLoader {
    /**
     * Using this method, you can add a resource loader to the initialization process of
     * the game (sounds, textures, etc., <b>except shaders</b>)
     *
     * @param assetsLoader
     */
    void addAssetsLoader(IAssetsInitializer assetsLoader);

    /**
     * Using this method, you can add a shaders loader to the initialization process of
     * the game
     *
     * @param assetsLoader
     */
    void addShadersLoader(AbstractShadersInitializer<JGemsShaderManager> shadersLoader);
}
