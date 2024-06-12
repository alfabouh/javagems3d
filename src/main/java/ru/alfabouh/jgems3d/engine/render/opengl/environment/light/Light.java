package ru.alfabouh.jgems3d.engine.render.opengl.environment.light;

import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;

public abstract class Light implements IWorldDynamic {
    public static final int
            POINT_LIGHT = (1 << 2);

    private final Vector3d offset;
    private final Vector3d lightColor;
    private final Vector3d lightPos;
    private PhysicsObject attachedTo;
    private boolean enabled;
    private boolean isActive;

    public Light() {
        this(new Vector3d(0.0d), new Vector3d(0.0d), new Vector3d(0.0d));
    }

    public Light(Vector3d lightPos, Vector3d lightColor) {
        this(lightPos, lightColor, new Vector3d(0.0d));
    }

    public Light(Vector3d lightPos) {
        this(lightPos, new Vector3d(1.0d), new Vector3d(0.0d));
    }

    public Light(Vector3d lightPos, Vector3d lightColor, Vector3d offset) {
        this.lightColor = new Vector3d(lightColor);
        this.lightPos = new Vector3d(lightPos);
        this.offset = new Vector3d(offset);
        this.enabled = true;
        this.isActive = false;
        this.attachedTo = null;
    }

    public Light(PhysicsObject physicsObject) {
        this(physicsObject.getRenderPosition(), new Vector3d(1.0d), new Vector3d(0.0d));
    }

    public Light(PhysicsObject physicsObject, Vector3d lightColor) {
        this(physicsObject.getRenderPosition(), lightColor, new Vector3d(0.0d));
    }

    public Light(PhysicsObject physicsObject, Vector3d lightColor, Vector3d offset) {
        this(physicsObject.getRenderPosition(), lightColor, offset);
    }

    public PhysicsObject getAttachedTo() {
        return this.attachedTo;
    }

    public void setAttachedTo(PhysicsObject attachedTo) {
        this.attachedTo = attachedTo;
    }

    public void start() {
        if (this.isAttached()) {
            this.attachedTo.onAddLight(this);
        }
        this.isActive = true;
    }

    public void stop() {
        if (this.isAttached()) {
            this.attachedTo.onRemoveLight(this);
        }
        this.isActive = false;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isEnabled() {
        return this.isActive() && this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAttached() {
        return this.getAttachedTo() != null;
    }

    public abstract int lightCode();

    public Vector3d getLightColor() {
        return new Vector3d(this.lightColor);
    }

    public Light setLightColor(Vector3d lightColor) {
        this.lightColor.set(lightColor);
        return this;
    }

    public Vector3d getLightPos() {
        return new Vector3d(this.lightPos);
    }

    public Light setLightPos(Vector3d lightPos) {
        this.lightPos.set(lightPos);
        return this;
    }

    public Vector3d getOffset() {
        return new Vector3d(this.offset);
    }

    public Light setOffset(Vector3d offset) {
        this.offset.set(offset);
        return this;
    }
}
