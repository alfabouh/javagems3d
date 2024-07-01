package ru.alfabouh.jgems3d.engine.system.resources.assets;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup cube;
    public MeshDataGroup ground;
    public MeshDataGroup ground2;
    public MeshDataGroup door2;
    public MeshDataGroup plank;

    @Override
    public void load(ResourceCache ResourceCache) {
        JGems.get().getScreen().addLineInLoadingScreen("Loading models...");
        this.cube = this.createMesh("/assets/jgems/models/cube/cube.obj", true);
        this.ground = this.createMesh("/assets/jgems/models/map01/map01.obj", true);
        this.ground2 = this.createMesh("/assets/jgems/models/map02/map02.obj", true);
        this.door2 = this.createMesh("/assets/jgems/models/door2/door2.obj", false);
        this.plank = this.createMesh("/assets/jgems/models/plank/plank.obj", true);

        JGems.get().getScreen().addLineInLoadingScreen("Models successfully loaded...");
    }

    private MeshDataGroup createMesh(String path, boolean constructCollisionMesh) {
        MeshDataGroup meshDataGroup = ResourceManager.createMesh(path);
        if (constructCollisionMesh) {
            meshDataGroup.constructCollisionMesh();
        }
        return meshDataGroup;
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.POST;
    }

    @Override
    public int loadOrder() {
        return 2;
    }
}
