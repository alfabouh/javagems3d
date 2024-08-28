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

package jgems_api.horror.resources;

import ru.jgems3d.engine.system.resources.assets.loaders.TextureAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.service.path.JGemsPath;


public class HorrorTexturesLoader implements IAssetsLoader {
    public TextureSample cross;
    public TextureSample gas;
    public TextureSample brains;
    public TextureSample beer;
    public TextureSample ghost;

    @Override
    public void load(GameResources gameResources) {
        this.cross = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath("/assets/horror/textures/cross/cross.png"), new TextureSample.Params(false, false, false, false));
        this.gas = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath("/assets/horror/textures/gas/gas.png"), new TextureSample.Params(false, false, false, false));
        this.brains = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath("/assets/horror/textures/brains/brains.png"), new TextureSample.Params(false, false, false, false));
        this.beer = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath("/assets/horror/textures/beer/beer.png"), new TextureSample.Params(false, false, false, false));
        this.ghost = gameResources.createTextureOrDefault(TextureAssetsLoader.DEFAULT, new JGemsPath("/assets/horror/textures/ghost/ghost.png"), new TextureSample.Params(false, false, false, false));
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.NORMAL;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.HIGH;
    }
}

