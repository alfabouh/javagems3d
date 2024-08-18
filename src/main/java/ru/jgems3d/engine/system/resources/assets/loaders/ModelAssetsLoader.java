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

package ru.jgems3d.engine.system.resources.assets.loaders;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.manager.GameResources;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup cube;
    public MeshDataGroup ground2;

    @Override
    public void load(GameResources gameResources) {
        this.cube = gameResources.createMesh(new JGemsPath(JGems3D.Paths.MODELS, "cube/cube.obj"), true);
        this.ground2 = gameResources.createMesh(new JGemsPath(JGems3D.Paths.MODELS, "map04/map04.obj"), true);
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
