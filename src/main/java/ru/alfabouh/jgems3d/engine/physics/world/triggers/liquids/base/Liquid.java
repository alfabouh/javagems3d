package ru.alfabouh.jgems3d.engine.physics.world.triggers.liquids.base;

import ru.alfabouh.jgems3d.engine.physics.entities.properties.collision.CollisionFilter;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.Zone;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.zones.SimpleTriggerZone;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldTicked;

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
        this.getSimpleTriggerZone().setCollisionGroup(CollisionFilter.LIQUID);
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
