package ru.alfabouh.jgems3d.engine.physics.world.object;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.physics.objects.base.IControllable;
import ru.alfabouh.jgems3d.engine.physics.particles.ParticleFX;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.AttachedCamera;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.ArrayList;
import java.util.List;

public abstract class WorldItem implements IWorldObject {
    private static int globalId;
    protected final Vector3f position;
    protected final Vector3f rotation;
    private final World world;
    private final Vector3f prevPosition;
    private final String itemName;
    private final int itemId;
    private final List<Light> attachedLights;
    private int spawnTick;
    private boolean isDead;
    private boolean spawned;
    private Vector3f scale;
    private PhysicsObject relativeRenderObject;

    public WorldItem(World world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        this.itemName = itemName;
        this.rotation = new Vector3f(rot);
        this.position = new Vector3f(pos);
        this.prevPosition = new Vector3f(this.position);
        this.scale = scale;
        this.world = world;
        this.attachedLights = new ArrayList<>();
        this.spawned = false;
        this.isDead = false;
        this.itemId = WorldItem.globalId++;
        this.relativeRenderObject = null;
    }

    public WorldItem(World world, Vector3f pos, Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public WorldItem(World world ,Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public WorldItem(World world, String itemName) {
        this(world, new Vector3f(1.0f), new Vector3f(0.0f), new Vector3f(0.0f), itemName);
    }

    public void onSpawn(IWorld iWorld) {
        this.spawnTick = iWorld.getTicks();
        if (!this.isParticle()) {
            SystemLogging.get().getLogManager().log("Add entity in world - [ " + this + " ]");
        }
        this.spawned = true;
    }

    public void onDestroy(IWorld iWorld) {
        this.clearLights();
        if (!this.isParticle()) {
            SystemLogging.get().getLogManager().log("Removed entity from world - [ " + this + " ]");
        }
    }

    public boolean isSpawned() {
        return this.spawned;
    }

    public void setRelativeRenderObject(PhysicsObject relativeRenderObject) {
        this.relativeRenderObject = relativeRenderObject;
    }

    public String toString() {
        return this.getItemName() + "(" + this.getItemId() + ")";
    }

    public Vector3f getPrevPosition() {
        return new Vector3f(this.prevPosition);
    }

    public void setPrevPosition(Vector3f vector3f) {
        this.prevPosition.set(vector3f);
    }

    public int getTicksExisted() {
        return this.getWorld().getTicks() - this.spawnTick;
    }

    public Vector3f getPosition() {
        return new Vector3f(this.position);
    }

    public void setPosition(Vector3f vector3f) {
        this.position.set(vector3f);
    }

    public Vector3f getRotation() {
        return new Vector3f(this.rotation);
    }

    public void setRotation(Vector3f vector3f) {
        this.rotation.set(vector3f);
    }

    public Vector3f getScale() {
        return new Vector3f(this.scale);
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public boolean canBeDestroyed() {
        return true;
    }

    public Vector3f getLookVector() {
        return MathHelper.calcLookVector(this.getRotation());
    }

    public void setDead() {
        if (this.canBeDestroyed()) {
            this.destroy();
        }
    }

    public void destroy() {
        this.isDead = true;
        this.getWorld().removeItem(this);
    }

    public boolean isRemoteControlled() {
        return this instanceof IControllable && ((IControllable) this).isValidController();
    }

    public final List<Light> getAttachedLights() {
        return new ArrayList<>(this.attachedLights);
    }

    public final boolean hasLight() {
        return !this.attachedLights.isEmpty();
    }

    public final void disableLight(int i) {
        this.getAttachedLights().get(i).stop();
    }

    public final void clearLights() {
        for (Light light : this.attachedLights) {
            light.stop();
        }
        this.attachedLights.clear();
    }

    public final Light attachLight(Light light) {
        this.attachedLights.add(light);
        if (this.relativeRenderObject() == null) {
            throw new JGemsException("Couldn't attach light to NULL render object!");
        }
        light.setAttachedTo(this.relativeRenderObject());
        return light;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public World getWorld() {
        return this.world;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getItemName() {
        return this.itemName;
    }

    public boolean tryAttachRenderCamera(AttachedCamera attachedCamera) {
        if (this.relativeRenderObject() == null) {
            return false;
        }
        attachedCamera.attachCameraToItem(this.relativeRenderObject());
        return true;
    }

    public boolean isParticle() {
        return this instanceof ParticleFX;
    }

    private PhysicsObject relativeRenderObject() {
        return this.relativeRenderObject;
    }
}
