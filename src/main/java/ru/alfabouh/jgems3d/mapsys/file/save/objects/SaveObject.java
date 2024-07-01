package ru.alfabouh.jgems3d.mapsys.file.save.objects;

import ru.alfabouh.jgems3d.mapsys.toolbox.table.object.attributes.AttributeContainer;

import java.io.Serializable;

public class SaveObject implements Serializable {
    private static final long serialVersionUID = -228L;

    private final String objectId;
    private final AttributeContainer attributeContainer;

    public SaveObject(AttributeContainer attributeContainer, String objectId) {
        this.attributeContainer = attributeContainer;
        this.objectId = objectId;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public AttributeContainer getAttributeContainer() {
        return this.attributeContainer;
    }
}
