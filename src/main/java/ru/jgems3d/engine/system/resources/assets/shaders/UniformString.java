package ru.jgems3d.engine.system.resources.assets.shaders;

public final class UniformString {
    private final String uniformRoot;
    private final String uniformPostfix;
    private final int uniformArrayIndex;

    public UniformString(String uniformRoot) {
        this(uniformRoot, "", -1);
    }

    public UniformString(String uniformRoot, int uniformArrayIndex) {
        this(uniformRoot, "", uniformArrayIndex);
    }

    public UniformString(String uniformRoot, String uniformPostfix, int uniformArrayIndex) {
        this.uniformRoot = uniformRoot;
        this.uniformPostfix = uniformPostfix;
        this.uniformArrayIndex = uniformArrayIndex;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        String uniformRoot = this.getUniformRoot();
        int uniformArrayIndex = this.getUniformArrayIndex();
        String uniformPostfix = this.getUniformPostfix();

        if (uniformArrayIndex < 0) {
            return uniformRoot;
        } else {
            return uniformRoot + "[" + uniformArrayIndex + "]" + uniformPostfix;
        }
    }

    private int getUniformArrayIndex() {
        return this.uniformArrayIndex;
    }

    private String getUniformPostfix() {
        return this.uniformPostfix;
    }

    private String getUniformRoot() {
        return this.uniformRoot;
    }
}
