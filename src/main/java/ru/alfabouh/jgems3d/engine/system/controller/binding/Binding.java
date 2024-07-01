package ru.alfabouh.jgems3d.engine.system.controller.binding;

import ru.alfabouh.jgems3d.engine.system.controller.components.Key;

public class Binding {
    private final String description;
    private Key key;

    private Binding(Key key, String description) {
        this.key = key;
        this.description = description;
    }

    private Binding(String description) {
        this(null, description);
    }

    @SuppressWarnings("all")
    public static Binding createBinding(Key key, String description) {
        return new Binding(key, description);
    }

    public void setKeyToBinding(Key key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

    public String getDescription() {
        return this.description;
    }

    public Key getKey() {
        return this.key;
    }

    public String toString() {
        String keyName = this.getKey() == null ? "NULL" : this.getKey().getKeyName();
        return keyName + " - " + this.getDescription();
    }
}
