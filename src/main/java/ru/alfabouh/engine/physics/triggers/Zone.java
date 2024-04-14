package ru.alfabouh.engine.physics.triggers;

import org.joml.Vector3d;

public class Zone {
    private final Vector3d location;
    private final Vector3d size;

    public Zone(Vector3d location, Vector3d size) {
        this.location = location;
        this.size = size;
    }

    public Vector3d getLocation() {
        return new Vector3d(this.location);
    }

    public Vector3d getSize() {
        return new Vector3d(this.size);
    }
}
