package ru.BouH.engine.game.resources.assets;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.cache.GameCache;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup knife;
    public MeshDataGroup cube;
    public MeshDataGroup house;
    public MeshDataGroup ground;
    public MeshDataGroup door1;
    public MeshDataGroup plank;

    @Override
    public void load(GameCache gameCache) {
        Game.getGame().getScreen().addLineInLoadingScreen("Loading models...");
        this.cube = ResourceManager.createMesh("/assets/models/cube/", "cube.obj");
        this.knife = ResourceManager.createMesh("/assets/models/knife/", "knife.obj");
        this.house = ResourceManager.createMesh("/assets/models/house/", "house.obj");
        this.ground = ResourceManager.createMesh("/assets/models/nuke/", "Nuke.obj");
        this.door1 = ResourceManager.createMesh("/assets/models/door1/", "door1.obj");
        this.plank = ResourceManager.createMesh("/assets/models/plank/", "plank.obj");

        this.ground.constructCollisionMesh();
        this.house.constructCollisionMesh();
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
