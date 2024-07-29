package ru.jgems3d.engine.system.resources.assets.shaders;

public class UniformBufferObject {
    private final String id;
    private final int binding;
    private final int bufferSize;

    public UniformBufferObject(String id, int binding, int bufferSize) {
        this.id = id;
        this.binding = binding;
        this.bufferSize = bufferSize;
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