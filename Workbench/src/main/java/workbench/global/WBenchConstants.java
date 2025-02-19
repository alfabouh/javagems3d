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

package workbench.global;

import javagems3d.JGems3D;

public abstract class WBenchConstants {
    public static final float CAM_SENS = 0.001f;
    public static float FOV = (float) Math.toRadians(60.0f);
    public static float Z_NEAR = 0.1f;
    public static float Z_FAR = (float) JGems3D.MAP_MAX_SIZE * 2.0f;
}
