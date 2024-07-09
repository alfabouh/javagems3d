package ru.alfabouh.jgems3d.engine.system.resources.assets.loaders;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.manager.objects.GameResources;

public class ModelAssetsLoader implements IAssetsLoader {
    public MeshDataGroup cube;
    public MeshDataGroup ground2;
    public MeshDataGroup sponza;
    public MeshDataGroup door2;
    public MeshDataGroup plank;

    @Override
    public void load(GameResources gameResources) {
        JGems.get().getScreen().tryAddLineInLoadingScreen("Loading models...");
        this.cube = this.createMesh(gameResources, "/assets/jgems/models/cube/cube.obj", false, true);
        this.ground2 = this.createMesh(gameResources, "/assets/jgems/models/map02/map02.obj", true, false);
        this.sponza = this.createMesh(gameResources, "/assets/jgems/models/sponza/sponza.obj", false, false);
        this.door2 = this.createMesh(gameResources, "/assets/jgems/models/door2/door2.obj", false, false);
        this.plank = this.createMesh(gameResources, "/assets/jgems/models/plank/plank.obj", false, true);
        JGems.get().getScreen().tryAddLineInLoadingScreen("Models successfully loaded...");
    }

    private MeshDataGroup createMesh(GameResources gameResources, String path, boolean constructStaticCollisionMesh, boolean constructDynamicCollisionMesh) {
        MeshDataGroup meshDataGroup = gameResources.createMesh(path);
        meshDataGroup.constructCollisionMesh(constructStaticCollisionMesh, constructDynamicCollisionMesh);
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
