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

package javagems3d.engine.physics.entities.misc;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.engine.physics.entities.properties.collision.CollisionFilter;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.physics.world.basic.IWorldTicked;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.physics.world.triggers.Zone;
import javagems3d.engine.physics.world.triggers.zones.SimpleTriggerZone;

public class EntityDoor extends WorldItem implements IWorldTicked {
    private final SimpleTriggerZone doorTriggerZone;
    private boolean opened;
    private boolean reversedOpening;

    public EntityDoor(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName, boolean reversedOpening) {
        super(world, pos, rot, itemName);
        this.opened = false;
        this.reversedOpening = reversedOpening;

        this.doorTriggerZone = new SimpleTriggerZone(new Zone(new Vector3f(this.getPosition()).add(0.0f, 1.0f, 0.0f), new Vector3f(3.0f)));
        this.doorTriggerZone.setCollisionFilter(CollisionFilter.PLAYER);
        this.doorTriggerZone.setTriggerAction((e) -> {
            this.opened = true;
        });
    }

    public boolean isReversedOpening() {
        return this.reversedOpening;
    }

    public void setReversedOpening(boolean reversedOpening) {
        this.reversedOpening = reversedOpening;
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.getDoorTriggerZone().onSpawn(iWorld);
    }

    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        this.getDoorTriggerZone().onDestroy(iWorld);
    }

    public boolean canBeDestroyed() {
        return false;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.isOpened()) {
            if (this.isReversedOpening()) {
                this.setRotation(new Vector3f(this.startRot).add(0.0f, (float) -Math.toRadians(90.0f), 0.0f));
                this.setPosition(new Vector3f(this.startPos).sub(-0.7f, 0.0f, 0.7f));
            } else {
                this.setRotation(new Vector3f(this.startRot).add(0.0f, (float) Math.toRadians(90.0f), 0.0f));
                this.setPosition(new Vector3f(this.startPos).sub(0.7f, 0.0f, 0.7f));
            }
        } else {
            this.setRotation(this.startRot);
            this.setPosition(this.startPos);
        }
        this.opened = false;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public SimpleTriggerZone getDoorTriggerZone() {
        return this.doorTriggerZone;
    }
}
