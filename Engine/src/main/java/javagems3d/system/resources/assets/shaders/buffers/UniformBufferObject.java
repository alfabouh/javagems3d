package javagems3d.system.resources.assets.shaders.buffers;

public record UniformBufferObject(String id, int binding, int bufferSize) {

    @Override
    public int hashCode() {
        return this.binding();
    }
}