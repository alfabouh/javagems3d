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
        this.cube = ResourceManager.createMesh("/assets/jgems/models/cube/cube.obj");
        this.ground = ResourceManager.createMesh("/assets/jgems/models/map01/map01.obj");
        this.ground2 = ResourceManager.createMesh("/assets/jgems/models/map02/map02.obj");
        this.door2 = ResourceManager.createMesh("/assets/jgems/models/door2/door2.obj");
        this.plank = ResourceManager.createMesh("/assets/jgems/models/plank/plank.obj");

        this.ground.constructCollisionMeshForStaticObject();
        this.ground2.constructCollisionMeshForStaticObject();

        JGems.get().getScreen().addLineInLoadingScreen("Models successfully loaded...");
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
