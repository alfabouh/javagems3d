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
import jgems_api.horror.entities.ai.MTEnemyChaseAI;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.audio.sound.data.SoundType;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.ai.navigation.NavigationAI;
import ru.jgems3d.engine.physics.world.basic.AIBasedWorldItem;

public class GhostEnemy extends AIBasedWorldItem {
    private MTEnemyChaseAI<GhostEnemy> ai;

    public GhostEnemy(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
    }

    public GhostEnemy(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        super(world, pos, rot, itemName);
    }

    public GhostEnemy(PhysicsWorld world, Vector3f pos, String itemName) {
        super(world, pos, itemName);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        JGemsHelper.getSoundManager().playSoundAtEntity(HorrorGame.get().horrorSoundsLoader.ghost, SoundType.WORLD_AMBIENT_SOUND, 0.5f, 1.0f, 2.25f, this);
        JGemsHelper.getSoundManager().playSoundAtEntity(HorrorGame.get().horrorSoundsLoader.en_steps, SoundType.WORLD_AMBIENT_SOUND, 2.0f, 1.0f, 2.25f, this);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        HorrorGamePlayerState.updateState(this);

        if (HorrorGamePlayerState.wantsToBeSafe) {
            this.ai.teleport(HorrorGamePlayerState.findRandomVertexFormDist(this.getWorld().getMapNavGraph(), this.ai.getFollow().getPosition(), 48.0f));
            HorrorGamePlayerState.wantsToBeSafe = false;
        }
    }

    public MTEnemyChaseAI<GhostEnemy> getAi() {
        return this.ai;
    }

    @Override
    public void init(AIBasedWorldItem aiBasedWorldItem) {
        this.ai = new MTEnemyChaseAI<>(this, 0);
        this.ai.setOffsetFromVertexPos(new Vector3f(0.0f, 1.0f, 0.0f));
        this.addNewAI(this.ai);
    }
}
