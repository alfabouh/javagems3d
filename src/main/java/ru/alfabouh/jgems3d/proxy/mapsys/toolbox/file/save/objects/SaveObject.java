package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects;

import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes.AttributeContainer;

import java.io.Serializable;

public class SaveObject implements Serializable {
    private static final long serialVersionUID = -228L;

    private final String objectId;
    private final Vector3d position;
    private final Vector3d rotation;
    private final Vector3d scaling;
    private final AttributeContainer attributeContainer;

    public SaveObject(AttributeContainer attributeContainer, String objectId, Format3D format3D) {
        this(attributeContainer, objectId, format3D.getPosition(), format3D.getRotation(), format3D.getScaling());
    }

    public SaveObject(AttributeContainer attributeContainer, String objectId, Vector3d position, Vector3d rotation, Vector3d scaling) {
        this.attributeContainer = attributeContainer;
        this.objectId = objectId;
        this.position = position;
        this.rotation = rotation;
        this.scaling = scaling;
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

    public AttributeContainer getAttributeContainer() {
        return this.attributeContainer;
    }
}
