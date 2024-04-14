package ru.alfabouh.engine.physics.liquids;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.entities.states.EntityState;
import ru.alfabouh.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.engine.physics.triggers.Zone;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;

public class Water implements ILiquid {
    private final Zone zone;
    private btPairCachingGhostObject ghostObject;
    private btCollisionShape collisionShape;

    public Water(Zone zone) {
        this.zone = zone;
    }

    private void createGhostZone() {
        this.ghostObject = new btPairCachingGhostObject();
        double d1_1 = this.getZone().getSize().x / 2.0d - 0.1f;
        double d1_2 = this.getZone().getSize().y / 2.0d - 0.25d;
        double d1_3 = this.getZone().getSize().z / 2.0d - 0.1f;
        this.collisionShape = new btBoxShape(new btVector3(d1_1, d1_2, d1_3));
        this.ghostObject.setCollisionShape(this.collisionShape);
        this.ghostObject.setCollisionFlags(btCollisionObject.CF_NO_CONTACT_RESPONSE | btCollisionObject.CF_STATIC_OBJECT);
        try (btTransform transform = this.ghostObject.getWorldTransform()) {
            transform.setOrigin(new btVector3(this.getZone().getLocation().x, this.getZone().getLocation().y, this.getZone().getLocation().z));
            this.ghostObject.setWorldTransform(transform);
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        World world = (World) iWorld;
        for (JBulletEntity bullet : world.getAllBulletItems()) {
            btOverlappingPairCache btHashedOverlappingPairCache = world.getDynamicsWorld().getPairCache();
            boolean collided = btHashedOverlappingPairCache.findPair(bullet.getBulletObject().getBroadphaseHandle(), this.ghostObject.getBroadphaseHandle()) != null;
            if (collided) {
                bullet.entityState().setState(EntityState.StateType.IN_WATER);
            } else {
                bullet.entityState().removeState(EntityState.StateType.IN_WATER);
            }
            btHashedOverlappingPairCache.deallocate();
        }
    }

    @Override
    public Zone getZone() {
        return this.zone;
    }

    @Override
    public btPairCachingGhostObject triggerZoneGhostCollision() {
        return this.ghostObject;
    }

    public BodyGroup getBodyGroup() {
        return BodyGroup.LIQUID;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        this.createGhostZone();
        Game.getGame().getLogManager().log("Created new Liquid: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        this.collisionShape.deallocate();
        Game.getGame().getLogManager().log("Destroyed Liquid: Location=" + this.getZone().getLocation() + " | Size=" + this.getZone().getSize());
    }
}
