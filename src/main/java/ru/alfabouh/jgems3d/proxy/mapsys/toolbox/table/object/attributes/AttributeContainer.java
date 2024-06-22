package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes;

import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AttributeContainer {
    private final Map<String, Attribute<?>> attributeSet;

    public AttributeContainer(Attribute<?>... a) {
        this.attributeSet = new LinkedHashMap<>();
        for (Attribute<?> attribute : a) {
            this.attributeSet.put(attribute.getId(), attribute);
        }
    }

    public AttributeContainer(AttributeContainer toCopy) {
        this.attributeSet = new LinkedHashMap<>();
        for (Map.Entry<String, Attribute<?>> attribute : toCopy.getAttributeSet().entrySet()) {
            this.attributeSet.put(attribute.getKey(), attribute.getValue().copy());
        }
    }

    public AttributeContainer() {
        this.attributeSet = new LinkedHashMap<>();
    }

    public boolean hasAttributes() {
        return !this.getAttributeSet().isEmpty();
    }

    public <E extends Serializable> Attribute<E> tryGetAttributeByID(AttributeIDS attributeIDS, Class<E> eClass) {
        return this.tryGetAttributeByID(attributeIDS.getId(), eClass);
    }

    @SuppressWarnings("unchecked")
    public <E extends Serializable> Attribute<E> tryGetAttributeByID(String id, Class<E> eClass) {
        Attribute<?> attribute = this.getAttributeSet().get(id);
        if (attribute == null) {
            SystemLogging.get().getLogManager().warn("Couldn't find attribute with id: " + id);
            return null;
        }
        if (eClass.isInstance(attribute.getValue())) {
            return (Attribute<E>) attribute;
        } else {
            SystemLogging.get().getLogManager().warn("Attribute id: " + id + " has another type!");
        }
        return null;
    }

    public <E extends Serializable> E tryGetValueFromAttributeByID(AttributeIDS attributeIDS, Class<E> eClass) {
        return this.tryGetValueFromAttributeByID(attributeIDS.getId(), eClass);
    }

    public <E extends Serializable> E tryGetValueFromAttributeByID(String id, Class<E> eClass) {
        Attribute<E> attribute = this.tryGetAttributeByID(id, eClass);
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    public AttributeContainer addAttribute(Attribute<?> attribute) {
        this.getAttributeSet().put(attribute.getId(), attribute);
        return this;
    }

    public Map<String, Attribute<?>> getAttributeSet() {
        return this.attributeSet;
    }
}
