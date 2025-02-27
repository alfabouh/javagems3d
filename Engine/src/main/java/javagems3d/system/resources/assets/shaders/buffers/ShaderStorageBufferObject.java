package javagems3d.system.resources.assets.shaders.buffers;

import java.nio.ByteBuffer;

public final class ShaderStorageBufferObject {
    private final int binding;
    private final int bufferSize;
    private ByteBuffer mappedBuffer;

    public ShaderStorageBufferObject(int binding) {
        this(binding, -1);
    }

    public ShaderStorageBufferObject(int binding, int bufferSize) {
        this.binding = binding;
        this.bufferSize = bufferSize;
        this.mappedBuffer = null;
    }

    @Override
    public int hashCode() {
        return this.getBinding();
    }

    public ByteBuffer getMappedBuffer() {
        return this.mappedBuffer;
    }

    public void setMappedBuffer(ByteBuffer mappedBuffer) {
        this.mappedBuffer = mappedBuffer;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public int getBinding() {
        return this.binding;
    }
}