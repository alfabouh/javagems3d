package javagems3d.system.map.loaders.tbox;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.system.core.player.IPlayerConstructor;
import javagems3d.system.resources.managing.resources.SystemResources;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.graph.Graph;
import javagems3d.system.map.MapInfo;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.service.exceptions.JGemsNotFoundException;
import javagems3d.system.service.path.JGemsPath;
import logger.managers.LoggingManager;
import javagems3d.temp.map_sys.read.TBoxMapReader;
import javagems3d.temp.map_sys.save.container.TBoxMapContainer;
import javagems3d.temp.map_sys.save.objects.SaveObject;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public class TBoxMapLoader implements IMapLoader {
    private MapInfo mapInfo;
    private Set<SaveObject> saveObjectSet;
    private Graph navMesh;

    private TBoxMapLoader(MapObject mapObject) {
        if (mapObject != null) {
            this.readMap(mapObject);
        }
    }

    public static TBoxMapLoader create(JGemsPath pathToMap) {
        try {
            return new TBoxMapLoader(TBoxMapLoader.readMapFromJar(pathToMap));
        } catch (IOException | ClassNotFoundException | JGemsNotFoundException e) {
            LoggingManager.showExceptionDialog("Failed to lad map");
            Log.get().error("Failed to load map: " + pathToMap);
            Log.get().exception(e);
            return null;
        }
    }

    public static MapObject readMapFromJar(JGemsPath pathToMap) throws IOException, ClassNotFoundException, JGemsNotFoundException {
        Graph graph = null;
        JGemsPath pathTo = new JGemsPath(pathToMap, "nav.mesh");
        try {
            graph = Graph.readFromFile(pathTo);
        } catch (JGemsNotFoundException e) {
            Log.get().warn("Couldn't read NavFile " + pathTo);
        }
        return new MapObject(graph, TBoxMapReader.readMapFromJAR(pathToMap));
    }

    public void readMap(MapObject mapObject) {
        this.mapInfo = new MapInfo(mapObject.getMapContainer().getSaveMapProperties());
        this.saveObjectSet = mapObject.getMapContainer().getSaveObjectsSet();
        this.navMesh = mapObject.getNavMesh();
    }

    @Override
    public void createMap(SystemResources globalResources, SystemResources localResources, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {

    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
    }

    @Override
    public void preLoad(PhysicsWorld world, SceneWorld sceneWorld) {
    }

    @Override
    public void fillSkyBox(SkyBox.Background background) {
        //TODO
    }

    @Override
    public @Nullable IPlayerConstructor playerConstructor() {
        return null;
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
