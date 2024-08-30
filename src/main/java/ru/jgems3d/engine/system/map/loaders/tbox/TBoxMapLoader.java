/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.map.loaders.tbox;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.audio.sound.SoundBuffer;
import ru.jgems3d.engine.audio.sound.data.SoundType;
import ru.jgems3d.engine.graphics.opengl.environment.light.PointLight;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.triggers.Zone;
import ru.jgems3d.engine.physics.world.triggers.liquids.Water;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.map.MapInfo;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.service.exceptions.JGemsNotFoundException;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine_api.app.tbox.TBoxEntitiesUserData;
import ru.jgems3d.engine_api.app.tbox.containers.TUserData;
import ru.jgems3d.logger.managers.LoggingManager;
import ru.jgems3d.toolbox.map_sys.read.TBoxMapReader;
import ru.jgems3d.toolbox.map_sys.save.container.TBoxMapContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.SaveObject;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_table.ObjectsTable;

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
            LoggingManager.showExceptionDialog("Failed to lad map!");
            JGemsHelper.getLogger().error("Failed to load map: " + pathToMap);
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static MapObject readMapFromJar(JGemsPath pathToMap) throws IOException, ClassNotFoundException, JGemsNotFoundException {
        Graph graph = null;
        JGemsPath pathTo = new JGemsPath(pathToMap, "nav.mesh");
        try {
            graph = Graph.readFromFile(pathTo);
        } catch (JGemsNotFoundException e) {
            JGemsHelper.getLogger().warn("Couldn't read NavFile " + pathTo);
        }
        return new MapObject(graph, TBoxMapReader.readMapFromJAR(pathToMap));
    }

    public void readMap(MapObject mapObject) {
        this.mapInfo = new MapInfo(mapObject.getMapContainer().getSaveMapProperties());
        this.saveObjectSet = mapObject.getMapContainer().getSaveObjectsSet();
        this.navMesh = mapObject.getNavMesh();
    }

    @Override
    public void createMap(GameResources globalResources, GameResources localResources, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        TBoxEntitiesUserData tBoxEntitiesUserData = APIContainer.get().getTBoxEntitiesUserData();
        if (this.navMesh != null) {
            physicsWorld.setMapNavGraph(this.navMesh);
            this.navMesh = null;
        }
        if (saveObjectSet != null) {
            for (SaveObject saveObject : this.saveObjectSet) {
                String id = saveObject.getObjectId();
                Vector3f pos = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
                Vector3f rot = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
                Vector3f scale = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);

                TUserData objectUserData = tBoxEntitiesUserData.getEntityUserDataHashMap().get(id);

                switch (id) {
                    case ObjectsTable.PLAYER_START: {
                        this.mapInfo.addSpawnPoint(pos, rot.y);
                        break;
                    }
                    case ObjectsTable.AMBIENT_SOUND: {
                        float soundVolume = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.SOUND_VOL, Float.class);
                        float soundPitch = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.SOUND_PITCH, Float.class);
                        float soundRollOff = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.SOUND_ROLL_OFF, Float.class);
                        String soundAttribute = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.SOUND, String.class);

                        SoundBuffer soundBuffer = localResources.createSoundBuffer(new JGemsPath(soundAttribute), AL10.AL_FORMAT_MONO16);
                        if (soundBuffer == null) {
                            break;
                        }
                        JGemsHelper.getSoundManager().playSoundAt(soundBuffer, SoundType.WORLD_AMBIENT_SOUND, soundPitch, soundVolume, soundRollOff, pos);
                        break;
                    }
                    case ObjectsTable.POINT_LIGHT: {
                        float brightness = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.BRIGHTNESS, Float.class);
                        Vector3f color = saveObject.getAttributeContainer().getValueFromAttributeByID(AttributeID.COLOR, Vector3f.class);

                        PointLight pointLight = new PointLight();
                        pointLight.setLightPos(pos);
                        pointLight.setBrightness(brightness);
                        pointLight.setLightColor(color);

                        JGemsHelper.WORLD.addLight(pointLight);
                        break;
                    }
                    case ObjectsTable.TRIGGER_ZONE: {
                        APIContainer.get().getApiGameInfo().getAppManager().getAppConfiguration().getMapLoaderManager().placeTBoxTriggerZoneOnMap(physicsWorld, pos, scale, id, saveObject.getAttributeContainer(), objectUserData);
                        break;
                    }
                    case ObjectsTable.WATER_LIQUID: {
                        JGemsHelper.WORLD.addLiquid(new Water(new Zone(pos, scale)), JGemsResourceManager.globalRenderDataAssets.water);
                        break;
                    }
                    case ObjectsTable.GENERIC_MARKER: {
                        APIContainer.get().getApiGameInfo().getAppManager().getAppConfiguration().getMapLoaderManager().handleTBoxMarker(sceneWorld, physicsWorld, globalResources, localResources, id, saveObject.getAttributeContainer(), objectUserData);
                        break;
                    }
                    default: {
                        APIContainer.get().getApiGameInfo().getAppManager().getAppConfiguration().getMapLoaderManager().placeTBoxEntityOnMap(sceneWorld, physicsWorld, globalResources, localResources, id, saveObject.getAttributeContainer(), objectUserData);
                    }
                }
            }
        }
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
        APIContainer.get().getApiGameInfo().getAppManager().getAppConfiguration().getMapLoaderManager().mapPostLoad(world, sceneWorld);
    }

    @Override
    public void preLoad(PhysicsWorld world, SceneWorld sceneWorld) {
        APIContainer.get().getApiGameInfo().getAppManager().getAppConfiguration().getMapLoaderManager().mapPreLoad(world, sceneWorld);
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
