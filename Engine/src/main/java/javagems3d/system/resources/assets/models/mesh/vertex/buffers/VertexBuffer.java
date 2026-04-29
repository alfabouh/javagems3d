package javagems3d.system.resources.assets.models.mesh.vertex.buffers;

import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record VertexBuffer<T extends Number>(RenderAttributePointer renderAttributePointer, List<T> values) {
    public VertexBuffer(@Nullable RenderAttributePointer renderAttributePointer, @NotNull List<T> values) {
        this.values = values;
        this.renderAttributePointer = renderAttributePointer;
    }

    public int getLength() {
        return this.values().size();
    }
}
