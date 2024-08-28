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

import jgems_api.horror.HorrorGame;
import jgems_api.horror.HorrorGamePlayerState;
import jgems_api.horror.render.RenderHorrorPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.audio.sound.data.SoundType;
import ru.jgems3d.engine.physics.entities.player.SimpleKinematicPlayer;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class HorrorSimplePlayer extends SimpleKinematicPlayer {
    public HorrorSimplePlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot);
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        super.performController(rotationInput, xyzInput, isFocused);
    }

    @Override
    protected void onTick(IWorld iWorld) {
        super.onTick(iWorld);
        HorrorGamePlayerState.runStamina -= 0.01f;
    }

    protected void swim(Vector3f dir, float speed) {
        super.swim(dir, speed);
        if (dir.length() > 0.0f) {
            if (this.getPhysicsCharacter().onGround() && this.getTicksExisted() % 15 == 0) {
                JGemsHelper.getSoundManager().playLocalSound(HorrorGame.get().horrorSoundsLoader.slosh[JGems3D.random.nextInt(4)], SoundType.BACKGROUND_SOUND, 1.25f, 1.0f);
            }
        }
    }

    @Override
    public float getEyeHeight() {
        return (float) (0.45f + Math.cos(RenderHorrorPlayer.stepBobbing * 0.2f) * 0.1f);
    }

    public boolean canJump() {
        return false;
    }

    protected float walkSpeed() {
        return HorrorGamePlayerState.runStamina > 0 ? 0.2f : 0.125f;
    }
}
