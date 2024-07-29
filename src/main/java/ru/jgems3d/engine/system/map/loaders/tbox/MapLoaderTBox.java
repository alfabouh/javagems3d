package ru.jgems3d.engine.system.map.loaders.tbox;

import org.joml.Vector3f;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.math.Pair;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.map.MapInfo;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.files.JGPath;
import ru.jgems3d.engine_api.app.tbox.AppTBoxObjectsContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.logger.managers.LoggingManager;
import ru.jgems3d.toolbox.map_sys.read.TBoxMapReader;
import ru.jgems3d.toolbox.map_sys.save.container.SaveContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.SaveObject;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

import java.io.IOException;
import java.util.Set;

public class MapLoaderTBox implements IMapLoader {
    private MapInfo mapInfo;
    private Set<SaveObject> saveObjectSet;

    public MapLoaderTBox(SaveContainer saveContainer) {
        if (saveContainer != null) {
            this.readMap(saveContainer);
        }
    }

    public static SaveContainer readMapFromJar(String mapName) {
        try {
            return TBoxMapReader.readMapFolderFromJAR(mapName);
        } catch (IOException | ClassNotFoundException e) {
            LoggingManager.showExceptionDialog("Failed to lad map!");
            JGemsHelper.getLogger().error("Failed to load map: " + mapName);
            e.printStackTrace(System.err);
        }
        return null;
    }

    public void readMap(SaveContainer saveContainer) {
        this.mapInfo = new MapInfo(saveContainer.getSaveMapProperties());
        this.saveObjectSet = saveContainer.getSaveObjectsSet();
    }

    @Override
    public void createMap(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        AppTBoxObjectsContainer appTBoxObjectsContainer = APIContainer.get().getAppTBoxObjectsContainer();

        if (saveObjectSet != null) {
            for (SaveObject saveObject : this.saveObjectSet) {
                String id = saveObject.getObjectId();
                Vector3f pos = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
                Vector3f rot = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
                Vector3f scale = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);

                Pair<TEntityContainer, TRenderContainer> containerPair = appTBoxObjectsContainer.getMap().get(id);

                if (containerPair != null) {
                    TEntityContainer tEntityContainer = containerPair.getFirst();
                    TRenderContainer tRenderContainer = containerPair.getSecond();

                    ObjectCategory type = tEntityContainer.getObjectCategory();

                    if (type.equals(ObjectCategory.GENERIC)) {
                        if (id.equals("player_start")) {
                            this.mapInfo.addSpawnPoint(pos, rot.y);
                            continue;
                        } else {
                            //TODO
                        }
                    } else {
                        APIContainer.get().getApiGameInfo().getAppManager().putIncomingObjectOnMap(sceneWorld, physicsWorld, JGemsHelper.getLocalResources(), id, type, tEntityContainer.getAttributeContainer(), tRenderContainer);
                    }
                }
            }
        }
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {

    }

    @Override
    public MapInfo getLevelInfo() {
        return this.mapInfo;
    }
}
