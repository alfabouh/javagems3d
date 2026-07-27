/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.physics.entities.bullet.bodies;

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

    public JGemsDynamicBody(IColliderConstructor colliderConstructor, PhysicsWorld world, String itemName) {
        this(colliderConstructor, world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    protected IColliderConstructor getColliderConstructor() {
        return this.colliderConstructor;
    }
}
