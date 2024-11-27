package javagems3d.system.resources.assets.models.mesh.vertex.pointers;

public final class RenderAttributePointer {
    private final int index;
    private final int length;
    private final int bytes;
    private final boolean normalized;
    private final int stride;
    private final int pointer;

    public RenderAttributePointer(int index, int length, int bytes) {
        this(index, length, bytes, false, 0, 0);
    }

    public RenderAttributePointer(int index, int lengthInMemory, int bytes, boolean normalized, int stride, int pointer) {
        this.bytes = bytes;
        this.index = index;
        this.length = lengthInMemory;
        this.normalized = normalized;
        this.stride = stride;
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj == this.getIndex();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.getIndex();
    }

    public int getBytes() {
        return this.bytes;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLengthInMemory() {
        return this.length;
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
