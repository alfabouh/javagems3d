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

package javagems3d.physics.world.triggers.liquids.base;

import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.zones.SimpleTriggerZone;

public abstract class Liquid implements IWorldObject, IWorldTicked {
    private final SimpleTriggerZone simpleTriggerZone;

    public Liquid(Zone zone) {
        this.simpleTriggerZone = new SimpleTriggerZone(zone);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.getSimpleTriggerZone().onUpdate(iWorld);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        this.getSimpleTriggerZone().onSpawn(iWorld);
        this.init();
    }

    protected void init() {
        this.getSimpleTriggerZone().setTriggerAction(this::onEntityEnteredLiquid);
        this.getSimpleTriggerZone().setCollisionGroup(CollisionType.LIQUID);
        this.getSimpleTriggerZone().setCollisionFilter(CollisionType.PLAYER, CollisionType.DN_BODY);
    }

    protected abstract void onEntityEnteredLiquid(Object e);

    @Override
    public void onDestroy(IWorld iWorld) {
        this.getSimpleTriggerZone().onDestroy(iWorld);
    }

    public SimpleTriggerZone getSimpleTriggerZone() {
        return this.simpleTriggerZone;
    }
}
