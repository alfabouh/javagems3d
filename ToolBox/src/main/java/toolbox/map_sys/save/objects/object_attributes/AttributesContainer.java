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

package toolbox.map_sys.save.objects.object_attributes;

import logger.SystemLogging;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Object's attributes. If you want to get the attribute value, you can do it like this
 * <pre>
 *     {@code
 *           Vector3f pos = attributesContainer.tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
 *     }
 * </pre>
 */
public final class AttributesContainer implements Serializable {
    private static final long serialVersionUID = -228L;
    private final Map<String, Attribute<?>> attributeSet;

    public AttributesContainer(Attribute<?>... a) {
        this.attributeSet = new LinkedHashMap<>();
        for (Attribute<?> attribute : a) {
            this.attributeSet.put(attribute.getId(), attribute);
        }
    }

    public AttributesContainer(AttributesContainer toCopy) {
        this.attributeSet = new LinkedHashMap<>();
        for (Map.Entry<String, Attribute<?>> attribute : toCopy.getAttributeSet().entrySet()) {
            this.attributeSet.put(attribute.getKey(), attribute.getValue().copy());
        }
    }

    public AttributesContainer() {
        this.attributeSet = new LinkedHashMap<>();
    }

    public boolean hasAttributes() {
        return !this.getAttributeSet().isEmpty();
    }

    public <E extends Serializable> Attribute<E> getAttributeByID(AttributeID attributeID, Class<E> eClass) {
        return this.getAttributeByID(attributeID.getId(), eClass);
    }

    @SuppressWarnings("unchecked")
    public <E extends Serializable> Attribute<E> getAttributeByID(String id, Class<E> eClass) {
        Attribute<?> attribute = this.getAttributeSet().get(id);
        if (attribute == null) {
            return null;
        }
        if (eClass.isInstance(attribute.getValue())) {
            return (Attribute<E>) attribute;
        } else {
            SystemLogging.get().getLogManager().warn("Attribute id: " + id + " has another type: " + attribute.getValue().getClass());
        }
        return null;
    }

    public <E extends Serializable> E getValueFromAttributeByID(AttributeID attributeID, Class<E> eClass) {
        return this.getValueFromAttributeByID(attributeID.getId(), eClass);
    }

    public <E extends Serializable> E getValueFromAttributeByID(String id, Class<E> eClass) {
        Attribute<E> attribute = this.getAttributeByID(id, eClass);
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    public AttributesContainer addAttribute(Attribute<?> attribute) {
        this.getAttributeSet().put(attribute.getId(), attribute);
        return this;
    }

    public Map<String, Attribute<?>> getAttributeSet() {
        return this.attributeSet;
    }
}
