package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table;

import org.joml.Vector3d;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.AbstractObjectData;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.MarkerObjectData;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.ModeledObjectData;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.ObjectType;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.Attribute;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeContainer;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeIDS;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeType;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

public class ObjectTable {
    private final Map<String, AbstractObjectData> objects;

    public ObjectTable() {
        this.objects = new HashMap<>();
    }

    public void init() {
        Attribute<Vector3d> colorPlayerAttribute = new Attribute<>(AttributeType.COLOR3, AttributeIDS.COLOR, new Vector3d(1.0f, 0.0f, 0.0f));

        this.addObject("cube_physics", new ModeledObjectData(ResourceManager.shaderAssets.world_object, ResourceManager.createModel("/assets/jgems/models/cube/cube.obj"), ObjectType.PHYSICS_OBJECT));
        this.addObject("door_physics", new ModeledObjectData(ResourceManager.shaderAssets.world_object, ResourceManager.createModel("/assets/jgems/models/door2/door2.obj"), ObjectType.PHYSICS_OBJECT));
        this.addObject("map01_physics", new ModeledObjectData(ResourceManager.shaderAssets.world_object, ResourceManager.createModel("/assets/jgems/models/map01/map01.obj"), ObjectType.PHYSICS_OBJECT));

        this.addObject("cube_model", new ModeledObjectData(ResourceManager.shaderAssets.world_object, ResourceManager.createModel("/assets/jgems/models/cube/cube.obj"), ObjectType.MODEL_OBJECT));

        this.addObject("player_start", new MarkerObjectData(new AttributeContainer(colorPlayerAttribute), ResourceManager.shaderAssets.world_marker, ToolBox.get().getResourceManager().getModelResources().player, ObjectType.SPECIAL_OBJECT));
        this.addObject("generic_marker", new MarkerObjectData(ResourceManager.shaderAssets.world_marker, ToolBox.get().getResourceManager().getModelResources().pointer, ObjectType.SPECIAL_OBJECT));
    }

    public void addObject(String key, AbstractObjectData mapObject) {
        this.getObjects().put(key, mapObject);
    }

    public Map<String, AbstractObjectData> getObjects() {
        return this.objects;
    }
}
