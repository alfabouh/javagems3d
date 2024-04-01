package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
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

    public WorldDoor(World world, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        super(world, pos, rot, itemName);
        this.startingRotation = new Vector3d(rot);
        this.startingPosition = new Vector3d(pos);
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.doorTriggerZone = new DoorTriggerZone(new Zone(this.getPosition(), new Vector3d(3.0d)));
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
            this.setRotation(new Vector3d(this.startingRotation).add(0.0d, Math.toRadians(90.0f), 0.0d));
            this.setPosition(new Vector3d(this.startingPosition).sub(-0.5d, 0.0d, -0.5d));
        } else {
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
