package javagems3d.system.resources.assets.models.mesh.vertex.attributes;

import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public abstract class VertexAttribute<T> {
    private final RenderAttributePointer attributePointer;
    private List<T> values;

    public VertexAttribute(RenderAttributePointer attributePointer) {
        this.attributePointer = attributePointer;
        this.values = new ArrayList<>();
    }

    public VertexAttribute<T> set(List<T> values) {
        this.values = values;
        return this;
    }

    public VertexAttribute<T> put(List<T> values) {
        this.getValues().addAll(values);
        return this;
    }

    public VertexAttribute<T> put(T value) {
        this.getValues().add(value);
        return this;
    }

    public abstract void pushGLBuffer();
    public abstract Buffer getBuffer();
    public abstract int attributeType();

    public void clearData() {
        this.getValues().clear();
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

    public RenderAttributePointer getAttributePointer() {
        return this.attributePointer;
    }
}
