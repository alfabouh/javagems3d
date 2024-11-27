package javagems3d.system.resources.assets.models.mesh.vertex.buffers;

import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class VertexBuffer<T> {
    private final List<T> values;
    private final RenderAttributePointer renderAttributePointer;

    public VertexBuffer(@Nullable RenderAttributePointer renderAttributePointer, @NotNull List<T> values) {
        this.values = values;
        this.renderAttributePointer = renderAttributePointer;
    }

    public RenderAttributePointer getRenderAttributePointer() {
        return this.renderAttributePointer;
    }

    public List<T> getValues() {
        return this.values;
    }

    public int getLength() {
        return this.getValues().size();
    }
}
