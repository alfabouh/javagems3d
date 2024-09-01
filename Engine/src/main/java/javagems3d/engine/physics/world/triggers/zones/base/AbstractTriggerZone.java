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

package javagems3d.engine.physics.world.triggers.zones.base;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import org.joml.Vector3f;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.physics.entities.properties.collision.CollisionFilter;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.engine.physics.world.triggers.Zone;

public abstract class AbstractTriggerZone implements ITriggerZone {
    protected Zone zone;
    private PhysicsGhostObject ghostObject;

    public AbstractTriggerZone(Zone zone) {
        this.initZone(zone);
        this.zone = zone;
    }

    private void initZone(Zone zone) {
        this.ghostObject = new PhysicsGhostObject(new BoxCollisionShape(DynamicsUtils.convertV3F_JME(zone.getSize().mul(0.5f))));
        this.getGhostObject().setPhysicsLocation(DynamicsUtils.convertV3F_JME(zone.getLocation()));
        this.getGhostObject().setUserObject(this);

        this.setCollisionFilter(CollisionFilter.DN_BODY, CollisionFilter.PLAYER);
        this.setCollisionGroup(CollisionFilter.GHOST);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        JGemsHelper.getLogger().log("Add trigger in world - [ " + this + " ]");
        ((PhysicsWorld) iWorld).getDynamics().addCollisionObject(this.getGhostObject());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        JGemsHelper.getLogger().log("Removed trigger from world - [ " + this + " ]");
        ((PhysicsWorld) iWorld).getDynamics().removeCollisionObject(this.getGhostObject());
    }

    public void setLocation(Vector3f location) {
        this.zone = new Zone(location, this.getZone().getSize());
        DynamicsUtils.translateGhost(this.getGhostObject(), location);
    }

    public PhysicsGhostObject getGhostObject() {
        return this.ghostObject;
    }

    public int getCollisionGroup() {
        return this.getGhostObject().getCollisionGroup();
    }

    public void setCollisionGroup(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getGhostObject().setCollisionGroup(i);
    }

    public int getCollisionFilter() {
        return this.getGhostObject().getCollideWithGroups();
    }

    public void setCollisionFilter(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getGhostObject().setCollideWithGroups(i);
    }

    @Override
    public Zone getZone() {
        return this.zone;
    }

    public String toString() {
        return this.getZone() + " - " + this.getClass().getName();
    }
}
