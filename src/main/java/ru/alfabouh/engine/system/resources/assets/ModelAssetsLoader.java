package ru.alfabouh.engine.system.resources.assets;

import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.engine.system.resources.cache.GameCache;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup cube;
    public MeshDataGroup ground;
    public MeshDataGroup ground2;
    public MeshDataGroup door2;
    public MeshDataGroup plank;

    @Override
    public void load(GameCache gameCache) {
        JGems.get().getScreen().addLineInLoadingScreen("Loading models...");
        this.cube = ResourceManager.createMesh("/assets/models/cube/", "cube.obj");
        this.ground = ResourceManager.createMesh("/assets/models/map01/", "map01.obj");
        this.ground2 = ResourceManager.createMesh("/assets/models/map02/", "map02.obj");
        this.door2 = ResourceManager.createMesh("/assets/models/door2/", "door2.obj");
        this.plank = ResourceManager.createMesh("/assets/models/plank/", "plank.obj");

        this.ground.constructCollisionMesh();
        this.ground2.constructCollisionMesh();
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
