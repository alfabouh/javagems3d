package ru.alfabouh.engine.game.resources.assets;

import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.engine.game.resources.cache.GameCache;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup cube;
    public MeshDataGroup ground;
    public MeshDataGroup door2;
    public MeshDataGroup plank;

    @Override
    public void load(GameCache gameCache) {
        Game.getGame().getScreen().addLineInLoadingScreen("Loading models...");
        this.cube = ResourceManager.createMesh("/assets/models/cube/", "cube.obj");
        this.ground = ResourceManager.createMesh("/assets/models/map01/", "map01.obj");
        this.door2 = ResourceManager.createMesh("/assets/models/door2/", "door2.obj");
        this.plank = ResourceManager.createMesh("/assets/models/plank/", "plank.obj");

        this.ground.constructCollisionMesh();
        Game.getGame().getScreen().addLineInLoadingScreen("Models successfully loaded...");
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
