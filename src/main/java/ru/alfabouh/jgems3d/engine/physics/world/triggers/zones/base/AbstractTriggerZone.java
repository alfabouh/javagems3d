package ru.alfabouh.jgems3d.engine.physics.world.triggers.zones.base;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import ru.alfabouh.jgems3d.engine.physics.entities.properties.collision.CollisionFilter;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.Zone;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public abstract class AbstractTriggerZone implements ITriggerZone {
    protected final Zone zone;
    private PhysicsGhostObject ghostObject;

    public AbstractTriggerZone(Zone zone) {
        this.zone = zone;
    }

    private void initZone(Zone zone) {
        this.ghostObject = new PhysicsGhostObject(new BoxCollisionShape(DynamicsUtils.convertV3F_JME(zone.getSize().mul(0.5f))));
        this.getGhostObject().setPhysicsLocation(DynamicsUtils.convertV3F_JME(zone.getLocation()));
        this.getGhostObject().setUserObject(this);

        this.setCollisionFilter(CollisionFilter.DN_BODY);
        this.setCollisionGroup(CollisionFilter.GHOST);
    }

    public void setCollisionFilter(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getGhostObject().setCollideWithGroups(i);
    }

    public void setCollisionGroup(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getGhostObject().setCollisionGroup(i);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        SystemLogging.get().getLogManager().log("Add trigger in world - [ " + this + " ]");
        this.initZone(this.getZone());
        ((World) iWorld).getDynamics().addCollisionObject(this.getGhostObject());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        SystemLogging.get().getLogManager().log("Removed trigger from world - [ " + this + " ]");
        ((World) iWorld).getDynamics().removeCollisionObject(this.getGhostObject());
    }

    public PhysicsGhostObject getGhostObject() {
        return this.ghostObject;
    }

    public int getCollisionGroup() {
        return this.getGhostObject().getCollisionGroup();
    }

    public int getCollisionFilter() {
        return this.getGhostObject().getCollideWithGroups();
    }

    @Override
    public Zone getZone() {
        return this.zone;
    }

    public String toString() {
        return this.getZone() + " - " + this.getClass().getName();
    }
}
