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

package ru.jgems3d.engine.physics.entities.player;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.physics.entities.properties.controller.IControllable;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

public abstract class Player extends WorldItem implements IControllable {
    public Player(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
    }

    public Player(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        super(world, pos, rot, itemName);
    }

    public Player(PhysicsWorld world, Vector3f pos, String itemName) {
        super(world, pos, itemName);
    }

    public Player(PhysicsWorld world, String itemName) {
        super(world, itemName);
    }

    public abstract float height();
}
