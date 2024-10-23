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

package jgems_api.test.resources;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.loaders.base.IAssetsLoader;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.service.path.JGemsPath;


public class ModelLoader implements IAssetsLoader {
    public MeshGroup ground2;

    @Override
    public void load(GameResources gameResources) {
        this.ground2 = gameResources.createMesh(new JGemsPath(JGems3D.Paths.MODELS, "map04/map04.obj"), true, true);
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

