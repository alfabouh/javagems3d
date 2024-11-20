package javagems3d.system.resources.assets.models.mesh.vertex.buffers;

import java.util.List;

public final class VertexBuffer<T> {
    private final List<T> values;

    public VertexBuffer(List<T> values) {
        this.values = values;
    }

    public List<T> getValues() {
        return this.values;
    }

    public int getLength() {
        return this.getValues().size();
    }
}
