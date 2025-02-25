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

package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.loading.models.MemMode;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup defaultCube_gr;
    public MeshBuffer defaultCube_bff;

    public MeshBuffer grassCube;
    public MeshBuffer ground2;
    public MeshBuffer ground3;
    public MeshBuffer test_anim;

    @Override
    public void load(SystemResources systemResources) {
        this.createDefaults(systemResources);
        this.grassCube = systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEF_PATHS.MODELS, "sponza/sponza.obj"), ModelLoaderFlags.DEFAULT, MemMode.ERASE_NODES_DATA);
        this.ground2 = systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEF_PATHS.MODELS, "map04/map04.obj"), ModelLoaderFlags.DEFAULT, MemMode.ERASE_NODES_DATA);
        this.ground3 = systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEF_PATHS.MODELS, "map05/map05.obj"), ModelLoaderFlags.DEFAULT, MemMode.ERASE_NODES_DATA);
        this.test_anim = systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEF_PATHS.MODELS, "cube/cube.obj"), ModelLoaderFlags.DEFAULT, MemMode.ERASE_NODES_DATA);

        // systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEF_PATHS.MODELS, "cube/cube.obj"), ModelMeshLoader.ModelLoaderFlags.LOAD_IN_INDIRECT_BUFFER);
        //systemResources.createMeshBuffer(new JGemsPath(JGems3D.DEF_PATHS.MODELS, "test_anim/boblamp.md5mesh"), ModelMeshLoader.ModelLoaderFlags.DEFAULT);
    }

    private void createDefaults(SystemResources systemResources) {
        this.defaultCube_bff = IAssetsInitializer.createDefaultCubeBuffer();
        systemResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_BFF", this.defaultCube_bff);
        systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(this.defaultCube_bff);

        this.defaultCube_gr = IAssetsInitializer.createDefaultCubeGroup();
        systemResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_GR", this.defaultCube_gr);
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
