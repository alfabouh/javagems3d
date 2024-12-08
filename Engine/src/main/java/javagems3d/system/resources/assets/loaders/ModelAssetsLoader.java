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
import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.loaders.base.IAssetsLoader;
import javagems3d.system.resources.assets.models.loaders.ModelMeshLoader;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.service.path.JGemsPath;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshBuffer cube;
    public MeshBuffer ground2;
    public MeshGroup ground3;
    public MeshGroup test_anim;

    @Override
    public void load(GameResources gameResources) {
        this.cube = gameResources.createMeshBuffer(new JGemsPath(JGems3D.Paths.MODELS, "cube/cube.obj"), ModelMeshLoader.FLAGS.LOAD_IN_INDIRECT_BUFFER);
        this.ground2 = gameResources.createMeshBuffer(new JGemsPath(JGems3D.Paths.MODELS, "map04/map04.obj"), ModelMeshLoader.FLAGS.DEFAULT);
        this.ground3 = gameResources.createMeshGroup(new JGemsPath(JGems3D.Paths.MODELS, "map05/map05.obj"), ModelMeshLoader.FLAGS.DEFAULT);
        this.test_anim = gameResources.createMeshGroup(new JGemsPath(JGems3D.Paths.MODELS, "test_anim/boblamp.md5mesh"), ModelMeshLoader.FLAGS.DEFAULT);

        JGemsHelper.UTILS.createMeshCollisionData(this.cube, this.ground2, this.ground3, this.test_anim);
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.NORMAL;
    }
}
