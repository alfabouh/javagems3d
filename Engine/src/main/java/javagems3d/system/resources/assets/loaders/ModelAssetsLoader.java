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

package javagems3d.system.resources.assets.loaders;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.loaders.base.IAssetsLoader;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.service.path.JGemsPath;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshGroup cube;
    public MeshGroup ground2;
    public MeshGroup ground3;

    @Override
    public void load(GameResources gameResources) {
        this.cube = gameResources.createMesh(new JGemsPath(JGems3D.Paths.MODELS, "cube/cube.obj"), true, true);
        this.ground2 = gameResources.createMesh(new JGemsPath(JGems3D.Paths.MODELS, "map04/map04.obj"), true, true);
        this.ground3 = gameResources.createMesh(new JGemsPath(JGems3D.Paths.MODELS, "map05/map05.obj"), true, true);
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.NORMAL;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.NORMAL;
    }
}
