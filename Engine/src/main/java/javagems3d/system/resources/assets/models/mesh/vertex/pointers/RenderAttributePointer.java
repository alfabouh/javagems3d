package javagems3d.system.resources.assets.models.mesh.vertex.pointers;

public final class RenderAttributePointer {
    private final int index;
    private final int size;
    private final boolean normalized;
    private final int stride;
    private final int pointer;

    public RenderAttributePointer(int index, int size) {
        this(index, size, false, 0, 0);
    }

    public RenderAttributePointer(int index, int size, boolean normalized, int stride, int pointer) {
        this.index = index;
        this.size = size;
        this.normalized = normalized;
        this.stride = stride;
        this.pointer = pointer;
    }

    @Override
    public int hashCode() {
        return this.getIndex();
    }

    public int getIndex() {
        return this.index;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isNormalized() {
        return this.normalized;
    }

    public int getStride() {
        return this.stride;
    }

    public int getPointer() {
        return this.pointer;
    }
}
