package jgems_api.example.resources;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.manager.GameResources;


public class ModelLoader implements IAssetsLoader {
    public MeshDataGroup ground2;

    @Override
    public void load(GameResources gameResources) {
        this.ground2 = this.createMesh(gameResources, new JGPath(JGems3D.Paths.MODELS, "map04/map04.obj"), true);
    }

    private MeshDataGroup createMesh(GameResources gameResources, JGPath path, boolean constructCollisionMesh) {
        MeshDataGroup meshDataGroup = gameResources.createMesh(path);
        if (constructCollisionMesh) {
            JGemsHelper.tryCreateMeshCollisionData(meshDataGroup);
        }
        return meshDataGroup;
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.NORMAL;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}

