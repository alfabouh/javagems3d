package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders;

import java.util.HashSet;
import java.util.Set;

public class Uniform {
    private final String id;
    private final int arraySize;
    private final Set<String> fields;

    public Uniform(String id, int arraySize) {
        this.id = id;
        this.fields = new HashSet<>();
        this.arraySize = Math.max(arraySize, 1);
    }

    public Uniform(String id) {
        this(id, 1);
    }

    public Set<String> getFields() {
        return this.fields;
    }

    public String getId() {
        return this.id;
    }

    public int getArraySize() {
        return this.arraySize;
    }
}
