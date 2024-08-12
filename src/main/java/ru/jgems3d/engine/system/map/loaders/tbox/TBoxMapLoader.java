package ru.jgems3d.engine.system.map.loaders.tbox;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.service.collections.Pair;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.map.MapInfo;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine_api.app.tbox.AppTBoxObjectsContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TEntityContainer;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.logger.managers.LoggingManager;
import ru.jgems3d.toolbox.map_sys.read.TBoxMapReader;
import ru.jgems3d.toolbox.map_sys.save.container.TBoxMapContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.SaveObject;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

import java.io.IOException;
import java.util.Set;

public class TBoxMapLoader implements IMapLoader {
    private MapInfo mapInfo;
    private Set<SaveObject> saveObjectSet;
    private Graph navMesh;

    public TBoxMapLoader(MapObject mapObject) {
        if (mapObject != null) {
            this.readMap(mapObject);
        }
    }

    public static MapObject readMapFromJar(JGemsPath pathToMap) {
        try {
            return new MapObject(Graph.readFromFile(new JGemsPath(pathToMap, "nav.mesh")), TBoxMapReader.readMapFolderFromJAR(pathToMap));
        } catch (IOException | ClassNotFoundException e) {
            LoggingManager.showExceptionDialog("Failed to lad map!");
            JGemsHelper.getLogger().error("Failed to load map: " + pathToMap);
            e.printStackTrace(System.err);
        }
        return null;
    }

    public void readMap(MapObject mapObject) {
        this.mapInfo = new MapInfo(mapObject.getMapContainer().getSaveMapProperties());
        this.saveObjectSet = mapObject.getMapContainer().getSaveObjectsSet();
        this.navMesh = mapObject.getNavMesh();
    }

    @Override
    public void createMap(GameResources localResources, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
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
                        APIContainer.get().getApiGameInfo().getAppManager().placeObjectInTBoxMap(sceneWorld, physicsWorld, localResources, id, type, tEntityContainer.getAttributeContainer(), tRenderContainer);
                    }
                }
            }

            if (this.navMesh != null) {
                physicsWorld.setMapNavGraph(this.navMesh);
                this.navMesh = null;
            }
        }
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {

    }

    @Override
    public @NotNull MapInfo getLevelInfo() {
        return this.mapInfo;
    }

    public static class MapObject {
        private final Graph navMesh;
        private final TBoxMapContainer mapContainer;

        public MapObject(Graph navMesh, TBoxMapContainer mapContainer) {
            this.navMesh = navMesh;
            this.mapContainer = mapContainer;
        }

        public Graph getNavMesh() {
            return this.navMesh;
        }

        public TBoxMapContainer getMapContainer() {
            return this.mapContainer;
        }
    }
}
