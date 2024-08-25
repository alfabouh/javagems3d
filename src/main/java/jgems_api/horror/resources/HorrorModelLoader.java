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

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.service.path.JGemsPath;


public class HorrorModelLoader implements IAssetsLoader {

    @Override
    public void load(GameResources gameResources) {
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.NORMAL;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}

