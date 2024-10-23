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

package toolbox.resources.models;

import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.service.path.JGemsPath;
import toolbox.resources.TBoxResourceManager;

public class ModelResources {
    public MeshGroup zone_cube;

    public MeshGroup xyz;
    public MeshGroup cubic;
    public MeshGroup sphere;
    public MeshGroup pointer;
    public MeshGroup player;

    public void init(TBoxResourceManager resourceManager) {
        this.zone_cube = resourceManager.createModel(new JGemsPath("/assets/toolbox/models/zone_cube/zone_cube.obj"));

        this.cubic = resourceManager.createModel(new JGemsPath("/assets/toolbox/models/cubic/cubic.obj"));
        this.xyz = resourceManager.createModel(new JGemsPath("/assets/toolbox/models/xyz/xyz.obj"));
        this.pointer = resourceManager.createModel(new JGemsPath("/assets/toolbox/models/pointer/pointer.obj"));
        this.sphere = resourceManager.createModel(new JGemsPath("/assets/toolbox/models/sphere/sphere.obj"));
        this.player = resourceManager.createModel(new JGemsPath("/assets/toolbox/models/player/player.obj"));
    }
}
