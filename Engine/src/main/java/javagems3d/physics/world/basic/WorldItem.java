/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.physics.world.basic;

import javagems3d.help.JGemsHelper;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;

public abstract class WorldItem implements IWorldObject {
    public static int globalId;

    private final Object positionLock = new Object();
    private final Object rotationLock = new Object();
    private final Object scalingLock = new Object();
    private final Object prevPositionLock = new Object();
    private final Object stateLock = new Object();

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
        synchronized (this.positionLock) {
            if (position != null) {
                this.startPosition = new Vector3f(position);
            }
        }

        synchronized (this.rotationLock) {
            if (rotation != null) {
                this.startRotation = new Vector3f(rotation);
            }
        }

        synchronized (this.scalingLock) {
            if (scaling != null) {
                this.startScaling = new Vector3f(scaling);
            }
        }
    }

    public void resetWarp() {
        this.setPosition(this.startPosition);
        this.setRotation(this.startRotation);
    }

    public void onSpawn(IWorld iWorld) {
        Log.get().trace("Added entity in world - [ " + this + " ]");

        synchronized (this.stateLock) {
            this.spawnTick = iWorld.getTicks();
            this.spawned = true;
        }
    }

    public void onDestroy(IWorld iWorld) {
        Log.get().trace("Removed entity from world - [ " + this + " ]");
    }

    public boolean isSpawned() {
        synchronized (this.stateLock) {
            return this.spawned;
        }
    }

    public Vector3f getPrevPosition() {
        synchronized (this.prevPositionLock) {
            return new Vector3f(this.prevPosition);
        }
    }

    public void setPrevPosition(Vector3f vector3f) {
        synchronized (this.prevPositionLock) {
            this.prevPosition.set(vector3f);
        }
    }

    public int getTicksExisted() {
        synchronized (this.stateLock) {
            return this.getWorld().getTicks() - this.spawnTick;
        }
    }

    public Vector3f getPosition() {
        synchronized (this.positionLock) {
            return new Vector3f(this.position);
        }
    }

    public void setPosition(Vector3f vector3f) {
        synchronized (this.positionLock) {
            this.position.set(vector3f);
        }
    }

    public Vector3f getRotation() {
        synchronized (this.rotationLock) {
            return new Vector3f(this.rotation);
        }
    }

    public void setRotation(Vector3f vector3f) {
        synchronized (this.rotationLock) {
            this.rotation.set(vector3f);
        }
    }

    public Vector3f getScaling() {
        synchronized (this.scalingLock) {
            return new Vector3f(this.scaling);
        }
    }

    public void setScaling(Vector3f scaling) {
        synchronized (this.scalingLock) {
            this.scaling = scaling;
        }
    }

    public boolean canBeDestroyed() {
        return true;
    }

    public Vector3f getLookVector() {
        return JGemsHelper.math().calcLookVector(this.getRotation());
    }

    public void setDead() {
        if (this.canBeDestroyed()) {
            this.destroy();
        }
    }

    public void destroy() {
        synchronized (this.stateLock) {
            this.isDead = true;
            this.getWorld().removeObject(this);
        }
    }

    public boolean isRemoteControlled() {
        return this instanceof IControllable && ((IControllable) this).isValidController();
    }

    public boolean isDead() {
        synchronized (this.stateLock) {
            return this.isDead;
        }
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
