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

package jgems_api.horror;

import jgems_api.horror.entities.GhostEnemy;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.audio.sound.GameSound;
import ru.jgems3d.engine.audio.sound.data.SoundType;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.graph.GraphVertex;

public abstract class HorrorGamePlayerState {
    public static final int MAX_BRAINS = 9;

    public static float madness;
    public static int brainsCollected;
    public static float zippoFluid;
    public static float runStamina;

    public static GameSound noiseSound;
    public static GameSound breathSound;

    public static boolean wantsToBeSafe;

    public static boolean lose;
    public static boolean won;

    public static int closeTick;

    static {
        HorrorGamePlayerState.reset();
    }

    public static void updateState(GhostEnemy ghostEnemy) {
        float distToPlayer = ghostEnemy.getPosition().distance(JGemsHelper.getCurrentPlayer().getPosition());

        if (HorrorGamePlayerState.brainsCollected == HorrorGamePlayerState.MAX_BRAINS) {
            HorrorGamePlayerState.won();
            return;
        }
        if (distToPlayer < 1.0f) {
            HorrorGamePlayerState.lose();
            return;
        }

        float dst = 16.0f;
        float madnessFactor = 1.0f - (Math.min(distToPlayer, dst) / dst);

        if (madnessFactor > 0) {
            HorrorGamePlayerState.madness = Math.min(HorrorGamePlayerState.madness + (0.005f * madnessFactor), 1.0f);
        } else {
            HorrorGamePlayerState.madness = Math.max(HorrorGamePlayerState.madness - 0.01f, 0.0f);
        }

        if (HorrorGamePlayerState.madness > 0.7f) {
            if (!HorrorGamePlayerState.breathSound.isPlaying()) {
                HorrorGamePlayerState.breathSound.playSound();
            }
        } else {
            HorrorGamePlayerState.breathSound.pauseSound();
        }

        if (HorrorGamePlayerState.madness > 0.2f) {
            HorrorGamePlayerState.noiseSound.setVolume(HorrorGamePlayerState.madness);
            if (!HorrorGamePlayerState.noiseSound.isPlaying()) {
                HorrorGamePlayerState.noiseSound.playSound();
            }
        } else {
            HorrorGamePlayerState.noiseSound.pauseSound();
        }
    }

    public static void lose() {
        JGemsHelper.GAME.lockController();
        JGemsHelper.GAME.zeroRenderTick();
        JGemsHelper.GAME.killItems();
        HorrorGamePlayerState.lose = true;
        JGemsHelper.getSoundManager().playLocalSound(HorrorGame.get().horrorSoundsLoader.meat, SoundType.BACKGROUND_SOUND, 1.0f, 0.75f);
        HorrorGamePlayerState.noiseSound.setVolume(1.0f);
        if (!HorrorGamePlayerState.noiseSound.isPlaying()) {
            HorrorGamePlayerState.noiseSound.playSound();
        }
    }

    public static void won() {
        JGemsHelper.GAME.lockController();
        JGemsHelper.GAME.zeroRenderTick();
        JGemsHelper.GAME.killItems();
        HorrorGamePlayerState.won = true;
        JGemsHelper.getSoundManager().playLocalSound(HorrorGame.get().horrorSoundsLoader.victory, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
    }

    public static void reset() {
        HorrorGamePlayerState.madness = 0.0f;
        HorrorGamePlayerState.brainsCollected = 0;
        HorrorGamePlayerState.zippoFluid = 1.0f;
        HorrorGamePlayerState.runStamina = 0.0f;
        HorrorGamePlayerState.wantsToBeSafe = false;

        HorrorGamePlayerState.lose = false;
        HorrorGamePlayerState.won = false;

        HorrorGamePlayerState.closeTick = 0;
    }

    public static GraphVertex findRandomVertexFormDist(Graph graph, Vector3f point, float maxDist) {
        GraphVertex graphVertex = null;
        for (int i = 0; i < 128; i++) {
            GraphVertex gV = graph.getRandomVertex();
            if (gV.distanceTo(point) >= maxDist) {
                graphVertex = gV;
                break;
            }
        }
        return graphVertex != null ? graphVertex : graph.getRandomVertex();
    }
}
