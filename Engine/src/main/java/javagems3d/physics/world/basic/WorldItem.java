/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.physics.world.basic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;

/**
 * World Item is the main class from which all objects of the physical world are inherited.
 */
public abstract class WorldItem implements IWorldObject {
    private static int globalId;
    private final Vector3f position;
    private final Vector3f rotation;
    private final PhysicsWorld world;
    private final Vector3f prevPosition;
    private final String itemName;
    private final int itemId;
    protected Vector3f startPosition;
    protected Vector3f startRotation;
    protected Vector3f startScaling;
    private Vector3f scaling;
    private int spawnTick;
    private boolean isDead;
    private boolean spawned;

    public WorldItem(PhysicsWorld world, @NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scaling, String itemName) {
        this.itemName = (itemName == null || itemName.isEmpty()) ? "default_item" : itemName;

        this.world = world;
        this.spawned = false;
        this.isDead = false;
        this.itemId = WorldItem.globalId++;

        this.setStartTransformations(position, rotation, scaling);

        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scaling = new Vector3f(scaling);

        this.prevPosition = new Vector3f(position);
    }

    public WorldItem(PhysicsWorld world, Vector3f position, Vector3f rotation, String itemName) {
        this(world, position, rotation, new Vector3f(1.0f), itemName);
    }

    public WorldItem(PhysicsWorld world, Vector3f position, String itemName) {
        this(world, position, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public WorldItem(PhysicsWorld world, String itemName) {
        this(world, new Vector3f(1.0f), new Vector3f(0.0f), new Vector3f(0.0f), itemName);
    }

    public void setStartTransformations(@Nullable Vector3f position, @Nullable Vector3f rotation, @Nullable Vector3f scaling) {
        if (position != null) {
            this.startPosition = new Vector3f(position);
        }
        if (rotation != null) {
            this.startRotation = new Vector3f(rotation);
        }
        if (scaling != null) {
            this.startScaling = new Vector3f(scaling);
        }
    }

    public void resetWarp() {
        this.setPosition(this.startPosition);
        this.setRotation(this.startRotation);
    }

    public void onSpawn(IWorld iWorld) {
        this.spawnTick = iWorld.getTicks();
        JGemsHelper.getLogger().log("Add entity in world - [ " + this + " ]");
        this.spawned = true;
    }

    public void onDestroy(IWorld iWorld) {
        JGemsHelper.getLogger().log("Removed entity from world - [ " + this + " ]");
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
        return JGemsHelper.UTILS.calcLookVector(this.getRotation());
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

    public String toString() {
        return this.getItemName() + "(" + this.getItemId() + ")";
    }
}
