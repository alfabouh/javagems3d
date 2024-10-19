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

package javagems3d.physics.entities.bullet.bodies;

import javagems3d.physics.entities.bullet.basic.JGemsAbstractDynamicBody;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.physics.colliders.IColliderConstructor;
import javagems3d.physics.world.PhysicsWorld;

public class JGemsDynamicBody extends JGemsAbstractDynamicBody {
    private final IColliderConstructor colliderConstructor;

    public JGemsDynamicBody(IColliderConstructor colliderConstructor, PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scale, String itemName) {
        super(world, pos, rot, scale, itemName);
        this.colliderConstructor = colliderConstructor;
    }

    public JGemsDynamicBody(IColliderConstructor colliderConstructor, PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        this(colliderConstructor, world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JGemsDynamicBody(IColliderConstructor colliderConstructor, PhysicsWorld world, @NotNull Vector3f pos, String itemName) {
        this(colliderConstructor, world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    protected IColliderConstructor constructCollision() {
        return this.colliderConstructor;
    }
}
