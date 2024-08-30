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

package ru.jgems3d.toolbox.map_table;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine_api.app.tbox.TBoxEntitiesObjectData;
import ru.jgems3d.engine_api.app.tbox.containers.TObjectData;
import ru.jgems3d.toolbox.ToolBox;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.Attribute;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeTarget;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_table.object.AABBZoneObjectData;
import ru.jgems3d.toolbox.map_table.object.AbstractObjectData;
import ru.jgems3d.toolbox.map_table.object.MarkerObjectData;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;
import ru.jgems3d.toolbox.resources.TBoxResourceManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;

public class ObjectsTable {
    public static final String WATER_LIQUID = "water_liquid";
    public static final String TRIGGER_ZONE = "trigger_zone";
    public static final String POINT_LIGHT = "point_light";
    public static final String PLAYER_START = "player_start";
    public static final String GENERIC_MARKER = "generic_marker";
    public static final String AMBIENT_SOUND = "ambient_sound";

    private final Map<String, AbstractObjectData> objects;

    public ObjectsTable() {
        this.objects = new TreeMap<>();
    }

    public void init(TBoxResourceManager tBoxResourceManager) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        APIContainer.get().getApiTBoxInfo().getAppInstance().initEntitiesObjectData(tBoxResourceManager, APIContainer.get().getTBoxEntitiesObjectData());

        TBoxEntitiesObjectData objectData = APIContainer.get().getTBoxEntitiesObjectData();

        Attribute<Float> soundVolume = new Attribute<>(AttributeTarget.FLOAT_0_50, AttributeID.SOUND_VOL, 1.0f);
        Attribute<Float> soundPitch = new Attribute<>(AttributeTarget.FLOAT_0_50, AttributeID.SOUND_PITCH, 1.0f);
        Attribute<Float> soundRollOff = new Attribute<>(AttributeTarget.FLOAT_0_50, AttributeID.SOUND_ROLL_OFF, 1.0f);
        Attribute<String> soundAttribute = new Attribute<>(AttributeTarget.STRING, AttributeID.SOUND, JGems3D.Paths.SOUNDS);

        Attribute<Vector3f> transformPosXYZ = new Attribute<>(AttributeTarget.POSITION_XYZ, AttributeID.POSITION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformScalingXYZ = new Attribute<>(AttributeTarget.SCALING_XYZ, AttributeID.SCALING_XYZ, new Vector3f(1.0f));

        Attribute<Vector3f> rotationPlayerAttribute = new Attribute<>(AttributeTarget.ROTATION_Y, AttributeID.ROTATION_XYZ, new Vector3f(0.0f, (float) Math.toRadians(90.0f), 0.0f));

        Attribute<Vector3f> colorAttributeStatic1 = new Attribute<>(AttributeTarget.STATIC_NO_EDIT, AttributeID.COLOR, new Vector3f(1.0f, 0.0f, 0.0f));
        Attribute<Vector3f> colorAttributeStatic2 = new Attribute<>(AttributeTarget.STATIC_NO_EDIT, AttributeID.COLOR, new Vector3f(0.0f, 1.0f, 0.0f));
        Attribute<Vector3f> colorAttributeStatic3 = new Attribute<>(AttributeTarget.STATIC_NO_EDIT, AttributeID.COLOR, new Vector3f(0.0f, 0.0f, 10f));

        Attribute<Vector3f> colorAttribute = new Attribute<>(AttributeTarget.COLOR3, AttributeID.COLOR, new Vector3f(1.0f));

        Attribute<Float> brightness = new Attribute<>(AttributeTarget.FLOAT_0_50, AttributeID.BRIGHTNESS, 1.0f);
        Attribute<Vector3f> minScale = new Attribute<>(AttributeTarget.STATIC_NO_EDIT, AttributeID.SCALING_XYZ, new Vector3f(0.125f));
        Attribute<Vector3f> minScale2 = new Attribute<>(AttributeTarget.STATIC_NO_EDIT, AttributeID.SCALING_XYZ, new Vector3f(0.5f));

        Attribute<String> name = new Attribute<>(AttributeTarget.STRING, AttributeID.NAME, "id");

        for (Map.Entry<String, TObjectData> objectData1 : objectData.getEntityObjectDataHashMap().entrySet()) {
            this.addObject(objectData1.getKey(), objectData1.getValue().getAbstractObjectData());
        }

        this.addObject(ObjectsTable.WATER_LIQUID, new AABBZoneObjectData(new AttributesContainer(transformPosXYZ, transformScalingXYZ, colorAttributeStatic3), TBoxResourceManager.shaderResources().world_transparent_color, ObjectCategory.ZONES));
        this.addObject(ObjectsTable.TRIGGER_ZONE, new AABBZoneObjectData(new AttributesContainer(transformPosXYZ, transformScalingXYZ, colorAttributeStatic2, name), TBoxResourceManager.shaderResources().world_transparent_color, ObjectCategory.ZONES));

        this.addObject(ObjectsTable.POINT_LIGHT, new MarkerObjectData(new AttributesContainer(transformPosXYZ, brightness, colorAttribute, minScale), TBoxResourceManager.shaderResources().world_object_nolight, ToolBox.get().getResourceManager().getModelResources().sphere, ObjectCategory.GENERIC));
        this.addObject(ObjectsTable.PLAYER_START, new MarkerObjectData(new AttributesContainer(transformPosXYZ, rotationPlayerAttribute, colorAttributeStatic1), TBoxResourceManager.shaderResources().world_object, ToolBox.get().getResourceManager().getModelResources().player, ObjectCategory.GENERIC));
        this.addObject(ObjectsTable.GENERIC_MARKER, new MarkerObjectData(new AttributesContainer(transformPosXYZ, colorAttribute, name), TBoxResourceManager.shaderResources().world_object, ToolBox.get().getResourceManager().getModelResources().pointer, ObjectCategory.GENERIC));
        this.addObject(ObjectsTable.AMBIENT_SOUND, new MarkerObjectData(new AttributesContainer(transformPosXYZ, minScale2, soundVolume, soundPitch, soundRollOff, soundAttribute, colorAttributeStatic2), TBoxResourceManager.shaderResources().world_object, ToolBox.get().getResourceManager().getModelResources().cubic, ObjectCategory.GENERIC));
    }

    public void addObject(String key, AbstractObjectData mapObject) {
        this.getObjects().put(key, mapObject);
    }

    public Map<String, AbstractObjectData> getObjects() {
        return this.objects;
    }
}