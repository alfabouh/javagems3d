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

package javagems3d.physics.world.basic;

import javagems3d.physics.world.PhysicsWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BasicWorldItem extends WorldItem {
    public BasicWorldItem(PhysicsWorld world, @NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scaling, String itemName) {
        super(world, position, rotation, scaling, itemName);
    }

    public BasicWorldItem(PhysicsWorld world, Vector3f position, Vector3f rotation, String itemName) {
        super(world, position, rotation, itemName);
    }

    public BasicWorldItem(PhysicsWorld world, Vector3f position, String itemName) {
        super(world, position, itemName);
    }

    public BasicWorldItem(PhysicsWorld world, String itemName) {
        super(world, itemName);
    }
}
