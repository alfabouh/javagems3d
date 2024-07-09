package ru.alfabouh.jgems3d.toolbox.map_table;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.math.Pair;
import ru.alfabouh.jgems3d.toolbox.map_table.object.AbstractObjectData;
import ru.alfabouh.jgems3d.toolbox.map_table.object.MarkerObjectData;
import ru.alfabouh.jgems3d.toolbox.map_table.object.ModeledObjectData;
import ru.alfabouh.jgems3d.toolbox.map_table.object.ObjectType;
import ru.alfabouh.jgems3d.map_sys.save.objects.object_attributes.Attribute;
import ru.alfabouh.jgems3d.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.alfabouh.jgems3d.map_sys.save.objects.object_attributes.AttributeFlag;
import ru.alfabouh.jgems3d.map_sys.save.objects.object_attributes.AttributeIDS;
import ru.alfabouh.jgems3d.toolbox.map_table.yml.YMLMapObjects;
import ru.alfabouh.jgems3d.toolbox.map_table.yml.containers.YMLMapObjectsContainer;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.resources.TBoxResourceManager;

import java.util.HashMap;
import java.util.Map;

public class ObjectsTable {
    private final Map<String, AbstractObjectData> objects;

    public ObjectsTable() {
        this.objects = new HashMap<>();
    }

    public void init() {
        YMLMapObjects ymlMapObjects = new YMLMapObjects();
        YMLMapObjectsContainer mapObjectsContainer = ymlMapObjects.loadYAMLObject(JGems.loadFileJar("/assets/jgems/configs/map_objects.yml"));

        Attribute<Float> soundVolume = new Attribute<>(AttributeFlag.FLOAT_0_50, AttributeIDS.SOUND_VOL, 1.0f);
        Attribute<String> soundAttribute = new Attribute<>(AttributeFlag.STRING, AttributeIDS.SOUND, "");

        Attribute<Vector3f> transformPosXYZ = new Attribute<>(AttributeFlag.POSITION_XYZ, AttributeIDS.POSITION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformRotXYZ = new Attribute<>(AttributeFlag.ROTATION_XYZ, AttributeIDS.ROTATION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformScaleXYZ = new Attribute<>(AttributeFlag.SCALING_XYZ, AttributeIDS.SCALING_XYZ, new Vector3f(1.0f));

        Attribute<Vector3f> rotationPlayerAttribute = new Attribute<>(AttributeFlag.ROTATION_Y, AttributeIDS.ROTATION_XYZ, new Vector3f(0.0f, (float) Math.toRadians(90.0f), 0.0f));

        Attribute<Vector3f> colorPlayerStaticAttribute = new Attribute<>(AttributeFlag.STATIC_NO_EDIT, AttributeIDS.COLOR, new Vector3f(1.0f, 0.0f, 0.0f));
        Attribute<Vector3f> colorAttributeSound = new Attribute<>(AttributeFlag.STATIC_NO_EDIT, AttributeIDS.COLOR, new Vector3f(0.0f, 1.0f, 0.0f));

        Attribute<Vector3f> colorAttribute = new Attribute<>(AttributeFlag.COLOR3, AttributeIDS.COLOR, new Vector3f(1.0f));
        Attribute<Boolean> physicStatic = new Attribute<>(AttributeFlag.BOOL, AttributeIDS.IS_STATIC, true);

        for (Map.Entry<String, Pair<String, ObjectType>> e : mapObjectsContainer.getMap().entrySet()) {
            ObjectType objectType = e.getValue().getSecond();
            switch (objectType) {
                case PROP_OBJECT: {
                    this.addObject(e.getKey(), new ModeledObjectData(new AttributeContainer(transformPosXYZ, transformRotXYZ, transformScaleXYZ), TBoxResourceManager.shaderAssets.world_object, TBoxResourceManager.createModel(e.getValue().getFirst()), objectType));
                    break;
                }
                case PHYSICS_OBJECT: {
                    this.addObject(e.getKey(), new ModeledObjectData(new AttributeContainer(physicStatic, transformPosXYZ, transformRotXYZ, transformScaleXYZ), TBoxResourceManager.shaderAssets.world_object, TBoxResourceManager.createModel(e.getValue().getFirst()), objectType));
                    break;
                }
            }
        }
        this.addObject("door_physics", new ModeledObjectData(new AttributeContainer(transformPosXYZ, transformRotXYZ, transformScaleXYZ), TBoxResourceManager.shaderAssets.world_object, TBoxResourceManager.createModel("/assets/jgems/models/door2/door2.obj"), ObjectType.PHYSICS_OBJECT));

        this.addObject("player_start", new MarkerObjectData(new AttributeContainer(transformPosXYZ, rotationPlayerAttribute, colorPlayerStaticAttribute), TBoxResourceManager.shaderAssets.world_marker, ToolBox.get().getResourceManager().getModelResources().player, ObjectType.MARKER_OBJECT));
        this.addObject("generic_marker", new MarkerObjectData(new AttributeContainer(transformPosXYZ, colorAttribute), TBoxResourceManager.shaderAssets.world_marker, ToolBox.get().getResourceManager().getModelResources().pointer, ObjectType.MARKER_OBJECT));
        this.addObject("ambient_sound", new MarkerObjectData(new AttributeContainer(transformPosXYZ, soundVolume, soundAttribute, colorAttributeSound), TBoxResourceManager.shaderAssets.world_marker, ToolBox.get().getResourceManager().getModelResources().cubic, ObjectType.MARKER_OBJECT));
    }

    public void addObject(String key, AbstractObjectData mapObject) {
        this.getObjects().put(key, mapObject);
    }

    public Map<String, AbstractObjectData> getObjects() {
        return this.objects;
    }
}
