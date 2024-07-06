package ru.alfabouh.jgems3d.engine.system.map.current;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.models.SceneProp;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.misc.PhysDoor;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.common.PhysDynamicEntity;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.common.PhysStaticEntity;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.system.map.MapInfo;
import ru.alfabouh.jgems3d.engine.system.map.legacy.loader.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.YMLRenderEntityData;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.YMLRenderObjects;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.YMLRenderPropData;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers.YMLRenderEntityDataContainer;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers.YMLRenderObjectsContainer;
import ru.alfabouh.jgems3d.engine.system.resources.yml_loaders.containers.YMLRenderPropDataContainer;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.managers.LoggingManager;
import ru.alfabouh.jgems3d.mapsys.file.read.TBoxReader;
import ru.alfabouh.jgems3d.mapsys.file.save.container.SaveContainer;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.MapProperties;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.SaveObject;
import ru.alfabouh.jgems3d.mapsys.toolbox.table.object.attributes.AttributeIDS;

import java.io.IOException;
import java.util.Set;

public class MapLoaderTBox implements IMapLoader {
    private MapProperties mapProperties;
    private MapInfo mapInfo;
    private Set<SaveObject> saveObjectSet;

    public MapLoaderTBox(SaveContainer saveContainer) {
        if (saveContainer != null) {
            this.readMap(saveContainer);
        }
    }

    public static SaveContainer readMapFromJar(String mapName) {
        try {
            return TBoxReader.readMapFolderFromJAR(mapName);
        } catch (IOException | ClassNotFoundException e) {
            LoggingManager.showExceptionDialog("Failed to lad map!");
            SystemLogging.get().getLogManager().error("Failed to load map: " + mapName);
            e.printStackTrace(System.err);
        }
        return null;
    }

    public void readMap(SaveContainer saveContainer) {
        this.mapInfo = new MapInfo(saveContainer.getSaveMapProperties());
        this.saveObjectSet = saveContainer.getSaveObjectsSet();
    }

    @Override
    public void createMap(World world) {
        YMLRenderEntityData ymlRenderEntityData = new YMLRenderEntityData();
        YMLRenderEntityDataContainer entityDataContainer = ymlRenderEntityData.loadYAMLObject(JGems.loadFileJar("/assets/jgems/configs/entities_render_data.yml"));

        YMLRenderPropData ymlRenderPropData = new YMLRenderPropData();
        YMLRenderPropDataContainer propDataContainer = ymlRenderPropData.loadYAMLObject(JGems.loadFileJar("/assets/jgems/configs/props_render_data.yml"));

        YMLRenderObjects ymlRenderObjects = new YMLRenderObjects();
        YMLRenderObjectsContainer objectsContainer = ymlRenderObjects.loadYAMLObject(JGems.loadFileJar("/assets/jgems/configs/map_objects.yml"));

        if (saveObjectSet != null) {
            for (SaveObject saveObject : this.saveObjectSet) {
                String id = saveObject.getObjectId();
                Vector3f pos = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.POSITION_XYZ, Vector3f.class);
                Vector3f rot = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.ROTATION_XYZ, Vector3f.class);
                Vector3f scale = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.SCALING_XYZ, Vector3f.class);
                if (objectsContainer.getMap().containsKey(id)) {
                    String type = objectsContainer.getMap().get(id).getType();
                    String meshDataGroupS = objectsContainer.getMap().get(id).getMeshDataGroupName();
                    MeshDataGroup meshDataGroup = JGemsResourceManager.getLocalGameResources().createMesh(meshDataGroupS);
                    String renderObjectData1 = objectsContainer.getMap().get(id).getRenderObjectData();

                    switch (type) {
                        case "entity": {
                            RenderObjectData renderObjectData = entityDataContainer.getMap().get(renderObjectData1);
                            boolean isStatic = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.IS_STATIC, Boolean.class);
                            if (isStatic) {
                                meshDataGroup.constructCollisionMesh(true, false);
                                PhysStaticEntity worldModeledBrush = new PhysStaticEntity(world, id, RigidBodyObject.PhysProperties.createProperties(), pos, meshDataGroup);
                                JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(renderObjectData, meshDataGroup));
                                worldModeledBrush.setCanBeDestroyed(false);
                                worldModeledBrush.setRotation(rot);
                                worldModeledBrush.setScale(scale);
                                worldModeledBrush.setDebugDrawing(false);
                            } else {
                                meshDataGroup.constructCollisionMesh(false, true);
                                PhysDynamicEntity worldModeledBrush = new PhysDynamicEntity(world, id, RigidBodyObject.PhysProperties.createProperties(), pos, meshDataGroup);
                                JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(renderObjectData, meshDataGroup));
                                worldModeledBrush.setRotation(rot);
                                worldModeledBrush.setScale(scale);
                                worldModeledBrush.setDebugDrawing(false);
                            }
                            break;
                        }
                        case "prop": {

                            ModelRenderParams modelRenderParams = propDataContainer.getMap().get(renderObjectData1).getModelRenderParams();
                            IRenderFabric renderFabric = propDataContainer.getMap().get(renderObjectData1).getRenderFabric();
                            Model<Format3D> model = new Model<>(new Format3D(), meshDataGroup);
                            model.getFormat().setPosition(pos);
                            model.getFormat().setRotation(rot);
                            model.getFormat().setScaling(scale);
                            JGems.get().getScreen().getScene().getSceneWorld().addRenderObjectInScene(new SceneProp(renderFabric, model, modelRenderParams));
                            break;
                        }
                        default: {
                            SystemLogging.get().getLogManager().warn("Skipped map object type: " + type);
                            break;
                        }
                    }
                    continue;
                }
                switch (id) {
                    case "door_physics": {
                        PhysDoor physDoor = new PhysDoor(world, pos, rot, "door", false);
                        JGems.get().getProxy().addItemInWorlds(physDoor, JGemsResourceManager.renderDataAssets.door1);
                        break;
                    }
                    case "player_start": {
                        this.mapInfo.addSpawnPoint(pos, rot.y);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public MapInfo getLevelInfo() {
        return this.mapInfo;
    }
}
