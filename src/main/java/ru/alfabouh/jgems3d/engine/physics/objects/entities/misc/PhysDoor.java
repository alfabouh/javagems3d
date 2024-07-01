package ru.alfabouh.jgems3d.engine.physics.objects.entities.misc;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.data.SoundType;
import ru.alfabouh.jgems3d.engine.physics.triggers.Zone;
import ru.alfabouh.jgems3d.engine.physics.triggers.zones.DoorTriggerZone;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;

public class PhysDoor extends WorldItem implements IWorldDynamic {
    private final Vector3f startingRotation;
    private final Vector3f startingPosition;
    private DoorTriggerZone doorTriggerZone;
    private boolean wasOpened;
    private boolean reversedOpening;

    public PhysDoor(World world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName, boolean reversedOpening) {
        super(world, pos, rot, itemName);
        this.startingRotation = new Vector3f(rot);
        this.startingPosition = new Vector3f(pos);
        this.wasOpened = false;
        this.reversedOpening = reversedOpening;
    }

    public boolean isReversedOpening() {
        return this.reversedOpening;
    }

    public void setReversedOpening(boolean reversedOpening) {
        this.reversedOpening = reversedOpening;
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.doorTriggerZone = new DoorTriggerZone(new Zone(new Vector3f(this.getPosition()).add(0.0f, 1.0f, 0.0f), new Vector3f(3.0f)));
        this.getWorld().addTriggerZone(this.getDoorTriggerZone());
    }

    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        this.getWorld().removeTriggerZone(this.getDoorTriggerZone());
    }

    public boolean canBeDestroyed() {
        return false;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.isOpened()) {
            if (this.isReversedOpening()) {
                this.setRotation(new Vector3f(this.startingRotation).add(0.0f, (float) -Math.toRadians(90.0f), 0.0f));
                this.setPosition(new Vector3f(this.startingPosition).sub(-0.7f, 0.0f, 0.7f));
            } else {
                this.setRotation(new Vector3f(this.startingRotation).add(0.0f, (float) Math.toRadians(90.0f), 0.0f));
                this.setPosition(new Vector3f(this.startingPosition).sub(0.7f, 0.0f, 0.7f));
            }
            if (!this.wasOpened) {
                JGems.get().getSoundManager().playSoundAt(ResourceManager.soundAssetsLoader.door, SoundType.WORLD_SOUND, 1.75f, 1.0f, 1.0f, this.getPosition());
            }
            this.wasOpened = true;
        } else {
            this.wasOpened = false;
            this.setRotation(this.startingRotation);
            this.setPosition(this.startingPosition);
        }
    }

    public boolean isOpened() {
        return !this.getDoorTriggerZone().isActive();
    }

    public DoorTriggerZone getDoorTriggerZone() {
        return this.doorTriggerZone;
    }
}
