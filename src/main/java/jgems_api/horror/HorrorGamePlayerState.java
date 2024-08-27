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

public abstract class HorrorGamePlayerState {
    public static final int MAX_BRAINS = 9;

    public static int brainsCollected;
    public static float zippoFluid;
    public static float runStamina;

    static {
        HorrorGamePlayerState.reset();
    }

    public static void reset() {
        HorrorGamePlayerState.brainsCollected = 0;
        HorrorGamePlayerState.zippoFluid = 1.0f;
        HorrorGamePlayerState.runStamina = 0.0f;
    }
}
