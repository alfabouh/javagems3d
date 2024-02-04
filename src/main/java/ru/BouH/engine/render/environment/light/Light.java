package ru.BouH.engine.render.environment.light;

import org.joml.Vector3d;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.render.scene.objects.items.PhysicsObjectModeled;

public abstract class Light implements IWorldDynamic {
    public static final int
            POINT_LIGHT = (1 << 2);

    private final Vector3d offset;
    private final Vector3d lightColor;
    private final Vector3d lightPos;
    private PhysicsObjectModeled attachedTo;
    private boolean enabled;

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
        this.enabled = false;
        this.attachedTo = null;
    }

    public Light(PhysicsObjectModeled physicsObject) {
        this(physicsObject.getRenderPosition(), new Vector3d(1.0d), new Vector3d(0.0d));
    }

    public Light(PhysicsObjectModeled physicsObject, Vector3d lightColor) {
        this(physicsObject.getRenderPosition(), lightColor, new Vector3d(0.0d));
    }

    public Light(PhysicsObjectModeled physicsObject, Vector3d lightColor, Vector3d offset) {
        this(physicsObject.getRenderPosition(), lightColor, offset);
    }

    public PhysicsObjectModeled getAttachedTo() {
        return this.attachedTo;
    }

    public void setAttachedTo(PhysicsObjectModeled attachedTo) {
        this.attachedTo = attachedTo;
    }

    public void enable() {
        if (this.isAttached()) {
            this.attachedTo.onAddLight(this);
        }
        this.setEnabled(true);
    }

    public void disable() {
        if (this.isAttached()) {
            this.attachedTo.onRemoveLight(this);
        }
        this.setEnabled(false);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isAttached() {
        return this.getAttachedTo() != null;
    }

    public abstract int lightCode();

    public Light setLightColor(Vector3d lightColor) {
        this.lightColor.set(lightColor);
        return this;
    }

    public Light setLightPos(Vector3d lightPos) {
        this.lightPos.set(lightPos);
        return this;
    }

    public Light setOffset(Vector3d offset) {
        this.offset.set(offset);
        return this;
    }

    public Vector3d getLightColor() {
        return new Vector3d(this.lightColor);
    }

    public Vector3d getLightPos() {
        return new Vector3d(this.lightPos);
    }

    public Vector3d getOffset() {
        return new Vector3d(this.offset);
    }
}
