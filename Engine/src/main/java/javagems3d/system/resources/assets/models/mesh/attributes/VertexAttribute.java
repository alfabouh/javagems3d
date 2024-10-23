package javagems3d.system.resources.assets.models.mesh.attributes;

import javagems3d.system.resources.assets.models.mesh.attributes.pointer.AttributePointer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public abstract class VertexAttribute<T> {
    private final AttributePointer attributePointer;
    private final List<T> values;

    public VertexAttribute(AttributePointer attributePointer) {
        this.attributePointer = attributePointer;
        this.values = new ArrayList<>();
    }

    public void put(List<T> values) {
        this.getValues().addAll(values);
    }

    public void put(T value) {
        this.getValues().add(value);
    }

    public abstract void pushGLBuffer();
    public abstract void bake();
    public abstract Buffer getBuffer();
    public abstract int attributeType();

    public void clearData() {
        this.getValues().clear();
        MemoryUtil.memFree(this.getBuffer());
    }

    public List<T> getValues() {
        return this.values;
    }

    @Override
    public final int hashCode() {
        return this.getAttributePointer().getIndex();
    }

    public int getIndex() {
        return this.getAttributePointer().getIndex();
    }

    public AttributePointer getAttributePointer() {
        return this.attributePointer;
    }
}
