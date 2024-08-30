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

package javagems3d.toolbox.map_sys.save.objects.object_attributes;

import org.jetbrains.annotations.NotNull;
import javagems3d.engine.system.service.exceptions.JGemsRuntimeException;

import java.io.*;

public final class Attribute<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -228L;
    private final String id;
    private final String description;
    private final AttributeTarget attributetarget;
    private T t;

    public Attribute(Attribute<T> attribute) {
        this.attributetarget = attribute.getAttributeType();
        this.id = attribute.getId();
        this.description = attribute.getDescription();
        this.t = new CopyFactory<T>(attribute.getValue()).deepCopy();
    }

    public Attribute(@NotNull AttributeTarget attributetarget, @NotNull String id, @NotNull String description, @NotNull T defaultValue) {
        this.attributetarget = attributetarget;
        this.id = id;
        this.description = description;
        this.t = defaultValue;
    }

    public Attribute(AttributeTarget attributetarget, @NotNull AttributeID name, @NotNull T defaultValue) {
        this(attributetarget, name.getId(), name.getDescription(), defaultValue);
    }

    public <E extends Serializable> void setValueWithCast(E e) {
        if (this.getValue().getClass().isAssignableFrom(e.getClass())) {
            @SuppressWarnings("unchecked")
            Attribute<E> attribute = (Attribute<E>) this;
            attribute.setValue(e);
        }
    }

    public T getValue() {
        return this.t;
    }

    public void setValue(T t) {
        this.t = t;
    }

    public AttributeTarget getAttributeType() {
        return this.attributetarget;
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
                throw new JGemsRuntimeException("Deep copy failed", e);
            }
        }
    }
}
