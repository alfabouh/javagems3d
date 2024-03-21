package ru.BouH.engine.physics.world.object;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.GameSound;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.entities.IControllable;
import ru.BouH.engine.physics.particles.ParticleFX;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.world.camera.AttachedCamera;

import java.util.ArrayList;
import java.util.List;

public abstract class WorldItem implements IWorldObject {
    private static int globalId;
    protected final Vector3d position;
    protected final Vector3d rotation;
    private final World world;
    private final Vector3d prevPosition;
    private final String itemName;
    private final int itemId;
    private final List<Light> attachedLights;
    private int spawnTick;
    private boolean isDead;
    private boolean spawned;
    private double scale;
    private PhysicsObject relativeRenderObject;

    public WorldItem(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        this.itemName = itemName;
        this.rotation = new Vector3d(rot);
        this.position = new Vector3d(pos);
        this.prevPosition = new Vector3d(this.position);
        this.scale = scale;
        this.world = world;
        this.attachedLights = new ArrayList<>();
        this.spawned = false;
        this.isDead = false;
        this.itemId = WorldItem.globalId++;
        this.relativeRenderObject = null;
    }

    public WorldItem(World world, double scale, Vector3d pos, String itemName) {
        this(world, scale, pos, new Vector3d(0.0d), itemName);
    }

    public WorldItem(World world, double scale, String itemName) {
        this(world, scale, new Vector3d(0.0d), new Vector3d(0.0d), itemName);
    }

    public WorldItem(World world, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        this(world, 1.0d, pos, rot, itemName);
    }

    public WorldItem(World world, Vector3d pos, String itemName) {
        this(world, 1.0d, pos, new Vector3d(0.0d), itemName);
    }

    public WorldItem(World world, String itemName) {
        this(world, 1.0d, new Vector3d(0.0d), new Vector3d(0.0d), itemName);
    }

    public void onSpawn(IWorld iWorld) {
        this.spawnTick = iWorld.getTicks();
        if (!this.isParticle()) {
            Game.getGame().getLogManager().log("Add entity in world - [ " + this + " ]");
        }
        this.spawned = true;
    }

    public void onDestroy(IWorld iWorld) {
        this.clearLights();
        if (!this.isParticle()) {
            Game.getGame().getLogManager().log("Removed entity from world - [ " + this + " ]");
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

    public Vector3d getPrevPosition() {
        return new Vector3d(this.prevPosition);
    }

    public void setPrevPosition(Vector3d vector3d) {
        this.prevPosition.set(vector3d);
    }

    public int getTicksExisted() {
        return this.getWorld().getTicks() - this.spawnTick;
    }

    public Vector3d getPosition() {
        return new Vector3d(this.position);
    }

    public void setPosition(Vector3d vector3d) {
        this.position.set(vector3d);
    }

    public Vector3d getRotation() {
        return new Vector3d(this.rotation);
    }

    public void setRotation(Vector3d vector3d) {
        this.rotation.set(vector3d);
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean canBeDestroyed() {
        return true;
    }

    public Vector3d getLookVector() {
        return MathHelper.calcLookVector(this.getRotation());
    }

    public void setDead() {
        if (this.canBeDestroyed()) {
            this.isDead = true;
            this.getWorld().removeItem(this);
        }
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
            throw new GameException("Couldn't attach light to NULL render object!");
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

    public boolean tryAttachSoundTo(GameSound sound) {
        if (this.relativeRenderObject() == null) {
            return false;
        }
        sound.setAttachedTo(this.relativeRenderObject());
        return true;
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
