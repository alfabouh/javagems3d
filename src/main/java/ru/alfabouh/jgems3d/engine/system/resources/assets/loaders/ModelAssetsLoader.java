package ru.alfabouh.jgems3d.engine.system.resources.assets.loaders;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.system.JGemsHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.manager.GameResources;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup cube;
    public MeshDataGroup ground2;
    public MeshDataGroup sponza;
    public MeshDataGroup door2;

    @Override
    public void load(GameResources gameResources) {
        JGems.get().getScreen().tryAddLineInLoadingScreen("Loading models...");
        this.cube = this.createMesh(gameResources, "/assets/jgems/models/cube/cube.obj", true);
        this.ground2 = this.createMesh(gameResources, "/assets/jgems/models/map04/map04.obj", true);
        this.sponza = this.createMesh(gameResources, "/assets/jgems/models/cube/cube.obj", false);//sponza
        this.door2 = this.createMesh(gameResources, "/assets/jgems/models/door2/door2.obj", false);
        JGems.get().getScreen().tryAddLineInLoadingScreen("Models successfully loaded...");
    }

    private MeshDataGroup createMesh(GameResources gameResources, String path, boolean constructCollisionMesh) {
        MeshDataGroup meshDataGroup = gameResources.createMesh(path);
        if (constructCollisionMesh) {
            JGemsHelper.tryCreateMeshCollisionData(meshDataGroup);
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
