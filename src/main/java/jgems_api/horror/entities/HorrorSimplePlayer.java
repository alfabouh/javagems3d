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

package jgems_api.horror.entities;

import jgems_api.horror.render.RenderHorrorPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.physics.entities.player.SimpleKinematicPlayer;
import ru.jgems3d.engine.physics.world.PhysicsWorld;

public class HorrorSimplePlayer extends SimpleKinematicPlayer {
    public HorrorSimplePlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot);
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        super.performController(rotationInput, xyzInput, isFocused);
    }

    @Override
    public float getEyeHeight() {
        return (float) (0.45f + Math.cos(RenderHorrorPlayer.stepBobbing * 0.2f) * 0.1f);
    }

    public boolean canJump() {
        return false;
    }

    protected float walkSpeed() {
        return 0.125f;
    }
}
