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

package ru.jgems3d.toolbox.resources.models;

import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.toolbox.resources.TBoxResourceManager;

public class ModelResources {
    public MeshDataGroup zone_cube;

    public MeshDataGroup xyz;
    public MeshDataGroup cubic;
    public MeshDataGroup sphere;
    public MeshDataGroup pointer;
    public MeshDataGroup player;

    public void init(ResourceCache resourceCache) {
        this.zone_cube = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/zone_cube/zone_cube.obj"));

        this.cubic = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/cubic/cubic.obj"));
        this.xyz = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/xyz/xyz.obj"));
        this.pointer = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/pointer/pointer.obj"));
        this.sphere = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/sphere/sphere.obj"));
        this.player = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/player/player.obj"));
    }
}
