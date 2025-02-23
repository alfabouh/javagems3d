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

package workbench.resources.initialization;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.resources.GameResources;

public class ModelAssetsInitializer implements IAssetsInitializer {
    public MeshGroup defaultCube_gr;
    public MeshBuffer defaultCube_bff;

    @Override
    public void load(GameResources gameResources) {
        this.createDefaults(gameResources);
    }

    private void createDefaults(GameResources gameResources) {
        this.defaultCube_bff = IAssetsInitializer.createDefaultCubeBuffer();
        gameResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_BFF", this.defaultCube_bff);
        gameResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(this.defaultCube_bff);

        this.defaultCube_gr = IAssetsInitializer.createDefaultCubeGroup();
        gameResources.getResourceCache().addObjectInBuffer("DEFAULT_CUBE_GR", this.defaultCube_gr);
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
