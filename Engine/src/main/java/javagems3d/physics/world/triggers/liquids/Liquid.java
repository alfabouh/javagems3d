package javagems3d.physics.world.triggers.liquids;

import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.triggers.ITriggerAction;
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
        this.getSimpleTriggerZone().setTriggerAction(new ITriggerAction() {
            @Override
            public void contactContinue(Object userObject, long pointId) {
                Liquid.this.onEntityCollideLiquid(userObject, pointId);
            }

            @Override
            public void contactStarted(Object userObject, long manifoldId) {
            }

            @Override
            public void contactEnded(Object userObject, long manifoldId) {
            }
        });
        this.getSimpleTriggerZone().setCollisionGroup(CollisionType.LIQUID);
        this.getSimpleTriggerZone().setCollideWithGroups(CollisionType.PLAYER, CollisionType.DN_BODY);
    }

    protected abstract void onEntityCollideLiquid(Object e, long pointId);

    public Zone getZone() {
        return this.getSimpleTriggerZone().getZone();
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        this.getSimpleTriggerZone().onDestroy(iWorld);
    }

    public SimpleTriggerZone getSimpleTriggerZone() {
        return this.simpleTriggerZone;
    }
}
