package javagems3d.physics.world.triggers;

import org.joml.Vector3f;

public record Zone(Vector3f location, Vector3f size) {

    @Override
    public Vector3f location() {
        return new Vector3f(this.location);
    }

    @Override
    public Vector3f size() {
        return new Vector3f(this.size);
    }

    public String toString() {
        return this.location() + " - " + this.size();
    }
}
