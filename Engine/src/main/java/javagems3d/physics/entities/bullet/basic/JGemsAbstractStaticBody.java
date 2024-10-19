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

package javagems3d.physics.entities.bullet.basic;

import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class JGemsAbstractStaticBody extends JGemsBody {
    public JGemsAbstractStaticBody(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(world, pos, rot, scale, itemName);
    }

    public JGemsAbstractStaticBody(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JGemsAbstractStaticBody(PhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    protected void postInit(DynamicsSystem dynamicsSystem, JGemsPhysicsRigidBody jGemsPhysicsRigidBody) {
        this.makeStatic();
        this.setCollisionGroup(CollisionType.ST_BODY);
        this.setCollisionFilterNegative(CollisionType.LIQUID, CollisionType.GHOST);
    }
}
