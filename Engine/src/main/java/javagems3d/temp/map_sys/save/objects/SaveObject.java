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

package javagems3d.temp.map_sys.save.objects;

import javagems3d.temp.map_sys.save.objects.object_attributes.AttributesContainer;

import java.io.Serializable;

public class SaveObject implements Serializable {
    private static final long serialVersionUID = -228L;

    private final String objectId;
    private final AttributesContainer attributesContainer;

    public SaveObject(AttributesContainer attributesContainer, String objectId) {
        this.attributesContainer = attributesContainer;
        this.objectId = objectId;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public AttributesContainer getAttributeContainer() {
        return this.attributesContainer;
    }
}
