package ru.alfabouh.jgems3d.engine.system.map.current;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.props.PhysStaticProp;
import ru.alfabouh.jgems3d.engine.physics.objects.materials.Materials;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.misc.PhysDoor;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.system.map.MapInfo;
import ru.alfabouh.jgems3d.engine.system.map.legacy.loader.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
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

    public void readMap(SaveContainer saveContainer) {
        this.mapInfo = new MapInfo(saveContainer.getSaveMapProperties());
        this.saveObjectSet = saveContainer.getSaveObjectsSet();
    }

    @Override
    public void createMap(World world) {
        if (saveObjectSet != null) {
            for (SaveObject saveObject : this.saveObjectSet) {
                String id = saveObject.getObjectId();
                Vector3f pos = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.POSITION_XYZ, Vector3f.class);
                Vector3f rot = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.ROTATION_XYZ, Vector3f.class);
                Vector3f rot_neg = new Vector3f(rot).negate();
                Vector3f scale = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeIDS.SCALING_XYZ, Vector3f.class);
                switch (id) {
                    case "map01_physics": {
                        PhysStaticProp worldModeledBrush = new PhysStaticProp(world, "grass", RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), pos, ResourceManager.modelAssets.ground);
                        JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(ResourceManager.renderDataAssets.ground, ResourceManager.modelAssets.ground));
                        worldModeledBrush.setRotation(rot_neg);
                        worldModeledBrush.setScale(scale);
                        worldModeledBrush.setDebugDrawing(false);
                        break;
                    }
                    case "door_physics": {
                        PhysDoor physDoor = new PhysDoor(world, pos, rot, "door", false);
                        JGems.get().getProxy().addItemInWorlds(physDoor, ResourceManager.renderDataAssets.door1);
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
}
