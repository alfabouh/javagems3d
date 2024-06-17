package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects;

import org.joml.Vector3d;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.properties.MapObjectProperties;

import java.io.Serializable;

public class SaveModeledObject implements Serializable {
    private static final long serialVersionUID = -228L;

    private final MapObjectProperties mapObjectProperties;
    private final String objectId;
    private final Vector3d position;
    private final Vector3d rotation;
    private final Vector3d scaling;

    private SaveModeledObject(MapObjectProperties mapObjectProperties, String objectId, Vector3d position, Vector3d rotation, Vector3d scaling) {
        this.mapObjectProperties = mapObjectProperties;
        this.objectId = objectId;
        this.position = position;
        this.rotation = rotation;
        this.scaling = scaling;
    }

    public static SaveModeledObject constructSaveContainer(MapObjectProperties mapObjectProperties, String objectId, Vector3d position, Vector3d rotation, Vector3d scaling) {
        return new SaveModeledObject(mapObjectProperties, objectId, position, rotation, scaling);
    }

    public Vector3d getScaling() {
        return this.scaling;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public MapObjectProperties getMapObjectProperties() {
        return this.mapObjectProperties;
    }
}
