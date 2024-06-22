package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes;

import org.jetbrains.annotations.NotNull;

import java.io.*;

public final class Attribute <T extends Serializable> {
    private final String id;
    private final String description;
    private final AttributeType attributeType;
    private T t;

    public Attribute(Attribute<T> attribute) {
        this.attributeType = attribute.getAttributeType();
        this.id = attribute.getId();
        this.description = attribute.getDescription();
        this.t = new CopyFactory<T>(attribute.getValue()).deepCopy();
    }

    public Attribute(@NotNull AttributeType attributeType, @NotNull String id, @NotNull String description, @NotNull T defaultValue) {
        this.attributeType = attributeType;
        this.id = id;
        this.description = description;
        this.t = defaultValue;
    }

    public Attribute(AttributeType attributeType, @NotNull AttributeIDS name, @NotNull T defaultValue) {
        this(attributeType, name.getId(), name.getDescription(), defaultValue);
    }

    public void setValue(T t) {
        this.t = t;
    }

    public T getValue() {
        return this.t;
    }

    public AttributeType getAttributeType() {
        return this.attributeType;
    }

    public String getDescription() {
        return this.description;
    }

    public String getId() {
        return this.id;
    }

    public Attribute<T> copy() {
        return new Attribute<>(this);
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    private static class CopyFactory<T extends Serializable> {
        private final T t;

        public CopyFactory(T t) {
            this.t = t;
        }

        @SuppressWarnings("unchecked")
        public T deepCopy() {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(this.t);
                oos.flush();
                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bis);
                return (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Deep copy failed", e);
            }
        }
    }
}
