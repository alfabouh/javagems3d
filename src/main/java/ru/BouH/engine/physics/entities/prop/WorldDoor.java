package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.triggers.Zone;
import ru.BouH.engine.physics.triggers.zones.DoorTriggerZone;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;

public class WorldDoor extends WorldItem implements IWorldDynamic {
    private final Vector3d startingRotation;
    private final Vector3d startingPosition;
    private DoorTriggerZone doorTriggerZone;
    private boolean wasOpened;
    private boolean reversedOpening;

    public WorldDoor(World world, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName, boolean reversedOpening) {
        super(world, pos, rot, itemName);
        this.startingRotation = new Vector3d(rot);
        this.startingPosition = new Vector3d(pos);
        this.wasOpened = false;
        this.reversedOpening = reversedOpening;
    }

    public void setReversedOpening(boolean reversedOpening) {
        this.reversedOpening = reversedOpening;
    }

    public boolean isReversedOpening() {
        return this.reversedOpening;
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.doorTriggerZone = new DoorTriggerZone(new Zone(new Vector3d(this.getPosition()).add(0.0f, 1.0f, 0.0f), new Vector3d(3.0d)));
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
                this.setRotation(new Vector3d(this.startingRotation).add(0.0d, -Math.toRadians(90.0f), 0.0d));
                this.setPosition(new Vector3d(this.startingPosition).sub(-0.7d, 0.0d, 0.7d));
            } else {
                this.setRotation(new Vector3d(this.startingRotation).add(0.0d, Math.toRadians(90.0f), 0.0d));
                this.setPosition(new Vector3d(this.startingPosition).sub(0.7d, 0.0d, 0.7d));
            }
            if (!this.wasOpened) {
                Game.getGame().getSoundManager().playSoundAt(ResourceManager.soundAssetsLoader.door, SoundType.WORLD_SOUND, 1.75f, 1.0f, 1.0f, this.getPosition());
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
