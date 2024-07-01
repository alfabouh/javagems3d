package ru.alfabouh.jgems3d.engine.physics.triggers;

import org.joml.Vector3f;

public class Zone {
    private final Vector3f location;
    private final Vector3f size;

    public Zone(Vector3f location, Vector3f size) {
        this.location = location;
        this.size = size;
    }

    public Vector3f getLocation() {
        return new Vector3f(this.location);
    }

    public Vector3f getSize() {
        return new Vector3f(this.size);
    }
}
