package javagems3d.system.resources.assets.shaders.buffers;

public final class UniformBufferObject {
    private final String id;
    private final int binding;
    private final int bufferSize;

    public UniformBufferObject(String id, int binding, int bufferSize) {
        this.id = id;
        this.binding = binding;
        this.bufferSize = bufferSize;
    }

    @Override
    public int hashCode() {
        return this.getBinding();
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public String getId() {
        return this.id;
    }

    public int getBinding() {
        return this.binding;
    }
}