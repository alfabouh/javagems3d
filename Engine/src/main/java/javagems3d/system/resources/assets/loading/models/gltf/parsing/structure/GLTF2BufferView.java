package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

public final class GLTF2BufferView {
    private final int id;
    private final int byteLength;
    private final int byteOffset;
    private final int byteStride;
    private final int target;

    public GLTF2BufferView(int id, int byteLength, int byteOffset, int byteStride, int target) {
        this.id = id;
        this.byteLength = byteLength;
        this.byteOffset = byteOffset;
        this.byteStride = byteStride;
        this.target = target;
    }

    public int getId() {
        return this.id;
    }

    public int getByteLength() {
        return this.byteLength;
    }

    public int getByteOffset() {
        return this.byteOffset;
    }

    public int getByteStride() {
        return this.byteStride;
    }

    public int getTarget() {
        return this.target;
    }
}
