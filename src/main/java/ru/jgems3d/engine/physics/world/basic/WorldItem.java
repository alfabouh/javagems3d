package ru.jgems3d.engine.physics.world.basic;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.math.MathHelper;
import ru.jgems3d.engine.physics.entities.properties.controller.IControllable;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.JGemsHelper;

public abstract class WorldItem implements IWorldObject {
    private static int globalId;

    protected Vector3f startPos;
    protected Vector3f startRot;
    protected Vector3f startScaling;

    private final Vector3f position;
    private final Vector3f rotation;
    private Vector3f scaling;

    private final PhysicsWorld world;
    private final Vector3f prevPosition;
    private final String itemName;
    private final int itemId;
    private int spawnTick;
    private boolean isDead;
    private boolean spawned;

    public WorldItem(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        this.itemName = (itemName == null || itemName.isEmpty()) ? "default_item" : itemName;

        this.world = world;
        this.spawned = false;
        this.isDead = false;
        this.itemId = WorldItem.globalId++;

        this.startPos = new Vector3f(pos);
        this.startRot = new Vector3f(rot);
        this.startScaling = new Vector3f(scaling);

        this.position = new Vector3f(pos);
        this.rotation = new Vector3f(rot);
        this.scaling = new Vector3f(scaling);

        this.prevPosition = new Vector3f(pos);
    }

    public WorldItem(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public WorldItem(PhysicsWorld world, Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public WorldItem(PhysicsWorld world, String itemName) {
        this(world, new Vector3f(1.0f), new Vector3f(0.0f), new Vector3f(0.0f), itemName);
    }

    public void resetWarp() {
        this.setPosition(this.startPos);
        this.setRotation(this.startRot);
    }

    public void onSpawn(IWorld iWorld) {
        this.spawnTick = iWorld.getTicks();
        if (!this.isParticle()) {
            JGemsHelper.getLogger().log("Add entity in world - [ " + this + " ]");
        }
        this.spawned = true;
    }

    public void onDestroy(IWorld iWorld) {
        if (!this.isParticle()) {
            JGemsHelper.getLogger().log("Removed entity from world - [ " + this + " ]");
        }
    }

    public boolean isSpawned() {
        return this.spawned;
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

    public Vector3f getScaling() {
        return new Vector3f(this.scaling);
    }

    public void setScaling(Vector3f scaling) {
        this.scaling = scaling;
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

    public boolean isDead() {
        return this.isDead;
    }

    public PhysicsWorld getWorld() {
        return this.world;
    }

    public int getItemId() {
        return this.itemId;
    }

    public String getItemName() {
        return this.itemName;
    }

    public boolean isParticle() {
        return false;
    }

    public String toString() {
        return this.getItemName() + "(" + this.getItemId() + ")";
    }
}
