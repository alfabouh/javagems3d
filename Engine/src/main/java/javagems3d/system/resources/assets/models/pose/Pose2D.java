package javagems3d.system.resources.assets.models.pose;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public final class Pose2D implements IPose {
    private final Vector2f position;
    private final Vector2f scale;
    private float rotation;

    public Pose2D(@NotNull Vector2f position, float rotation, Vector2f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Pose2D(Vector2f position, float rotation) {
        this(position, rotation, new Vector2f(1.0f));
    }

    public Pose2D(Vector2f position) {
        this(position, 0.0f, new Vector2f(1.0f));
    }

    public Pose2D() {
        this(new Vector2f(0.0f), 0.0f, new Vector2f(1.0f));
    }

    public Vector2f getPosition() {
        return this.position;
    }

    public void setPosition(Vector2f position) {
        this.getPosition().set(position);
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Vector2f getScale() {
        return this.scale;
    }

    public void setScale(Vector2f scale) {
        this.getScale().set(scale);
    }

    @Override
    public Pose2D copy() {
        return new Pose2D(new Vector2f(this.getPosition()), this.getRotation(), new Vector2f(this.getScale()));
    }
}
